package com.github.mujave.iec104.core.parser.frame.codec;

import cn.hutool.core.util.ByteUtil;
import com.github.mujave.iec104.core.constant.Ti;
import com.github.mujave.iec104.core.parser.ParserException;
import com.github.mujave.iec104.core.parser.frame.AbstractInfoElementCodec;
import com.github.mujave.iec104.core.parser.frame.ASDUFrame;
import com.github.mujave.iec104.core.parser.frame.InfoElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeasuredValueCodec extends AbstractInfoElementCodec {

    private static final List<Ti> SUPPORTED_TIS = Arrays.asList(Ti.M_ME_NA_1, Ti.M_ME_NB_1);

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
            int requiredLen = 15 + number * 3;
            assertLength(msg, requiredLen, "M_ME_NA_1/M_ME_NB_1(SQ=true)长度不足,需要" + requiredLen + "字节,实际" + msg.length + "字节");
            int sAddress = parseAddress(msg, 12);
            for (int i = 0; i < number; i++) {
                elements.add(InfoElement.<Short>builder()
                    .address(sAddress + i)
                    .value(ByteUtil.bytesToShort(new byte[]{msg[15 + i * 3], msg[16 + i * 3]}))
                    .qds((short) (msg[17 + i * 3] & 0xff))
                    .build());
            }
        } else {
            int requiredLen = 12 + number * 6;
            assertLength(msg, requiredLen, "M_ME_NA_1/M_ME_NB_1(SQ=false)长度不足,需要" + requiredLen + "字节,实际" + msg.length + "字节");
            for (int i = 0; i < number; i++) {
                elements.add(InfoElement.<Short>builder()
                    .address(parseAddress(msg, 12 + (6 * i)))
                    .value(ByteUtil.bytesToShort(new byte[]{msg[15 + i * 6], msg[16 + i * 6]}))
                    .qds((short) (msg[17 + i * 6] & 0xff))
                    .build());
            }
        }
        return elements;
    }
}