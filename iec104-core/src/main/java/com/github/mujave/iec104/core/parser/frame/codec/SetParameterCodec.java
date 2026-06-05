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

public class SetParameterCodec extends AbstractInfoElementCodec {

    private final Ti ti;

    public SetParameterCodec(Ti ti) {
        this.ti = ti;
    }

    @Override
    public List<Ti> getSupportedTis() {
        return Collections.singletonList(ti);
    }

    @Override
    public List<InfoElement> decode(byte[] msg, ASDUFrame frame) throws ParserException {
        switch (ti) {
            case C_SE_NB_1:
                assertLength(msg, 18, "C_SE_NB_1长度不足,需要18字节");
                frame.setQos((short) (msg[17] & 0xff));
                return Collections.singletonList(
                    InfoElement.<Short>builder()
                        .address(parseAddress(msg, 12))
                        .value(ByteUtil.bytesToShort(ArrayUtil.sub(msg, 15, 17)))
                        .build()
                );
            case C_SE_NC_1:
                assertLength(msg, 20, "C_SE_NC_1长度不足,需要20字节");
                frame.setQos((short) (msg[19] & 0xff));
                return Collections.singletonList(
                    InfoElement.<Float>builder()
                        .address(parseAddress(msg, 12))
                        .value(Float.intBitsToFloat(ByteUtil.bytesToInt(ArrayUtil.sub(msg, 15, 19))))
                        .build()
                );
            case C_SE_NA_2: {
                List<InfoElement> elements = new ArrayList<>();
                short number = frame.getNumber();
                int requiredLen = 12 + number * 7;
                assertLength(msg, requiredLen, "C_SE_NA_2长度不足,需要" + requiredLen + "字节,实际" + msg.length + "字节");
                for (int i = 0; i < number; i++) {
                    elements.add(InfoElement.<Float>builder()
                        .address(parseAddress(msg, 12 + (7 * i)))
                        .value(Float.intBitsToFloat(ByteUtil.bytesToInt(ArrayUtil.sub(msg, 15 + (7 * i), 19 + (7 * i)))))
                        .build());
                }
                frame.setQos((short) (msg[msg.length - 1] & 0xff));
                return elements;
            }
            default:
                throw new ParserException("不支持的设置参数类型: " + ti.name());
        }
    }
}