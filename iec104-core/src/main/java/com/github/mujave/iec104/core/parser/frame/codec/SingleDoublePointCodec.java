package com.github.mujave.iec104.core.parser.frame.codec;

import com.github.mujave.iec104.core.constant.Ti;
import com.github.mujave.iec104.core.parser.ParserException;
import com.github.mujave.iec104.core.parser.frame.AbstractInfoElementCodec;
import com.github.mujave.iec104.core.parser.frame.ASDUFrame;
import com.github.mujave.iec104.core.parser.frame.InfoElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SingleDoublePointCodec extends AbstractInfoElementCodec {

    private static final List<Ti> SUPPORTED_TIS = Arrays.asList(Ti.M_SP_NA_1, Ti.M_DP_NA_1);

    @Override
    public List<Ti> getSupportedTis() {
        return SUPPORTED_TIS;
    }

    @Override
    public List<InfoElement> decode(byte[] msg, ASDUFrame frame) throws ParserException {
        List<InfoElement> elements = new ArrayList<>();
        boolean sq = frame.isSq();
        short number = frame.getNumber();

        if (sq) {
            int requiredLen = 15 + number;
            assertLength(msg, requiredLen, "M_SP_NA_1/M_DP_NA_1(SQ=true)长度不足,需要" + requiredLen + "字节,实际" + msg.length + "字节");
            int sAddress = parseAddress(msg, 12);
            for (int i = 0; i < number; i++) {
                elements.add(InfoElement.<Short>builder()
                    .address(sAddress + i)
                    .value((short) (msg[15 + i] & 0xff))
                    .build());
            }
        } else {
            int requiredLen = 12 + number * 4;
            assertLength(msg, requiredLen, "M_SP_NA_1/M_DP_NA_1(SQ=false)长度不足,需要" + requiredLen + "字节,实际" + msg.length + "字节");
            for (int i = 0; i < number; i++) {
                elements.add(InfoElement.<Short>builder()
                    .address(parseAddress(msg, 12 + (4 * i)))
                    .value((short) (msg[15 + (4 * i)] & 0xff))
                    .build());
            }
        }
        return elements;
    }
}