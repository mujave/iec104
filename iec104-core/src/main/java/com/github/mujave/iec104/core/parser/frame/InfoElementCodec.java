package com.github.mujave.iec104.core.parser.frame;

import com.github.mujave.iec104.core.constant.Ti;
import com.github.mujave.iec104.core.parser.ParserException;

import java.util.List;

public interface InfoElementCodec {

    Ti getTi();

    List<Ti> getSupportedTis();

    List<InfoElement> decode(byte[] msg, ASDUFrame frame) throws ParserException;

    byte[] encode(ASDUFrame frame);
}