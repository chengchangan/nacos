/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.client.naming.net;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.SystemPropertyKeyConst;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.CommonParams;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.alibaba.nacos.api.selector.AbstractSelector;
import com.alibaba.nacos.api.selector.ExpressionSelector;
import com.alibaba.nacos.api.selector.SelectorType;
import com.alibaba.nacos.client.config.impl.SpasAdapter;
import com.alibaba.nacos.client.monitor.MetricsMonitor;
import com.alibaba.nacos.client.naming.beat.BeatInfo;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.alibaba.nacos.client.naming.utils.NetUtils;
import com.alibaba.nacos.client.naming.utils.SignUtil;
import com.alibaba.nacos.client.naming.utils.UtilAndComs;
import com.alibaba.nacos.client.security.SecurityProxy;
import com.alibaba.nacos.client.utils.AppNameUtils;
import com.alibaba.nacos.client.utils.TemplateUtils;
import com.alibaba.nacos.common.constant.HttpHeaderConsts;
import com.alibaba.nacos.common.http.HttpRestResult;
import com.alibaba.nacos.common.http.client.NacosRestTemplate;
import com.alibaba.nacos.common.http.param.Header;
import com.alibaba.nacos.common.http.param.Query;
import com.alibaba.nacos.common.lifecycle.Closeable;
import com.alibaba.nacos.common.utils.HttpMethod;
import com.alibaba.nacos.common.utils.IoUtils;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.common.utils.ThreadUtils;
import com.alibaba.nacos.common.utils.UuidUtils;
import com.alibaba.nacos.common.utils.VersionUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.*;

import static com.alibaba.nacos.client.utils.LogUtils.NAMING_LOGGER;

/**
 * 说明：
 *      1、此类封装了 Client端的 对服务的 增删改查功能
 *      2、还提供的登录安全验证功能
 *
 *
 * Naming proxy.
 *
 * @author nkorange
 */
public class NamingProxy implements Closeable {
    /**
     * 说明：
     *  1、单利manager，实现方式：私有内部内，装有静态方法
     *  2、然后获取 HttpTemplate，（内部是有缓存Template的。只是在第一次使用时创建）
     *
     */
    private final NacosRestTemplate nacosRestTemplate = NamingHttpClientManager.getInstance().getNacosRestTemplate();

    private static final int DEFAULT_SERVER_PORT = 8848;

    private int serverPort = DEFAULT_SERVER_PORT;

    private final String namespaceId;

    /**
     * nacos 集群中其中一个 实例 IP:Port
     */
    private final String endpoint;

    private String nacosDomain;

    /**
     *　配置文件配置的
     *  nacos:
     *      discovery:
     *         server-addr: ip:port
     */
    private List<String> serverList;

    private List<String> serversFromEndpoint = new ArrayList<String>();

    private final SecurityProxy securityProxy;

    private long lastSrvRefTime = 0L;

    private final long vipSrvRefInterMillis = TimeUnit.SECONDS.toMillis(30);

    private final long securityInfoRefreshIntervalMills = TimeUnit.SECONDS.toMillis(5);

    private Properties properties;

    private ScheduledExecutorService executorService;

    public NamingProxy(String namespaceId, String endpoint, String serverList, Properties properties) {
        // 登陆、校验过期时间等
        this.securityProxy = new SecurityProxy(properties, nacosRestTemplate);
        this.properties = properties;
        this.setServerPort(DEFAULT_SERVER_PORT);
        this.namespaceId = namespaceId;
        this.endpoint = endpoint;
        if (StringUtils.isNotEmpty(serverList)) {
            this.serverList = Arrays.asList(serverList.split(","));
            if (this.serverList.size() == 1) {
                this.nacosDomain = serverList;
            }
        }
        // 关键逻辑： 定时刷新【服务端实例IP列表】和【登陆信息】
        this.initRefreshTask();
    }

