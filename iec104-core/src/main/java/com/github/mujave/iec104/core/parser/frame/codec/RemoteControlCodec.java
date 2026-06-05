package com.github.mujave.iec104.core.parser.frame.codec;

import com.github.mujave.iec104.core.constant.Ti;
import com.github.mujave.iec104.core.parser.ParserException;
import com.github.mujave.iec104.core.parser.frame.AbstractInfoElementCodec;
import com.github.mujave.iec104.core.parser.frame.ASDUFrame;
import com.github.mujave.iec104.core.parser.frame.DoublePointRemoteControl;
import com.github.mujave.iec104.core.parser.frame.InfoElement;
import com.github.mujave.iec104.core.parser.frame.RemoteDebug;
import com.github.mujave.iec104.core.parser.frame.SinglePointRemoteControl;

import java.util.Collections;
import java.util.List;

public class RemoteControlCodec extends AbstractInfoElementCodec {

    private final Ti ti;

    public RemoteControlCodec(Ti ti) {
        this.ti = ti;
    }

    @Override
    public List<Ti> getSupportedTis() {
        return Collections.singletonList(ti);
    }

    @Override
    public List<InfoElement> decode(byte[] msg, ASDUFrame frame) throws ParserException {
        assertLength(msg, 16, ti.name() + "长度不足,需要16字节");
        
        Object value;
        switch (ti) {
            case C_SC_NA_1:
                value = new SinglePointRemoteControl(msg[15]);
                break;
            case C_DC_NA_1:
                value = new DoublePointRemoteControl(msg[15]);
                break;
            case C_RC_NA_1:
                value = new RemoteDebug(msg[15]);
                break;
            default:
                throw new ParserException("不支持的遥控类型: " + ti.name());
        }
        
        return Collections.singletonList(
            InfoElement.builder()
                .address(parseAddress(msg, 12))
                .value(value)
                .build()
        );
    }
}