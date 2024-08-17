package com.github.mujave.iec104.core.parser;


import cn.hutool.core.util.ByteUtil;
import com.github.mujave.iec104.core.constant.Cot;
import com.github.mujave.iec104.core.constant.Ti;
import com.github.mujave.iec104.core.parser.frame.*;
import com.github.mujave.iec104.core.parser.infowrapper.IInfoWrapper;

import java.util.HashMap;

/**
 * asdu的解析器
 *
 * @author 张雨
 * @date 2023/11/23 11:17
 */
class ASDUParser implements Parser {

    @Override
    public ASDUFrame analysis(byte[] msg) {
        // 解析ASDU
        Ti ti = Ti.valueOfCode((short) (msg[6] & 0xff));
        ASDUFrame res = ASDUFrame.builder().ti(ti)
                .sq((msg[7] & 0x80) == 128)
                .number((short) (msg[7] & 0x7f))
                .t((msg[8] & 0x80) == 128)
                .p_n((msg[8] & 0x40) == 64)
                .cot(Cot.valueOfCode((short) (msg[8] & 0x3f)))
                .address(ByteUtil.bytesToShort(new byte[]{msg[10], msg[11]})).build();
        //解析封装信息元素,包括每个信息元素的地址和信息值
         res.wrapperInfo(msg);

        return res;
    }

}
