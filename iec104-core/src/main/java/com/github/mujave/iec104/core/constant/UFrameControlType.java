package com.github.mujave.iec104.core.constant;

/**
 * U帧控制域类型
 *  _C:命令 _V:确认
 * @author 张雨
 * @date 2023/11/23 10:16
 */
public enum UFrameControlType {
    TESTFR_C, STOPDT_C, STARTDT_C,
    TESTFR_V, STOPDT_V, STARTDT_V;
}
