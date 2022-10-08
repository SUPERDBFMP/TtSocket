package indi.dbfmp.ttsocket.common;

import cn.hutool.crypto.symmetric.AES;
import indi.dbfmp.ttsocket.common.util.ScMessageCodec;
import indi.dbfmp.ttsocket.protocol.ClientConnect;
import indi.dbfmp.ttsocket.protocol.ScMessage;
import indi.dbfmp.ttsocket.protocol.SerializationException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.junit.Test;

public class UtilTest {

    private AES aes = new AES("CBC", "PKCS7Padding",
            // 密钥，可以自定义 16位
            "0123456789A55554".getBytes(),
            // iv加盐，按照实际需求添加 16位
            "8799845331111111".getBytes());

    @Test
    public void deserializeReqTest() throws SerializationException {
        ClientConnect.ConnectAuthReq req = ClientConnect.ConnectAuthReq
                .newBuilder()
                .setUserName("ht")
                .setPwd("123")
                .build();
        byte[] data = req.toByteArray();
        byte[] encrypt = aes.encrypt(data);
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(encrypt.length);
        byteBuf.writeBytes(encrypt);
        ScMessage scMessage = ScMessage.builder().command(Byte.parseByte("0")).data(byteBuf).build();
        ClientConnect.ConnectAuthReq o = ScMessageCodec.deserializeReq(scMessage,aes);
        System.out.println(o);
    }

    @Test
    public void serializeDeserializeReqTest() throws SerializationException {
        ClientConnect.ConnectAuthReq req = ClientConnect.ConnectAuthReq
                .newBuilder()
                .setUserName("ht")
                .setPwd("123")
                .build();
        ByteBuf byteBuf = ScMessageCodec.serializeReq(PooledByteBufAllocator.DEFAULT, req,aes);
        int length = byteBuf.readInt();
        System.out.println("数据长度"+length);
        ScMessage scMessage = ScMessageCodec.deserializeToScMessage(byteBuf, Byte.parseByte("1"));
        ClientConnect.ConnectAuthReq o = ScMessageCodec.deserializeReq(scMessage,aes);
        System.out.println(o);

    }

    @Test
    public void serializeDeserializeRespTest() throws SerializationException {
        ClientConnect.ConnectAuthResp connectResp = ClientConnect.ConnectAuthResp
                .newBuilder()
                .setCode(200)
                .setMessage("操作成功")
                .build();
        ByteBuf byteBuf = ScMessageCodec.serializeResp(PooledByteBufAllocator.DEFAULT, connectResp,aes);
        int length = byteBuf.readInt();
        System.out.println("数据长度"+length);
        ScMessage scMessage = ScMessageCodec.deserializeToScMessage(byteBuf, Byte.parseByte("1"));
        ClientConnect.ConnectAuthResp o = ScMessageCodec.deserializeResp(scMessage,aes);
        System.out.println(o);
        System.out.println(o.getMessage());

    }



}
