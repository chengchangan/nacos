// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: metadata.proto

package com.alibaba.nacos.istio.model.mcp;

public interface MetadataOrBuilder extends
    // @@protoc_insertion_point(interface_extends:istio.mcp.v1alpha1.Metadata)
    com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     * Fully qualified name of the resource. Unique in context of a collection.
     * The fully qualified name consists of a directory and basename. The directory identifies
     * the resources location in a resource hierarchy. The basename identifies the specific
     * resource name within the context of that directory.
     * The directory and basename are composed of one or more segments. Segments must be
     * valid [DNS labels](https://tools.ietf.org/html/rfc1123). "/" is the delimiter between
     * segments
     * The rightmost segment is the basename. All segments to the
     * left of the basename form the directory. Segments moving towards the left
     * represent higher positions in the resource hierarchy, similar to reverse
     * DNS notation. e.g.
     *    /&lt;org&gt;/&lt;team&gt;/&lt;subteam&gt;/&lt;resource basename&gt;
     * An empty directory indicates a resource that is located at the root of the
     * hierarchy, e.g.
     *    /&lt;globally scoped resource&gt;
     * On Kubernetes the resource hierarchy is two-levels: namespaces and
     * cluster-scoped (i.e. global).
     * Namespace resources fully qualified name is of the form:
     *    "&lt;k8s namespace&gt;/&lt;k8s resource name&gt;"
     * Cluster scoped resources are located at the root of the hierarchy and are of the form:
     *    "/&lt;k8s resource name&gt;"
     * </pre>
     *
     * <code>string name = 1;</code>
     *
     * @return The name.
     */
    java.lang.String getName();

    /**
     * <pre>
     * Fully qualified name of the resource. Unique in context of a collection.
     * The fully qualified name consists of a directory and basename. The directory identifies
     * the resources location in a resource hierarchy. The basename identifies the specific
     * resource name within the context of that directory.
     * The directory and basename are composed of one or more segments. Segments must be
     * valid [DNS labels](https://tools.ietf.org/html/rfc1123). "/" is the delimiter between
     * segments
     * The rightmost segment is the basename. All segments to the
     * left of the basename form the directory. Segments moving towards the left
     * represent higher positions in the resource hierarchy, similar to reverse
     * DNS notation. e.g.
     *    /&lt;org&gt;/&lt;team&gt;/&lt;subteam&gt;/&lt;resource basename&gt;
     * An empty directory indicates a resource that is located at the root of the
     * hierarchy, e.g.
     *    /&lt;globally scoped resource&gt;
     * On Kubernetes the resource hierarchy is two-levels: namespaces and
     * cluster-scoped (i.e. global).
     * Namespace resources fully qualified name is of the form:
     *    "&lt;k8s namespace&gt;/&lt;k8s resource name&gt;"
     * Cluster scoped resources are located at the root of the hierarchy and are of the form:
     *    "/&lt;k8s resource name&gt;"
     * </pre>
     *
     * <code>string name = 1;</code>
     *
     * @return The bytes for name.
     */
    com.google.protobuf.ByteString
    getNameBytes();

    /**
     * <pre>
     * The creation timestamp of the resource.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp create_time = 2;</code>
     *
     * @return Whether the createTime field is set.
     */
    boolean hasCreateTime();

    /**
     * <pre>
     * The creation timestamp of the resource.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp create_time = 2;</code>
     *
     * @return The createTime.
     */
    com.google.protobuf.Timestamp getCreateTime();

    /**
     * <pre>
     * The creation timestamp of the resource.
     * </pre>
     *
     * <code>.google.protobuf.Timestamp create_time = 2;</code>
     */
    com.google.protobuf.TimestampOrBuilder getCreateTimeOrBuilder();

    /**
     * <pre>
     * Resource version. This is used to determine when resources change across
     * resource updates. It should be treated as opaque by consumers/sinks.
     * </pre>
     *
     * <code>string version = 3;</code>
     *
     * @return The version.
     */
    java.lang.String getVersion();

    /**
     * <pre>
     * Resource version. This is used to determine when resources change across
     * resource updates. It should be treated as opaque by consumers/sinks.
     * </pre>
     *
     * <code>string version = 3;</code>
     *
     * @return The bytes for version.
     */
    com.google.protobuf.ByteString
    getVersionBytes();

    /**
     * <pre>
     * Map of string keys and values that can be used to organize and categorize
     * resources within a collection.
     * </pre>
     *
     * <code>map&lt;string, string&gt; labels = 4;</code>
     */
    int getLabelsCount();

    /**
     * <pre>
     * Map of string keys and values that can be used to organize and categorize
     * resources within a collection.
     * </pre>
     *
     * <code>map&lt;string, string&gt; labels = 4;</code>
     */
    boolean containsLabels(
        java.lang.String key);

    /**
     * Use {@link #getLabelsMap()} instead.
     */
    @java.lang.Deprecated
    java.util.Map<java.lang.String, java.lang.String>
    getLabels();

    /**
     * <pre>
     * Map of string keys and values that can be used to organize and categorize
     * resources within a collection.
     * </pre>
     *
     * <code>map&lt;string, string&gt; labels = 4;</code>
     */
    java.util.Map<java.lang.String, java.lang.String>
    getLabelsMap();

    /**
     * <pre>
     * Map of string keys and values that can be used to organize and categorize
     * resources within a collection.
     * </pre>
     *
     * <code>map&lt;string, string&gt; labels = 4;</code>
     */

    java.lang.String getLabelsOrDefault(
        java.lang.String key,
        java.lang.String defaultValue);

    /**
     * <pre>
     * Map of string keys and values that can be used to organize and categorize
     * resources within a collection.
     * </pre>
     *
     * <code>map&lt;string, string&gt; labels = 4;</code>
     */

    java.lang.String getLabelsOrThrow(
        java.lang.String key);

    /**
     * <pre>
     * Map of string keys and values that can be used by source and sink to communicate
     * arbitrary metadata about this resource.
     * </pre>
     *
     * <code>map&lt;string, string&gt; annotations = 5;</code>
     */
    int getAnnotationsCount();

    /**
     * <pre>
     * Map of string keys and values that can be used by source and sink to communicate
     * arbitrary metadata about this resource.
     * </pre>
     *
     * <code>map&lt;string, string&gt; annotations = 5;</code>
     */
    boolean containsAnnotations(
        java.lang.String key);

    /**
     * Use {@link #getAnnotationsMap()} instead.
     */
    @java.lang.Deprecated
    java.util.Map<java.lang.String, java.lang.String>
    getAnnotations();

    /**
     * <pre>
     * Map of string keys and values that can be used by source and sink to communicate
     * arbitrary metadata about this resource.
     * </pre>
     *
     * <code>map&lt;string, string&gt; annotations = 5;</code>
     */
    java.util.Map<java.lang.String, java.lang.String>
    getAnnotationsMap();

    /**
     * <pre>
     * Map of string keys and values that can be used by source and sink to communicate
     * arbitrary metadata about this resource.
     * </pre>
     *
     * <code>map&lt;string, string&gt; annotations = 5;</code>
     */

    java.lang.String getAnnotationsOrDefault(
        java.lang.String key,
        java.lang.String defaultValue);

    /**
     * <pre>
     * Map of string keys and values that can be used by source and sink to communicate
     * arbitrary metadata about this resource.
     * </pre>
     *
     * <code>map&lt;string, string&gt; annotations = 5;</code>
     */

    java.lang.String getAnnotationsOrThrow(
        java.lang.String key);
}
