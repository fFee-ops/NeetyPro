package netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * Created by yazai
 * Date: 23:35 2021/10/30
 * Description:测试丢弃功能
 */
public class TestThrow {
    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello world",
                CharsetUtil.UTF_8);
        System.out.println("byteBuf的容量为：" + byteBuf.capacity());
        System.out.println("byteBuf的可读容量为：" + byteBuf.readableBytes());
        System.out.println("byteBuf的可写容量为：" + byteBuf.writableBytes());
        while (byteBuf.isReadable()) {
            System.out.println((char) byteBuf.readByte());
        }
        byteBuf.discardReadBytes(); //丢弃已读的字节空间
        System.out.println("byteBuf的容量为：" + byteBuf.capacity());
        System.out.println("byteBuf的可读容量为：" + byteBuf.readableBytes());
        System.out.println("byteBuf的可写容量为：" + byteBuf.writableBytes());
    }

}
