/**
 * Copyright 2003-2008 陈乜云（非也,Chen Nieyun）
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.engine.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.model.Duration;

/**
 * 
 * @author chennieyun
 */
public class DefaultCalendarService implements ICalendarService {

    public static final String hour_format = "hour_format";
    public static final String day_format = "day_format";
//    public static final String business_time_format = "business_time_format";
    public static final String business_time = "business_time";
    public static final String business_time_monday = "business_time.monday";
    public static final String business_time_tuesday = "business_time.tuesday";
    public static final String business_time_wednesday = "business_time.wednesday";
    public static final String business_time_thursday = "business_time.thursday";
    public static final String business_time_friday = "business_time.friday";
    public static final String business_time_saturday = "business_time.saturday";
    public static final String business_time_sunday = "business_time.sunday";
    public static final String hours_of_business_day = "hours_of_business_day";
//    public static final String hours_of_business_week = "hours_of_business_week";

    //用于日历换算的属性
    private Properties businessCalendarProperties = new Properties();
    
    protected RuntimeContext rtCtx = null;

    public DefaultCalendarService() {
        //初始化businessCalendarProperties
        businessCalendarProperties.setProperty(hour_format, "HH:mm");
        businessCalendarProperties.setProperty(day_format, "yyyy-MM-dd");
        businessCalendarProperties.setProperty(business_time, "8:30-12:00 & 13:30-17:30");
        businessCalendarProperties.setProperty(hours_of_business_day, "7.5");
    }

