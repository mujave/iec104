package com.github.mujave.iec104.core.parser.frame;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * CP56Time2a 格式时标
 * <p>
 * CP56time2a是101/104规约中定义的时间格式，101和104是电力通信规约的一种，2000年左右进行了修订，现在使用的104规约应该就是2000年的版本。
 * </p>
 * @author mujave
 */
public class Cp56Time2a extends DateTime implements Frame {


    public static Cp56Time2a of(long timeMillis) {
        return new Cp56Time2a(timeMillis);
    }

    public static Cp56Time2a of(Date date) {
        return new Cp56Time2a(date);
    }

    public static Cp56Time2a of(byte[] bytes) {
        return new Cp56Time2a(bytes);
    }

    public static Cp56Time2a of(String dateStr, String format) {
        return of(DateUtil.parse(dateStr, format));
    }

    public Cp56Time2a() {
        this(TimeZone.getDefault());
    }

    public Cp56Time2a(byte[] bytes) {
        this.setField(DateField.YEAR, 2000+(bytes[6] & 0x7F) );
        this.setField(DateField.MONTH, (bytes[5] & 0x0F)-1 );
        this.setField(DateField.DAY_OF_MONTH, bytes[4] & 0x1F);
        this.setField(DateField.HOUR_OF_DAY, bytes[3] & 0x1F);
        this.setField(DateField.MINUTE, bytes[2] & 0x3F);
        int milliseconds =  ((bytes[1] & 0xff) << 8 )+ (bytes[0] & 0xff);
        this.setField(DateField.SECOND, milliseconds / 1000);
        this.setField(DateField.MILLISECOND, milliseconds % 1000);
        super.setTimeZone(TimeZone.getDefault());
    }

    public Cp56Time2a(TimeZone timeZone) {
        this(System.currentTimeMillis(), timeZone);
    }

    public Cp56Time2a(Date date) {
        this(date, TimeZone.getDefault());
    }

    public Cp56Time2a(Date date, TimeZone timeZone) {
        this(((Date) ObjectUtil.defaultIfNull(date, new Date())).getTime(), timeZone);
    }

    public Cp56Time2a(long timeMillis) {
        this(timeMillis, TimeZone.getDefault());
    }

    public Cp56Time2a(long timeMillis, TimeZone timeZone) {
        super(timeMillis);
        super.setTimeZone((TimeZone) ObjectUtil.defaultIfNull(timeZone, TimeZone::getDefault));
    }

    /**
     * 时间转16进制字符串
     *
     * @return 16进制字符串
     */
    public String toHexStr() {
        StringBuilder builder = new StringBuilder();
        String milliSecond = String.format("%04X", (this.second() * 1000) + this.millisecond());
        builder.append(milliSecond.substring(2, 4));
        builder.append(milliSecond.substring(0, 2));
        builder.append(String.format("%02X", this.minute() & 0x3F));
        builder.append(String.format("%02X", this.hour(true) & 0x1F));
        int week = this.dayOfWeek();
        if (week == Calendar.SUNDAY) {
            week = 7;
        } else {
            week--;
        }
        builder.append(String.format("%02X", (week << 5) + (this.dayOfMonth() & 0x1F)));
        builder.append(String.format("%02X", this.monthBaseOne()));
        builder.append(String.format("%02X", this.year() - 2000));
        return builder.toString();
    }

    @Override
    public String console() {
        return this.toString(DatePattern.NORM_DATETIME_MS_PATTERN);
    }
}
