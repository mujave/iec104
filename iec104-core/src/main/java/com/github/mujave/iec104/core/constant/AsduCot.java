package com.github.mujave.iec104.core.constant;

/**
 * 传送原因枚举
 */
public enum   AsduCot {
    NO_USE(0x00, "未用"),
    PER_CYC(0x01, "周期、循环"),
    BACK(0x02, "背景扫描"),
    SPONT(0x03, "突发(自发)"),
    START_ACT(0x04, "初始化完成"),
    REQ(0x05, "请求或者被请求"),
    ACT(0x06, "激活"),
    ACTCON(0x07, "激活确认"),
    DEACT(0x08, "停止激活"),
    DEACTCON(0x09, "停止激活确认"),
    ACTTERM(0x0a, "激活终止"),
    FILE_TRANS(0x0d, "文件传输"),
    INTROGEN(0x14, "响应站召唤");

    private short code;
    private String name;

    AsduCot(int code, String name) {
        this.code = (short) code;
        this.name = name;
    }

    public static AsduCot valueOfCode(short code) {
        for (AsduCot value : AsduCot.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
