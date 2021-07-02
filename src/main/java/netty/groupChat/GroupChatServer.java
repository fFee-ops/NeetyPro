package netty.groupChat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by yazai
 * Date: 上午10:38 2021/7/2
 * Description: 群聊服务端
 */
public class GroupChatServer {
    //端口
    private int port;

    public GroupChatServer(int port) {
        this.port = port;
    }

    //用来处理客户端请求
    public void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)// 设置线程队列得到连接个数
                .childOption(ChannelOption.SO_KEEPALIVE, true)//设置保持活动连接状态
                .childHandler(new ChannelInitializer<SocketChannel>() {//添加handler进行业务处理
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //先获取到pipeline
                        ChannelPipeline pipeline = ch.pipeline();

                        //向pipeline加入handler
                        // 解码器
                        pipeline.addLast("decoder", new StringDecoder());
                        //编码器
                        pipeline.addLast("encoder", new StringEncoder());
                        //自己的业务处理器
                        pipeline.addLast(new GroupChatServerHandler());
                    }
                });

        System.out.println("服务器启动成功");
        ChannelFuture channelFuture = serverBootstrap.bind(port);
        //监听关闭
        channelFuture.channel().closeFuture().sync();

        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

    }

    public static void main(String[] args) throws InterruptedException {
        new GroupChatServer(7000).run();
    }
}
