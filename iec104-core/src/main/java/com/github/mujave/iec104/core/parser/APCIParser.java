package com.github.mujave.iec104.core.parser;

import cn.hutool.core.util.ByteUtil;
import com.github.mujave.iec104.core.constant.UFrameControlType;
import com.github.mujave.iec104.core.parser.frame.*;

/**
 * APCI解析器
 *
 * @author 张雨
 * @date 2023/11/23 11:20
 */
class APCIParser implements Parser {

    ASDUParser asduParser = new ASDUParser();

    @Override
    public AIec104Frame analysis(byte[] msg) throws ParserException {
        short b_1 = (short) (msg[2] & 0xff);
        short b_3 = (short) (msg[4] & 0xff);
        //根据控制域判断报文类型
        if ((b_1 & 0x01) == 0 && (b_3 & 0x01) == 0) {
            //I帧
            return analysis_I(msg);
        } else if ((b_1 & 0x03) == 1 && (b_3 & 0x01) == 0) {
            //S帧
            return analysis_S(msg);
        } else if ((b_1 & 0x03) == 3 && (b_3 & 0x01) == 0) {
            //U帧
            return analysis_U(msg);
        }
        return null;
    }

    public IIec104Frame analysis_I(byte[] msg) throws ParserException {
        IIec104Frame frame = new IIec104Frame();
        frame.setSendNo((short) (ByteUtil.bytesToShort(new byte[]{msg[2], msg[3]}) >> 1));
        frame.setReceiveNo((short) (ByteUtil.bytesToShort(new byte[]{msg[4], msg[5]}) >> 1));
        //解析ASDU包装
        try {
            ASDUFrame asdu = asduParser.analysis(msg);
            frame.setAsdu(asdu);
            return frame;
        } catch (Exception e) {
            throw new ParserException("解析I帧出现错误");
        }
    }

    public SIec104Frame analysis_S(byte[] msg) {
        short receiveNo = (short) (ByteUtil.bytesToShort(new byte[]{msg[4], msg[5]}) >> 1);
        SIec104Frame frame = new SIec104Frame();
        frame.setReceiveNo(receiveNo);
        return frame;
    }

    public UIec104Frame analysis_U(byte[] msg) {
        UIec104Frame uIec104Frame = new UIec104Frame();
        short b_1 = (short) ((short) (msg[2] & 0xff) >> 2);
        switch (b_1) {
            case 1:
                uIec104Frame.setFunction(UFrameControlType.STARTDT_C);
                break;
            case 2:
                uIec104Frame.setFunction(UFrameControlType.STARTDT_V);
                break;
            case 4:
                uIec104Frame.setFunction(UFrameControlType.STOPDT_C);
                break;
            case 8:
                uIec104Frame.setFunction(UFrameControlType.STOPDT_V);
                break;
            case 16:
                uIec104Frame.setFunction(UFrameControlType.TESTFR_C);
                break;
            case 32:
                uIec104Frame.setFunction(UFrameControlType.TESTFR_V);
                break;
        }
        return uIec104Frame;
    }
}
