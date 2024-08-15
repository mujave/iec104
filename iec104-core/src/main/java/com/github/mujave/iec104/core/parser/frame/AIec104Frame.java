package com.github.mujave.iec104.core.parser.frame;


import com.github.mujave.iec104.core.constant.FrameType;

/**
 * 104帧的抽象,这里只定义了帧类型
 * @author 张雨
 * @date 2023/11/23 9:27
 */
public abstract class AIec104Frame implements Frame{

    private FrameType frameType;

    public FrameType getFrameType() {
        return frameType;
    }

    public void setFrameType(FrameType frameType) {
        this.frameType = frameType;
    }
}
