package com.github.mujave.iec104.core.parser;


import com.github.mujave.iec104.core.parser.frame.AIec104Frame;

/**
 * IEC 104 协议 APDU 解析器入口
 * 
 * <p>APDU (Application Protocol Data Unit) 是 IEC 104 协议的应用层数据单元，
 * 其结构如下：
 * <pre>
 * |------------------------APDU-----------------------|
 * |-68H-LEN-|--------APCI--------|--------ASDU--------|
 *   2字节     4-6字节            可变长度
 * </pre>
 * 
 * <p>APDU 解析器负责：
 * <ul>
 *   <li>验证启动符（必须为 0x68）</li>
 *   <li>验证报文长度一致性</li>
 *   <li>识别帧类型（I帧/S帧/U帧）</li>
 *   <li>调用 APCI 解析器进行进一步解析</li>
 * </ul>
 *
 * @author 张雨
 * @date 2023/11/23 9:12
 */
public class APDUParser implements Parser {

    /** APCI 解析器实例 */
    private APCIParser apciParser;

    /**
     * 构造函数，初始化 APCI 解析器
     */
    public APDUParser() {
        this.apciParser = new APCIParser();
    }

    /**
     * 解析 APDU 报文
     * 
     * <p>该方法是 APDU 解析的入口，执行以下步骤：
     * <ol>
     *   <li>检查报文是否为空或长度不足（至少 6 字节）</li>
     *   <li>验证启动符是否为 0x68</li>
     *   <li>验证声明长度与实际长度是否一致</li>
     *   <li>验证 APCI 最小长度（至少 4 字节）</li>
     *   <li>根据控制域识别帧类型</li>
     *   <li>验证各类帧的最小长度要求</li>
     *   <li>调用 APCI 解析器解析具体帧</li>
     * </ol>
     * 
     * @param msg 待解析的字节数组
     * @return 解析后的 IEC 104 帧对象
     * @throws ParserException 当报文格式错误或长度不足时抛出
     */
    @Override
    public AIec104Frame analysis(byte[] msg) throws ParserException {
        // 检查报文基本长度
        if (msg == null || msg.length < 6) {
            throw new ParserException("报文长度不足,APDU至少需要6字节");
        }
        
        // 验证启动符
        if ((msg[0] & 0xff) != 0x68) {
            throw new ParserException("启动符检验错误");
        }
        
        // 获取声明长度并验证一致性
        int declaredLen = msg[1] & 0xff;
        if (declaredLen + 2 != msg.length) {
            throw new ParserException("报文长度与Bit2不一致");
        }
        
        // 验证 APCI 最小长度
        if (declaredLen < 4) {
            throw new ParserException("APCI长度不足,至少需要4字节");
        }
        
        // 根据控制域识别帧类型
        short b1 = (short) (msg[2] & 0xff);
        short b3 = (short) (msg[4] & 0xff);
        boolean isIFrame = (b1 & 0x01) == 0 && (b3 & 0x01) == 0;
        boolean isSFrame = (b1 & 0x03) == 1 && (b3 & 0x01) == 0;
        boolean isUFrame = (b1 & 0x03) == 3 && (b3 & 0x01) == 0;
        
        // 验证各类帧的长度要求
        if (isIFrame) {
            if (declaredLen < 10) {
                throw new ParserException("I帧ASDU长度不足,至少需要10字节");
            }
        } else if (isSFrame || isUFrame) {
            if (declaredLen != 4) {
                throw new ParserException("S帧/U帧长度必须为4字节");
            }
        } else {
            throw new ParserException("无法识别的帧类型");
        }
        
        // 调用 APCI 解析器解析具体帧
        AIec104Frame iec104Frame = apciParser.analysis(msg);
        return iec104Frame;
    }
}