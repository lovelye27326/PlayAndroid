package com.yfy.play.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.yfy.play.R;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static final long MIN_Millis = 60 * 1000;
    public static final long HOUR_Millis = 60 * 60 * 1000;
    public static final long HALF_HOUR_Millis = HOUR_Millis / 2; //半小时
    public static final long FOUR_HOUR = HOUR_Millis * 4; //四小时
    public static final long DAY_Millis = 24 * HOUR_Millis;
    public static final long MONTH_Millis = 30 * DAY_Millis;
    public static final long YEAR_Millis = 365 * DAY_Millis;
    public static final String DEFAULT_DATE_FORMAT_STR = "yyyy-MM-dd HH : mm";
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT =
            new SimpleDateFormat(DEFAULT_DATE_FORMAT_STR);
    public static final String DEFAULT_DATE_FORMAT_HM_STR = "yyyy-MM-dd HH:mm";
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT_HM =
            new SimpleDateFormat(DEFAULT_DATE_FORMAT_HM_STR);
    public static final String Y_M_D_H_M_S_STR = "yyyy-MM-dd HH:mm:ss";
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat Y_M_D_H_M_S =
            new SimpleDateFormat(Y_M_D_H_M_S_STR);
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat DATE_FORMAT_DATE =
            new SimpleDateFormat("yyyy-MM-dd");
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat DATE_FORMAT_DATE_1 =
            new SimpleDateFormat(" HH : mm ");
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat DATE_FORMAT_DATE_2 =
            new SimpleDateFormat("yyyy/MM/dd HH : mm");
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat DATE_FORMAT_DATE_POINT =
            new SimpleDateFormat("yyyy.MM.dd");
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat HM =
            new SimpleDateFormat("HH时mm分");
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat DATE_FORMAT_E =
            new SimpleDateFormat("MM月dd日 E");
    //    public static final SimpleDateFormat HM = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private TimeUtils() {
        throw new AssertionError();
    }

    /**
     * long time to string
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    public static String getConciseTime(long timeInMillis, long nowInMillis, Context context) {
        if (context == null) return "";
        long diff = nowInMillis - timeInMillis;
        if (diff >= YEAR_Millis) {
            int year = (int) (diff / YEAR_Millis);
            return context.getString(R.string.before_year, year);
        }
        if (diff >= MONTH_Millis) {
            int month = (int) (diff / MONTH_Millis);
            return context.getString(R.string.before_month, month);
        }

        if (diff >= DAY_Millis) {
            int day = (int) (diff / DAY_Millis);
            return context.getString(R.string.before_day, day);
        }

        if (diff >= HOUR_Millis) {
            int hour = (int) (diff / HOUR_Millis);
            return context.getString(R.string.before_hour, hour);
        }

        if (diff >= HALF_HOUR_Millis) {
            return context.getString(R.string.before_half_hour);
        }
        // 计算差多少分钟
        long min = diff % DAY_Millis % HOUR_Millis / MIN_Millis;
        if (min > 1) return context.getString(R.string.before_min, min);
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return context.getString(R.string.just_now);
    }

    public static String getConciseTime(long timeInMillis, Context context) {
        return getConciseTime(timeInMillis, getCurrentTimeInLong(), context);
    }

    /**
     * get current time in milliseconds
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    public static long convert(String dateString, String dateFormat) throws Exception {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = sdf.parse(dateString);
        long timestamp = 0L;
        if (date != null) {
            timestamp = date.getTime();
        }
        return timestamp;
    }

    public static int getDateDiff(String date1, String date2) {
        String dateFormat = "yyyy-MM-dd";
        long nowInMillis;
        long timeInMillis;
        int day = 0;
        try {
            nowInMillis = convert(date1, dateFormat);
            timeInMillis = convert(date2, dateFormat);
            long diff = Math.abs(nowInMillis - timeInMillis);
            if (diff >= DAY_Millis) {
                day = (int) (diff / DAY_Millis);
                return day;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return day;
    }


    /**
     * 使用三方库格式化日期，线程安全
     */
    public static String formatTimestamp(long timestamp, String formatStr) {
        // 将时间戳转换为Instant对象
        Instant instant = Instant.ofEpochMilli(timestamp);

        // 将Instant转换为系统默认时区的ZonedDateTime对象
        ZonedDateTime dateTime = instant.atZone(ZoneId.systemDefault());

        // 创建一个DateTimeFormatter格式化器
        DateTimeFormatter formatter;
        if (TextUtils.isEmpty(formatStr)) {
            formatter = DateTimeFormatter.ofPattern(Y_M_D_H_M_S_STR);// "yyyy-MM-dd HH:mm:ss";
        } else {
            formatter = DateTimeFormatter.ofPattern(formatStr);
        }
        // 使用格式化器格式化日期时间
        return dateTime.format(formatter);
    }


    /**
     * 方法接受一个以毫秒为单位的时间戳，将其转换为秒和纳秒，
     * 然后使用这些值和指定的时区偏移量（这里是东八区，即UTC+8）来创建 LocalDateTime 对象。
     * 最后，使用 DateTimeFormatter 来格式化这个日期时间对象。
     */
    public static String formatTimestampWithZone8(long timestamp, String formatStr) {
        // 将毫秒时间戳转换为秒
        long epochSecond = timestamp / 1000;
        // 获取纳秒部分
        int nanoOfSecond = (int) (timestamp % 1000) * 1_000_000;

        // 使用LocalDateTime.ofEpochSecond创建LocalDateTime对象
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(epochSecond, nanoOfSecond, ZoneOffset.ofHours(8)); //东八区，即UTC+8

        // 创建一个DateTimeFormatter格式化器
        DateTimeFormatter formatter;
        if (TextUtils.isEmpty(formatStr)) {
            formatter = DateTimeFormatter.ofPattern(Y_M_D_H_M_S_STR);// "yyyy-MM-dd HH:mm:ss";
        } else {
            formatter = DateTimeFormatter.ofPattern(formatStr);
        }
        // 使用格式化器格式化日期时间
        return dateTime.format(formatter);
    }

}
