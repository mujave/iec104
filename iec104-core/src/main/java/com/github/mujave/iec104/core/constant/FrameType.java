package com.github.mujave.iec104.core.constant;

/**
 * IEC 104 协议帧类型枚举
 * 
 * <p>IEC 104 协议定义了三种帧类型：
 * <ul>
 *   <li><b>I帧 (Information Frame)</b>: 信息帧，用于传输用户数据</li>
 *   <li><b>S帧 (Supervisory Frame)</b>: 监控帧，用于确认接收</li>
 *   <li><b>U帧 (Unnumbered Frame)</b>: 无编号帧，用于控制功能</li>
 * </ul>
 * 
 * <p>帧类型由控制域的最低两位决定：
 * <ul>
 *   <li>I帧: b1[0] == 0 且 b3[0] == 0</li>
 *   <li>S帧: b1[1:0] == 01 且 b3[0] == 0</li>
 *   <li>U帧: b1[1:0] == 11 且 b3[0] == 0</li>
 * </ul>
 */
public enum FrameType {
    
    /** 信息帧 - 用于传输用户数据 */
    I,
    
    /** 监控帧 - 用于确认接收 */
    S,
    
    /** 无编号帧 - 用于控制功能 */
    U,
    
    /** 未知帧类型 */
    UNKNOWN
}