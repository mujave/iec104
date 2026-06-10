package com.github.mujave.iec104.core.parser.frame;

import cn.hutool.core.util.ByteUtil;
import com.github.mujave.iec104.core.constant.Ti;
import com.github.mujave.iec104.core.parser.ParserException;

import java.util.List;

public abstract class AbstractInfoElementCodec implements InfoElementCodec {

    protected void assertLength(byte[] msg, int minLength, String errorMsg) throws ParserException {
        if (msg.length < minLength) {
            throw new ParserException(errorMsg);
        }
    }

    protected int parseAddress(byte[] msg, int offset) {
        return ByteUtil.bytesToInt(new byte[]{msg[offset], msg[offset + 1], msg[offset + 2], 0x00});
    }

    @Override
    public Ti getTi() {
        List<Ti> supportedTis = getSupportedTis();
        return supportedTis.isEmpty() ? null : supportedTis.get(0);
    }

    @Override
    public byte[] encode(ASDUFrame frame) {
        throw new UnsupportedOperationException("Encode not implemented for " + getClass().getSimpleName());
    }
}