package com.github.mujave.iec104.core.parser.frame;

import cn.hutool.core.util.StrUtil;
import com.github.mujave.iec104.core.constant.FrameType;

/**
 * S格式 帧
 *
 * @author 张雨
 * @date 2023/11/23 9:31
 */
public class SIec104Frame extends AIec104Frame {

    /**
     * 接收序列号
     */
    private short receiveNo;

    public SIec104Frame() {
        this.setFrameType(FrameType.S);
    }

    public short getReceiveNo() {
        return receiveNo;
    }

    public void setReceiveNo(short receiveNo) {
        this.receiveNo = receiveNo;
    }

    @Override
    public String console() {
        return StrUtil.format("S帧 接收序列号:{}", receiveNo);
    }
}
