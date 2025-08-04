package com.github.mujave.iec104.core.parser.frame;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.StrUtil;
import com.github.mujave.iec104.core.constant.Cot;
import com.github.mujave.iec104.core.constant.Ti;
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
    public void wrapperInfo(byte[] msg) {
        infoElements = new ArrayList<>();
        switch (ti) {
            case C_IC_NA_1:
                //召唤命令
            case C_RP_NA_1:
                //复位命令
            case M_EI_NA_1:
                infoElements.add(InfoElement.builder().address(ByteUtil.bytesToInt(new byte[]{msg[12], msg[13], msg[14], 0x00})).qds((short) (msg[15] & 0xff)).build());
                break;
            case C_CS_NA_1:
                infoElements.add(InfoElement.builder().address(ByteUtil.bytesToInt(new byte[]{msg[12], msg[13], msg[14], 0x00})).date(Cp56Time2a.of(ArrayUtil.sub(msg, 15, 22))).build());
                break;
            case M_SP_NA_1:
            case M_DP_NA_1:
                //不带时标的单双点遥信
                if (sq) {
                    //地址连续
                    int sAddress = ByteUtil.bytesToInt(new byte[]{msg[12], msg[13], msg[14], 0x00});
                    for (int i = 0; i < number; i++) {
                        infoElements.add(InfoElement.<Short>builder().address(sAddress + i).value((short) (msg[15 + i] & 0xff)).build());
                    }
                } else {
                    for (int i = 0; i < number; i++) {
                        infoElements.add(InfoElement.<Short>builder()
                                .address(ByteUtil.bytesToInt(new byte[]{msg[12 + (4 * i)], msg[13 + (4 * i)], msg[14 + (4 * i)], 0x00}))
                                .value((short) (msg[15 + (4 * i)] & 0xff))
                                .build());
                    }
                }
                break;
            case M_SP_TB_1:
            case M_DP_TB_1:
                //带时标的单双点遥信
                if (sq) {
                    //按照DL/T 634.5104-2009规定，带长时标的单/双点信息遥信报文并不存在信息元素序列（SQ=1）的情况
                } else {
                    for (int i = 0; i < number; i++) {
                        infoElements.add(InfoElement.<Short>builder()
                                .address(ByteUtil.bytesToInt(new byte[]{msg[12 + (11 * i)], msg[13 + (11 * i)], msg[14 + (11 * i)], 0x00}))
                                .value((short) (msg[15 + (11 * i)] & 0xff))
                                .date(Cp56Time2a.of(ArrayUtil.sub(msg, 16 + i * 11, 23 + i * 11)))
                                .build());
                    }
                }
                break;
            case M_ME_NA_1:
            case M_ME_NB_1:
                if (sq) {
                    //地址连续
                    int sAddress = ByteUtil.bytesToInt(new byte[]{msg[12], msg[13], msg[14], 0x00});
                    for (int i = 0; i < number; i++) {
                        infoElements.add(InfoElement.<Short>builder()
                                .address(sAddress + i)
                                .value(ByteUtil.bytesToShort(new byte[]{msg[15 + i * 3], msg[16 + i * 3]}))
                                .qds((short) (msg[17 + i * 3] & 0xff))
                                .build());
                    }
                } else {
                    for (int i = 0; i < number; i++) {
                        infoElements.add(InfoElement.<Short>builder()
                                .address(ByteUtil.bytesToInt(new byte[]{msg[12 + (6 * i)], msg[13 + (6 * i)], msg[14 + (6 * i)], 0x00}))
                                .value(ByteUtil.bytesToShort(new byte[]{msg[15 + i * 6], msg[16 + i * 6]}))
                                .qds((short) (msg[17 + i * 6] & 0xff))
                                .build());
                    }
                }
                break;
            case M_ME_NC_1:
                if (sq) {
                    //地址连续
                    int sAddress = ByteUtil.bytesToInt(new byte[]{msg[12], msg[13], msg[14], 0x00});
                    for (int i = 0; i < number; i++) {
                        infoElements.add(InfoElement.<Float>builder()
                                .address(sAddress + i)
                                .value(Float.intBitsToFloat(ByteUtil.bytesToInt(ArrayUtil.sub(msg, 15 + i * 5, 18 + i * 5))))
                                .qds((short) (msg[19 + i * 5] & 0xff))
                                .build());
                    }
                } else {
                    for (int i = 0; i < number; i++) {
                        infoElements.add(InfoElement.<Float>builder()
                                .address(ByteUtil.bytesToInt(new byte[]{msg[12 + (8 * i)], msg[13 + (8 * i)], msg[14 + (8 * i)], 0x00}))
                                .value(Float.intBitsToFloat(ByteUtil.bytesToInt(ArrayUtil.sub(msg, 15 + i * 8, 19 + i * 8))))
                                .qds((short) (msg[19 + i * 8] & 0xff))
                                .build());
                    }
                }
                break;
            case M_ME_TF_1:
                if (sq) {
                    //地址连续
                    int sAddress = ByteUtil.bytesToInt(new byte[]{msg[12], msg[13], msg[14], 0x00});
                    for (int i = 0; i < number; i++) {
                        infoElements.add(InfoElement.<Float>builder()
                                .address(sAddress + i)
                                .value(Float.intBitsToFloat(ByteUtil.bytesToInt(ArrayUtil.sub(msg, 15 + i * 12, 19 + i * 12))))
                                .qds((short) (msg[19 + i * 12] & 0xff))
                                .date(Cp56Time2a.of(ArrayUtil.sub(msg, 20 + i * 12, 27 + i * 12)))
                                .build());
                    }
                } else {
                    for (int i = 0; i < number; i++) {
                        infoElements.add(InfoElement.<Float>builder()
                                .address(ByteUtil.bytesToInt(new byte[]{msg[12 + (15 * i)], msg[13 + (15 * i)], msg[14 + (15 * i)], 0x00}))
                                .value(Float.intBitsToFloat(ByteUtil.bytesToInt(ArrayUtil.sub(msg, 15 + i * 15, 19 + i * 15))))
                                .qds((short) (msg[19 + i * 15] & 0xff))
                                .date(Cp56Time2a.of(ArrayUtil.sub(msg, 20 + i * 15, 27 + i * 15)))
                                .build());
                    }
                }
                break;
            case C_SC_NA_1:
                infoElements.add(InfoElement.builder().address(ByteUtil.bytesToInt(new byte[]{msg[12], msg[13], msg[14], 0x00})).value(new SinglePointRemoteControl(msg[15])).build());
                break;
            case C_DC_NA_1:
                infoElements.add(InfoElement.builder().address(ByteUtil.bytesToInt(new byte[]{msg[12], msg[13], msg[14], 0x00})).value(new DoublePointRemoteControl(msg[15])).build());
                break;
            case C_RC_NA_1:
                //遥调
                infoElements.add(InfoElement.builder().address(ByteUtil.bytesToInt(new byte[]{msg[12], msg[13], msg[14], 0x00})).value(new RemoteDebug(msg[15])).build());
                break;
            case C_RD_NA_1:
                //读单个参数命令(参数设置)
                infoElements.add(InfoElement.<Float>builder().address(ByteUtil.bytesToInt(new byte[]{msg[12], msg[13], msg[14], 0x00}))
                        .value(Float.intBitsToFloat(ByteUtil.bytesToInt(ArrayUtil.sub(msg, 15, 19))))
                        .build());
                break;
            case C_RD_NA_2:
                //读多个参数命令(参数设置)
                for (int i = 0; i < number; i++) {
                    infoElements.add(InfoElement.<Float>builder()
                            .address(ByteUtil.bytesToInt(new byte[]{msg[12 + (7 * i)], msg[13 + (7 * i)], msg[14 + (7 * i)], 0x00}))
                            .value(Float.intBitsToFloat(ByteUtil.bytesToInt(ArrayUtil.sub(msg, 15 + (7 * i), 19 + (7 * i)))))
                            .build());
                }
                break;
            case C_SE_NB_1:
                infoElements.add(InfoElement.<Short>builder()
                        .address(ByteUtil.bytesToInt(new byte[]{msg[12], msg[13], msg[14], 0x00}))
                        .value(ByteUtil.bytesToShort(ArrayUtil.sub(msg, 15, 17))).build());
                this.qos = (short) (msg[17] & 0xff);
                break;
            case C_SE_NC_1:
                //预置/激活单个参数命令(参数设置)
                infoElements.add(InfoElement.<Float>builder().address(ByteUtil.bytesToInt(new byte[]{msg[12], msg[13], msg[14], 0x00})).value(Float.intBitsToFloat(ByteUtil.bytesToInt(ArrayUtil.sub(msg, 15, 19)))).build());
                this.qos = (short) (msg[19] & 0xff);
                break;
            case C_SE_NA_2:
                //预置/激活多个参数命令(参数设置)
                for (int i = 0; i < number; i++) {
                    infoElements.add(InfoElement.<Float>builder()
                            .address(ByteUtil.bytesToInt(new byte[]{msg[12 + (7 * i)], msg[13 + (7 * i)], msg[14 + (7 * i)], 0x00}))
                            .value(Float.intBitsToFloat(ByteUtil.bytesToInt(ArrayUtil.sub(msg, 15 + (7 * i), 19 + (7 * i)))))
                            .build());
                }
                this.qos = (short) (msg[msg.length - 1] & 0xff);
                break;
            default:
                log.error("无法解析报文asdu");
                break;
        }
    }
}
