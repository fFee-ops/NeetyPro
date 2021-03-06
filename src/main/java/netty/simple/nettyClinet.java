package netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by yazai
 * Date: 下午3:14 2021/6/29
 */
public class nettyClinet {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();

        //创建客户端启动对象
        //注意客户端使用的不是 ServerBootstrap 而是 Bootstrap
        Bootstrap bootstrap = new Bootstrap();
        //设置相关参数
        bootstrap.group(group) //设置线程组
                .channel(NioSocketChannel.class) // 设置客户端通道的实现类(反射)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyClientHandler()); //加入自己的处理器
                    }
                });
        System.out.println("客户端 准备完成...");

        //启动客户端去连接服务器端
        //关于 ChannelFuture 要分析，涉及到 netty 的异步模型
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668).sync();
        //给关闭通道进行监听
        channelFuture.channel().closeFuture().sync();
        group.shutdownGracefully();
    }
}
