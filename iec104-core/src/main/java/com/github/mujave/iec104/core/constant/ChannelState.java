package com.github.mujave.iec104.core.constant;

/**
 *
 * @author 张雨
 * @date 2023/11/27 14:14
 */
public enum ChannelState {
    /**
     * 注册完成 成功
     */
    INITIALIZE,
    /**
     * 初始化结束,已完成链路初始化
     */
    READY,
    /**
     * 在T3的单位时间内没有发送信息,通道处于空闲状态
     */
    IDLE,
    /**
     * 通道发送的信息发生过无法解析的错误报文
     */
    EXCEPTION;
}
