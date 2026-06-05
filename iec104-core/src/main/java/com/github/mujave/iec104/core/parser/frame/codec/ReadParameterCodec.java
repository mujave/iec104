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

public class ReadParameterCodec extends AbstractInfoElementCodec {

    private final Ti ti;

    public ReadParameterCodec(Ti ti) {
        this.ti = ti;
    }

    @Override
    public List<Ti> getSupportedTis() {
        return Collections.singletonList(ti);
    }

    @Override
    public List<InfoElement> decode(byte[] msg, ASDUFrame frame) throws ParserException {
        if (ti == Ti.C_RD_NA_1) {
            assertLength(msg, 19, "C_RD_NA_1长度不足,需要19字节");
            return Collections.singletonList(
                InfoElement.<Float>builder()
                    .address(parseAddress(msg, 12))
                    .value(Float.intBitsToFloat(ByteUtil.bytesToInt(ArrayUtil.sub(msg, 15, 19))))
                    .build()
            );
        } else if (ti == Ti.C_RD_NA_2) {
            List<InfoElement> elements = new ArrayList<>();
            short number = frame.getNumber();
            int requiredLen = 12 + number * 7;
            assertLength(msg, requiredLen, "C_RD_NA_2长度不足,需要" + requiredLen + "字节,实际" + msg.length + "字节");
            for (int i = 0; i < number; i++) {
                elements.add(InfoElement.<Float>builder()
                    .address(parseAddress(msg, 12 + (7 * i)))
                    .value(Float.intBitsToFloat(ByteUtil.bytesToInt(ArrayUtil.sub(msg, 15 + (7 * i), 19 + (7 * i)))))
                    .build());
            }
            return elements;
        }
        throw new ParserException("不支持的读参数类型: " + ti.name());
    }
}