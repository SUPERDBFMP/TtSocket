package indi.dbfmp.ttsocket.protocol;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 统一报文类，用户客户端和服务端之间的通信
 *
 * server-client message
 */
@SuperBuilder
@Data
public class ScMessage {

    /**
     * 协议魔数
     */
    public static final byte MAGIC_NUMBER = 0x33;

    /**
     * 协议版本号
     */
    private Byte version = 1;


    /**
     * 指令，消息类型，代表data的数据dto
     */
    private Byte command;

    /**
     * 数据内容
     */
    private ByteBuf data;

}
