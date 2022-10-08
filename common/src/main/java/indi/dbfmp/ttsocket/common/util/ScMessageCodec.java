package indi.dbfmp.ttsocket.common.util;

import cn.hutool.crypto.symmetric.AES;
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
    public static ByteBuf serializeResp(ByteBufAllocator allocator, Message msg, AES aes) throws SerializationException{
        ScMsgMapping mapping = scMsgMappingRespMap.get(msg.getClass().getSimpleName());
        if (null == mapping) {
            throw new SerializationException("无法获取command消息类型,className:" + msg.getClass().getSimpleName());
        }
        byte[] dataBytes = msg.toByteArray();
        byte[] encryptData = aes.encrypt(dataBytes);
        //加密数据
        ByteBuf byteBuf = allocator.directBuffer(4 + 1 + encryptData.length);
        //消息长度
        byteBuf.writeInt(encryptData.length + 1);
        //消息类型
        byteBuf.writeByte(mapping.getCommand());
        //消息
        byteBuf.writeBytes(encryptData);
        return byteBuf;
    }

    /**
     * 序列化请求消息
     * @param allocator byte池
     * @param msg 消息
     * @return byte数据
     * @throws SerializationException 序列化异常
     */
    public static ByteBuf serializeReq(ByteBufAllocator allocator, Message msg, AES aes) throws SerializationException{
        ScMsgMapping mapping = scMsgMappingReqMap.get(msg.getClass().getSimpleName());
        if (null == mapping) {
            throw new SerializationException("无法获取command消息类型,className:" + msg.getClass().getSimpleName());
        }
        byte[] dataBytes = msg.toByteArray();
        byte[] encryptData = aes.encrypt(dataBytes);
        //加密数据
        ByteBuf byteBuf = allocator.directBuffer(4 + 1 + encryptData.length);
        //消息长度,+1是消息类型长度为1
        byteBuf.writeInt(encryptData.length + 1);
        //消息类型
        byteBuf.writeByte(mapping.getCommand());
        //消息
        byteBuf.writeBytes(encryptData);
        return byteBuf;
    }



    /**
     * 反序列化请求消息
     * @param msg
     * @param <T>
     * @return
     * @throws SerializationException
     */
    @SuppressWarnings("all")
    public static <T> T deserializeReq(ScMessage msg, AES aes) throws SerializationException{
        ScMsgMapping mapping = scMsgMappingMap.get(msg.getCommand());
        if (null == mapping) {
            throw new SerializationException("无法获取command消息类型,command:" + msg.getCommand());
        }
        Class<? extends Message> reqMsgClass = mapping.getReqMsgClass();
        byte[] data = new byte[msg.getData().readableBytes()];
        msg.getData().readBytes(data);
        byte[] decryptData = aes.decrypt(data);
        try {
            return (T)MethodUtils.invokeStaticMethod(reqMsgClass, "parseFrom", decryptData);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new SerializationException("反射消息对象失败",e);
        }
    }

    /**
     * 反序列化响应消息
     * @param msg
     * @param <T>
     * @return
     * @throws SerializationException
     */
    @SuppressWarnings("all")
    public static <T> T deserializeResp(ScMessage msg, AES aes) throws SerializationException{
        ScMsgMapping mapping = scMsgMappingMap.get(msg.getCommand());
        if (null == mapping) {
            throw new SerializationException("无法获取command消息类型,command:" + msg.getCommand());
        }
        Class<? extends Message> reqMsgClass = mapping.getRespMsgClass();
        byte[] data = new byte[msg.getData().readableBytes()];
        msg.getData().readBytes(data);
        byte[] decryptData = aes.decrypt(data);
        try {
            return (T)MethodUtils.invokeStaticMethod(reqMsgClass, "parseFrom", decryptData);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new SerializationException("反射消息对象失败",e);
        }
    }

    /**
     * 反序列化为ScMessage
     * @param buf
     * @param version
     * @return
     */
    public static ScMessage deserializeToScMessage(ByteBuf buf,Byte version) {
        return ScMessage.builder()
                .version(version)
                .command(buf.readByte())
                .data(buf.readBytes(buf.readableBytes()))
                .build();
    }

    @Getter
    @AllArgsConstructor
    public enum ScMsgMapping{

        /**
         * 链接并认证
         */
        CONNECT_AUTH(ConnectAuth,ClientConnect.ConnectAuthReq.class,ClientConnect.ConnectAuthResp.class),
        ;

        private byte command;

        private Class<? extends Message> reqMsgClass;

        private Class<? extends Message> respMsgClass;
    }

}
