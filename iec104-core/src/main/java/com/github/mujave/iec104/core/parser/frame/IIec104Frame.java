package com.github.mujave.iec104.core.parser.frame;

import cn.hutool.core.util.StrUtil;
import com.github.mujave.iec104.core.constant.FrameType;


/**
 * I格式 帧
 *
 * @author 张雨
 * @date 2023/11/23 9:31
 */
public class IIec104Frame extends AIec104Frame  {

    /**
     * 发送序列号
     */
    private short sendNo;
    /**
     * 接收序列号
     */
    private short receiveNo;

    private ASDUFrame asdu;

    public short getSendNo() {
        return sendNo;
    }

    public void setSendNo(short sendNo) {
        this.sendNo = sendNo;
    }

    public short getReceiveNo() {
        return receiveNo;
    }

    public void setReceiveNo(short receiveNo) {
        this.receiveNo = receiveNo;
    }

    public ASDUFrame getAsdu() {
        return asdu;
    }

    public void setAsdu(ASDUFrame asdu) {
        this.asdu = asdu;
    }

    public IIec104Frame() {
        this.setFrameType(FrameType.I);
    }

    @Override
    public String console() {
        return StrUtil.format("\nI帧 发送序列号:{}  接收序列号:{}\n---------------------------------------------\n{}",
                sendNo, receiveNo,asdu.console());
    }
}
