// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: mcp.proto

package com.alibaba.nacos.istio.model.mcp;

public final class Mcp {
    private Mcp() {
    }

    public static void registerAllExtensions(
        com.google.protobuf.ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(
        com.google.protobuf.ExtensionRegistry registry) {
        registerAllExtensions(
            (com.google.protobuf.ExtensionRegistryLite) registry);
    }

    static final com.google.protobuf.Descriptors.Descriptor
        internal_static_istio_mcp_v1alpha1_SinkNode_descriptor;
    static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internal_static_istio_mcp_v1alpha1_SinkNode_fieldAccessorTable;
    static final com.google.protobuf.Descriptors.Descriptor
        internal_static_istio_mcp_v1alpha1_SinkNode_AnnotationsEntry_descriptor;
    static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internal_static_istio_mcp_v1alpha1_SinkNode_AnnotationsEntry_fieldAccessorTable;
    static final com.google.protobuf.Descriptors.Descriptor
        internal_static_istio_mcp_v1alpha1_MeshConfigRequest_descriptor;
    static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internal_static_istio_mcp_v1alpha1_MeshConfigRequest_fieldAccessorTable;
    static final com.google.protobuf.Descriptors.Descriptor
        internal_static_istio_mcp_v1alpha1_MeshConfigResponse_descriptor;
    static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internal_static_istio_mcp_v1alpha1_MeshConfigResponse_fieldAccessorTable;
    static final com.google.protobuf.Descriptors.Descriptor
        internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigRequest_descriptor;
    static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigRequest_fieldAccessorTable;
    static final com.google.protobuf.Descriptors.Descriptor
        internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigRequest_InitialResourceVersionsEntry_descriptor;
    static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigRequest_InitialResourceVersionsEntry_fieldAccessorTable;
    static final com.google.protobuf.Descriptors.Descriptor
        internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigResponse_descriptor;
    static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigResponse_fieldAccessorTable;
    static final com.google.protobuf.Descriptors.Descriptor
        internal_static_istio_mcp_v1alpha1_RequestResources_descriptor;
    static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internal_static_istio_mcp_v1alpha1_RequestResources_fieldAccessorTable;
    static final com.google.protobuf.Descriptors.Descriptor
        internal_static_istio_mcp_v1alpha1_RequestResources_InitialResourceVersionsEntry_descriptor;
    static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internal_static_istio_mcp_v1alpha1_RequestResources_InitialResourceVersionsEntry_fieldAccessorTable;
    static final com.google.protobuf.Descriptors.Descriptor
        internal_static_istio_mcp_v1alpha1_Resources_descriptor;
    static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internal_static_istio_mcp_v1alpha1_Resources_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor
    getDescriptor() {
        return descriptor;
    }

    private static com.google.protobuf.Descriptors.FileDescriptor
        descriptor;

    static {
        java.lang.String[] descriptorData = {
            "\n\tmcp.proto\022\022istio.mcp.v1alpha1\032\014status." +
                "proto\032\016resource.proto\"\216\001\n\010SinkNode\022\n\n\002id" +
                "\030\001 \001(\t\022B\n\013annotations\030\002 \003(\0132-.istio.mcp." +
                "v1alpha1.SinkNode.AnnotationsEntry\0322\n\020An" +
                "notationsEntry\022\013\n\003key\030\001 \001(\t\022\r\n\005value\030\002 \001" +
                "(\t:\0028\001\"\256\001\n\021MeshConfigRequest\022\024\n\014version_" +
                "info\030\001 \001(\t\022/\n\tsink_node\030\002 \001(\0132\034.istio.mc" +
                "p.v1alpha1.SinkNode\022\020\n\010type_url\030\003 \001(\t\022\026\n" +
                "\016response_nonce\030\004 \001(\t\022(\n\014error_detail\030\005 " +
                "\001(\0132\022.google.rpc.Status\"|\n\022MeshConfigRes" +
                "ponse\022\024\n\014version_info\030\001 \001(\t\022/\n\tresources" +
                "\030\002 \003(\0132\034.istio.mcp.v1alpha1.Resource\022\020\n\010" +
                "type_url\030\003 \001(\t\022\r\n\005nonce\030\004 \001(\t\"\325\002\n\034Increm" +
                "entalMeshConfigRequest\022/\n\tsink_node\030\001 \001(" +
                "\0132\034.istio.mcp.v1alpha1.SinkNode\022\020\n\010type_" +
                "url\030\002 \001(\t\022p\n\031initial_resource_versions\030\003" +
                " \003(\0132M.istio.mcp.v1alpha1.IncrementalMes" +
                "hConfigRequest.InitialResourceVersionsEn" +
                "try\022\026\n\016response_nonce\030\004 \001(\t\022(\n\014error_det" +
                "ail\030\005 \001(\0132\022.google.rpc.Status\032>\n\034Initial" +
                "ResourceVersionsEntry\022\013\n\003key\030\001 \001(\t\022\r\n\005va" +
                "lue\030\002 \001(\t:\0028\001\"\227\001\n\035IncrementalMeshConfigR" +
                "esponse\022\033\n\023system_version_info\030\001 \001(\t\022/\n\t" +
                "resources\030\002 \003(\0132\034.istio.mcp.v1alpha1.Res" +
                "ource\022\031\n\021removed_resources\030\003 \003(\t\022\r\n\005nonc" +
                "e\030\004 \001(\t\"\324\002\n\020RequestResources\022/\n\tsink_nod" +
                "e\030\001 \001(\0132\034.istio.mcp.v1alpha1.SinkNode\022\022\n" +
                "\ncollection\030\002 \001(\t\022d\n\031initial_resource_ve" +
                "rsions\030\003 \003(\0132A.istio.mcp.v1alpha1.Reques" +
                "tResources.InitialResourceVersionsEntry\022" +
                "\026\n\016response_nonce\030\004 \001(\t\022(\n\014error_detail\030" +
                "\005 \001(\0132\022.google.rpc.Status\022\023\n\013incremental" +
                "\030\006 \001(\010\032>\n\034InitialResourceVersionsEntry\022\013" +
                "\n\003key\030\001 \001(\t\022\r\n\005value\030\002 \001(\t:\0028\001\"\254\001\n\tResou" +
                "rces\022\033\n\023system_version_info\030\001 \001(\t\022\022\n\ncol" +
                "lection\030\002 \001(\t\022/\n\tresources\030\003 \003(\0132\034.istio" +
                ".mcp.v1alpha1.Resource\022\031\n\021removed_resour" +
                "ces\030\004 \003(\t\022\r\n\005nonce\030\005 \001(\t\022\023\n\013incremental\030" +
                "\006 \001(\0102\235\002\n\033AggregatedMeshConfigService\022p\n" +
                "\031StreamAggregatedResources\022%.istio.mcp.v" +
                "1alpha1.MeshConfigRequest\032&.istio.mcp.v1" +
                "alpha1.MeshConfigResponse\"\000(\0010\001\022\213\001\n\036Incr" +
                "ementalAggregatedResources\0220.istio.mcp.v" +
                "1alpha1.IncrementalMeshConfigRequest\0321.i" +
                "stio.mcp.v1alpha1.IncrementalMeshConfigR" +
                "esponse\"\000(\0010\0012v\n\016ResourceSource\022d\n\027Estab" +
                "lishResourceStream\022$.istio.mcp.v1alpha1." +
                "RequestResources\032\035.istio.mcp.v1alpha1.Re" +
                "sources\"\000(\0010\0012t\n\014ResourceSink\022d\n\027Establi" +
                "shResourceStream\022\035.istio.mcp.v1alpha1.Re" +
                "sources\032$.istio.mcp.v1alpha1.RequestReso" +
                "urces\"\000(\0010\001B%\n!com.alibaba.nacos.istio.m" +
                "odel.mcpP\001b\006proto3"
        };
        descriptor = com.google.protobuf.Descriptors.FileDescriptor
            .internalBuildGeneratedFileFrom(descriptorData,
                new com.google.protobuf.Descriptors.FileDescriptor[]{
                    com.google.rpc.StatusProto.getDescriptor(),
                    com.alibaba.nacos.istio.model.mcp.ResourceOuterClass.getDescriptor(),
                });
        internal_static_istio_mcp_v1alpha1_SinkNode_descriptor =
            getDescriptor().getMessageTypes().get(0);
        internal_static_istio_mcp_v1alpha1_SinkNode_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_istio_mcp_v1alpha1_SinkNode_descriptor,
            new java.lang.String[]{"Id", "Annotations",});
        internal_static_istio_mcp_v1alpha1_SinkNode_AnnotationsEntry_descriptor =
            internal_static_istio_mcp_v1alpha1_SinkNode_descriptor.getNestedTypes().get(0);
        internal_static_istio_mcp_v1alpha1_SinkNode_AnnotationsEntry_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_istio_mcp_v1alpha1_SinkNode_AnnotationsEntry_descriptor,
            new java.lang.String[]{"Key", "Value",});
        internal_static_istio_mcp_v1alpha1_MeshConfigRequest_descriptor =
            getDescriptor().getMessageTypes().get(1);
        internal_static_istio_mcp_v1alpha1_MeshConfigRequest_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_istio_mcp_v1alpha1_MeshConfigRequest_descriptor,
            new java.lang.String[]{"VersionInfo", "SinkNode", "TypeUrl", "ResponseNonce", "ErrorDetail",});
        internal_static_istio_mcp_v1alpha1_MeshConfigResponse_descriptor =
            getDescriptor().getMessageTypes().get(2);
        internal_static_istio_mcp_v1alpha1_MeshConfigResponse_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_istio_mcp_v1alpha1_MeshConfigResponse_descriptor,
            new java.lang.String[]{"VersionInfo", "Resources", "TypeUrl", "Nonce",});
        internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigRequest_descriptor =
            getDescriptor().getMessageTypes().get(3);
        internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigRequest_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigRequest_descriptor,
            new java.lang.String[]{"SinkNode", "TypeUrl", "InitialResourceVersions", "ResponseNonce", "ErrorDetail",});
        internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigRequest_InitialResourceVersionsEntry_descriptor =
            internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigRequest_descriptor.getNestedTypes().get(0);
        internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigRequest_InitialResourceVersionsEntry_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigRequest_InitialResourceVersionsEntry_descriptor,
            new java.lang.String[]{"Key", "Value",});
        internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigResponse_descriptor =
            getDescriptor().getMessageTypes().get(4);
        internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigResponse_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_istio_mcp_v1alpha1_IncrementalMeshConfigResponse_descriptor,
            new java.lang.String[]{"SystemVersionInfo", "Resources", "RemovedResources", "Nonce",});
        internal_static_istio_mcp_v1alpha1_RequestResources_descriptor =
            getDescriptor().getMessageTypes().get(5);
        internal_static_istio_mcp_v1alpha1_RequestResources_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_istio_mcp_v1alpha1_RequestResources_descriptor,
            new java.lang.String[]{"SinkNode", "Collection", "InitialResourceVersions", "ResponseNonce", "ErrorDetail", "Incremental",});
        internal_static_istio_mcp_v1alpha1_RequestResources_InitialResourceVersionsEntry_descriptor =
            internal_static_istio_mcp_v1alpha1_RequestResources_descriptor.getNestedTypes().get(0);
        internal_static_istio_mcp_v1alpha1_RequestResources_InitialResourceVersionsEntry_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_istio_mcp_v1alpha1_RequestResources_InitialResourceVersionsEntry_descriptor,
            new java.lang.String[]{"Key", "Value",});
        internal_static_istio_mcp_v1alpha1_Resources_descriptor =
            getDescriptor().getMessageTypes().get(6);
        internal_static_istio_mcp_v1alpha1_Resources_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_istio_mcp_v1alpha1_Resources_descriptor,
            new java.lang.String[]{"SystemVersionInfo", "Collection", "Resources", "RemovedResources", "Nonce", "Incremental",});
        com.google.rpc.StatusProto.getDescriptor();
        com.alibaba.nacos.istio.model.mcp.ResourceOuterClass.getDescriptor();
    }

    // @@protoc_insertion_point(outer_class_scope)
}
