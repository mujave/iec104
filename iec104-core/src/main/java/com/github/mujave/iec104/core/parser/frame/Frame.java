package com.github.mujave.iec104.core.parser.frame;

/**
 * 抽象  帧或者帧片段
 *
 * @author 张雨
 * @date 2023/11/23 11:32
 */
public interface Frame {

    /**
     * 在控制台打印报文帧解析内容
     * @return
     */
    String console();
}
