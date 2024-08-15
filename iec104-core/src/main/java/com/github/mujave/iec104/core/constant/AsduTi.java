package com.github.mujave.iec104.core.constant;

/**
 * 类型标识符
 */
public enum AsduTi {
    M_SP_NA_1(0x01, "（遥信）单点信息"),
    M_DP_NA_1(0x03, "（遥信）双点信息"),
    M_ME_NA_1(0x09, "（遥测）归一化值,带品质描述词"),
    M_ME_NB_1(0x0B, "（遥测）标度化值,带品质描述词"),
    M_ME_NC_1(0x0D, "（遥测）短浮点数,带品质描述词"),
    M_ME_TF_1(0x24, "（遥测）短浮点数,带品质描述词,带CP56Time2a时标"),
    M_SP_TB_1(0x1E, "（遥信）单点信息,带CP56Time2a时标"),
    M_DP_TB_1(0x1F, "（遥信）双点信息,带CP56Time2a时标"),
    C_SC_NA_1(0x2D, "（遥控）单命令"),
    C_DC_NA_1(0x2E, "（遥控）双命令"),
    C_RC_NA_1(0x2F, "升降档命令"),
    C_RD_NA_1(0x66, "读单个参数命令(参数设置)"),
    C_RD_NA_2(0x84, "读多个参数命令(参数设置)"),
    C_SE_NA_1(0x30, "遥调设点,归一化(参数设置)"),
    C_SE_NB_1(0x31, "遥调设点,标度化(参数设置)"),
    C_SE_NC_1(0x32, "遥调设点,短浮点数(参数设置)"),
    C_SE_NA_2(0x88, "预置/激活多个参数命令(参数设置)"),
    M_EI_NA_1(0x46, "初始化结束"),
    C_IC_NA_1(0x64, "召唤命令"),
    C_CS_NA_1(0x67, "时钟同步/读取命令"),
    C_RP_NA_1(0x69, "复位进程命令");


    private short code;
    private String name;

    AsduTi(int code, String name) {
        this.code = (short) code;
        this.name = name;
    }

    public static AsduTi valueOfCode(short code) {
        for (AsduTi value : AsduTi.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public short getCode() {
        return code;
    }

    public String getCodeHex() {
        return String.format("%02X", code);
    }
}
