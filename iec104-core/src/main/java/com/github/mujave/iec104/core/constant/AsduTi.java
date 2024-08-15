package com.github.mujave.iec104.core.constant;

/**
 * 类型标识符
 */
public enum AsduTi {

    //------监视方向的过程信息------
    M_SP_NA_1(1, "(遥信)单点信息"),
    M_DP_NA_1(3, "(遥信)双点信息"),
    M_ME_NA_1(9, "(遥测)归一化值,带品质描述词"),
    M_ME_NB_1(11, "(遥测)标度化值,带品质描述词"),
    M_ME_NC_1(13, "(遥测)短浮点数,带品质描述词"),
    M_ME_TF_1(36, "(遥测)短浮点数,带品质描述词,带CP56Time2a时标"),
    M_SP_TB_1(30, "(遥信)单点信息,带CP56Time2a时标"),
    M_DP_TB_1(31, "(遥信)双点信息,带CP56Time2a时标"),

    //------控制方向的过程信息------
    C_SC_NA_1(0x2D, "(遥控)单命令"),
    C_DC_NA_1(0x2E, "(遥控)双命令"),
    C_RC_NA_1(0x2F, "升降档命令"),
    C_RD_NA_1(0x66, "读单个参数命令(参数设置)"),
    C_RD_NA_2(0x84, "读多个参数命令(参数设置)"),
    C_SE_NA_1(0x30, "遥调设点,归一化(参数设置)"),
    C_SE_NB_1(0x31, "遥调设点,标度化(参数设置)"),
    C_SE_NC_1(0x32, "遥调设点,短浮点数(参数设置)"),
    C_SE_NA_2(0x88, "预置/激活多个参数命令(参数设置)"),

    //------监视方向的系统信息------
    M_EI_NA_1(0x46, "初始化结束"),

    //------控制方向的系统信息------
    C_IC_NA_1(0x64, "召唤命令"),
    C_CS_NA_1(0x67, "时钟同步/读取命令"),
    C_RP_NA_1(0x69, "复位进程命令"),

    //------控制方向的参数------
    P_ME_NA_1(110,"测量值参数,归一化数"),
    P_ME_NB_1(111,"测量值参数,标度化数"),
    P_ME_NC_1(112,"测量值参数,短浮点数"),
    P_AC_NA_1(113,"参数激活"),

    //------文件传输------
    F_FR_NA_1(120, "文件已准备好"),
    F_SR_NA_1(121, "节已准备好"),
    F_SC_NA_1(122, "召唤目录/选择文件/召唤文件/召唤节"),
    F_LS_NA_1(123, "最后的节/最后的段"),
    F_AF_NA_1(124, "确认文件/确认节"),
    F_SG_NA_1(125, "段"),
    F_DR_TA_1(126, "目录"),
    F_SC_NB_1(127, "日志查询-请求存档文件"),


    ;


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