    /**
     * 计算一定时间间隔后的日期。
     * 在本缺省实现中只区分工作日/自然日，工作时/自然时；其他都按照自然时间间隔计算。
     * 这种实现方法已经满足绝大多数业务需求。
     * @param fromDate 开始日期
     * @param duration 时间间隔
     * @return
     */
    public Date dateAfter(Date fromDate, Duration duration) {
        if (duration.getUnit().equals(Duration.SECOND) ||
                duration.getUnit().equals(Duration.MINUTE) ||
                duration.getUnit().equals(Duration.WEEK) ||
                duration.getUnit().equals(Duration.MONTH) ||
                duration.getUnit().equals(Duration.YEAR) ||
                (duration.getUnit().equals(Duration.DAY) && !duration.isBusinessTime()) ||
                (duration.getUnit().equals(Duration.HOUR) && !duration.isBusinessTime())) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(fromDate);
            if (duration.getUnit().equals(Duration.MONTH)) {
                cal.add(Calendar.MONTH, duration.getValue());
            } else if (duration.getUnit().equals(Duration.YEAR)) {
                cal.add(Calendar.YEAR, duration.getValue());
            } else {
                cal.add(Calendar.MILLISECOND, duration.getDurationInMilliseconds(null));
            }
            return cal.getTime();
        } //计算工作日间隔
        else if (duration.getUnit().equals(Duration.DAY) && duration.isBusinessTime()) {
            float hoursPerDay = Float.parseFloat(this.getBusinessCalendarProperties().getProperty(hours_of_business_day));
            int totalDurationInMillseconds = (int) (duration.getValue() * hoursPerDay * 60 * 60 * 1000);

            return businessDateAfter(fromDate, totalDurationInMillseconds);

        } //计算工作时间隔
        else if (duration.getUnit().equals(Duration.HOUR) && duration.isBusinessTime()) {
            int totalDurationInMillseconds = (int) (duration.getValue() * 60 * 60 * 1000);
            return businessDateAfter(fromDate, totalDurationInMillseconds);
        }
        return null;
    }

    protected Date businessDateAfter(Date fromDate, int totalDurationInMillseconds) {
        int remaining = totalDurationInMillseconds;
        int workingTimeOfDay = 0;
        Date theDate = fromDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat(this.businessCalendarProperties.getProperty(hour_format));
        boolean spanDay = false;
        while (remaining > 0) {
            workingTimeOfDay = this.getTotalWorkingTime(theDate);
            remaining = remaining - workingTimeOfDay;

            if (remaining > 0) {
                //theDate = theDate+(1 biz day);
                Calendar cal = Calendar.getInstance();
                cal.setTime(theDate);
                cal.add(Calendar.DATE, 1);
                while (!this.isBusinessDay(cal.getTime())) {
                    cal.add(Calendar.DATE, 1);
                }
                spanDay = true;

                //定位到新的一天的开始工作时间点
                String businessTime = this.getBusinessTime(cal.getTime());
                int idx = businessTime.indexOf("-");
                Date dateTmp = null;
                try {
                    dateTmp = dateFormat.parse(businessTime.substring(0, idx));
                } catch (ParseException ex) {
                    Logger.getLogger(DefaultCalendarService.class.getName()).log(Level.SEVERE, null, ex);
                }
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(dateTmp);
                cal.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal2.get(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, cal2.get(Calendar.MILLISECOND));
                theDate = cal.getTime();
            }
        }

        if (remaining < 0) {
            //精确计算结束时间点                
            remaining = remaining + workingTimeOfDay;

            String businessTime = getBusinessTime(theDate);
            if (businessTime != null) {
                StringTokenizer stringTokenizer = new StringTokenizer(businessTime, "&");
                int totalTime = 0;
                while (stringTokenizer.hasMoreTokens()) {
                    String bizTimeSpan = stringTokenizer.nextToken().trim();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(theDate);
                    //如果跨了日期或者当前时间在工作时间段之前，则定位到下一时间段的起始点
                    int inTheSpan = testTimeInTheTimeSpan(theDate, bizTimeSpan);
                    if (spanDay || inTheSpan == -1) {

                        int index1 = bizTimeSpan.indexOf(":");
                        int index2 = bizTimeSpan.indexOf("-");
                        int hour = Integer.parseInt(bizTimeSpan.substring(0, index1));
                        int minute = Integer.parseInt(bizTimeSpan.substring(index1 + 1, index2));

                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minute);
                        theDate = cal.getTime();
                    } else if (inTheSpan == 1) {
                        continue;
                    } else if (inTheSpan == 0) {
                        String timeStart = dateFormat.format(theDate);
                        int indexTmp = bizTimeSpan.indexOf("-");
                        bizTimeSpan = timeStart + bizTimeSpan.substring(indexTmp);
                    }

                    totalTime = getTotalWorkingTime(bizTimeSpan);
                    if (totalTime < remaining) {
                        remaining = remaining - totalTime;

                    } else {
                        cal.add(Calendar.MILLISECOND, remaining);
                        theDate = cal.getTime();
                        break;
                    }
                }
            }
        }

        return theDate;
    }

    /**
     * 获得当天的(剩余)工时，以毫秒为单位
     * @param date
     * @return
     */
    public int getTotalWorkingTime(Date date) {
        if (!this.isBusinessDay(date)) {
            return 0;//非工作日的工时为0
        }

        String businessTime = getBusinessTime(date);
        if (businessTime == null) {
            return 0;
        }

//判断date属于哪个时间段
        SimpleDateFormat dFormat = new SimpleDateFormat(this.businessCalendarProperties.getProperty(hour_format));
        String paramTimeStr = dFormat.format(date);
        StringTokenizer stringTokenizer = new StringTokenizer(businessTime, "&");
        List timeSpanList = new ArrayList();
        while (stringTokenizer.hasMoreTokens()) {
            String timeSpan = stringTokenizer.nextToken();
            int isInTheSpan = testTimeInTheTimeSpan(date, timeSpan);
            if (isInTheSpan == -1) {
                timeSpanList.add(timeSpan);
            } else if (isInTheSpan == 0) {
                String tmp = paramTimeStr + timeSpan.substring(timeSpan.indexOf("-"));
                timeSpanList.add(tmp);
            }

        }

        int totalTime = 0;
        for (int i = 0; i <
                timeSpanList.size(); i++) {
            String timeSpan = (String) timeSpanList.get(i);
            totalTime =
                    totalTime + getTotalWorkingTime(timeSpan);
        }

        return totalTime;
    }

    /**
     * 检查时间点是否在给定的区间内
     * @param timeStr
     * @param timeSpan
     * @return 0-在给定的区间内，-1在给定的区间前，1在给定的区间后
     */
    private int testTimeInTheTimeSpan(Date d, String timeSpan) {
        SimpleDateFormat dFormat = new SimpleDateFormat(this.businessCalendarProperties.getProperty(day_format));
        String dayStr = dFormat.format(d);
        dFormat.applyPattern(this.businessCalendarProperties.getProperty(day_format) + " " + this.businessCalendarProperties.getProperty(hour_format));
        int idx = timeSpan.indexOf("-");
        String date1Str = dayStr + " " + timeSpan.substring(0, idx);
        String date2Str = dayStr + " " + timeSpan.substring(idx + 1);
        Date date1 = null;
        Date date2 = null;
        try {
            date2 = dFormat.parse(date2Str);
        } catch (ParseException ex) {
            Logger.getLogger(DefaultCalendarService.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            date1 = dFormat.parse(date1Str);
        } catch (ParseException ex) {
            Logger.getLogger(DefaultCalendarService.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (d.before(date1)) {
            return -1;
        } else if (d.after(date2)) {
            return 1;
        }

        return 0;
    }

    private String getBusinessTime(Date date) {
        String businessTime = this.businessCalendarProperties.getProperty(business_time);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String businessTime2 = null;
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1:
                businessTime2 = this.businessCalendarProperties.getProperty(business_time_sunday);//周日
                break;

            case 2:
                businessTime2 = this.businessCalendarProperties.getProperty(business_time_monday);//周1
                break;

            case 3:
                businessTime2 = this.businessCalendarProperties.getProperty(business_time_tuesday);//周2
                break;

            case 4:
                businessTime2 = this.businessCalendarProperties.getProperty(business_time_wednesday);//周3
                break;

            case 5:
                businessTime2 = this.businessCalendarProperties.getProperty(business_time_thursday);//周4
                break;

            case 6:
                businessTime2 = this.businessCalendarProperties.getProperty(business_time_friday);//周5
                break;

            case 7:
                businessTime2 = this.businessCalendarProperties.getProperty(business_time_saturday);//周六
                break;

        }


        if (businessTime2 != null) {
            businessTime = businessTime2;
        }

        return businessTime;
    }

    /**
     * 获得时间段的工作时间，以毫秒表示
     * @param timeSpan 例如:8:30-12:00
     * @return
     */
    private int getTotalWorkingTime(String timeSpan) {
        if (timeSpan == null) {
            return 0;
        }

        StringTokenizer stringTokenizer = new StringTokenizer(timeSpan, "-");
        String dStr1 = null;
        String dStr2 = null;
        if (stringTokenizer.hasMoreTokens()) {
            dStr1 = stringTokenizer.nextToken();
        }

        if (stringTokenizer.hasMoreTokens()) {
            dStr2 = stringTokenizer.nextToken();
        }

        if (dStr1 == null || dStr2 == null) {
            return 0;
        }

        SimpleDateFormat dFormat = new SimpleDateFormat(this.businessCalendarProperties.getProperty(hour_format));
        Date d1 = null;
        try {
            d1 = dFormat.parse(dStr1);
        } catch (ParseException ex) {
            Logger.getLogger(DefaultCalendarService.class.getName()).log(Level.SEVERE, null, ex);
        }
        Date d2 = null;
        try {
            d2 = dFormat.parse(dStr2);
        } catch (ParseException ex) {
            Logger.getLogger(DefaultCalendarService.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (d1 == null || d2 == null) {
            return 0;
        }

        return (int) (d2.getTime() - d1.getTime());

    }
//    private int getDurationInMilliseconds
    /**
     * 缺省实现，周六周日都是非工作日，其他的都为工作日。
     * 实际应用中，可以在数据库中建立一张非工作日表，将周末以及法定节假日录入其中，
     * 然后在该方法中读该表的数据来判断工作日和非工作日。
     * @param d
     * @return
     */
    public boolean isBusinessDay(Date d) {
        if (d == null) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1 || dayOfWeek == 7) {
            return false;
        }

        return true;
    }

    public Properties getBusinessCalendarProperties() {
        return businessCalendarProperties;
    }

    /**
     * 在外部定义的属性被合并到缺省定义中，这样的话外部之需要修改部分属性即可。
     * @param props
     */
    public void setBusinessCalendarProperties(Properties props) {
        this.businessCalendarProperties.putAll(props);
    }
    
    public Date getSysDate() {
        return new Date();
    }

    public void setRuntimeContext(RuntimeContext ctx) {
        this.rtCtx = ctx;
    }
    
    public RuntimeContext getRuntimeContext(){
        return this.rtCtx;
    }    
}
