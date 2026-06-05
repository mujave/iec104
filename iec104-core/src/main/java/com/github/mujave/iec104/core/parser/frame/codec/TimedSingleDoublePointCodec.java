package com.github.mujave.iec104.core.parser.frame.codec;

import cn.hutool.core.util.ArrayUtil;
import com.github.mujave.iec104.core.constant.Ti;
import com.github.mujave.iec104.core.parser.ParserException;
import com.github.mujave.iec104.core.parser.frame.AbstractInfoElementCodec;
import com.github.mujave.iec104.core.parser.frame.ASDUFrame;
import com.github.mujave.iec104.core.parser.frame.Cp56Time2a;
import com.github.mujave.iec104.core.parser.frame.InfoElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimedSingleDoublePointCodec extends AbstractInfoElementCodec {

    private static final List<Ti> SUPPORTED_TIS = Arrays.asList(Ti.M_SP_TB_1, Ti.M_DP_TB_1);

    @Override
    public List<Ti> getSupportedTis() {
        return SUPPORTED_TIS;
    }

    @Override
    public List<InfoElement> decode(byte[] msg, ASDUFrame frame) throws ParserException {
        if (frame.isSq()) {
            throw new ParserException("带时标的单/双点遥信不支持SQ=1（地址连续）");
        }

        List<InfoElement> elements = new ArrayList<>();
        short number = frame.getNumber();
        int requiredLen = 12 + number * 11;
        assertLength(msg, requiredLen, "M_SP_TB_1/M_DP_TB_1长度不足,需要" + requiredLen + "字节,实际" + msg.length + "字节");

        for (int i = 0; i < number; i++) {
            elements.add(InfoElement.<Short>builder()
                .address(parseAddress(msg, 12 + (11 * i)))
                .value((short) (msg[15 + (11 * i)] & 0xff))
                .date(Cp56Time2a.of(ArrayUtil.sub(msg, 16 + i * 11, 23 + i * 11)))
                .build());
        }
        return elements;
    }
}