package com.github.mujave.iec104.core.parser.frame;

import cn.hutool.core.util.StrUtil;
import com.github.mujave.iec104.core.constant.FrameType;
import com.github.mujave.iec104.core.constant.UFrameControlType;

/**
 * U格式 帧
 *
 * @author 张雨
 * @date 2023/11/23 9:31
 */
public class UIec104Frame extends AIec104Frame {

    private UFrameControlType function;

    public UIec104Frame() {
        this.setFrameType(FrameType.U);
    }

    public UFrameControlType getFunction() {
        return function;
    }

    public void setFunction(UFrameControlType function) {
        this.function = function;
    }

    @Override
    public String console() {
        return StrUtil.format("U帧 功能:{}", function.toString());
    }

}
