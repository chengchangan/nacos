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

package com.alibaba.nacos.sys.utils;

import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.notify.SlowEvent;
import com.alibaba.nacos.sys.env.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alibaba.nacos.sys.env.Constants.IP_ADDRESS;
import static com.alibaba.nacos.sys.env.Constants.NACOS_SERVER_IP;
import static com.alibaba.nacos.sys.env.Constants.PREFER_HOSTNAME_OVER_IP;
import static com.alibaba.nacos.sys.env.Constants.SYSTEM_PREFER_HOSTNAME_OVER_IP;
import static com.alibaba.nacos.sys.env.Constants.USE_ONLY_SITE_INTERFACES;

/**
 * Network card operation tool class.
 *
 * @author Nacos
 */
public class InetUtils {

    private static final Logger LOG = LoggerFactory.getLogger(InetUtils.class);

    private static final String NUM = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";

    private static final String IP_REGEX = "^" + NUM + "\\." + NUM + "\\." + NUM + "\\." + NUM + "$";

    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEX);

    private static String selfIp;

    private static boolean useOnlySiteLocalInterface = false;

    private static boolean preferHostnameOverIp = false;

    private static final List<String> PREFERRED_NETWORKS = new ArrayList<String>();

    private static final List<String> IGNORED_INTERFACES = new ArrayList<String>();

    static {
        NotifyCenter.registerToSharePublisher(IPChangeEvent.class);

        useOnlySiteLocalInterface = Boolean.parseBoolean(ApplicationUtils.getProperty(USE_ONLY_SITE_INTERFACES));

        List<String> networks = ApplicationUtils.getPropertyList(Constants.PREFERRED_NETWORKS);
        PREFERRED_NETWORKS.addAll(networks);

        List<String> interfaces = ApplicationUtils.getPropertyList(Constants.IGNORED_INTERFACES);
        IGNORED_INTERFACES.addAll(interfaces);

        final long delayMs = Long.getLong("nacos.core.inet.auto-refresh", 30_000L);

        Runnable ipAutoRefresh = new Runnable() {
            @Override
            public void run() {
                String nacosIp = System.getProperty(NACOS_SERVER_IP);
                if (StringUtils.isBlank(nacosIp)) {
                    nacosIp = ApplicationUtils.getProperty(IP_ADDRESS);
                }

                if (!StringUtils.isBlank(nacosIp) && !isIP(nacosIp)) {
                    throw new RuntimeException("nacos address " + nacosIp + " is not ip");
                }
                String tmpSelfIp = nacosIp;
                if (StringUtils.isBlank(tmpSelfIp)) {
                    preferHostnameOverIp = Boolean.getBoolean(SYSTEM_PREFER_HOSTNAME_OVER_IP);

                    if (!preferHostnameOverIp) {
                        preferHostnameOverIp = Boolean
                            .parseBoolean(ApplicationUtils.getProperty(PREFER_HOSTNAME_OVER_IP));
                    }

                    if (preferHostnameOverIp) {
                        InetAddress inetAddress;
                        try {
                            inetAddress = InetAddress.getLocalHost();
                            if (inetAddress.getHostName().equals(inetAddress.getCanonicalHostName())) {
                                tmpSelfIp = inetAddress.getHostName();
                            } else {
                                tmpSelfIp = inetAddress.getCanonicalHostName();
                            }
                        } catch (UnknownHostException ignore) {
                            LOG.warn("Unable to retrieve localhost");
                        }
                    } else {
                        tmpSelfIp = Objects.requireNonNull(findFirstNonLoopbackAddress()).getHostAddress();
                    }
                }

                if (!Objects.equals(selfIp, tmpSelfIp) && Objects.nonNull(selfIp)) {
                    IPChangeEvent event = new IPChangeEvent();
                    event.setOldIp(selfIp);
                    event.setNewIp(tmpSelfIp);
                    NotifyCenter.publishEvent(event);
                }
                selfIp = tmpSelfIp;
            }
        };

        ipAutoRefresh.run();
    }

    public static String getSelfIp() {
        return selfIp;
    }

    /**
     * findFirstNonLoopbackAddress.
     *
     * @return {@link InetAddress}
     */
    public static InetAddress findFirstNonLoopbackAddress() {
        InetAddress result = null;

        try {
            int lowest = Integer.MAX_VALUE;
            for (Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
                 nics.hasMoreElements(); ) {
                NetworkInterface ifc = nics.nextElement();
                if (ifc.isUp()) {
                    LOG.debug("Testing interface: " + ifc.getDisplayName());
                    if (ifc.getIndex() < lowest || result == null) {
                        lowest = ifc.getIndex();
                    } else {
                        continue;
                    }

                    if (!ignoreInterface(ifc.getDisplayName())) {
                        for (Enumeration<InetAddress> addrs = ifc.getInetAddresses(); addrs.hasMoreElements(); ) {
                            InetAddress address = addrs.nextElement();
                            if (address instanceof Inet4Address && !address.isLoopbackAddress() && isPreferredAddress(
                                address)) {
                                LOG.debug("Found non-loopback interface: " + ifc.getDisplayName());
                                result = address;
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            LOG.error("Cannot get first non-loopback address", ex);
        }

        if (result != null) {
            return result;
        }

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            LOG.warn("Unable to retrieve localhost");
        }

        return null;
    }

    private static boolean isPreferredAddress(InetAddress address) {
        if (useOnlySiteLocalInterface) {
            final boolean siteLocalAddress = address.isSiteLocalAddress();
            if (!siteLocalAddress) {
                LOG.debug("Ignoring address: " + address.getHostAddress());
            }
            return siteLocalAddress;
        }
        if (PREFERRED_NETWORKS.isEmpty()) {
            return true;
        }
        for (String regex : PREFERRED_NETWORKS) {
            final String hostAddress = address.getHostAddress();
            if (hostAddress.matches(regex) || hostAddress.startsWith(regex)) {
                return true;
            }
        }

        return false;
    }

    private static boolean ignoreInterface(String interfaceName) {
        for (String regex : IGNORED_INTERFACES) {
            if (interfaceName.matches(regex)) {
                LOG.debug("Ignoring interface: " + interfaceName);
                return true;
            }
        }
        return false;
    }

    public static boolean isIP(String str) {
        Matcher matcher = IP_PATTERN.matcher(str);
        return matcher.matches();
    }

    /**
     * {@link com.alibaba.nacos.core.cluster.ServerMemberManager} is listener.
     */
    @SuppressWarnings({"PMD.ClassNamingShouldBeCamelRule", "checkstyle:AbbreviationAsWordInName"})
    public static class IPChangeEvent extends SlowEvent {

        private String oldIp;

        private String newIp;

        public String getOldIp() {
            return oldIp;
        }

        public void setOldIp(String oldIp) {
            this.oldIp = oldIp;
        }

        public String getNewIp() {
            return newIp;
        }

        public void setNewIp(String newIp) {
            this.newIp = newIp;
        }

        @Override
        public String toString() {
            return "IPChangeEvent{" + "oldIp='" + oldIp + '\'' + ", newIp='" + newIp + '\'' + '}';
        }
    }

}
