package com.github.mujave.iec104.core.net;

import cn.hutool.core.util.ReflectUtil;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 通道处理,相当于server关于通信的一个配置
 *
 * @author 张雨
 * @date 2023/11/22 15:14
 */
public class Iec104NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 通道读取超时时间t3,默认20秒
     * <p>
     * 未使用但已打开的连接可通过发送测试APDU（TESTFR=act）并由接收站发送TESTFR=con，在两个方向上进行周期性测试。发送站和接收站在规定时间段内没有数据传输（超过时间t3）均可启动测试过程。每接收一帧（I帧、S帧或U帧）重新启动定时器t3。
     * </p><p>发送站和接收站应独立地监视连接,一旦收到对方发送过来的测试帧，就必须回答测试确认，而且本方就不需要再发送测试帧。
     * </p><p>当连接长时间缺乏活动性，又需要确保不断时，测试过程也可以在“激活”的连接上启动。“激活”状态是指当连接建立后，通过STARTDT来启动该连接上来自于被控站的数据传送。
     * </p>
     */
    private int t3;

    private Class<? extends ChannelHandlerAdapter> handlerClass;

    /**
     * @param t3           通道读取超时时间t3,有效值为一个大于0的时间间隔量,单位秒
     * @param handlerClass 消息处理适配器类型
     */
    public Iec104NettyChannelInitializer(int t3, Class<? extends ChannelHandlerAdapter> handlerClass) {
        this.t3 = t3;
        this.handlerClass = handlerClass;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // 日志打印
        channel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        // 粘包拆包处理,报文最大长度为255字节,长度标识占用一个字节并且位于第二字节所以就是偏移一个字节之后开始的下一个字节就是长度
        channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(255, 1, 1));
        // 解码转String，注意调整自己的编码格式GBK、UTF-8
        channel.pipeline().addLast(new ByteArrayDecoder());
        // 解码转String，注意调整自己的编码格式GBK、UTF-8
        channel.pipeline().addLast(new ByteArrayEncoder());
        // 在管道中添加我们自己的接收数据实现方法
        if (t3 > 0) {
            channel.pipeline().addLast(new IdleStateHandler(t3, 0, 0, TimeUnit.SECONDS));
        }
        channel.pipeline().addLast(ReflectUtil.newInstance(handlerClass));
    }
}
