package com.xbk.spring.util.base.date;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static long getWeekStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getDayStartTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getTodayStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        return getDayStartTime(cal);
    }


    public static long getTodayEndTime() {
        return getTomorrowStartTime() - 1;
    }

    public static long getTodayStartTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return getDayStartTime(cal);
    }

    public static long getTodayEndTime(long time) {
        return getTomorrowStartTime(time) - 1;
    }

    public static long getDayStartTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return getDayStartTime(cal);
    }

    public static long getDayEndTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return getDayEndTime(cal);
    }

    public static long getDayEndTime(Calendar cal) {
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getDayStartTime(LocalDate date) {
        LocalDateTime time = LocalTime.ofSecondOfDay(0).atDate(date);
        return toMills(time);
    }

    public static long getTodayStartTime2() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        return getDayStartTime(LocalDate.now());
    }

    public static LocalDateTime getDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone
                .getDefault().toZoneId());
    }

    public static LocalDate getDate(long timestamp) {
        LocalDateTime date = getDateTime(timestamp);
        return date == null ? null : date.toLocalDate();
    }

    public static long toMills(LocalDateTime time) {
        return time == null ? 0 : time.toEpochSecond(ZoneOffset.of("+8")) * 1000;
    }

    public static long getLastMonthInMills() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before30Days = now.minusDays(30);
        return before30Days.toEpochSecond(ZoneOffset.of("+8")) * 1000;
    }

    /**
     * @param dateTime 2018-05-06
     * @return
     */
    public static long parseTimeInMills(String dateTime) {
        String[] parts = dateTime.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        LocalDateTime time = LocalDateTime.of(year, month, day, 0, 0);
        return time.toEpochSecond(ZoneOffset.of("+8")) * 1000;
    }

    /**
     * 获取昨天开始的时间
     */
    public static long getYesterdayStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取昨天结束的时间
     */
    public static long getYesterdayEndTime() {
        return getTodayStartTime() - 1;
    }

    /**
     * 获取明天开始的时间
     */
    public static long getTomorrowStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getTomorrowStartTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getLastWeekByMillis(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return getLastWeekByMillis(calendar);
    }

    public static long getLastWeekByMillis(Calendar calendar) {
        calendar = (Calendar) calendar.clone();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        return calendar.getTimeInMillis();
    }

    // 判断当前时间是否是周六周天  true是 false不是
    public static boolean judgeTimeIsWeekend() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }


    /**
     * 获取指定时间所在周的周一开始时间
     */
    public static long getWeekStartTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取这个时间所在周的周week的第hour小时min份
     *
     * @param time 时间
     * @param week 周几 - 1
     * @param hour 时间
     * @param min  分钟
     * @return
     */
    public static long getWeekStartTime(Long time, Integer week, Integer hour, Integer min) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, week);
        return cal.getTimeInMillis();
    }

    /**
     * 获取指定时间所在月的开始时间
     */
    public static long getMonthStartTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取指定时间所在周的下周一开始时间
     */
    public static long getNextWeekStartTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getWeekStartTime(time));
        cal.add(Calendar.DATE, 7);
        return cal.getTimeInMillis();
    }

    /**
     * 获取指定时间所在周的周二开始时间
     */
    public static long getTuesdayStartTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getWeekStartTime(time));
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.add(Calendar.DATE, 1);
        return cal.getTimeInMillis();
    }

    /**
     * 获取指定时间所在周的周五开始时间
     */
    public static long getFridayStartTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getWeekStartTime(time));
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.add(Calendar.DATE, 4);
        return cal.getTimeInMillis();
    }

    /**
     * 获取指定时间所在周的周六开始时间
     */
    public static long getSaturdayStartTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getWeekStartTime(time));
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.add(Calendar.DATE, 5);
        return cal.getTimeInMillis();
    }

    /**
     * 获取指定时间所在周的周日开始时间
     */
    public static long getSundayStartTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getWeekStartTime(time));
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.add(Calendar.DATE, 6);
        return cal.getTimeInMillis();
    }

    // 获取指定时间的下一毫秒
    public static long waitUntilNextTime(final long lastTimestamp) {
        long currentTimeMillis = System.currentTimeMillis();
        while (currentTimeMillis <= lastTimestamp) {
            currentTimeMillis = System.currentTimeMillis();
        }
        return currentTimeMillis;
    }

    /**
     * 获取指定时间所在周的周顺序
     */
    public static int getDayOfWeek(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        dayOfWeek = dayOfWeek - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        return dayOfWeek;
    }

    /**
     * 获取指定时间 增加指定天后的时间
     */
    public static long getAddDayStartTime(long time, int day) {
        if (time < 0) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.add(Calendar.DATE, day);
        return cal.getTimeInMillis();
    }

    /**
     * 获取指定时间 增加一天后的时间
     */
    public static long getAddOneDayTime(long time) {
        if (time < 0) {
            return 0;
        }
        return getAddDayStartTime(time, 1);
    }

    /**
     * 获取指定时间 的偏移  天   时   分   的时间
     */
    public static long getScheduleTime(long time, int day, int hour, int minute) {
        if (time <= 0) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTimeInMillis();
    }

    public static int getDaysAfter(long targetTime, long startTime) {
        long diff = targetTime - getTodayStartTime(startTime);
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
    }

    public static long getTodayLeftTime() {
        return getTomorrowStartTime() - System.currentTimeMillis();
    }

    public static long getTimeIntervalFromNow(String time) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(time);
        return date.getTime() - (new Date().getTime());
    }

    // 获取指定月的开始时间
    public static long getMonthStartTime(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        // 获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    // 获取指定月的结束时间
    public static long getMonthEndTime(int year, int month) {
        long monthStartTime = getMonthStartTime(year, month);
        return getScheduleTime(monthStartTime, getCurrentMonthDay(monthStartTime), 0, 0) - 1;
    }

    // 获取指定时间当月的天数
    public static int getCurrentMonthDay(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.DATE, 1);
        cal.roll(Calendar.DATE, -1);
        return cal.get(Calendar.DATE);
    }

    /**
     * 获取真实的秒，时间戳
     *
     * @param dateTime 20210604140000
     * @return 1625378400
     */
    public static long getTimeFromFormatString(String dateTime) {
        int year = Integer.parseInt(dateTime.substring(0, 4));
        int month = Integer.parseInt(dateTime.substring(4, 6));
        int day = Integer.parseInt(dateTime.substring(6, 8));
        int hour = Integer.parseInt(dateTime.substring(8, 10));
        int minute = Integer.parseInt(dateTime.substring(10, 12));
        int second = Integer.parseInt(dateTime.substring(12, 14));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        return calendar.getTimeInMillis() / 1000;
    }

    public static long getDayStartTime(long time, int afterDay) {
        if (time < 0) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.add(Calendar.DATE, afterDay);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 根据分钟获取时间
     *
     * @return
     */
    public static String getHourMin(Integer min) {
        StringBuffer result = new StringBuffer();
        if (min > 60) {
            Integer hour = min / 60;
            result.append(hour);
            result.append("小时");
        }
        min = min % 60;
        if (min != 0) {
            result.append(min);
            result.append("分钟");
        }
        return result.toString();
    }

    /**
     * 将日期对象转换为yyyy-MM-dd HH:mm:ss格式字符串
     *
     * @param date 时间对象
     * @return yyyy-MM-dd HH:mm:ss格式字符串
     */
    public static String toStringYmdHmsWthH(Date date) {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date);
    }

    /**
     * 将日期对象转换为yyyy-MM-dd HH:mm:ss格式字符串
     *
     * @param date 时间对象
     * @return yyyy-MM-dd HH:mm:ss格式字符串
     */
    public static String toStringYmdHmsWth(Date date) {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(date);
    }

    /**
     * “2010-07-14 09:00:02”这个时间字符串中提取”09:00“。
     *
     * @param date
     * @return
     */
    public static String toStringHm(Date date) {
        return String.format("%tR", date);
    }

    /**
     * 排课时间段格式化
     *
     * @param date
     * @return
     */
    public static String toScheduleTime(Date start, Date end) {
        return toStringYmdHmsWth(start) + "-" + toStringHm(end);

    }

    private final static SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final static SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * 获得本周的第一天，周一
     *
     * @return
     */
    public static Date getWeekStartTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        try {
            int weekday = c.get(Calendar.DAY_OF_WEEK) - 2;
            c.add(Calendar.DATE, -weekday);
            c.setTime(longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00.000"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     * 获得本周的最后一天，周日
     *
     * @return
     */
    public static Date getWeekEndTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        try {
            int weekday = c.get(Calendar.DAY_OF_WEEK);
            c.add(Calendar.DATE, 8 - weekday);
            c.setTime(longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59.999"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c.getTime();
    }

    /**
     * 将日期对象转换为yyyy/MM/dd格式字符串
     *
     * @param date 时间对象
     * @return yyyy/MM/dd格式字符串
     */
    public static String toStringYmdWthB(Date date) {
        return (new SimpleDateFormat("yyyy/MM/dd")).format(date);
    }

    /**
     * 时间戳转换  xx时xx分xx秒
     *
     * @param time
     * @return
     */
    public static String transTimeToHMS(long time) {

        int mi = 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = time / dd;
        long hour = (time - day * dd) / hh;
        long minute = (time - day * dd - hour * hh) / mi;
        long second = (time - day * dd - hour * hh - minute * mi);
        StringBuilder result = new StringBuilder();
        if (hour != 0) {
            result.append(hour).append("时");
        }
        if (minute != 0) {
            result.append(minute).append("分");
        }
        if (second != 0) {
            result.append(second).append("秒");
        }
        if (StringUtils.isBlank(result.toString())) {
            result.append("0分0秒");
        }
        return result.toString();
    }

    public static Date LocalDateTimeToUDate(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

}
