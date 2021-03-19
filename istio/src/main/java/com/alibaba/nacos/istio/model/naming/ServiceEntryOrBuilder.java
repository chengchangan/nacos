// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: service_entry.proto

package com.alibaba.nacos.istio.model.naming;

public interface ServiceEntryOrBuilder extends
    // @@protoc_insertion_point(interface_extends:istio.networking.v1alpha3.ServiceEntry)
    com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     * REQUIRED. The hosts associated with the ServiceEntry. Could be a DNS
     * name with wildcard prefix.
     * 1. The hosts field is used to select matching hosts in VirtualServices and DestinationRules.
     * 2. For HTTP traffic the HTTP Host/Authority header will be matched against the hosts field.
     * 3. For HTTPs or TLS traffic containing Server Name Indication (SNI), the SNI value
     * will be matched against the hosts field.
     * Note that when resolution is set to type DNS
     * and no endpoints are specified, the host field will be used as the DNS name
     * of the endpoint to route traffic to.
     * </pre>
     *
     * <code>repeated string hosts = 1;</code>
     *
     * @return A list containing the hosts.
     */
    java.util.List<java.lang.String>
    getHostsList();

    /**
     * <pre>
     * REQUIRED. The hosts associated with the ServiceEntry. Could be a DNS
     * name with wildcard prefix.
     * 1. The hosts field is used to select matching hosts in VirtualServices and DestinationRules.
     * 2. For HTTP traffic the HTTP Host/Authority header will be matched against the hosts field.
     * 3. For HTTPs or TLS traffic containing Server Name Indication (SNI), the SNI value
     * will be matched against the hosts field.
     * Note that when resolution is set to type DNS
     * and no endpoints are specified, the host field will be used as the DNS name
     * of the endpoint to route traffic to.
     * </pre>
     *
     * <code>repeated string hosts = 1;</code>
     *
     * @return The count of hosts.
     */
    int getHostsCount();

    /**
     * <pre>
     * REQUIRED. The hosts associated with the ServiceEntry. Could be a DNS
     * name with wildcard prefix.
     * 1. The hosts field is used to select matching hosts in VirtualServices and DestinationRules.
     * 2. For HTTP traffic the HTTP Host/Authority header will be matched against the hosts field.
     * 3. For HTTPs or TLS traffic containing Server Name Indication (SNI), the SNI value
     * will be matched against the hosts field.
     * Note that when resolution is set to type DNS
     * and no endpoints are specified, the host field will be used as the DNS name
     * of the endpoint to route traffic to.
     * </pre>
     *
     * <code>repeated string hosts = 1;</code>
     *
     * @param index The index of the element to return.
     * @return The hosts at the given index.
     */
    java.lang.String getHosts(int index);

    /**
     * <pre>
     * REQUIRED. The hosts associated with the ServiceEntry. Could be a DNS
     * name with wildcard prefix.
     * 1. The hosts field is used to select matching hosts in VirtualServices and DestinationRules.
     * 2. For HTTP traffic the HTTP Host/Authority header will be matched against the hosts field.
     * 3. For HTTPs or TLS traffic containing Server Name Indication (SNI), the SNI value
     * will be matched against the hosts field.
     * Note that when resolution is set to type DNS
     * and no endpoints are specified, the host field will be used as the DNS name
     * of the endpoint to route traffic to.
     * </pre>
     *
     * <code>repeated string hosts = 1;</code>
     *
     * @param index The index of the value to return.
     * @return The bytes of the hosts at the given index.
     */
    com.google.protobuf.ByteString
    getHostsBytes(int index);

    /**
     * <pre>
     * The virtual IP addresses associated with the service. Could be CIDR
     * prefix. For HTTP traffic, generated route configurations will include http route
     * domains for both the `addresses` and `hosts` field values and the destination will
     * be identified based on the HTTP Host/Authority header.
     * If one or more IP addresses are specified,
     * the incoming traffic will be identified as belonging to this service
     * if the destination IP matches the IP/CIDRs specified in the addresses
     * field. If the Addresses field is empty, traffic will be identified
     * solely based on the destination port. In such scenarios, the port on
     * which the service is being accessed must not be shared by any other
     * service in the mesh. In other words, the sidecar will behave as a
     * simple TCP proxy, forwarding incoming traffic on a specified port to
     * the specified destination endpoint IP/host. Unix domain socket
     * addresses are not supported in this field.
     * </pre>
     *
     * <code>repeated string addresses = 2;</code>
     *
     * @return A list containing the addresses.
     */
    java.util.List<java.lang.String>
    getAddressesList();

    /**
     * <pre>
     * The virtual IP addresses associated with the service. Could be CIDR
     * prefix. For HTTP traffic, generated route configurations will include http route
     * domains for both the `addresses` and `hosts` field values and the destination will
     * be identified based on the HTTP Host/Authority header.
     * If one or more IP addresses are specified,
     * the incoming traffic will be identified as belonging to this service
     * if the destination IP matches the IP/CIDRs specified in the addresses
     * field. If the Addresses field is empty, traffic will be identified
     * solely based on the destination port. In such scenarios, the port on
     * which the service is being accessed must not be shared by any other
     * service in the mesh. In other words, the sidecar will behave as a
     * simple TCP proxy, forwarding incoming traffic on a specified port to
     * the specified destination endpoint IP/host. Unix domain socket
     * addresses are not supported in this field.
     * </pre>
     *
     * <code>repeated string addresses = 2;</code>
     *
     * @return The count of addresses.
     */
    int getAddressesCount();

    /**
     * <pre>
     * The virtual IP addresses associated with the service. Could be CIDR
     * prefix. For HTTP traffic, generated route configurations will include http route
     * domains for both the `addresses` and `hosts` field values and the destination will
     * be identified based on the HTTP Host/Authority header.
     * If one or more IP addresses are specified,
     * the incoming traffic will be identified as belonging to this service
     * if the destination IP matches the IP/CIDRs specified in the addresses
     * field. If the Addresses field is empty, traffic will be identified
     * solely based on the destination port. In such scenarios, the port on
     * which the service is being accessed must not be shared by any other
     * service in the mesh. In other words, the sidecar will behave as a
     * simple TCP proxy, forwarding incoming traffic on a specified port to
     * the specified destination endpoint IP/host. Unix domain socket
     * addresses are not supported in this field.
     * </pre>
     *
     * <code>repeated string addresses = 2;</code>
     *
     * @param index The index of the element to return.
     * @return The addresses at the given index.
     */
    java.lang.String getAddresses(int index);

    /**
     * <pre>
     * The virtual IP addresses associated with the service. Could be CIDR
     * prefix. For HTTP traffic, generated route configurations will include http route
     * domains for both the `addresses` and `hosts` field values and the destination will
     * be identified based on the HTTP Host/Authority header.
     * If one or more IP addresses are specified,
     * the incoming traffic will be identified as belonging to this service
     * if the destination IP matches the IP/CIDRs specified in the addresses
     * field. If the Addresses field is empty, traffic will be identified
     * solely based on the destination port. In such scenarios, the port on
     * which the service is being accessed must not be shared by any other
     * service in the mesh. In other words, the sidecar will behave as a
     * simple TCP proxy, forwarding incoming traffic on a specified port to
     * the specified destination endpoint IP/host. Unix domain socket
     * addresses are not supported in this field.
     * </pre>
     *
     * <code>repeated string addresses = 2;</code>
     *
     * @param index The index of the value to return.
     * @return The bytes of the addresses at the given index.
     */
    com.google.protobuf.ByteString
    getAddressesBytes(int index);

    /**
     * <pre>
     * REQUIRED. The ports associated with the external service. If the
     * Endpoints are Unix domain socket addresses, there must be exactly one
     * port.
     * </pre>
     *
     * <code>repeated .istio.networking.v1alpha3.Port ports = 3;</code>
     */
    java.util.List<com.alibaba.nacos.istio.model.Port>
    getPortsList();

    /**
     * <pre>
     * REQUIRED. The ports associated with the external service. If the
     * Endpoints are Unix domain socket addresses, there must be exactly one
     * port.
     * </pre>
     *
     * <code>repeated .istio.networking.v1alpha3.Port ports = 3;</code>
     */
    com.alibaba.nacos.istio.model.Port getPorts(int index);

    /**
     * <pre>
     * REQUIRED. The ports associated with the external service. If the
     * Endpoints are Unix domain socket addresses, there must be exactly one
     * port.
     * </pre>
     *
     * <code>repeated .istio.networking.v1alpha3.Port ports = 3;</code>
     */
    int getPortsCount();

    /**
     * <pre>
     * REQUIRED. The ports associated with the external service. If the
     * Endpoints are Unix domain socket addresses, there must be exactly one
     * port.
     * </pre>
     *
     * <code>repeated .istio.networking.v1alpha3.Port ports = 3;</code>
     */
    java.util.List<? extends com.alibaba.nacos.istio.model.PortOrBuilder>
    getPortsOrBuilderList();

    /**
     * <pre>
     * REQUIRED. The ports associated with the external service. If the
     * Endpoints are Unix domain socket addresses, there must be exactly one
     * port.
     * </pre>
     *
     * <code>repeated .istio.networking.v1alpha3.Port ports = 3;</code>
     */
    com.alibaba.nacos.istio.model.PortOrBuilder getPortsOrBuilder(
        int index);

    /**
     * <pre>
     * Specify whether the service should be considered external to the mesh
     * or part of the mesh.
     * </pre>
     *
     * <code>.istio.networking.v1alpha3.ServiceEntry.Location location = 4;</code>
     *
     * @return The enum numeric value on the wire for location.
     */
    int getLocationValue();

    /**
     * <pre>
     * Specify whether the service should be considered external to the mesh
     * or part of the mesh.
     * </pre>
     *
     * <code>.istio.networking.v1alpha3.ServiceEntry.Location location = 4;</code>
     *
     * @return The location.
     */
    com.alibaba.nacos.istio.model.naming.ServiceEntry.Location getLocation();

    /**
     * <pre>
     * REQUIRED: Service discovery mode for the hosts. Care must be taken
     * when setting the resolution mode to NONE for a TCP port without
     * accompanying IP addresses. In such cases, traffic to any IP on
     * said port will be allowed (i.e. 0.0.0.0:&lt;port&gt;).
     * </pre>
     *
     * <code>.istio.networking.v1alpha3.ServiceEntry.Resolution resolution = 5;</code>
     *
     * @return The enum numeric value on the wire for resolution.
     */
    int getResolutionValue();

    /**
     * <pre>
     * REQUIRED: Service discovery mode for the hosts. Care must be taken
     * when setting the resolution mode to NONE for a TCP port without
     * accompanying IP addresses. In such cases, traffic to any IP on
     * said port will be allowed (i.e. 0.0.0.0:&lt;port&gt;).
     * </pre>
     *
     * <code>.istio.networking.v1alpha3.ServiceEntry.Resolution resolution = 5;</code>
     *
     * @return The resolution.
     */
    com.alibaba.nacos.istio.model.naming.ServiceEntry.Resolution getResolution();

    /**
     * <pre>
     * One or more endpoints associated with the service.
     * </pre>
     *
     * <code>repeated .istio.networking.v1alpha3.ServiceEntry.Endpoint endpoints = 6;</code>
     */
    java.util.List<com.alibaba.nacos.istio.model.naming.ServiceEntry.Endpoint>
    getEndpointsList();

    /**
     * <pre>
     * One or more endpoints associated with the service.
     * </pre>
     *
     * <code>repeated .istio.networking.v1alpha3.ServiceEntry.Endpoint endpoints = 6;</code>
     */
    com.alibaba.nacos.istio.model.naming.ServiceEntry.Endpoint getEndpoints(int index);

    /**
     * <pre>
     * One or more endpoints associated with the service.
     * </pre>
     *
     * <code>repeated .istio.networking.v1alpha3.ServiceEntry.Endpoint endpoints = 6;</code>
     */
    int getEndpointsCount();

    /**
     * <pre>
     * One or more endpoints associated with the service.
     * </pre>
     *
     * <code>repeated .istio.networking.v1alpha3.ServiceEntry.Endpoint endpoints = 6;</code>
     */
    java.util.List<? extends com.alibaba.nacos.istio.model.naming.ServiceEntry.EndpointOrBuilder>
    getEndpointsOrBuilderList();

    /**
     * <pre>
     * One or more endpoints associated with the service.
     * </pre>
     *
     * <code>repeated .istio.networking.v1alpha3.ServiceEntry.Endpoint endpoints = 6;</code>
     */
    com.alibaba.nacos.istio.model.naming.ServiceEntry.EndpointOrBuilder getEndpointsOrBuilder(
        int index);

    /**
     * <pre>
     * A list of namespaces to which this service is exported. Exporting a service
     * allows it to be used by sidecars, gateways and virtual services defined in
     * other namespaces. This feature provides a mechanism for service owners
     * and mesh administrators to control the visibility of services across
     * namespace boundaries.
     * If no namespaces are specified then the service is exported to all
     * namespaces by default.
     * The value "." is reserved and defines an export to the same namespace that
     * the service is declared in. Similarly the value "*" is reserved and
     * defines an export to all namespaces.
     * For a Kubernetes Service, the equivalent effect can be achieved by setting
     * the annotation "networking.istio.io/exportTo" to a comma-separated list
     * of namespace names.
     * NOTE: in the current release, the `exportTo` value is restricted to
     * "." or "*" (i.e., the current namespace or all namespaces).
     * </pre>
     *
     * <code>repeated string export_to = 7;</code>
     *
     * @return A list containing the exportTo.
     */
    java.util.List<java.lang.String>
    getExportToList();

    /**
     * <pre>
     * A list of namespaces to which this service is exported. Exporting a service
     * allows it to be used by sidecars, gateways and virtual services defined in
     * other namespaces. This feature provides a mechanism for service owners
     * and mesh administrators to control the visibility of services across
     * namespace boundaries.
     * If no namespaces are specified then the service is exported to all
     * namespaces by default.
     * The value "." is reserved and defines an export to the same namespace that
     * the service is declared in. Similarly the value "*" is reserved and
     * defines an export to all namespaces.
     * For a Kubernetes Service, the equivalent effect can be achieved by setting
     * the annotation "networking.istio.io/exportTo" to a comma-separated list
     * of namespace names.
     * NOTE: in the current release, the `exportTo` value is restricted to
     * "." or "*" (i.e., the current namespace or all namespaces).
     * </pre>
     *
     * <code>repeated string export_to = 7;</code>
     *
     * @return The count of exportTo.
     */
    int getExportToCount();

    /**
     * <pre>
     * A list of namespaces to which this service is exported. Exporting a service
     * allows it to be used by sidecars, gateways and virtual services defined in
     * other namespaces. This feature provides a mechanism for service owners
     * and mesh administrators to control the visibility of services across
     * namespace boundaries.
     * If no namespaces are specified then the service is exported to all
     * namespaces by default.
     * The value "." is reserved and defines an export to the same namespace that
     * the service is declared in. Similarly the value "*" is reserved and
     * defines an export to all namespaces.
     * For a Kubernetes Service, the equivalent effect can be achieved by setting
     * the annotation "networking.istio.io/exportTo" to a comma-separated list
     * of namespace names.
     * NOTE: in the current release, the `exportTo` value is restricted to
     * "." or "*" (i.e., the current namespace or all namespaces).
     * </pre>
     *
     * <code>repeated string export_to = 7;</code>
     *
     * @param index The index of the element to return.
     * @return The exportTo at the given index.
     */
    java.lang.String getExportTo(int index);

    /**
     * <pre>
     * A list of namespaces to which this service is exported. Exporting a service
     * allows it to be used by sidecars, gateways and virtual services defined in
     * other namespaces. This feature provides a mechanism for service owners
     * and mesh administrators to control the visibility of services across
     * namespace boundaries.
     * If no namespaces are specified then the service is exported to all
     * namespaces by default.
     * The value "." is reserved and defines an export to the same namespace that
     * the service is declared in. Similarly the value "*" is reserved and
     * defines an export to all namespaces.
     * For a Kubernetes Service, the equivalent effect can be achieved by setting
     * the annotation "networking.istio.io/exportTo" to a comma-separated list
     * of namespace names.
     * NOTE: in the current release, the `exportTo` value is restricted to
     * "." or "*" (i.e., the current namespace or all namespaces).
     * </pre>
     *
     * <code>repeated string export_to = 7;</code>
     *
     * @param index The index of the value to return.
     * @return The bytes of the exportTo at the given index.
     */
    com.google.protobuf.ByteString
    getExportToBytes(int index);

    /**
     * <pre>
     * The list of subject alternate names allowed for workload instances that
     * implement this service. This information is used to enforce
     * [secure-naming](https://istio.io/docs/concepts/security/#secure-naming).
     * If specified, the proxy will verify that the server
     * certificate's subject alternate name matches one of the specified values.
     * </pre>
     *
     * <code>repeated string subject_alt_names = 8;</code>
     *
     * @return A list containing the subjectAltNames.
     */
    java.util.List<java.lang.String>
    getSubjectAltNamesList();

    /**
     * <pre>
     * The list of subject alternate names allowed for workload instances that
     * implement this service. This information is used to enforce
     * [secure-naming](https://istio.io/docs/concepts/security/#secure-naming).
     * If specified, the proxy will verify that the server
     * certificate's subject alternate name matches one of the specified values.
     * </pre>
     *
     * <code>repeated string subject_alt_names = 8;</code>
     *
     * @return The count of subjectAltNames.
     */
    int getSubjectAltNamesCount();

    /**
     * <pre>
     * The list of subject alternate names allowed for workload instances that
     * implement this service. This information is used to enforce
     * [secure-naming](https://istio.io/docs/concepts/security/#secure-naming).
     * If specified, the proxy will verify that the server
     * certificate's subject alternate name matches one of the specified values.
     * </pre>
     *
     * <code>repeated string subject_alt_names = 8;</code>
     *
     * @param index The index of the element to return.
     * @return The subjectAltNames at the given index.
     */
    java.lang.String getSubjectAltNames(int index);

    /**
     * <pre>
     * The list of subject alternate names allowed for workload instances that
     * implement this service. This information is used to enforce
     * [secure-naming](https://istio.io/docs/concepts/security/#secure-naming).
     * If specified, the proxy will verify that the server
     * certificate's subject alternate name matches one of the specified values.
     * </pre>
     *
     * <code>repeated string subject_alt_names = 8;</code>
     *
     * @param index The index of the value to return.
     * @return The bytes of the subjectAltNames at the given index.
     */
    com.google.protobuf.ByteString
    getSubjectAltNamesBytes(int index);
}
