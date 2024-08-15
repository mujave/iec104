package com.github.mujave.iec104.core.parser;

import com.github.mujave.iec104.core.parser.frame.Frame;

/**
 * 解析器实现接口的抽象
 *
 * @author 张雨
 */
public interface Parser {

    /**
     * 104解析
     * @param msg 报文字节数组
     * @return
     */
    Frame analysis(byte[] msg) throws ParserException;
}
