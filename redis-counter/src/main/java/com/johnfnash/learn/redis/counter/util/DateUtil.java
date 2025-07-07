package com.johnfnash.learn.redis.counter.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 日期工具类
 */
public class DateUtil {

    static Logger logger = LoggerFactory.getLogger(DateUtil.class.getName());

    public static final String C_DATE_DIVISION = "-";

    public static final String C_TIME_PATTON_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String C_DATE_PATTON_DEFAULT = "yyyy-MM-dd";
    public static final String C_DATETIME_PATION_DEFAULT = "yyyy-MM-dd HH:mm";
    public static final String C_DATA_PATTON_YYYYMMDD = "yyyyMMdd";
    public static final String C_DATA_PATTON_YYYYMM = "yyyyMM";
    public static final String C_TIME_PATTON_HHMMSS = "HH:mm:ss";
    public static final String C_DATE_PATTON_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final int C_ONE_SECOND = 1000;
    public static final int C_ONE_MINUTE = 60 * C_ONE_SECOND;
    public static final int C_ONE_HOUR = 60 * C_ONE_MINUTE;
    public static final long C_ONE_DAY = 24 * (long) C_ONE_HOUR;

    private static final int[] DAY_OF_MONTH = new int[] {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public static final String QUARTER = "Q";

    /**
     * Return the current date
     * 
     * @return － DATE<br>
     */
    public static Date getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        Date currDate = cal.getTime();
        return currDate;
    }

    /**
     * 去掉时分秒，比较日期
     *
     * @param date1
     * @param date2
     * @return -2:error -1:date1 before date2 0:date1=date2 1:date1 after date2
     */
    public static int dayCompare(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return -2;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        date1 = cal.getTime();

        cal.setTime(date2);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        date2 = cal.getTime();

        long diff = date1.getTime() - date2.getTime();
        if (diff == 0) {
            return 0;
        }
        return diff < 0 ? -1 : 1;
    }

    /**
     * Return the current date string
     * 
     * @return － 产生的日期字符串<br>
     */
    public static String getCurrentDateStr() {
        Calendar cal = Calendar.getInstance();
        Date currDate = cal.getTime();

        return format(currDate);
    }

    /**
     * Return the current date in the specified format
     * 
     * @param strFormat
     * @return
     */
    public static String getCurrentDateStr(String strFormat) {
        Calendar cal = Calendar.getInstance();
        Date currDate = cal.getTime();

        return format(currDate, strFormat);
    }

    /**
     * Parse a string and return a date value
     * 
     * @param dateValue
     * @return
     * @throws Exception
     */
    public static Date parseDate(String dateValue) {
        return parseDate(C_DATE_PATTON_DEFAULT, dateValue);
    }

    /**
     * Parse a strign and return a datetime value
     * 
     * @param dateValue
     * @return
     */
    public static Date parseDateTime(String dateValue) {
        return parseDate(C_TIME_PATTON_DEFAULT, dateValue);
    }

    public static Date parseTime(String dateValue) {
        return parseTime(C_DATETIME_PATION_DEFAULT, dateValue);
    }

    /**
     * Parse a string and return the date value in the specified format
     * 
     * @param strFormat
     * @param dateValue
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static Date parseDate(String strFormat, String dateValue) {
        if (dateValue == null || "".equals(dateValue))
            return null;

        if (strFormat == null)
            strFormat = C_TIME_PATTON_DEFAULT;

        SimpleDateFormat dateFormat = new SimpleDateFormat(strFormat);
        Date newDate = null;

        try {
            newDate = dateFormat.parse(dateValue);
        } catch (ParseException pe) {
            //logger.error("", pe);
            newDate = null;
        }

        return newDate;
    }

    /**
     * 将Timestamp类型的日期转换为系统参数定义的格式的字符串。
     * 
     * @param aTs_Datetime
     *            需要转换的日期。
     * @return 转换后符合给定格式的日期字符串
     */
    public static String format(Date aTs_Datetime) {
        return format(aTs_Datetime, C_DATE_PATTON_DEFAULT);
    }

    /**
     * 将Timestamp类型的日期转换为系统参数定义的格式的字符串。
     * 
     * @param aTs_Datetime
     *            需要转换的日期。
     * @return 转换后符合给定格式的日期字符串
     */
    public static String formatTime(Date aTs_Datetime) {
        return format(aTs_Datetime, C_TIME_PATTON_DEFAULT);
    }