    /**
     *
     * 1、定时刷新 Nacos Server 集群的实例列表
     * 2、定时刷新登陆信息
     *
     */
    private void initRefreshTask() {

        this.executorService = new ScheduledThreadPoolExecutor(2, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("com.alibaba.nacos.client.naming.updater");
                t.setDaemon(true);
                return t;
            }
        });

        // 定时获取最新的 Nacos 集群Server 节点的地址信息
        this.executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                refreshSrvIfNeed();
            }
        }, 0, vipSrvRefInterMillis, TimeUnit.MILLISECONDS);

        // 定时登陆，刷新最后登陆时间
        this.executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                securityProxy.login(getServerList());
            }
        }, 0, securityInfoRefreshIntervalMills, TimeUnit.MILLISECONDS);

        // todo 疑问：都开启了定时调度任务，为啥这里还要调用一次呢
        refreshSrvIfNeed();
        this.securityProxy.login(getServerList());
    }

    /**
     * 从 Nacos 集群中的一个终端，获取整个 Nacos集群可用实例IP:Port列表
     *
     */
    public List<String> getServerListFromEndpoint() {

        try {
            String urlString = "http://" + endpoint + "/nacos/serverlist";
            Header header = builderHeader();
            HttpRestResult<String> restResult = nacosRestTemplate.get(urlString, header, Query.EMPTY, String.class);
            if (!restResult.ok()) {
                throw new IOException(
                    "Error while requesting: " + urlString + "'. Server returned: " + restResult.getCode());
            }

            String content = restResult.getData();
            List<String> list = new ArrayList<String>();
            for (String line : IoUtils.readLines(new StringReader(content))) {
                if (!line.trim().isEmpty()) {
                    list.add(line.trim());
                }
            }

            return list;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 关键逻辑：
     *      获取注册中心IP列表（就是获取 nacos集群实例IP列表）
     *  1、覆盖本地的注册中心IP列表
     *  2、刷新最后更新时间
     *
     */
    private void refreshSrvIfNeed() {
        try {

            if (!CollectionUtils.isEmpty(serverList)) {
                NAMING_LOGGER.debug("server list provided by user: " + serverList);
                return;
            }

            if (System.currentTimeMillis() - lastSrvRefTime < vipSrvRefInterMillis) {
                return;
            }

            List<String> list = getServerListFromEndpoint();

            if (CollectionUtils.isEmpty(list)) {
                throw new Exception("Can not acquire Nacos list");
            }

            if (!CollectionUtils.isEqualCollection(list, serversFromEndpoint)) {
                NAMING_LOGGER.info("[SERVER-LIST] server list is updated: " + list);
            }

            serversFromEndpoint = list;
            lastSrvRefTime = System.currentTimeMillis();
        } catch (Throwable e) {
            NAMING_LOGGER.warn("failed to update server list", e);
        }
    }

    /**
     *
     * 注册一个服务实例 到 Nacos集群上
     *
     *
     * register a instance to service with specified instance properties.
     *
     * @param serviceName name of service
     * @param groupName   group of service
     * @param instance    instance to register
     * @throws NacosException nacos exception
     */
    public void registerService(String serviceName, String groupName, Instance instance) throws NacosException {

        NAMING_LOGGER.info("[REGISTER-SERVICE] {} registering service {} with instance: {}", namespaceId, serviceName,
            instance);

        final Map<String, String> params = new HashMap<String, String>(16);
        params.put(CommonParams.NAMESPACE_ID, namespaceId);
        params.put(CommonParams.SERVICE_NAME, serviceName);
        params.put(CommonParams.GROUP_NAME, groupName);
        params.put(CommonParams.CLUSTER_NAME, instance.getClusterName());
        params.put("ip", instance.getIp());
        params.put("port", String.valueOf(instance.getPort()));
        params.put("weight", String.valueOf(instance.getWeight()));
        params.put("enable", String.valueOf(instance.isEnabled()));
        params.put("healthy", String.valueOf(instance.isHealthy()));
        params.put("ephemeral", String.valueOf(instance.isEphemeral()));
        params.put("metadata", JacksonUtils.toJson(instance.getMetadata()));

        // 关键逻辑：将服务实例注册到 Nacos集群的其中一个实例上面（ 感觉很奇怪）
        reqApi(UtilAndComs.nacosUrlInstance, params, HttpMethod.POST);

    }

    /**
     * 注销一个服务
     *
     *
     * deregister instance from a service.
     *
     * @param serviceName name of service
     * @param instance    instance
     * @throws NacosException nacos exception
     */
    public void deregisterService(String serviceName, Instance instance) throws NacosException {

        NAMING_LOGGER
            .info("[DEREGISTER-SERVICE] {} deregistering service {} with instance: {}", namespaceId, serviceName,
                instance);

        final Map<String, String> params = new HashMap<String, String>(8);
        params.put(CommonParams.NAMESPACE_ID, namespaceId);
        params.put(CommonParams.SERVICE_NAME, serviceName);
        params.put(CommonParams.CLUSTER_NAME, instance.getClusterName());
        params.put("ip", instance.getIp());
        params.put("port", String.valueOf(instance.getPort()));
        params.put("ephemeral", String.valueOf(instance.isEphemeral()));

        reqApi(UtilAndComs.nacosUrlInstance, params, HttpMethod.DELETE);
    }

    /**
     *
     * 更新一个服务
     *
     * Update instance to service.
     *
     * @param serviceName service name
     * @param groupName   group name
     * @param instance    instance
     * @throws NacosException nacos exception
     */
    public void updateInstance(String serviceName, String groupName, Instance instance) throws NacosException {
        NAMING_LOGGER
            .info("[UPDATE-SERVICE] {} update service {} with instance: {}", namespaceId, serviceName, instance);

        final Map<String, String> params = new HashMap<String, String>(8);
        params.put(CommonParams.NAMESPACE_ID, namespaceId);
        params.put(CommonParams.SERVICE_NAME, serviceName);
        params.put(CommonParams.GROUP_NAME, groupName);
        params.put(CommonParams.CLUSTER_NAME, instance.getClusterName());
        params.put("ip", instance.getIp());
        params.put("port", String.valueOf(instance.getPort()));
        params.put("weight", String.valueOf(instance.getWeight()));
        params.put("enabled", String.valueOf(instance.isEnabled()));
        params.put("ephemeral", String.valueOf(instance.isEphemeral()));
        params.put("metadata", JacksonUtils.toJson(instance.getMetadata()));

        reqApi(UtilAndComs.nacosUrlInstance, params, HttpMethod.PUT);
    }

    /**
     * 查询服务信息
     *
     * Query Service.
     *
     * @param serviceName service name
     * @param groupName   group name
     * @return service
     * @throws NacosException nacos exception
     */
    public Service queryService(String serviceName, String groupName) throws NacosException {
        NAMING_LOGGER.info("[QUERY-SERVICE] {} query service : {}, {}", namespaceId, serviceName, groupName);

        final Map<String, String> params = new HashMap<String, String>(3);
        params.put(CommonParams.NAMESPACE_ID, namespaceId);
        params.put(CommonParams.SERVICE_NAME, serviceName);
        params.put(CommonParams.GROUP_NAME, groupName);

        String result = reqApi(UtilAndComs.nacosUrlService, params, HttpMethod.GET);
        return JacksonUtils.toObj(result, Service.class);
    }

    /**
     * Create service.
     *
     * @param service  service
     * @param selector selector
     * @throws NacosException nacos exception
     */
    public void createService(Service service, AbstractSelector selector) throws NacosException {

        NAMING_LOGGER.info("[CREATE-SERVICE] {} creating service : {}", namespaceId, service);

        final Map<String, String> params = new HashMap<String, String>(6);
        params.put(CommonParams.NAMESPACE_ID, namespaceId);
        params.put(CommonParams.SERVICE_NAME, service.getName());
        params.put(CommonParams.GROUP_NAME, service.getGroupName());
        params.put("protectThreshold", String.valueOf(service.getProtectThreshold()));
        params.put("metadata", JacksonUtils.toJson(service.getMetadata()));
        params.put("selector", JacksonUtils.toJson(selector));

        reqApi(UtilAndComs.nacosUrlService, params, HttpMethod.POST);

    }

    /**
     * Delete service.
     *
     * @param serviceName service name
     * @param groupName   group name
     * @return true if delete ok
     * @throws NacosException nacos exception
     */
    public boolean deleteService(String serviceName, String groupName) throws NacosException {
        NAMING_LOGGER.info("[DELETE-SERVICE] {} deleting service : {} with groupName : {}", namespaceId, serviceName,
            groupName);

        final Map<String, String> params = new HashMap<String, String>(6);
        params.put(CommonParams.NAMESPACE_ID, namespaceId);
        params.put(CommonParams.SERVICE_NAME, serviceName);
        params.put(CommonParams.GROUP_NAME, groupName);

        String result = reqApi(UtilAndComs.nacosUrlService, params, HttpMethod.DELETE);
        return "ok".equals(result);
    }

    /**
     * Update service.
     *
     * @param service  service
     * @param selector selector
     * @throws NacosException nacos exception
     */
    public void updateService(Service service, AbstractSelector selector) throws NacosException {
        NAMING_LOGGER.info("[UPDATE-SERVICE] {} updating service : {}", namespaceId, service);

        final Map<String, String> params = new HashMap<String, String>(6);
        params.put(CommonParams.NAMESPACE_ID, namespaceId);
        params.put(CommonParams.SERVICE_NAME, service.getName());
        params.put(CommonParams.GROUP_NAME, service.getGroupName());
        params.put("protectThreshold", String.valueOf(service.getProtectThreshold()));
        params.put("metadata", JacksonUtils.toJson(service.getMetadata()));
        params.put("selector", JacksonUtils.toJson(selector));

        reqApi(UtilAndComs.nacosUrlService, params, HttpMethod.PUT);
    }

    /**
     * Query instance list.
     *
     * @param serviceName service name
     * @param clusters    clusters
     * @param udpPort     udp port
     * @param healthyOnly healthy only
     * @return instance list
     * @throws NacosException nacos exception
     */
    public String queryList(String serviceName, String clusters, int udpPort, boolean healthyOnly)
        throws NacosException {

        final Map<String, String> params = new HashMap<String, String>(8);
        params.put(CommonParams.NAMESPACE_ID, namespaceId);
        params.put(CommonParams.SERVICE_NAME, serviceName);
        params.put("clusters", clusters);
        params.put("udpPort", String.valueOf(udpPort));
        params.put("clientIP", NetUtils.localIP());
        params.put("healthyOnly", String.valueOf(healthyOnly));

        return reqApi(UtilAndComs.nacosUrlBase + "/instance/list", params, HttpMethod.GET);
    }

    /**
     *
     * 关键逻辑：
     *      发送心跳检测
     *
     *
     * Send beat.
     *
     * @param beatInfo         beat info
     * @param lightBeatEnabled light beat
     * @return beat result
     * @throws NacosException nacos exception
     */
    public JsonNode sendBeat(BeatInfo beatInfo, boolean lightBeatEnabled) throws NacosException {

        if (NAMING_LOGGER.isDebugEnabled()) {
            NAMING_LOGGER.debug("[BEAT] {} sending beat to server: {}", namespaceId, beatInfo.toString());
        }
        Map<String, String> params = new HashMap<String, String>(8);
        Map<String, String> bodyMap = new HashMap<String, String>(2);
        if (!lightBeatEnabled) {
            bodyMap.put("beat", JacksonUtils.toJson(beatInfo));
        }
        params.put(CommonParams.NAMESPACE_ID, namespaceId);
        params.put(CommonParams.SERVICE_NAME, beatInfo.getServiceName());
        params.put(CommonParams.CLUSTER_NAME, beatInfo.getCluster());
        params.put("ip", beatInfo.getIp());
        params.put("port", String.valueOf(beatInfo.getPort()));
        String result = reqApi(UtilAndComs.nacosUrlBase + "/instance/beat", params, bodyMap, HttpMethod.PUT);
        return JacksonUtils.toObj(result);
    }

    /**
     * 检查服务是否健康
     *
     * Check Server healthy.
     *
     * @return true if server is healthy
     */
    public boolean serverHealthy() {

        try {
            String result = reqApi(UtilAndComs.nacosUrlBase + "/operator/metrics", new HashMap<String, String>(2),
                HttpMethod.GET);
            JsonNode json = JacksonUtils.toObj(result);
            String serverStatus = json.get("status").asText();
            return "UP".equals(serverStatus);
        } catch (Exception e) {
            return false;
        }
    }

    public ListView<String> getServiceList(int pageNo, int pageSize, String groupName) throws NacosException {
        return getServiceList(pageNo, pageSize, groupName, null);
    }

    public ListView<String> getServiceList(int pageNo, int pageSize, String groupName, AbstractSelector selector)
        throws NacosException {

        Map<String, String> params = new HashMap<String, String>(4);
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));
        params.put(CommonParams.NAMESPACE_ID, namespaceId);
        params.put(CommonParams.GROUP_NAME, groupName);

        if (selector != null) {
            switch (SelectorType.valueOf(selector.getType())) {
                case none:
                    break;
                case label:
                    ExpressionSelector expressionSelector = (ExpressionSelector) selector;
                    params.put("selector", JacksonUtils.toJson(expressionSelector));
                    break;
                default:
                    break;
            }
        }

        String result = reqApi(UtilAndComs.nacosUrlBase + "/service/list", params, HttpMethod.GET);

        JsonNode json = JacksonUtils.toObj(result);
        ListView<String> listView = new ListView<String>();
        listView.setCount(json.get("count").asInt());
        listView.setData(JacksonUtils.toObj(json.get("doms").toString(), new TypeReference<List<String>>() {
        }));

        return listView;
    }

    public String reqApi(String api, Map<String, String> params, String method) throws NacosException {
        return reqApi(api, params, Collections.EMPTY_MAP, method);
    }

    /**
     * 每一个Api 都作用在 Nacos集群的每一台机器上
     *
     */
    public String reqApi(String api, Map<String, String> params, Map<String, String> body, String method)
        throws NacosException {
        //　将请求发送给每一个 Nacos Server 实例
        return reqApi(api, params, body, getServerList(), method);
    }

    /**
     *
     * 关键逻辑：
     *      Nacos Client  ---->  Nacos Server
     *  如果 Nacos server 集群版，则随机挑选其中一个 Server进行请求，如果失败，则延用上次调用的顺序，往下取一个server进行调用，成功一个Server就算成功，全失败抛出异常
     *  如果 Nacos server 单机版，则调用这一个Server节点，如果失败，则重试三次
     *
     *
     * Request api.
     *
     * @param api     api
     * @param params  parameters
     * @param body    body
     * @param servers servers
     * @param method  http method
     * @return result
     * @throws NacosException nacos exception
     */
    public String reqApi(String api, Map<String, String> params, Map<String, String> body, List<String> servers,
                         String method) throws NacosException {

        params.put(CommonParams.NAMESPACE_ID, getNamespaceId());
        // 校验是否 Nacos server 实例 是否存在
        if (CollectionUtils.isEmpty(servers) && StringUtils.isEmpty(nacosDomain)) {
            throw new NacosException(NacosException.INVALID_PARAM, "no server available");
        }

        NacosException exception = new NacosException();

        // 集群版
        if (servers != null && !servers.isEmpty()) {

            Random random = new Random(System.currentTimeMillis());
            int index = random.nextInt(servers.size());
            // 随机挑选一台节点进行调用,如果失败了,则顺序往后面的节点进行重试
            for (int i = 0; i < servers.size(); i++) {
                String server = servers.get(index);
                try {
                    return callServer(api, params, body, server, method);
                } catch (NacosException e) {
                    exception = e;
                    if (NAMING_LOGGER.isDebugEnabled()) {
                        NAMING_LOGGER.debug("request {} failed.", server, e);
                    }
                }
                index = (index + 1) % servers.size();
            }
        }

        // 单机版
        if (StringUtils.isNotBlank(nacosDomain)) {
            // 单机版  失败了,默认情况重试3次
            for (int i = 0; i < UtilAndComs.REQUEST_DOMAIN_RETRY_COUNT; i++) {
                try {
                    return callServer(api, params, body, nacosDomain, method);
                } catch (NacosException e) {
                    exception = e;
                    if (NAMING_LOGGER.isDebugEnabled()) {
                        NAMING_LOGGER.debug("request {} failed.", nacosDomain, e);
                    }
                }
            }
        }

        NAMING_LOGGER.error("request: {} failed, servers: {}, code: {}, msg: {}", api, servers, exception.getErrCode(),
            exception.getErrMsg());

        throw new NacosException(exception.getErrCode(),
            "failed to req API:" + api + " after all servers(" + servers + ") tried: " + exception.getMessage());

    }

    /**
     * 获取 Nacos Server 实例列表
     * 关键逻辑：
     *  优先取 endPoint  --------->    endPoint 是 Nacos集群中的一个终端，通过一个终端，获取整个Nacos 集群的实例列表
     *  其次取 server-addr
     *
     */
    private List<String> getServerList() {
        List<String> snapshot = serversFromEndpoint;
        if (!CollectionUtils.isEmpty(serverList)) {
            snapshot = serverList;
        }
        return snapshot;
    }

    public String callServer(String api, Map<String, String> params, Map<String, String> body, String curServer)
        throws NacosException {
        return callServer(api, params, body, curServer, HttpMethod.GET);
    }

    /**
     *
     * 说明：
     *  设置安全信息
     *  发起请求
     *  监控信息
     *
     *
     * Call server.
     *
     * @param api       api
     * @param params    parameters
     * @param body      body
     * @param curServer ?
     * @param method    http method
     * @return result
     * @throws NacosException nacos exception
     */
    public String callServer(String api, Map<String, String> params, Map<String, String> body, String curServer,
                             String method) throws NacosException {
        long start = System.currentTimeMillis();
        long end = 0;
        injectSecurityInfo(params);
        Header header = builderHeader();

        String url;
        if (curServer.startsWith(UtilAndComs.HTTPS) || curServer.startsWith(UtilAndComs.HTTP)) {
            url = curServer + api;
        } else {
            if (!curServer.contains(UtilAndComs.SERVER_ADDR_IP_SPLITER)) {
                curServer = curServer + UtilAndComs.SERVER_ADDR_IP_SPLITER + serverPort;
            }
            url = NamingHttpClientManager.getInstance().getPrefix() + curServer + api;
        }

        try {
            HttpRestResult<String> restResult = nacosRestTemplate
                .exchangeForm(url, header, Query.newInstance().initParams(params), body, method, String.class);
            end = System.currentTimeMillis();
            // todo 监控信息，还没有了解过这个
            MetricsMonitor.getNamingRequestMonitor(method, url, String.valueOf(restResult.getCode()))
                .observe(end - start);

            if (restResult.ok()) {
                return restResult.getData();
            }
            if (HttpStatus.SC_NOT_MODIFIED == restResult.getCode()) {
                return StringUtils.EMPTY;
            }
            throw new NacosException(restResult.getCode(), restResult.getMessage());
        } catch (Exception e) {
            NAMING_LOGGER.error("[NA] failed to request", e);
            throw new NacosException(NacosException.SERVER_ERROR, e);
        }
    }

    /**
     *
     * 为请求设置安全校验信息
     *
     */
    private void injectSecurityInfo(Map<String, String> params) {

        // Inject token if exist:
        if (StringUtils.isNotBlank(securityProxy.getAccessToken())) {
            params.put(Constants.ACCESS_TOKEN, securityProxy.getAccessToken());
        }

        // Inject ak/sk if exist:
        String ak = getAccessKey();
        String sk = getSecretKey();
        params.put("app", AppNameUtils.getAppName());
        if (StringUtils.isNotBlank(ak) && StringUtils.isNotBlank(sk)) {
            try {
                String signData = getSignData(params.get("serviceName"));
                String signature = SignUtil.sign(signData, sk);
                params.put("signature", signature);
                params.put("data", signData);
                params.put("ak", ak);
            } catch (Exception e) {
                NAMING_LOGGER.error("inject ak/sk failed.", e);
            }
        }
    }

    /**
     * Build header.
     *
     * @return header
     */
    public Header builderHeader() {
        Header header = Header.newInstance();
        header.addParam(HttpHeaderConsts.CLIENT_VERSION_HEADER, VersionUtils.version);
        header.addParam(HttpHeaderConsts.USER_AGENT_HEADER, UtilAndComs.VERSION);
        header.addParam(HttpHeaderConsts.ACCEPT_ENCODING, "gzip,deflate,sdch");
        header.addParam(HttpHeaderConsts.CONNECTION, "Keep-Alive");
        header.addParam(HttpHeaderConsts.REQUEST_ID, UuidUtils.generateUuid());
        header.addParam(HttpHeaderConsts.REQUEST_MODULE, "Naming");
        return header;
    }

    private static String getSignData(String serviceName) {
        return StringUtils.isNotEmpty(serviceName) ? System.currentTimeMillis() + "@@" + serviceName
            : String.valueOf(System.currentTimeMillis());
    }

    public String getAccessKey() {
        if (properties == null) {

            return SpasAdapter.getAk();
        }

        return TemplateUtils
            .stringEmptyAndThenExecute(properties.getProperty(PropertyKeyConst.ACCESS_KEY), new Callable<String>() {

                @Override
                public String call() {
                    return SpasAdapter.getAk();
                }
            });
    }

    public String getSecretKey() {
        if (properties == null) {

            return SpasAdapter.getSk();
        }

        return TemplateUtils
            .stringEmptyAndThenExecute(properties.getProperty(PropertyKeyConst.SECRET_KEY), new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return SpasAdapter.getSk();
                }
            });
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
        setServerPort(DEFAULT_SERVER_PORT);
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;

        String sp = System.getProperty(SystemPropertyKeyConst.NAMING_SERVER_PORT);
        if (StringUtils.isNotBlank(sp)) {
            this.serverPort = Integer.parseInt(sp);
        }
    }

    @Override
    public void shutdown() throws NacosException {
        String className = this.getClass().getName();
        NAMING_LOGGER.info("{} do shutdown begin", className);
        ThreadUtils.shutdownThreadPool(executorService, NAMING_LOGGER);
        NamingHttpClientManager.getInstance().shutdown();
        NAMING_LOGGER.info("{} do shutdown stop", className);
    }
}

