package indi.dbfmp.ttsocket.protocol;

import com.google.protobuf.ByteString;
import indi.dbfmp.ttsocket.protocol.util.ScMessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.junit.Test;

public class UtilTest {

    @Test
    public void deserializeReqTest() throws SerializationException {
        ClientConnect.ConnectAuthReq req = ClientConnect.ConnectAuthReq
                .newBuilder()
                .setVersion(1)
                .setUserName("ht")
                .setPwd("123")
                .build();
        byte[] data = req.toByteArray();
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(data.length);
        byteBuf.writeBytes(data);
        ScMessage scMessage = ScMessage.builder().command(Byte.parseByte("0")).data(byteBuf).build();
        ClientConnect.ConnectAuthReq o = ScMessageCodec.deserializeReq(scMessage);
        System.out.println(o);
    }

    @Test
    public void serializeDeserializeReqTest() throws SerializationException {
        ClientConnect.ConnectAuthReq req = ClientConnect.ConnectAuthReq
                .newBuilder()
                .setVersion(1)
                .setUserName("ht")
                .setPwd("123")
                .build();
        ByteBuf byteBuf = ScMessageCodec.serializeReq(PooledByteBufAllocator.DEFAULT, req);
        int length = byteBuf.readInt();
        System.out.println("数据长度"+length);
        ScMessage scMessage = ScMessageCodec.deserializeToScMessage(byteBuf, Byte.parseByte("1"));
        ClientConnect.ConnectAuthReq o = ScMessageCodec.deserializeReq(scMessage);
        System.out.println(o);

    }

    @Test
    public void serializeDeserializeRespTest() throws SerializationException {
        ClientConnect.ConnectResp connectResp = ClientConnect.ConnectResp
                .newBuilder()
                .setCode(200)
                .setMessage("操作成功")
                .build();
        ByteBuf byteBuf = ScMessageCodec.serializeResp(PooledByteBufAllocator.DEFAULT, connectResp);
        int length = byteBuf.readInt();
        System.out.println("数据长度"+length);
        ScMessage scMessage = ScMessageCodec.deserializeToScMessage(byteBuf, Byte.parseByte("1"));
        ClientConnect.ConnectResp o = ScMessageCodec.deserializeResp(scMessage);
        System.out.println(o);

    }



}
