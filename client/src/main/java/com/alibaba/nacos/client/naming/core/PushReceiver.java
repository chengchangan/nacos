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

package com.alibaba.nacos.client.naming.core;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.lifecycle.Closeable;
import com.alibaba.nacos.common.utils.IoUtils;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.common.utils.ThreadUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.Charset;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import static com.alibaba.nacos.client.utils.LogUtils.NAMING_LOGGER;

/**
 * 关键逻辑：
 *    说明：
 *      接收远程推送过来的 ServiceInfo
 *      保存 ServiceInfo信息
 *      然后返回对方信息　ack
 *
 *
 * Push receiver.
 *
 * @author xuanyin
 */
public class PushReceiver implements Runnable, Closeable {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final int UDP_MSS = 64 * 1024;

    private ScheduledExecutorService executorService;

    private DatagramSocket udpSocket;

    private HostReactor hostReactor;

    private volatile boolean closed = false;

    public PushReceiver(HostReactor hostReactor) {
        try {
            this.hostReactor = hostReactor;
            this.udpSocket = new DatagramSocket();
            this.executorService = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    thread.setName("com.alibaba.nacos.naming.push.receiver");
                    return thread;
                }
            });

            this.executorService.execute(this);
        } catch (Exception e) {
            NAMING_LOGGER.error("[NA] init udp socket failed", e);
        }
    }

    /**
     * TODO：这个　DatagramSocket　不太清楚，应该是监听读取信息
     *
     * 1、通过　DatagramSocket　接收到的信息正确，则读取信息解析然后放到，serviceInfoMap中(这个里面存放了，服务对应的服务信息)
     * 2、给对方返回 ack确认信息
     *
     */
    @Override
    public void run() {
        while (!closed) {
            try {

                // byte[] is initialized with 0 full filled by default
                byte[] buffer = new byte[UDP_MSS];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // 关键逻辑：接收数据包，下面会进行解析内容
                udpSocket.receive(packet);

                String json = new String(IoUtils.tryDecompress(packet.getData()), UTF_8).trim();
                NAMING_LOGGER.info("received push data: " + json + " from " + packet.getAddress().toString());

                PushPacket pushPacket = JacksonUtils.toObj(json, PushPacket.class);
                String ack;
                // 关键逻辑：判断收到的数据是否有效，如果正确则存起来，然后返回信息给发送者
                if ("dom".equals(pushPacket.type) || "service".equals(pushPacket.type)) {
                    // 关键逻辑：解析 并 保存 ServiceInfo
                    hostReactor.processServiceJson(pushPacket.data);

                    // send ack to server
                    ack = "{\"type\": \"push-ack\"" + ", \"lastRefTime\":\"" + pushPacket.lastRefTime + "\", \"data\":"
                        + "\"\"}";
                } else if ("dump".equals(pushPacket.type)) {
                    // dump data to server
                    ack = "{\"type\": \"dump-ack\"" + ", \"lastRefTime\": \"" + pushPacket.lastRefTime + "\", \"data\":"
                        + "\"" + StringUtils.escapeJavaScript(JacksonUtils.toJson(hostReactor.getServiceInfoMap()))
                        + "\"}";
                } else {
                    // do nothing send ack only
                    ack = "{\"type\": \"unknown-ack\"" + ", \"lastRefTime\":\"" + pushPacket.lastRefTime
                        + "\", \"data\":" + "\"\"}";
                }
                // 关键逻辑：返回信息给发送者（Server端）
                udpSocket.send(new DatagramPacket(ack.getBytes(UTF_8), ack.getBytes(UTF_8).length,
                    packet.getSocketAddress()));
            } catch (Exception e) {
                NAMING_LOGGER.error("[NA] error while receiving push data", e);
            }
        }
    }

    @Override
    public void shutdown() throws NacosException {
        String className = this.getClass().getName();
        NAMING_LOGGER.info("{} do shutdown begin", className);
        ThreadUtils.shutdownThreadPool(executorService, NAMING_LOGGER);
        closed = true;
        udpSocket.close();
        NAMING_LOGGER.info("{} do shutdown stop", className);
    }

    public static class PushPacket {

        public String type;

        public long lastRefTime;

        public String data;
    }

    public int getUdpPort() {
        return this.udpSocket.getLocalPort();
    }
}
