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

package com.alibaba.nacos.client.naming.backups;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.client.naming.cache.ConcurrentDiskUtil;
import com.alibaba.nacos.client.naming.cache.DiskCache;
import com.alibaba.nacos.client.naming.core.HostReactor;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.alibaba.nacos.client.naming.utils.UtilAndComs;
import com.alibaba.nacos.common.lifecycle.Closeable;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.common.utils.ThreadUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.alibaba.nacos.client.utils.LogUtils.NAMING_LOGGER;

/**
 * Failover reactor.
 *
 * @author nkorange
 */
public class FailoverReactor implements Closeable {

    /**
     * 就是服务信息的备份路径
     */
    private final String failoverDir;

    private final HostReactor hostReactor;

    private final ScheduledExecutorService executorService;

    public FailoverReactor(HostReactor hostReactor, String cacheDir) {
        this.hostReactor = hostReactor;
        this.failoverDir = cacheDir + "/failover";
        // init executorService
        this.executorService = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("com.alibaba.nacos.naming.failover");
                return thread;
            }
        });
        this.init();
    }

    /**
     * 发生故障时，从本笃读取的备份文件（ServiceInfo信息）
     */
    private Map<String, ServiceInfo> serviceMap = new ConcurrentHashMap<String, ServiceInfo>();

    private final Map<String, String> switchParams = new ConcurrentHashMap<String, String>();

    private static final long DAY_PERIOD_MINUTES = 24 * 60;

    /**
     * Init.
     */
    public void init() {
        // 关键逻辑：检查是否存在故障，如果发生故障，添加标记 switchParams，然后读取备份的信息，到 serviceMap
        executorService.scheduleWithFixedDelay(new SwitchRefresher(), 0L, 5000L, TimeUnit.MILLISECONDS);

        // 每天执行一次，　将内存（Map）缓存的信息,刷新到磁盘 ----> 就是备份的过程
        executorService.scheduleWithFixedDelay(new DiskFileWriter(), 30, DAY_PERIOD_MINUTES, TimeUnit.MINUTES);

        // 启动后延迟10秒执行，如果没有没有故障目录，则创建，进行初始化
        // backup file on startup if failover directory is empty.
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    File cacheDir = new File(failoverDir);

                    if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                        throw new IllegalStateException("failed to create cache dir: " + failoverDir);
                    }

                    File[] files = cacheDir.listFiles();
                    if (files == null || files.length <= 0) {
                        new DiskFileWriter().run();
                    }
                } catch (Throwable e) {
                    NAMING_LOGGER.error("[NA] failed to backup file on startup.", e);
                }

            }
        }, 10000L, TimeUnit.MILLISECONDS);
    }

    /**
     * Add day.
     *
     * @param date start time
     * @param num  add day number
     * @return new date
     */
    public Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }

    @Override
    public void shutdown() throws NacosException {
        String className = this.getClass().getName();
        NAMING_LOGGER.info("{} do shutdown begin", className);
        ThreadUtils.shutdownThreadPool(executorService, NAMING_LOGGER);
        NAMING_LOGGER.info("{} do shutdown stop", className);
    }

    /**
     * 故障检测，每５秒检测一次
     *
     * 1、读指定目录下的文件
     * 2、解析 failover-mode 值
     * 3、将 结果存起来 switchParams
     * 4、如果是失败转移模式，则将之前缓存的信息读取解析出来，放到 serviceMap中
     */
    class SwitchRefresher implements Runnable {

        long lastModifiedMillis = 0L;

        @Override
        public void run() {
            try {
                File switchFile = new File(failoverDir + UtilAndComs.FAILOVER_SWITCH);
                if (!switchFile.exists()) {
                    switchParams.put("failover-mode", "false");
                    NAMING_LOGGER.debug("failover switch is not found, " + switchFile.getName());
                    return;
                }

                long modified = switchFile.lastModified();

                if (lastModifiedMillis < modified) {
                    lastModifiedMillis = modified;
                    // 关键逻辑；读取失败路径下的信息，然后解析是否故障转移模式
                    String failover = ConcurrentDiskUtil.getFileContent(failoverDir + UtilAndComs.FAILOVER_SWITCH, Charset.defaultCharset().toString());
                    if (!StringUtils.isEmpty(failover)) {
                        String[] lines = failover.split(DiskCache.getLineSeparator());

                        for (String line : lines) {
                            String line1 = line.trim();
                            if ("1".equals(line1)) {
                                switchParams.put("failover-mode", "true");
                                NAMING_LOGGER.info("failover-mode is on");
                                // 关键逻辑：如果是失败转移模式，就将之前备份的信息读取出来，放到 serviceMap中
                                new FailoverFileReader().run();
                            } else if ("0".equals(line1)) {
                                switchParams.put("failover-mode", "false");
                                NAMING_LOGGER.info("failover-mode is off");
                            }
                        }
                    } else {
                        switchParams.put("failover-mode", "false");
                    }
                }

            } catch (Throwable e) {
                NAMING_LOGGER.error("[NA] failed to read failover switch.", e);
            }
        }
    }

    /**
     * 如果是故障转移模式
     *
     *  就将本地文件中的服务信息 信息读取并解析出来放到 【serviceMap】 中
     *
     */
    class FailoverFileReader implements Runnable {

        @Override
        public void run() {
            Map<String, ServiceInfo> domMap = new HashMap<String, ServiceInfo>(16);

            BufferedReader reader = null;
            try {

                File cacheDir = new File(failoverDir);
                if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                    throw new IllegalStateException("failed to create cache dir: " + failoverDir);
                }

                File[] files = cacheDir.listFiles();
                if (files == null) {
                    return;
                }

                for (File file : files) {
                    if (!file.isFile()) {
                        continue;
                    }

                    if (file.getName().equals(UtilAndComs.FAILOVER_SWITCH)) {
                        continue;
                    }

                    ServiceInfo dom = new ServiceInfo(file.getName());

                    try {
                        String dataString = ConcurrentDiskUtil
                            .getFileContent(file, Charset.defaultCharset().toString());
                        reader = new BufferedReader(new StringReader(dataString));

                        String json;
                        if ((json = reader.readLine()) != null) {
                            try {
                                dom = JacksonUtils.toObj(json, ServiceInfo.class);
                            } catch (Exception e) {
                                NAMING_LOGGER.error("[NA] error while parsing cached dom : " + json, e);
                            }
                        }

                    } catch (Exception e) {
                        NAMING_LOGGER.error("[NA] failed to read cache for dom: " + file.getName(), e);
                    } finally {
                        try {
                            if (reader != null) {
                                reader.close();
                            }
                        } catch (Exception e) {
                            //ignore
                        }
                    }
                    if (!CollectionUtils.isEmpty(dom.getHosts())) {
                        domMap.put(dom.getKey(), dom);
                    }
                }
            } catch (Exception e) {
                NAMING_LOGGER.error("[NA] failed to read cache file", e);
            }

            if (domMap.size() > 0) {
                serviceMap = domMap;
            }
        }
    }

    /**
     * 每24小时执行一次
     *
     * 将内存缓存(Map)的服务信息，备份到本地磁盘
     *
     */
    class DiskFileWriter extends TimerTask {

        @Override
        public void run() {
            Map<String, ServiceInfo> map = hostReactor.getServiceInfoMap();
            for (Map.Entry<String, ServiceInfo> entry : map.entrySet()) {
                ServiceInfo serviceInfo = entry.getValue();
                if (StringUtils.equals(serviceInfo.getKey(), UtilAndComs.ALL_IPS)
                    || StringUtils.equals(serviceInfo.getName(), UtilAndComs.ENV_LIST_KEY)
                    || StringUtils.equals(serviceInfo.getName(), "00-00---000-ENV_CONFIGS-000---00-00")
                    || StringUtils.equals(serviceInfo.getName(), "vipclient.properties")
                    || StringUtils.equals(serviceInfo.getName(), "00-00---000-ALL_HOSTS-000---00-00")) {
                    continue;
                }

                DiskCache.write(serviceInfo, failoverDir);
            }
        }
    }

    public boolean isFailoverSwitch() {
        return Boolean.parseBoolean(switchParams.get("failover-mode"));
    }

    public ServiceInfo getService(String key) {
        ServiceInfo serviceInfo = serviceMap.get(key);

        if (serviceInfo == null) {
            serviceInfo = new ServiceInfo();
            serviceInfo.setName(key);
        }

        return serviceInfo;
    }
}
