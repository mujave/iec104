package com.github.mujave.iec104.core.parser;

import cn.hutool.core.util.ByteUtil;
import com.github.mujave.iec104.core.constant.FrameType;
import com.github.mujave.iec104.core.constant.UFrameControlType;
import com.github.mujave.iec104.core.parser.frame.*;

/**
 * APCI (Application Protocol Control Information) 解析器
 * 
 * <p>APCI 是 IEC 104 协议的控制信息部分，负责帧的传输控制。
 * 根据控制域的不同，APCI 分为三种帧类型：
 * <ul>
 *   <li><b>I帧 (Information Frame)</b>: 信息帧，用于传输用户数据</li>
 *   <li><b>S帧 (Supervisory Frame)</b>: 监控帧，用于确认接收</li>
 *   <li><b>U帧 (Unnumbered Frame)</b>: 无编号帧，用于控制功能</li>
 * </ul>
 *
 * @author 张雨
 * @date 2023/11/23 11:20
 */
class APCIParser implements Parser {

    /** ASDU 解析器实例 */
    ASDUParser asduParser = new ASDUParser();

    /**
     * 根据控制域识别帧类型
     * 
     * <p>帧类型由控制域的最低两位决定：
     * <ul>
     *   <li>I帧: b1[0] == 0 且 b3[0] == 0</li>
     *   <li>S帧: b1[1:0] == 01 且 b3[0] == 0</li>
     *   <li>U帧: b1[1:0] == 11 且 b3[0] == 0</li>
     * </ul>
     * 
     * @param msg 待解析的字节数组（至少需要 6 字节）
     * @return 帧类型枚举值
     */
    public FrameType identifyFrameType(byte[] msg) {
        short b_1 = (short) (msg[2] & 0b11111111);
        short b_3 = (short) (msg[4] & 0b11111111);
        
        if ((b_1 & 0b00000001) == 0 && (b_3 & 0b00000001) == 0) {
            return FrameType.I;
        } else if ((b_1 & 0b00000011) == 1 && (b_3 & 0b00000001) == 0) {
            return FrameType.S;
        } else if ((b_1 & 0b00000011) == 3 && (b_3 & 0b00000001) == 0) {
            return FrameType.U;
        }
        return FrameType.UNKNOWN;
    }

    /**
     * 解析 APCI 控制域，识别帧类型并调用相应的解析方法
     * 
     * @param msg 待解析的字节数组
     * @return 解析后的 IEC 104 帧对象
     * @throws ParserException 当解析 I 帧时发生错误
     */
    @Override
    public AIec104Frame analysis(byte[] msg) throws ParserException {
        FrameType frameType = identifyFrameType(msg);

        // 获取声明长度并验证一致性
        int declaredLen = msg[1] & 0xff;
        
        switch (frameType) {
            case I:
                if (declaredLen < 10) {
                    throw new ParserException("I帧ASDU长度不足,至少需要10字节");
                }
                return analysis_I(msg);
            case S:
                if (declaredLen != 4) {
                    throw new ParserException("S帧长度必须为4字节");
                }
                return analysis_S(msg);
            case U:
                if (declaredLen != 4) {
                    throw new ParserException("U帧长度必须为4字节");
                }
                return analysis_U(msg);
            default:
                return null;
        }
    }

    /**
     * 解析 I 帧（信息帧）
     * 
     * <p>I帧结构：
     * <pre>
     * | SendNo (2字节) | ReceiveNo (2字节) | ASDU (可变长度) |
     *    bit 1-15         bit 1-15
     * </pre>
     * 
     * <p>I帧用于传输用户数据，包含发送序列号和接收序列号用于流量控制。
     * 
     * @param msg 待解析的字节数组
     * @return 解析后的 I 帧对象
     * @throws ParserException 当解析 ASDU 时发生错误
     */
    public IIec104Frame analysis_I(byte[] msg) throws ParserException {
        IIec104Frame frame = new IIec104Frame();
        
        // 解析发送序列号（字节 2-3，去掉最低位）
        frame.setSendNo((short) (ByteUtil.bytesToShort(new byte[]{msg[2], msg[3]}) >> 1));
        
        // 解析接收序列号（字节 4-5，去掉最低位）
        frame.setReceiveNo((short) (ByteUtil.bytesToShort(new byte[]{msg[4], msg[5]}) >> 1));
        
        // 解析 ASDU 部分
        try {
            ASDUFrame asdu = asduParser.analysis(msg);
            frame.setAsdu(asdu);
            return frame;
        } catch (Exception e) {
            throw new ParserException("解析I帧出现错误 (" + e.getMessage() + ")");
        }
    }

    /**
     * 解析 S 帧（监控帧）
     * 
     * <p>S帧结构：
     * <pre>
     * | 000...001 (控制位) | ReceiveNo (2字节) |
     *                          bit 1-15
     * </pre>
     * 
     * <p>S帧用于确认接收，仅包含接收序列号，没有用户数据。
     * 
     * @param msg 待解析的字节数组
     * @return 解析后的 S 帧对象
     */
    public SIec104Frame analysis_S(byte[] msg) {
        // 解析接收序列号（字节 4-5，去掉最低位）
        short receiveNo = (short) (ByteUtil.bytesToShort(new byte[]{msg[4], msg[5]}) >> 1);
        SIec104Frame frame = new SIec104Frame();
        frame.setReceiveNo(receiveNo);
        return frame;
    }

    /**
     * 解析 U 帧（无编号帧）
     * 
     * <p>U帧结构：
     * <pre>
     * | Function Code (bit 2-7) | 11 (控制位) |
     * </pre>
     * 
     * <p>U帧用于传输控制命令，不包含序列号。
     * 功能码映射：
     * <ul>
     *   <li>0x01: STARTDT_C - 启动数据传输命令</li>
     *   <li>0x02: STARTDT_V - 启动数据传输确认</li>
     *   <li>0x04: STOPDT_C - 停止数据传输命令</li>
     *   <li>0x08: STOPDT_V - 停止数据传输确认</li>
     *   <li>0x10: TESTFR_C - 测试帧命令</li>
     *   <li>0x20: TESTFR_V - 测试帧确认</li>
     * </ul>
     * 
     * @param msg 待解析的字节数组
     * @return 解析后的 U 帧对象
     */
    public UIec104Frame analysis_U(byte[] msg) {
        UIec104Frame uIec104Frame = new UIec104Frame();
        
        // 提取功能码（字节 2 右移 2 位）
        short b_1 = (short) ((msg[2] & 0xff) >> 2);
        
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