    /**
     * 将Date类型的日期转换为系统参数定义的格式的字符串。
     * 
     * @param aTs_Datetime
     * @param as_Pattern
     * @return
     */
    public static String format(Date aTs_Datetime, String as_Pattern) {
        if (aTs_Datetime == null || as_Pattern == null)
            return null;

        SimpleDateFormat dateFromat = new SimpleDateFormat();
        dateFromat.applyPattern(as_Pattern);

        return dateFromat.format(aTs_Datetime);
    }

    /**
     * @param aTs_Datetime
     * @param as_Format
     * @return
     */
    public static String formatTime(Date aTs_Datetime, String as_Format) {
        if (aTs_Datetime == null || as_Format == null)
            return null;

        SimpleDateFormat dateFromat = new SimpleDateFormat();
        dateFromat.applyPattern(as_Format);

        return dateFromat.format(aTs_Datetime);
    }

    public static String getFormatTime(Date dateTime) {
        return formatTime(dateTime, C_TIME_PATTON_HHMMSS);
    }

    /**
     * @param aTs_Datetime
     * @param as_Pattern
     * @return
     */
    public static String format(Timestamp aTs_Datetime, String as_Pattern) {
        if (aTs_Datetime == null || as_Pattern == null)
            return null;

        SimpleDateFormat dateFromat = new SimpleDateFormat();
        dateFromat.applyPattern(as_Pattern);

        return dateFromat.format(aTs_Datetime);
    }

    /**
     * 取得指定日期N天后的日期
     * 
     * @param date
     * @param days
     * @return
     */
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.add(Calendar.DAY_OF_MONTH, days);

