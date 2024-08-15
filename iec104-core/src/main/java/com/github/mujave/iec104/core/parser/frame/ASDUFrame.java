package com.github.mujave.iec104.core.parser.frame;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.StrUtil;
import com.github.mujave.iec104.core.constant.AsduCot;
import com.github.mujave.iec104.core.constant.AsduTi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张雨
 * @date 2023/11/23 14:44
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Slf4j
public class ASDUFrame implements Frame {

    /**
     * 类型标识符
     */
    private AsduTi ti;

    /**
     * 可变结构限定词 sq
     * true - sq=1 地址连续
     * false - sq=0 地址不连续
     */
    private boolean sq;

    /**
     * 可变结构限定词 信息元素个数
     */
    private short number;

    private boolean t;

    private boolean p_n;

    private AsduCot cot;

    private short address;

    private List<InfoElement> infoElements;

    private Short qos;

    /**
     * 组装控制台打印
     *
     * @return
     */
    @Override
    public String console() {
        String head = StrUtil.format("  类型标识符:{}({})\n" + "  可变结构限定词:sq={}\t信息元素个数={}\n" + "  传输原因:{}({})\tT={}\tP/N={}\tASDU公共地址:{}\n" +
                        "---------------------------------------------\n"
                , ti.getName(), ti.name(), sq ? "地址连续" : "地址不连续", number, cot.getName(), cot.name(), t ? "未试验" : "试验", p_n ? "肯定确认" : "否定确认", address);

        StringBuilder body = new StringBuilder();

        for (int i = 1; i <= infoElements.size(); i++) {
            body.append(StrUtil.format("信息元素{}\t{}\n", i, infoElements.get(i - 1).console()));
        }
        if (qos != null) {
            body.append(StrUtil.format("设定命令限定词QOS:{}\n", qos));
        }
        return head + body;
    }
}
