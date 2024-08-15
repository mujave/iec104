package com.github.mujave.iec104.core.net;

import com.github.mujave.iec104.core.constant.ChannelState;
import io.netty.util.AttributeKey;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 一个tcp连接通道就是一个channel,每个channel可以定义一些
 * 参数,这些参数名就是AttributeKey,这里主要是定义当前channel
 * 的发送序列号和接收序列号
 *
 * @author 张雨
 * @date 2023/11/27 14:10
 */
public class ChannelAttributeKey {
    public static final AttributeKey<ChannelState> STATE_KEY = AttributeKey.valueOf("state");
    public static final AttributeKey<Short> ASDU_ADDRESS = AttributeKey.valueOf("asdu");
    public static final AttributeKey<String> REMOTE_IP = AttributeKey.valueOf("remote-ip");
    public static final AttributeKey<AtomicInteger> S_NO = AttributeKey.valueOf("s");
    public static final AttributeKey<AtomicInteger> R_NO = AttributeKey.valueOf("n");

}
