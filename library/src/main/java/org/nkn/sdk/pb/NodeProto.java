// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: pb/node.proto

package org.nkn.sdk.pb;

public final class NodeProto {
  private NodeProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  /**
   * Protobuf enum {@code pb.SyncState}
   */
  public enum SyncState
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>WAIT_FOR_SYNCING = 0;</code>
     */
    WAIT_FOR_SYNCING(0),
    /**
     * <code>SYNC_STARTED = 1;</code>
     */
    SYNC_STARTED(1),
    /**
     * <code>SYNC_FINISHED = 2;</code>
     */
    SYNC_FINISHED(2),
    /**
     * <code>PERSIST_FINISHED = 3;</code>
     */
    PERSIST_FINISHED(3),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>WAIT_FOR_SYNCING = 0;</code>
     */
    public static final int WAIT_FOR_SYNCING_VALUE = 0;
    /**
     * <code>SYNC_STARTED = 1;</code>
     */
    public static final int SYNC_STARTED_VALUE = 1;
    /**
     * <code>SYNC_FINISHED = 2;</code>
     */
    public static final int SYNC_FINISHED_VALUE = 2;
    /**
     * <code>PERSIST_FINISHED = 3;</code>
     */
    public static final int PERSIST_FINISHED_VALUE = 3;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static SyncState valueOf(int value) {
      return forNumber(value);
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     */
    public static SyncState forNumber(int value) {
      switch (value) {
        case 0: return WAIT_FOR_SYNCING;
        case 1: return SYNC_STARTED;
        case 2: return SYNC_FINISHED;
        case 3: return PERSIST_FINISHED;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<SyncState>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        SyncState> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<SyncState>() {
            public SyncState findValueByNumber(int number) {
              return SyncState.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return org.nkn.sdk.pb.NodeProto.getDescriptor().getEnumTypes().get(0);
    }

    private static final SyncState[] VALUES = values();

    public static SyncState valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private SyncState(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:pb.SyncState)
  }

  public interface NodeDataOrBuilder extends
      // @@protoc_insertion_point(interface_extends:pb.NodeData)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>bytes public_key = 1;</code>
     * @return The publicKey.
     */
    com.google.protobuf.ByteString getPublicKey();

    /**
     * <code>uint32 websocket_port = 2;</code>
     * @return The websocketPort.
     */
    int getWebsocketPort();

    /**
     * <code>uint32 json_rpc_port = 3;</code>
     * @return The jsonRpcPort.
     */
    int getJsonRpcPort();

    /**
     * <code>uint32 protocol_version = 4;</code>
     * @return The protocolVersion.
     */
    int getProtocolVersion();
  }
  /**
   * Protobuf type {@code pb.NodeData}
   */
  public  static final class NodeData extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:pb.NodeData)
      NodeDataOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use NodeData.newBuilder() to construct.
    private NodeData(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private NodeData() {
      publicKey_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new NodeData();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private NodeData(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {

              publicKey_ = input.readBytes();
              break;
            }
            case 16: {

              websocketPort_ = input.readUInt32();
              break;
            }
            case 24: {

              jsonRpcPort_ = input.readUInt32();
              break;
            }
            case 32: {

              protocolVersion_ = input.readUInt32();
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.nkn.sdk.pb.NodeProto.internal_static_pb_NodeData_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.nkn.sdk.pb.NodeProto.internal_static_pb_NodeData_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.nkn.sdk.pb.NodeProto.NodeData.class, org.nkn.sdk.pb.NodeProto.NodeData.Builder.class);
    }

    public static final int PUBLIC_KEY_FIELD_NUMBER = 1;
    private com.google.protobuf.ByteString publicKey_;
    /**
     * <code>bytes public_key = 1;</code>
     * @return The publicKey.
     */
    public com.google.protobuf.ByteString getPublicKey() {
      return publicKey_;
    }

    public static final int WEBSOCKET_PORT_FIELD_NUMBER = 2;
    private int websocketPort_;
    /**
     * <code>uint32 websocket_port = 2;</code>
     * @return The websocketPort.
     */
    public int getWebsocketPort() {
      return websocketPort_;
    }

    public static final int JSON_RPC_PORT_FIELD_NUMBER = 3;
    private int jsonRpcPort_;
    /**
     * <code>uint32 json_rpc_port = 3;</code>
     * @return The jsonRpcPort.
     */
    public int getJsonRpcPort() {
      return jsonRpcPort_;
    }

    public static final int PROTOCOL_VERSION_FIELD_NUMBER = 4;
    private int protocolVersion_;
    /**
     * <code>uint32 protocol_version = 4;</code>
     * @return The protocolVersion.
     */
    public int getProtocolVersion() {
      return protocolVersion_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!publicKey_.isEmpty()) {
        output.writeBytes(1, publicKey_);
      }
      if (websocketPort_ != 0) {
        output.writeUInt32(2, websocketPort_);
      }
      if (jsonRpcPort_ != 0) {
        output.writeUInt32(3, jsonRpcPort_);
      }
      if (protocolVersion_ != 0) {
        output.writeUInt32(4, protocolVersion_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!publicKey_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, publicKey_);
      }
      if (websocketPort_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(2, websocketPort_);
      }
      if (jsonRpcPort_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(3, jsonRpcPort_);
      }
      if (protocolVersion_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(4, protocolVersion_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof org.nkn.sdk.pb.NodeProto.NodeData)) {
        return super.equals(obj);
      }
      org.nkn.sdk.pb.NodeProto.NodeData other = (org.nkn.sdk.pb.NodeProto.NodeData) obj;

      if (!getPublicKey()
          .equals(other.getPublicKey())) return false;
      if (getWebsocketPort()
          != other.getWebsocketPort()) return false;
      if (getJsonRpcPort()
          != other.getJsonRpcPort()) return false;
      if (getProtocolVersion()
          != other.getProtocolVersion()) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + PUBLIC_KEY_FIELD_NUMBER;
      hash = (53 * hash) + getPublicKey().hashCode();
      hash = (37 * hash) + WEBSOCKET_PORT_FIELD_NUMBER;
      hash = (53 * hash) + getWebsocketPort();
      hash = (37 * hash) + JSON_RPC_PORT_FIELD_NUMBER;
      hash = (53 * hash) + getJsonRpcPort();
      hash = (37 * hash) + PROTOCOL_VERSION_FIELD_NUMBER;
      hash = (53 * hash) + getProtocolVersion();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.nkn.sdk.pb.NodeProto.NodeData parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.nkn.sdk.pb.NodeProto.NodeData parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.nkn.sdk.pb.NodeProto.NodeData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.nkn.sdk.pb.NodeProto.NodeData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.nkn.sdk.pb.NodeProto.NodeData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.nkn.sdk.pb.NodeProto.NodeData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.nkn.sdk.pb.NodeProto.NodeData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.nkn.sdk.pb.NodeProto.NodeData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.nkn.sdk.pb.NodeProto.NodeData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.nkn.sdk.pb.NodeProto.NodeData parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.nkn.sdk.pb.NodeProto.NodeData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.nkn.sdk.pb.NodeProto.NodeData parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(org.nkn.sdk.pb.NodeProto.NodeData prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code pb.NodeData}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:pb.NodeData)
        org.nkn.sdk.pb.NodeProto.NodeDataOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.nkn.sdk.pb.NodeProto.internal_static_pb_NodeData_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.nkn.sdk.pb.NodeProto.internal_static_pb_NodeData_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.nkn.sdk.pb.NodeProto.NodeData.class, org.nkn.sdk.pb.NodeProto.NodeData.Builder.class);
      }

