package com.github.mujave.iec104.core.parser.frame.codec;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ByteUtil;
import com.github.mujave.iec104.core.constant.Ti;
import com.github.mujave.iec104.core.parser.ParserException;
import com.github.mujave.iec104.core.parser.frame.AbstractInfoElementCodec;
import com.github.mujave.iec104.core.parser.frame.ASDUFrame;
import com.github.mujave.iec104.core.parser.frame.InfoElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FloatMeasuredValueCodec extends AbstractInfoElementCodec {

    @Override
    public List<Ti> getSupportedTis() {
        return Collections.singletonList(Ti.M_ME_NC_1);
    }

    @Override
    public List<InfoElement> decode(byte[] msg, ASDUFrame frame) throws ParserException {
        List<InfoElement> elements = new ArrayList<>();
        boolean sq = frame.isSq();
        short number = frame.getNumber();

        if (sq) {
            int requiredLen = 15 + number * 5;
            assertLength(msg, requiredLen, "M_ME_NC_1(SQ=true)长度不足,需要" + requiredLen + "字节,实际" + msg.length + "字节");
            int sAddress = parseAddress(msg, 12);
            for (int i = 0; i < number; i++) {
                elements.add(InfoElement.<Float>builder()
                    .address(sAddress + i)
                    .value(Float.intBitsToFloat(ByteUtil.bytesToInt(ArrayUtil.sub(msg, 15 + i * 5, 19 + i * 5))))
                    .qds((short) (msg[19 + i * 5] & 0xff))
                    .build());
            }
        } else {
            int requiredLen = 12 + number * 8;
            assertLength(msg, requiredLen, "M_ME_NC_1(SQ=false)长度不足,需要" + requiredLen + "字节,实际" + msg.length + "字节");
            for (int i = 0; i < number; i++) {
                elements.add(InfoElement.<Float>builder()
                    .address(parseAddress(msg, 12 + (8 * i)))
                    .value(Float.intBitsToFloat(ByteUtil.bytesToInt(ArrayUtil.sub(msg, 15 + i * 8, 19 + i * 8))))
                    .qds((short) (msg[19 + i * 8] & 0xff))
                    .build());
            }
        }
        return elements;
    }
}