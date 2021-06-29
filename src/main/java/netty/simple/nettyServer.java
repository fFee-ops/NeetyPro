package netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by yazai
 * Date: 下午2:37 2021/6/29
 */
public class nettyServer {
    public static void main(String[] args) throws InterruptedException {
        //创建 BossGroup 和 WorkerGroup
        //1. 创建两个线程组 bossGroup 和 workerGroup
        //2. bossGroup 只是处理连接请求 , 真正的和客户端业务处理，会交给 workerGroup 完成
        //3. 两个都是无限循环
        //4. bossGroup 和 workerGroup 含有的子线程(NioEventLoop)的个数: 默认实际 cpu 核数 * 2

        NioEventLoopGroup boosGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup(1);

        //用来配置服务器的参数
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        //链式编程来配置参数
        serverBootstrap.group(boosGroup, workGroup)//设置两个线程组
                .channel(NioServerSocketChannel.class) //使用 NioSocketChannel 作为服务器的通道实现
                .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列得到连接个数
                .childOption(ChannelOption.SO_KEEPALIVE, true) //设置保持活动连接状态
                .childHandler(new ChannelInitializer<SocketChannel>() {//创建一个通道测试对象(匿名对象)
                    //给 pipeline 设置处理器
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });

        System.out.println("Server is Ready....");

        //绑定一个端口并且同步, 生成了一个 ChannelFuture 对象
        //启动服务器(并绑定端口)
        ChannelFuture cf = serverBootstrap.bind(6668).sync();
        //对关闭通道进行监听
        cf.channel().closeFuture().sync();

        boosGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
