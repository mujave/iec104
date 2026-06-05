package com.github.mujave.iec104.core.parser.frame;

import cn.hutool.core.util.StrUtil;
import com.github.mujave.iec104.core.constant.Cot;
import com.github.mujave.iec104.core.constant.Ti;
import com.github.mujave.iec104.core.parser.ParserException;
import com.github.mujave.iec104.core.parser.frame.codec.InfoElementCodecFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    private Ti ti;

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

    private Cot cot;

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
                , ti.getName(), ti.name(), sq ? "地址连续" : "地址不连续", number, cot.getName(), cot.name(), t ? "试验" : "未试验", p_n ? "否定确认" : "肯定确认", address);

        StringBuilder body = new StringBuilder();

        for (int i = 1; i <= infoElements.size(); i++) {
            body.append(StrUtil.format("信息元素{}\t{}\n", i, infoElements.get(i - 1).console()));
        }
        if (qos != null) {
            body.append(StrUtil.format("设定命令限定词QOS:{}\n", qos));
        }
        return head + body;
    }


    /**
     * 包装消息元素集
     *
     * @param msg 主站传过来的消息
     */
    public void wrapperInfo(byte[] msg) throws ParserException {
        if (ti == null) {
            throw new ParserException("类型标识符TI为空");
        }
        if (msg == null || msg.length < 12) {
            throw new ParserException("ASDU报文长度不足,至少需要12字节");
        }
        
        infoElements = InfoElementCodecFactory.getCodec(ti).decode(msg, this);
    }
}