      // Construct using org.nkn.sdk.pb.NodeProto.NodeData.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        publicKey_ = com.google.protobuf.ByteString.EMPTY;

        websocketPort_ = 0;

        jsonRpcPort_ = 0;

        protocolVersion_ = 0;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.nkn.sdk.pb.NodeProto.internal_static_pb_NodeData_descriptor;
      }

      @java.lang.Override
      public org.nkn.sdk.pb.NodeProto.NodeData getDefaultInstanceForType() {
        return org.nkn.sdk.pb.NodeProto.NodeData.getDefaultInstance();
      }

      @java.lang.Override
      public org.nkn.sdk.pb.NodeProto.NodeData build() {
        org.nkn.sdk.pb.NodeProto.NodeData result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.nkn.sdk.pb.NodeProto.NodeData buildPartial() {
        org.nkn.sdk.pb.NodeProto.NodeData result = new org.nkn.sdk.pb.NodeProto.NodeData(this);
        result.publicKey_ = publicKey_;
        result.websocketPort_ = websocketPort_;
        result.jsonRpcPort_ = jsonRpcPort_;
        result.protocolVersion_ = protocolVersion_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.nkn.sdk.pb.NodeProto.NodeData) {
          return mergeFrom((org.nkn.sdk.pb.NodeProto.NodeData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.nkn.sdk.pb.NodeProto.NodeData other) {
        if (other == org.nkn.sdk.pb.NodeProto.NodeData.getDefaultInstance()) return this;
        if (other.getPublicKey() != com.google.protobuf.ByteString.EMPTY) {
          setPublicKey(other.getPublicKey());
        }
        if (other.getWebsocketPort() != 0) {
          setWebsocketPort(other.getWebsocketPort());
        }
        if (other.getJsonRpcPort() != 0) {
          setJsonRpcPort(other.getJsonRpcPort());
        }
        if (other.getProtocolVersion() != 0) {
          setProtocolVersion(other.getProtocolVersion());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        org.nkn.sdk.pb.NodeProto.NodeData parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.nkn.sdk.pb.NodeProto.NodeData) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private com.google.protobuf.ByteString publicKey_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>bytes public_key = 1;</code>
       * @return The publicKey.
       */
      public com.google.protobuf.ByteString getPublicKey() {
        return publicKey_;
      }
      /**
       * <code>bytes public_key = 1;</code>
       * @param value The publicKey to set.
       * @return This builder for chaining.
       */
      public Builder setPublicKey(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        publicKey_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bytes public_key = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearPublicKey() {
        
        publicKey_ = getDefaultInstance().getPublicKey();
        onChanged();
        return this;
      }

      private int websocketPort_ ;
      /**
       * <code>uint32 websocket_port = 2;</code>
       * @return The websocketPort.
       */
      public int getWebsocketPort() {
        return websocketPort_;
      }
      /**
       * <code>uint32 websocket_port = 2;</code>
       * @param value The websocketPort to set.
       * @return This builder for chaining.
       */
      public Builder setWebsocketPort(int value) {
        
        websocketPort_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 websocket_port = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearWebsocketPort() {
        
        websocketPort_ = 0;
        onChanged();
        return this;
      }

      private int jsonRpcPort_ ;
      /**
       * <code>uint32 json_rpc_port = 3;</code>
       * @return The jsonRpcPort.
       */
      public int getJsonRpcPort() {
        return jsonRpcPort_;
      }
      /**
       * <code>uint32 json_rpc_port = 3;</code>
       * @param value The jsonRpcPort to set.
       * @return This builder for chaining.
       */
      public Builder setJsonRpcPort(int value) {
        
        jsonRpcPort_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 json_rpc_port = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearJsonRpcPort() {
        
        jsonRpcPort_ = 0;
        onChanged();
        return this;
      }

      private int protocolVersion_ ;
      /**
       * <code>uint32 protocol_version = 4;</code>
       * @return The protocolVersion.
       */
      public int getProtocolVersion() {
        return protocolVersion_;
      }
      /**
       * <code>uint32 protocol_version = 4;</code>
       * @param value The protocolVersion to set.
       * @return This builder for chaining.
       */
      public Builder setProtocolVersion(int value) {
        
        protocolVersion_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 protocol_version = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearProtocolVersion() {
        
        protocolVersion_ = 0;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:pb.NodeData)
    }

    // @@protoc_insertion_point(class_scope:pb.NodeData)
    private static final org.nkn.sdk.pb.NodeProto.NodeData DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.nkn.sdk.pb.NodeProto.NodeData();
    }

    public static org.nkn.sdk.pb.NodeProto.NodeData getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<NodeData>
        PARSER = new com.google.protobuf.AbstractParser<NodeData>() {
      @java.lang.Override
      public NodeData parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new NodeData(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<NodeData> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<NodeData> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.nkn.sdk.pb.NodeProto.NodeData getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_pb_NodeData_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_pb_NodeData_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rpb/node.proto\022\002pb\"g\n\010NodeData\022\022\n\npubli" +
      "c_key\030\001 \001(\014\022\026\n\016websocket_port\030\002 \001(\r\022\025\n\rj" +
      "son_rpc_port\030\003 \001(\r\022\030\n\020protocol_version\030\004" +
      " \001(\r*\\\n\tSyncState\022\024\n\020WAIT_FOR_SYNCING\020\000\022" +
      "\020\n\014SYNC_STARTED\020\001\022\021\n\rSYNC_FINISHED\020\002\022\024\n\020" +
      "PERSIST_FINISHED\020\003B\033\n\016org.nkn.sdk.pbB\tNo" +
      "deProtob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_pb_NodeData_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_pb_NodeData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_pb_NodeData_descriptor,
        new java.lang.String[] { "PublicKey", "WebsocketPort", "JsonRpcPort", "ProtocolVersion", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
