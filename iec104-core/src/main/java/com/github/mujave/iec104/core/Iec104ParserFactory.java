package com.github.mujave.iec104.core;

import com.github.mujave.iec104.core.parser.APDUParser;
import com.github.mujave.iec104.core.parser.frame.ASDUFrame;

/**
 * iec 104 协议解析器的一个简单工厂
 * 包含解析器入口的创建/管理,信息元素解析场景的配置注册等
 */
public class Iec104ParserFactory {

    public static APDUParser createParser(){
        APDUParser apduParser = new APDUParser();
        return apduParser;
    }

}
