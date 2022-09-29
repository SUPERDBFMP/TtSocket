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
                .setVersion(ByteString.copyFrom(new byte[1]))
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
    public void methodName() {


    }



}