        return cal.getTime();
    }
    
    /**
    * 取得指定日期N月后的日期
     * 
     * @param date
     * @param months
     * @return
     */
    public static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.add(Calendar.MONTH, months);

        return cal.getTime();
    }

    /**
     * 取得当前日期N天前的日期
     * 
     * @param days
     * @return
     */
    public static Date dynamicDays(int days) {
        SimpleDateFormat dateFromat = new SimpleDateFormat("yyyy-MM-dd");
        Date retDate = null;
        try {
            Calendar cal = Calendar.getInstance();
            int date_int = days;
            if (date_int <= 0)
                return retDate;
            date_int = (date_int * -1) + 1;
            cal.add(Calendar.DAY_OF_YEAR, date_int);
            retDate = dateFromat.parse(dateFromat.format(cal.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retDate;
    }

    /**
     * 计算两个日期之间相差的天数
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static int daysBetween(Date date1, Date date2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 计算两个日期之间相差的时间戳
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static long timeInMillisBetween(Date date1, Date date2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1);
        return between_days;
    }

    /**
     * 计算当前日期相对于"1977-12-01"的天数
     * 
     * @param date
     * @return
     */
    public static long getRelativeDays(Date date) {
        Date relativeDate = DateUtil.parseDate("yyyy-MM-dd", "1977-12-01");

        return DateUtil.daysBetween(relativeDate, date);
    }

    public static Date getDateBeforTwelveMonth() {
        String date = "";
        Calendar cla = Calendar.getInstance();
        cla.setTime(getCurrentDate());
        int year = cla.get(Calendar.YEAR) - 1;
        int month = cla.get(Calendar.MONTH) + 1;
        if (month > 9) {
            date = String.valueOf(year) + C_DATE_DIVISION + String.valueOf(month) + C_DATE_DIVISION + "01";
        } else {
            date = String.valueOf(year) + C_DATE_DIVISION + "0" + String.valueOf(month) + C_DATE_DIVISION + "01";
        }

        Date dateBefore = parseDate(date);
        return dateBefore;
    }

    /**
     * 传入时间字符串,加一天后返回Date
     * 
     * @param date
     *            时间 格式 YYYY-MM-DD
     * @return
     */
    public static Date addDate(String date) {
        if (date == null) {
            return null;
        }

        Date tempDate = parseDate(C_DATE_PATTON_DEFAULT, date);
        String year = format(tempDate, "yyyy");
        String month = format(tempDate, "MM");
        String day = format(tempDate, "dd");

        GregorianCalendar calendar =
            new GregorianCalendar(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));

        calendar.add(GregorianCalendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     * 
     * @param date
     * @param days
     * @return
     */
    public static Date minusDate(String date, int days) {
        if (date == null) {
            return null;
        }
        Date tempDate = parseDate(C_DATE_PATTON_DEFAULT, date);
        String year = format(tempDate, "yyyy");
        String month = format(tempDate, "MM");
        String day = format(tempDate, "dd");
        GregorianCalendar calendar =
            new GregorianCalendar(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day) - days);
        return calendar.getTime();
    }

    /**
     * 查询条件的开始时间
     * 
     * @param startDate
     * @return
     */
    public static Date parseBeginDate(String startDate) {
        if (null == startDate || "".equals(startDate.trim())) {
            return null;
        }
        return beginOfTheDay(parseDate(startDate));

    }

    /**
     * 查询条件的结束时间
     * 
     * @param endDate
     * @return
     */
    public static Date parseEndDate(String endDate) {

        if (null == endDate || "".equals(endDate.trim())) {
            return null;
        }
        return endOfTheDay(parseDate(endDate));

    }

    /**
     * 获取今天00点:00分:00秒
     * 
     * @return
     */
    public static Date getTodayZero() {
        return DateUtil.parseDate(DateUtil.getCurrentDateStr("yyyy-MM-dd"));
    }

    /**
     * 获取今天00点:00分:00秒
     *
     * @return
     */
    public static Date getDateZero(Date date) {
        return DateUtil.parseDate(DateUtil.format(date));
    }

    /**
     * 某天的00点:00分:00秒
     * @return java.util.Date
     */
    public static Date beginOfTheDay(Date startTime) {
        if (startTime == null)
            return null;
        return DateUtil.parseDate(DateUtil.C_TIME_PATTON_DEFAULT,
            DateUtil.formatTime(startTime, DateUtil.C_TIME_PATTON_DEFAULT).substring(0, 11) + "00:00:00");
    }

    /**
     * 某天的23点:59分:59秒
     * @return java.util.Date
     */
    public static Date endOfTheDay(Date endTime) {
        if (endTime == null)
            return null;
        return DateUtil.parseDate(DateUtil.C_TIME_PATTON_DEFAULT,
            DateUtil.formatTime(endTime, DateUtil.C_TIME_PATTON_DEFAULT).substring(0, 11) + "23:59:59");
    }

    /**
     * 获取本月第一天的00点:00分:00秒
     * 
     * @return java.util.Date
     */
    public static Date beginOfThisMonth() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.get(Calendar.YEAR);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String startOfMonth =
            gregorianCalendar.get(Calendar.YEAR) + "-" + (gregorianCalendar.get(Calendar.MONTH) + 1) + "-1";
        try {
            return simpleDateFormat.parse(startOfMonth);
        } catch (ParseException e) {
            logger.error("", e);
        }
        return null;
    }

    public static Long getBetweenTime(Long startTime, Long endTime) {

        return (endTime - startTime) / 1000;

    }

    public static Date getNowDateTime(Date now) {

        String nowTimeStr = formatTime(now);

        return parseDateTime(nowTimeStr);

    }

    public static Date getNowDate() {

        String nowTimeStr = getCurrentDateStr(C_DATE_PATTON_DEFAULT);

        return parseDate(nowTimeStr);

    }

    public static Date AddOneDay(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date middle = parseDate("yyyy-MM-dd", date);
        String tempDate = df.format(middle.getTime() + 1 * 24 * 60 * 60 * 1000);
        return parseDate("yyyy-MM-dd", tempDate);
    }

    /**
     * 在当期日期增加N天
     * @return
     */
    public static Date addDay(int n) {
        long nowTime = System.currentTimeMillis();
        return new Date(nowTime + n * 24 * 60 * 60 * 1000);
    }

    public static Date addOneDay(String format, String date) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return DateUtil.parseDate(format,
            df.format(DateUtil.parseDate(format, date).getTime() + 1 * 24 * 60 * 60 * 1000));
    }

    public static Date addMinute(int n) {
        long nowTime = System.currentTimeMillis();
        return new Date(nowTime + n * 60 * 1000);
    }

    public static Date addHour(int n) {
        long nowTime = System.currentTimeMillis();
        return new Date(nowTime + n * 60 * 60 * 1000);
    }

    public static String getXmlTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyyMMddHHmmssSSSS");
        return sdf.format(new Date());

    }

    /**
     * 获得当年的第一天, 如2009-10-01 00:00:00
     * 
     * @param date
     * @return
     */
    public static Date getFirstDayOfYear(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.set(Calendar.DAY_OF_YEAR, gc.getActualMinimum(Calendar.DAY_OF_YEAR));
        gc.set(Calendar.HOUR_OF_DAY, gc.getActualMinimum(Calendar.HOUR_OF_DAY));
        gc.set(Calendar.MINUTE, gc.getActualMinimum(Calendar.MINUTE));
        gc.set(Calendar.SECOND, gc.getActualMinimum(Calendar.SECOND));
        gc.set(Calendar.MILLISECOND, gc.getActualMinimum(Calendar.MILLISECOND));
        return gc.getTime();
    }

    /**
     * 获得当年的第一天, 如2009-10-01 00:00:00
     * 
     * @param date
     * @return
     */
    public static Date getLastDayOfYear(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.set(Calendar.DAY_OF_YEAR, gc.getActualMaximum(Calendar.DAY_OF_YEAR));
        gc.set(Calendar.HOUR_OF_DAY, gc.getActualMaximum(Calendar.HOUR_OF_DAY));
        gc.set(Calendar.MINUTE, gc.getActualMaximum(Calendar.MINUTE));
        gc.set(Calendar.SECOND, gc.getActualMaximum(Calendar.SECOND));
        return gc.getTime();
    }

    public static int getDiffDays(Date date1, Date date2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        c1.set(11, 0);
        c1.set(12, 0);
        c1.set(13, 0);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);
        c2.set(11, 0);
        c2.set(12, 0);
        c2.set(13, 0);
        return Math.abs((int)(c1.getTime().getTime() / 1000L) - (int)(c2.getTime().getTime() / 1000L)) / 3600 / 24;
    }

    /**
     * 获取相差周
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getDiffWeek(Date date1, Date date2) {
        int diffDays = getDiffDays(date1, date2);
        int week = diffDays / 7 + 1;
        return week;
    }

    /**
     * 获得当月的第一天, 如2009-10-01 00:00:00
     * 
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.set(Calendar.DAY_OF_MONTH, gc.getActualMinimum(Calendar.DAY_OF_MONTH));
        gc.set(Calendar.HOUR_OF_DAY, gc.getActualMinimum(Calendar.HOUR_OF_DAY));
        gc.set(Calendar.MINUTE, gc.getActualMinimum(Calendar.MINUTE));
        gc.set(Calendar.SECOND, gc.getActualMinimum(Calendar.SECOND));
        return gc.getTime();
    }

    /**
     * 获得当月的最后一天, 如2009-10-31 23:59:59
     * 
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.set(Calendar.DAY_OF_MONTH, gc.getActualMaximum(Calendar.DAY_OF_MONTH));
        gc.set(Calendar.HOUR_OF_DAY, gc.getActualMaximum(Calendar.HOUR_OF_DAY));
        gc.set(Calendar.MINUTE, gc.getActualMaximum(Calendar.MINUTE));
        gc.set(Calendar.SECOND, gc.getActualMaximum(Calendar.SECOND));
        return gc.getTime();
    }

    /**
     * 获得当季度的第一天, 如2009-07-01
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfQuarter(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.set(Calendar.DAY_OF_MONTH, 1);
        gc.set(Calendar.MONTH, (gc.get(Calendar.MONTH)/3) * 3);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        return gc.getTime();
    }

    /**
     * 获得当季度的最后一天, 如2009-09-30
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfQuarter(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.set(Calendar.DAY_OF_MONTH, 1); // 防止月份中天数大于该月最大天数从而导致月份加1
        gc.set(Calendar.MONTH, gc.get(Calendar.MONTH) + 2 - gc.get(Calendar.MONTH) % 3);
        gc.set(Calendar.DAY_OF_MONTH, gc.getActualMaximum(Calendar.DAY_OF_MONTH));
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        return gc.getTime();
    }

    /**
     * 获得给定季度的第一天, 如2009-01-01
     *
     * @param quarter 季度 1/2/3/4
     * @return
     */
    public static Date getFirstDayOfQuarter(Integer quarter) {
        if (quarter == null || quarter < 0 || quarter > 4) {
            return getLastDayOfQuarter(new Date());
        }

        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1); // 防止月份中天数大于该月最大天数从而导致月份加1
        gc.set(Calendar.MONTH, (quarter - 1) * 3);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        return gc.getTime();
    }

    /**
     * 获得给定季度的最后一天, 如2009-09-30
     *
     * @param quarter 季度 1/2/3/4
     * @return
     */
    public static Date getLastDayOfQuarter(Integer quarter) {
        if (quarter == null || quarter < 0 || quarter > 4) {
            return getLastDayOfQuarter(new Date());
        }

        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.DAY_OF_MONTH, 1); // 防止月份中天数大于该月最大天数从而导致月份加1
        gc.set(Calendar.MONTH, quarter * 3 - 1);
        gc.set(Calendar.DAY_OF_MONTH, gc.getActualMaximum(Calendar.DAY_OF_MONTH));
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        return gc.getTime();
    }

    /**
     * 获取某周第一天日期 00点:00分:00秒
     * 
     * @param date
     *            这周中的一天
     * @param start
     *            1--第一天是周日， 2--第一天是周一， 类推
     * @return
     */
    public static Date getFirstDayOfWeek(Date date, int start) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - start;
        cal.add(Calendar.DATE, -day_of_week);

        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
        return cal.getTime();
    }

    /**
     * 获取某周最后一天日期 23:59:59
     * 
     * @param date
     *            这周中的一天
     * @param start
     *            start 1--第一天是周日， 2--第一天是周一， 类推
     * @return
     */
    public static Date getLastDayOfWeek(Date date, int start) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - start;
        cal.add(Calendar.DATE, -day_of_week); // 获得第一天
        cal.add(Calendar.DATE, 6); // +6天 就得到最后一天

        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
        return cal.getTime();
    }

    /**
     * 获取某个日期mins分钟后的日期
     * 
     * @param date
     * @param mins
     * @return
     */
    public static Date getAfterNMinute(Date date, int mins) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, mins);
        return cal.getTime();

    }

    /**
     * 获取指定日期的下一个星期
     * 
     * @param date
     * @return
     */
    public static Date getNextWeek(Date date) {
        GregorianCalendar gc = (GregorianCalendar)Calendar.getInstance();
        gc.setTime(date);
        gc.add(Calendar.DATE, 7);

        return gc.getTime();
    }

    /**
     * 获取指定日期的下几个月
     * 
     * @param date
     *            指定日期
     * @param num
     *            月数
     * @return
     */
    public static Date getNextMonth(Date date, int num) {
        GregorianCalendar gc = (GregorianCalendar)Calendar.getInstance();
        gc.setTime(date);
        gc.add(Calendar.MONTH, num);
        gc.getTime();
        return gc.getTime();
    }

    /**
     * 比较两个时间大少
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static boolean compareDate(Date date1, Date date2) {
        if (date1.getTime() < date2.getTime()) {
            return true;
        } else {
            return false;
        }
    }

    public static Date parseTime(String strFormat, String dateValue) {
        if (dateValue == null || "".equals(dateValue))
            return null;

        if (strFormat == null)
            strFormat = C_DATETIME_PATION_DEFAULT;

        SimpleDateFormat dateFormat = new SimpleDateFormat(strFormat);
        Date newDate = null;

        try {
            newDate = dateFormat.parse(dateValue);
        } catch (ParseException pe) {
            logger.error("", pe);
            newDate = null;
        }

        return newDate;
    }

    /**
     * 日是期计算并返回备注信息
     * 
     * @param dateValue
     * @return
     */
    public static String getDateValueToRemarks(String dateValue) {
        String dateStr = "";
        long dateLong = daysBetween(parseDate(dateValue), new Date());
        if (dateLong <= 7) {
            dateStr = "最近7天";
        } else if (dateLong > 7 && dateLong <= 30) {
            dateStr = "最近一个月";
        } else if (dateLong > 30 && dateLong <= 90) {
            dateStr = "最近三个月";
        } else if (dateLong > 90 && dateLong <= 180) {
            dateStr = "最近半年内";
        }
        return dateStr;
    }

    /**
     * 判断是否是闰年
     * 
     * @param year
     *            年份
     * @return 是true，否则false
     */
    public static boolean isLeapYear(int year) {
        Calendar calendar = Calendar.getInstance();
        return ((GregorianCalendar)calendar).isLeapYear(year);
    }

    /**
     * 取得一个月最多的天数
     * 
     * @param year
     *            年份
     * @param month
     *            月份，0表示1月，依此类推
     * @return 最多的天数
     */
    public static int getMaxDayOfMonth(int year, int month) {
        if (month == 1 && isLeapYear(year)) {
            return 29;
        }
        return DAY_OF_MONTH[month];
    }

    /**
     * 生成时间戳
     * 
     * @return
     */
    public static String createTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 计算两个日期之间的年差
     *
     * @param date
     *            要计算的日期
     * @return 年差
     */
    @SuppressWarnings("deprecation")
	public static Integer getYearsFromNow(Date date) {
        Date now = new Date();

        return now.getYear() - date.getYear();
    }

    public static Date getAgeDate(int age, boolean maxMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        if (maxMonth) {
            calendar.add(Calendar.YEAR, -age + 1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
        } else {
            calendar.add(Calendar.YEAR, -age);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }

        return calendar.getTime();
    }

    /**
     * 根据年龄计算出生年份对应的日期
     *
     * @param age
     *            年龄
     * @return
     */
    public static Date getAgeDate(int age) {
        return getAgeDate(age, false);
    }

    /**
     * 根据时间计算年龄
     * 
     * @param birthDate
     * @param now
     * @return
     */
    public static Optional<Integer> getAge(Date birthDate, Date now) {
        /**
         * by: lixf
         * 修改原因：如果不对birthDate为null处理，后边计算年龄会报错。
         */
        if (null == birthDate){
            return Optional.empty();
        }

        if (now.before(birthDate)) {
            return Optional.of(0);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        // 取出系统当前时间的年、月、日部分
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        // 将日期设置为出生日期
        cal.setTime(birthDate);
        // 取出出生日期的年、月、日部分
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        // 当前年份与出生年份相减，初步计算年龄
        int age = yearNow - yearBirth;
        // 当前月份与出生日期的月份相比，如果月份小于出生月份，则年龄上减1，表示不满多少周岁
        if (monthNow <= monthBirth) {
            // 如果月份相等，在比较日期，如果当前日，小于出生日，也减1，表示不满多少周岁
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return Optional.of(age);
    }

    /**
     * 根据时间计算儿童年龄
     *
     * @param birthDate
     * @param nowDate
     * @return
     */
    public static String getChildAge(Date birthDate, Date nowDate) {
        if (nowDate.before(birthDate)) {
            return "";
        }
        Calendar birthday = Calendar.getInstance();
        birthday.setTime(birthDate);
        Calendar now = Calendar.getInstance();
        now.setTime(nowDate);
        int day = now.get(Calendar.DAY_OF_MONTH) - birthday.get(Calendar.DAY_OF_MONTH);
        int month = now.get(Calendar.MONTH) - birthday.get(Calendar.MONTH);
        int year = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
        //按照减法原理，先day相减，不够向month借；然后month相减，不够向year借；最后year相减。
        if(day<0){
            month -= 1;
            now.add(Calendar.MONTH, -1);//得到上一个月，用来得到上个月的天数。
            day = day + now.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        if(month<0){
            month = (month+12)%12;
            year--;
        }
        StringBuilder sb = new StringBuilder();
        if(year > 0){
            sb.append(year).append("年");
        }
        if(month > 0){
            sb.append(month).append("月");
        }
        if(day > 0){
            sb.append(day).append("天");
        }
        return sb.toString();
    }

    /**
     * 当前季度的开始时间
     *
     * @return
     */
    public static Date getQuarterStartTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        try {
            if (currentMonth >= 1 && currentMonth <= 3)
                c.set(Calendar.MONTH, 0);
            else if (currentMonth >= 4 && currentMonth <= 6)
                c.set(Calendar.MONTH, 3);
            else if (currentMonth >= 7 && currentMonth <= 9)
                c.set(Calendar.MONTH, 6);
            else if (currentMonth >= 10 && currentMonth <= 12)
                c.set(Calendar.MONTH, 9);
            c.set(Calendar.DATE, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        return c.getTime();
    }

    /**
     * 当前季度的结束时间
     *
     * @return
     */
    public static Date getQuarterEndTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        try {
            if (currentMonth >= 1 && currentMonth <= 3) {
                c.set(Calendar.MONTH, 2);
                c.set(Calendar.DATE, 31);
            } else if (currentMonth >= 4 && currentMonth <= 6) {
                c.set(Calendar.MONTH, 5);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 7 && currentMonth <= 9) {
                c.set(Calendar.MONTH, 8);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 10 && currentMonth <= 12) {
                c.set(Calendar.MONTH, 11);
                c.set(Calendar.DATE, 31);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    /**
     * 当前季度数字：1-第一季度 2-第二季度...
     *
     * @param date
     *
     * @return 季度的数字表示，1,2,3,4
     */
    public static Integer getQuarterNumber(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        return (currentMonth + 2) / 3;
    }

    /**
     * 日期 yyyy-MM-dd 转换为时间 yyyy-MM-dd HH:mm:ss
     * 
     * @param day
     * @return
     */
    public static Date day2Date(Date day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /* 时间转换为字符串格式 eg 2018-10-10 */
    public static String dateToString(String datePattern, Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        String dateString = formatter.format(date);
        return dateString;
    }

    public static Calendar getCalendar(Date date) {
        val calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar;
    }

    public static String localDate2String(LocalDate localDate, String format) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(format);
        String dateStr = localDate.format(fmt);
        return dateStr;
    }

    public static String getSomeYearsAgo(int year, String format) {
        LocalDate now = LocalDate.now();
        now = now.plusYears(year * (long) -1);
        return localDate2String(now, format);
    }

    public static LocalDateTime date2LocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime;
    }

    public static LocalDate date2LocalDate(Date date) {
        LocalDateTime localDateTime = date2LocalDateTime(date);
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate;
    }

    public static LocalDate addYears(Date date, int year) {
        LocalDate localDate = date2LocalDate(date);
        localDate = localDate.plusYears(year);
        return localDate;
    }

    public static Date localDateToDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    public static String LocalDate2String(LocalDate localDate) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = localDate.format(fmt);
        return dateStr;
    }

    /**
     * date转localDate
     * @param date
     * @return
     */
	public static LocalDate toLocalDate(Date date) {
        if(date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * inputDate是否在startDate与endDate之间（包括相等）
     * @param inputDate
     * @param startDate
     * @param endDate
     * @return
     */
	public static Boolean between(Date inputDate, Date startDate, Date endDate) {
        if (inputDate == null || startDate == null || endDate == null) {
            return false;
        }
        LocalDate inputLocalDate = toLocalDate(inputDate);
        LocalDate startLocalDate = toLocalDate(startDate);
        LocalDate endLocalDate = toLocalDate(endDate);
        if (inputLocalDate.isBefore(startLocalDate)) {
            return false;
        }
        if (inputLocalDate.isAfter(endLocalDate)) {
            return false;
        }
        return true;
    }
    
    /**
     * 两个日期相差多少分钟
     * @param sourceDate
     * @param targetDate
     * @return
     */
    public static long diffMinute(Date sourceDate, Date targetDate) {
        LocalDateTime sourceLocalDateTime = date2LocalDateTime(sourceDate);
        LocalDateTime targetLocalDateTime = date2LocalDateTime(targetDate);
        long diffMins = ChronoUnit.MINUTES.between(sourceLocalDateTime, targetLocalDateTime);
        return diffMins;
    }

    /**
     * 08：00 - 12：00为AM
     * @return
     */
    public static Boolean isAM() {
        LocalTime now = LocalTime.now();
        LocalTime startTime = LocalTime.of(8, 0, 0);
        LocalTime endTime = LocalTime.of(12, 0, 0);
        Boolean am = now.isAfter(startTime) && now.isBefore(endTime);
        return am;
    }

    /**
     * 14：00 - 23：00为PM
     * @return
     */
    public static Boolean isPM() {
        LocalTime now = LocalTime.now();
        LocalTime startTime = LocalTime.of(13, 0, 0);
        LocalTime endTime = LocalTime.of(23, 0, 0);
        Boolean pm = now.isAfter(startTime) && now.isBefore(endTime);
        return pm;
    }

    /**
     * 获取本季度的第一天或最后一天
     * @Param: [today, isFirst: true 表示开始时间，false表示结束时间]
     * @return: java.lang.String
     * @Exception:
     */
    public static Date getStartOrEndDayOfQuarter(Date date, Boolean isFirst){
        LocalDate resDate;
        LocalDate localDate = toLocalDate(date);
        if (localDate == null) {
            return null;
        }

        Month month = localDate.getMonth();
        Month firstMonthOfQuarter = month.firstMonthOfQuarter();
        Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
        if (isFirst) {
            resDate = LocalDate.of(localDate.getYear(), firstMonthOfQuarter, 1);
        } else {
            resDate = LocalDate.of(localDate.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(localDate.isLeapYear()));
        }
        return localDateToDate(resDate);
    }

    /**
     * 获取在 fromDate 和 toDate 之间的日期列表，包括 fromDate 和 toDate
     * @param fromDate
     * @param toDate
     * @return
     */
    public static List<Date> getDateListBetween(Date fromDate, Date toDate) {

        List<Date> dates = new ArrayList<>();

        if (fromDate == null || toDate == null){
            return dates;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);

        Date date = outHoutAndMinuteAndSecond(toDate);

        if (compareDate(calendar.getTime(),date)) {

            while (true) {

                dates.add(calendar.getTime());
                calendar.add(Calendar.DATE,1);

                if (calendar.getTime().getTime() >= date.getTime()) {
                    dates.add(toDate);
                    break;
                }
            }
        }
    	return dates;
    }

    /**
     * //去掉时分秒
     * @param date
     * @author leiliang
     * @date 2020/3/28
     * @return
     **/
    public static Date outHoutAndMinuteAndSecond (Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date2 = calendar.getTime();
        return date2;
    }

    /**
     * 获取每个月第一天
     * @param dateNum
     * @return
     */
    public static Date getFirstDateOfMonth(int dateNum) {
        String dateStr = String.valueOf(dateNum);
        int year = Integer.parseInt(dateStr.substring(0, 4));
        int month = Integer.parseInt(dateStr.substring(4, 6));
        LocalDate localDate = LocalDate.of(year, month, 1);
        return localDateToDate(localDate);
    }

    /**
     * 获得日期在一年中的天数
     *
     * @param date
     * @return
     */
    public static int getDayInYear(Date date) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        return gc.get(Calendar.DAY_OF_YEAR);
    }

    public static Date format(Integer val, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(String.valueOf(val));
        return  date;
    }

    public static Integer dateToInteger(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dateStr = sdf.format(date);
        return Integer.parseInt(dateStr);
    }

    public static Integer getNextMonth(Integer val, String format) throws ParseException {
       return addMonths(val, format, 1);
    }

    public static Integer addMonths(int month) {
        LocalDate localDate = LocalDate.now();
        localDate = localDate.plusMonths(month);
        Date date = localDateToDate(localDate);
        String dateStr = format(date, C_DATA_PATTON_YYYYMM);
        Integer result = Integer.parseInt(dateStr);
        return result;
    }

    public static Integer addMonths(Integer val, String format, int months) throws ParseException {
        Date date = format(val, format);
        date = addMonths(date, months);
        Integer nextMonth = dateToInteger(date, format);
        return nextMonth;
    }


    /**
     * 判断2个时间是否为同一年
     * @param date1
     * @param date2
     * @return
     */
    public static Boolean isSameYear(Date date1,Date date2){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    /**
     * 判断2个时间是否为同一月
     * @param date1
     * @param date2
     * @return
     */
    public static Boolean isSameMonth(Date date1,Date date2){
        if(!isSameYear(date1,date2)){
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime 当前时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取N年前
     *
     * @param source 源日期
     * @param type   计算类型
     * @param num    计算数
     * @return Date
     */
    public static Date getNumDate(Date source, Integer type, Integer num) {
        if (source == null || type == null || num == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(source);
        calendar.add(type, num);
        return calendar.getTime();
    }

    /**
     * 根据年龄获取计算该年龄的出生日期时间段
     *
     * @param age 年龄
     * @return List<Date> 时间集合，出生日期始、出生日期止
     */
    public static List<Date> getDateOfBirth(Integer age) {
        List<Date> dates = new ArrayList<>();
        // 年龄未填或填的0就默认为0岁
        if (age == null || age < 0) {
            age = 0;
        }
        // 出生日期始   当前日期年份-年龄-1  ；当前日+1
        Date beginDate = addDays(localDateToDate(addYears(new Date(), -(age + 1))),1);
        // 出生日期止   当前日期年份-年龄
        Date endDate = localDateToDate(addYears(new Date(), -age));
        dates.add(beginDate);
        dates.add(endDate);
        return dates;
    }

    public static long getDiffSecond(Date startDate, Date endDate) {
        long diffTime = endDate.getTime() - startDate.getTime();
        long diffSeconds = diffTime / 1000;
        return diffSeconds;
    }

    public static void main(String[] args) throws ParseException {
        String format = format(new Date());
        System.out.println(format);
    }

    /**
     * 计算两个日期之间的月份（天数大于时月份不加1）
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getDiffMonth(Date startDate, Date endDate) {
        LocalDate localStartDate = toLocalDate(startDate);
        LocalDate localEndDate = toLocalDate(endDate);
        Period period = Period.between(localStartDate, localEndDate);
        int diffYear = period.getYears();
        int diffMonth = period.getMonths();
        return diffYear * 12 + diffMonth;
    }

    /**
     * 获取从开始日期到结束日期间的所有指定格式的月份
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param format 期望的日期格式，例如 yyyy-MM、yyyy年MM月
     * @return 包含所有格式化后月份的列表
     */
    public static List<String> getFormatMonthsBetween(LocalDate startDate, LocalDate endDate, String format) {
        List<String> months = new ArrayList<>();
        YearMonth start = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        while (!start.isAfter(end)) {
            months.add(start.format(formatter));
            start = start.plusMonths(1);
        }

        return months;
    }

    /**
     * 获取从开始日期到结束日期间的所有指定格式的月份
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param format 期望的日期格式，例如 yyyy-MM、yyyy年MM月
     * @return 包含所有格式化后月份的列表
     */
    public static List<String> getFormatMonthsBetween(Date startDate, Date endDate, String format) {
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return getFormatMonthsBetween(start, end, format);
    }

    /**
     * 获取从开始日期到结束日期间的所有指定格式的日期
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param format 期望的日期格式，例如 yyyy-MM、yyyy年MM月
     * @return 包含所有格式化后日期的列表
     */
    public static List<String> getFormatDayBetween(Date startDate, Date endDate, String format) {
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return getFormatDaysBetween(start, end, format);
    }

    /**
     * 获取从开始日期到结束日期间的所有指定格式的日期
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param format 期望的日期格式，例如 yyyy-MM-dd
     * @return 包含所有格式化后日期的列表
     */
    public static List<String> getFormatDaysBetween(LocalDate startDate, LocalDate endDate, String format) {
        List<String> dates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        while (!startDate.isAfter(endDate)) {
            dates.add(startDate.format(formatter));
            startDate = startDate.plusDays(1);
        }

        return dates;
    }

}
