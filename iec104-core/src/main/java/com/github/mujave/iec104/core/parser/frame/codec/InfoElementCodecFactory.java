package com.github.mujave.iec104.core.parser.frame.codec;

import com.github.mujave.iec104.core.constant.Ti;
import com.github.mujave.iec104.core.parser.ParserException;
import com.github.mujave.iec104.core.parser.frame.InfoElementCodec;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class InfoElementCodecFactory {

    private static final Map<Ti, InfoElementCodec> CODEC_MAP = new EnumMap<>(Ti.class);

    static {
        registerCodec(new CallResetInitCodec());
        registerCodec(new ClockSyncCodec());
        registerCodec(new SingleDoublePointCodec());
        registerCodec(new TimedSingleDoublePointCodec());
        registerCodec(new MeasuredValueCodec());
        registerCodec(new FloatMeasuredValueCodec());
        registerCodec(new TimedFloatMeasuredValueCodec());
        registerCodec(new RemoteControlCodec(Ti.C_SC_NA_1));
        registerCodec(new RemoteControlCodec(Ti.C_DC_NA_1));
        registerCodec(new RemoteControlCodec(Ti.C_RC_NA_1));
        registerCodec(new ReadParameterCodec(Ti.C_RD_NA_1));
        registerCodec(new ReadParameterCodec(Ti.C_RD_NA_2));
        registerCodec(new SetParameterCodec(Ti.C_SE_NB_1));
        registerCodec(new SetParameterCodec(Ti.C_SE_NC_1));
        registerCodec(new SetParameterCodec(Ti.C_SE_NA_2));
    }

    private static void registerCodec(InfoElementCodec codec) {
        for (Ti ti : codec.getSupportedTis()) {
            CODEC_MAP.put(ti, codec);
        }
    }

    public static InfoElementCodec getCodec(Ti ti) throws ParserException {
        InfoElementCodec codec = CODEC_MAP.get(ti);
        if (codec == null) {
            throw new ParserException("无法解析的类型标识符TI: " + (ti != null ? ti.name() : "null"));
        }
        return codec;
    }

    public static void registerCustomCodec(InfoElementCodec codec) {
        registerCodec(codec);
    }

    public static List<Ti> getSupportedTis() {
        return new ArrayList<>(CODEC_MAP.keySet());
    }
}