package com.github.mujave.iec104.core.parser.frame;

import cn.hutool.core.util.StrUtil;

/**
 * 双点遥控信息
 */
public class DoublePointRemoteControl implements Frame {

    /**
     * 命令类型
     * 0.遥控执行命令
     * 1.遥控选择命令
     */
    private boolean type;

    /**
     * 开关状态
     * 0.不允许，有错误
     * 1.开关分
     * 2.开关合
     * 3.不允许，有错误
     */
    private int statu;

    public DoublePointRemoteControl(boolean type, int statu) {
        this.type = type;
        this.statu = statu;
    }

    public DoublePointRemoteControl(byte data) {
        int d = data & 0xff;
        this.type = (d & 0x80) == 128;
        this.statu = (d & 0x03);
    }

    @Override
    public String console() {
        return StrUtil.format("遥控双点对象: {}   {}",
                type ? "遥控选择指令" : "遥控执行指令",
                getStatu(this.statu)
        );
    }

    @Override
    public String toString() {
        return this.console();
    }

    public String getStatuStr() {
        return getStatu(this.statu);
    }

    private String getStatu(int statu) {
        switch (statu) {
            case 1:
                return "开关分";
            case 2:
                return "开关合";
            case 3:
            case 0:
                return "不允许，有错误";
        }
        return "";
    }

    public String getHexStr(){
        return getHexStr(type,statu);
    }

    public String getHexStr(boolean type,int statu){
        if (type){
            return String.format("%02X",128+statu);
        }
        return String.format("%02X",statu);
    }

    public boolean isType() {
        return type;
    }

    public int getStatu() {
        return statu;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
