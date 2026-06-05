package com.github.mujave.iec104.core.parser.frame.codec;

import com.github.mujave.iec104.core.constant.Ti;
import com.github.mujave.iec104.core.parser.ParserException;
import com.github.mujave.iec104.core.parser.frame.AbstractInfoElementCodec;
import com.github.mujave.iec104.core.parser.frame.ASDUFrame;
import com.github.mujave.iec104.core.parser.frame.InfoElement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CallResetInitCodec extends AbstractInfoElementCodec {

    private static final List<Ti> SUPPORTED_TIS = Arrays.asList(Ti.C_IC_NA_1, Ti.C_RP_NA_1, Ti.M_EI_NA_1);

    @Override
    public List<Ti> getSupportedTis() {
        return SUPPORTED_TIS;
    }

    @Override
    public List<InfoElement> decode(byte[] msg, ASDUFrame frame) throws ParserException {
        assertLength(msg, 16, "召唤/复位/初始化结束命令长度不足,需要16字节");
        return Collections.singletonList(
            InfoElement.builder()
                .address(parseAddress(msg, 12))
                .qds((short) (msg[15] & 0xff))
                .build()
        );
    }
}