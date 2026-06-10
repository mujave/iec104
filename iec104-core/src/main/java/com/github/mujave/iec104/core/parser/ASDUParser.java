package com.github.mujave.iec104.core.parser;


import cn.hutool.core.util.ByteUtil;
import com.github.mujave.iec104.core.constant.Cot;
import com.github.mujave.iec104.core.constant.Ti;
import com.github.mujave.iec104.core.parser.frame.ASDUFrame;

/**
 * ASDU 解析器
 * 
 * <p>ASDU 是 IEC 104 协议的应用服务数据单元，包含实际的用户数据。
 * ASDU 结构如下：
 * <pre>
 * |   TI (1)   |   SQ+NUM (1)   |  COT (1)  |  T+P/N (1)  |  ADDR (2)  | InfoElements |
 *   类型标识符     可变结构限定词    传输原因     测试/确认位     公共地址      信息元素
 * </pre>
 * 
 * <p>ASDU 解析器负责：
 * <ul>
 *   <li>解析类型标识符 (TI)</li>
 *   <li>解析可变结构限定词 (SQ/NUM)</li>
 *   <li>解析传输原因 (COT)</li>
 *   <li>解析测试位 (T) 和确认位 (P/N)</li>
 *   <li>解析公共地址</li>
 *   <li>调用相应的信息元素编解码器解析信息元素</li>
 * </ul>
 *
 * @author 张雨
 * @date 2023/11/23 11:17
 */
class ASDUParser implements Parser {

    /**
     * 解析 ASDU 报文
     * 
     * <p>该方法执行以下步骤：
     * <ol>
     *   <li>验证报文长度（至少 12 字节）</li>
     *   <li>解析类型标识符 (TI) - 字节 6</li>
     *   <li>解析传输原因 (COT) - 字节 8 的低 6 位</li>
     *   <li>解析可变结构限定词 (SQ/NUM) - 字节 7</li>
     *   <li>解析测试位 (T) - 字节 8 的 bit 7</li>
     *   <li>解析确认位 (P/N) - 字节 8 的 bit 6</li>
     *   <li>解析公共地址 - 字节 10-11</li>
     *   <li>构建 ASDUFrame 对象</li>
     *   <li>调用 wrapperInfo 解析信息元素</li>
     * </ol>
     * 
     * @param msg 待解析的字节数组
     * @return 解析后的 ASDU 帧对象
     * @throws ParserException 当 TI 或 COT 无法识别时抛出
     */
    @Override
    public ASDUFrame analysis(byte[] msg) throws ParserException {
        // 验证报文最小长度
        if (msg == null || msg.length < 12) {
            throw new ParserException("ASDU报文长度不足");
        }
        
        // 解析类型标识符 (TI)
        Ti ti = Ti.valueOfCode((short) (msg[6] & 0xff));
        if (ti == null) {
            throw new ParserException("无法识别的类型标识符TI: " + (msg[6] & 0xff));
        }
        
        // 解析传输原因 (COT) - 取低 6 位
        Cot cot = Cot.valueOfCode((short) (msg[8] & 0x3f));
        if (cot == null) {
            throw new ParserException("无法识别的传送原因COT: " + (msg[8] & 0x3f));
        }
        
        // 构建 ASDUFrame 对象
        ASDUFrame res = ASDUFrame.builder()
                .ti(ti)                                    // 类型标识符
                .sq((msg[7] & 0x80) == 128)               // SQ: 地址连续标志 (bit 7)
                .number((short) (msg[7] & 0x7f))           // NUM: 信息元素个数 (bit 0-6)
                .t((msg[8] & 0x80) == 128)                // T: 测试位 (bit 7)
                .p_n((msg[8] & 0x40) == 64)               // P/N: 确认位 (bit 6)
                .cot(cot)                                 // 传输原因
                .address(ByteUtil.bytesToShort(new byte[]{msg[10], msg[11]}))  // 公共地址
                .build();
        
        // 解析信息元素
        res.wrapperInfo(msg);

        return res;
    }

}