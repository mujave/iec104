package com.github.mujave.iec104.core.parser.frame;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 信息体包装类
 * @param <T> 信息元素类型
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfoElement<T> implements Frame {

    /**
     * 信息元素地址 必须
     */
    private int address;
    /**
     * 信息元素值 必须
     */
    private T value;
    /**
     * 限定词,不同的传输原因限定词的名称不一样
     */
    private Short qds;
    /**
     * 时标
     */
    private Date date;

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String console() {
        StringBuilder res = new StringBuilder();
        res.append("地址:").append(address);
        if (value != null) {
            res.append("\t值:").append(value.toString());
        }
        if (qds != null) {
            res.append("\t描述词:").append(qds);
        }
        if (date != null) {
            res.append("\t信息元素时标:").append(DateUtil.format(date, DatePattern.ISO8601_PATTERN));
        }
        return res.toString();
    }
}
