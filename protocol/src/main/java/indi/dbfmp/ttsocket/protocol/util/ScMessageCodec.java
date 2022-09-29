package indi.dbfmp.ttsocket.protocol.util;

import com.google.protobuf.Message;
import indi.dbfmp.ttsocket.protocol.ClientConnect;
import indi.dbfmp.ttsocket.protocol.ScMessage;
import indi.dbfmp.ttsocket.protocol.SerializationException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.beanutils.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * ScMessage序列化，反序列化工具类
 */
public class ScMessageCodec {

    public static final byte ConnectAuth = 0;

    //command映射
    private static final Map<Byte,ScMsgMapping> scMsgMappingMap = new HashMap<>();
    //req映射
    private static final Map<String,ScMsgMapping> scMsgMappingReqMap = new HashMap<>();
    //resp映射
    private static final Map<String,ScMsgMapping> scMsgMappingRespMap = new HashMap<>();

    //初始化映射map
    static {
        ScMsgMapping[] mappings = ScMsgMapping.values();
        for (ScMsgMapping mapping : mappings) {
            scMsgMappingMap.put(mapping.getCommand(),mapping);
            scMsgMappingReqMap.put(mapping.getReqMsgClass().getSimpleName(),mapping);
            scMsgMappingRespMap.put(mapping.getRespMsgClass().getSimpleName(),mapping);
        }

    }


    /**
     * 序列化响应消息
     * @param allocator byte池
     * @param msg 消息
     * @return byte数据
     * @throws SerializationException 序列化异常
     */
    public static ByteBuf serializeResp(ByteBufAllocator allocator, Message msg) throws SerializationException{
        ScMsgMapping mapping = scMsgMappingRespMap.get(msg.getClass().getSimpleName());
        if (null == mapping) {
            throw new SerializationException("无法获取command消息类型,className:" + msg.getClass().getSimpleName());
        }
        byte[] dataBytes = msg.toByteArray();
        ByteBuf byteBuf = allocator.directBuffer(1 + dataBytes.length);
        byteBuf.writeByte(mapping.getCommand());
        byteBuf.writeBytes(dataBytes);
        return byteBuf;
    }

    @SuppressWarnings("all")
    public static <T> T deserializeReq(ScMessage msg) throws SerializationException{
        ScMsgMapping mapping = scMsgMappingMap.get(msg.getCommand());
        if (null == mapping) {
            throw new SerializationException("无法获取command消息类型,command:" + msg.getCommand());
        }
        Class<? extends Message> reqMsgClass = mapping.getReqMsgClass();
        byte[] data = new byte[msg.getData().readableBytes()];
        msg.getData().readBytes(data);
        try {
            return (T)MethodUtils.invokeStaticMethod(reqMsgClass, "parseFrom", data);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new SerializationException("反射消息对象失败",e);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum ScMsgMapping{

        /**
         * 链接并认证
         */
        CONNECT_AUTH(ConnectAuth,ClientConnect.ConnectAuthReq.class,ClientConnect.ConnectResp.class),
        ;

        private byte command;

        private Class<? extends Message> reqMsgClass;

        private Class<? extends Message> respMsgClass;
    }

}
