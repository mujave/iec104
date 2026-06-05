package com.github.mujave.iec104.core.parser.frame.codec;

import cn.hutool.core.util.ArrayUtil;
import com.github.mujave.iec104.core.constant.Ti;
import com.github.mujave.iec104.core.parser.ParserException;
import com.github.mujave.iec104.core.parser.frame.AbstractInfoElementCodec;
import com.github.mujave.iec104.core.parser.frame.ASDUFrame;
import com.github.mujave.iec104.core.parser.frame.Cp56Time2a;
import com.github.mujave.iec104.core.parser.frame.InfoElement;

import java.util.Collections;
import java.util.List;

public class ClockSyncCodec extends AbstractInfoElementCodec {

    @Override
    public List<Ti> getSupportedTis() {
        return Collections.singletonList(Ti.C_CS_NA_1);
    }

    @Override
    public List<InfoElement> decode(byte[] msg, ASDUFrame frame) throws ParserException {
        assertLength(msg, 22, "时钟同步命令长度不足,需要22字节");
        return Collections.singletonList(
            InfoElement.builder()
                .address(parseAddress(msg, 12))
                .date(Cp56Time2a.of(ArrayUtil.sub(msg, 15, 22)))
                .build()
        );
    }
}