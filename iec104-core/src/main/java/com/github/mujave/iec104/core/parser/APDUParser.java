package com.github.mujave.iec104.core.parser;


import com.github.mujave.iec104.core.parser.frame.AIec104Frame;

/**
 * 104 apdu解析器入口
 * |------------------------APDU-----------------------|
 * |-68H-LEN-|--------APCI--------|--------ASDU--------|
 *
 * @author 张雨
 * @date 2023/11/23 9:12
 */
public class APDUParser implements Parser {

    private APCIParser apciParser ;

    public APDUParser() {
        this.apciParser = new APCIParser();
    }

    @Override
    public AIec104Frame analysis(byte[] msg) throws ParserException {
        //检查启动符是否合法
        if ((msg[0] & 0xff) != 0x68) {
            throw new ParserException("启动符检验错误");
        }
        //检查长度域是否合法
        if ((msg[1] & 0xff) + 2 != msg.length) {
            //这里一般不会有错，因为基于netty处理网络粘包解包
            throw new ParserException("报文长度与Bit2不一致");
        }
        //开始解析apci,也就是控制欲
        AIec104Frame iec104Frame = apciParser.analysis(msg);
        return iec104Frame;
    }
}
