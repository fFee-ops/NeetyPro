package netty.groupChat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import sun.jvm.hotspot.tools.SysPropsDumper;

import java.text.SimpleDateFormat;

/**
 * Created by yazai
 * Date: 上午11:26 2021/7/2
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    //创建一个channel组，统一管理所有的channel
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /*
    一旦连接建立后第一个执行的方法：
        我们需要将channel加入到管理中心去,并通知别的客户端，有了新连接
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        //推送讯息

        /*
            该方法会将 channelGroup 中所有的 channel 遍历，并发送 消息，
        我们不需要自己遍历
        */
        channelGroup.writeAndFlush("[ 客 户 端 ]" + channel.remoteAddress() + " 加 入 聊 天 " + sdf.format(new
                java.util.Date()) + " \n");
        channelGroup.add(channel);
    }


    /*
       断开连接：会把xx断开连接推送给别的机器
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 离开了聊天\n");
        System.out.println("剩余存活用户：" + channelGroup.size());
    }


    /*
       表示 channel 处于活动状态, 提示 xx 上线
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 上线了~");
    }

    /*
         表示 channel 处于不活动状态, 提示 xx 离线了
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 离线了~");
    }

    /*
     真正读取数据
     */
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        final Channel channel = ctx.channel();

        channelGroup.forEach((ch) -> {
            if (ch != channel) {//不是本机
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + " 发送了消息：" + msg + "\n");
            } else {//是本机
                ch.writeAndFlush("[自己]发送了消息：" + msg + "\n");
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
