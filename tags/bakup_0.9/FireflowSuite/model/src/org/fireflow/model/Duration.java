/**
 * Copyright 2007-2008 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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
package org.fireflow.model;

import java.io.Serializable;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

/**
 * 时间间隔
 * @author 非也,nychen2000@163.com
 */
public class Duration implements Serializable {

    public static final String DAY = "DAY";
    public static final String MONTH = "MONTH";
    public static final String YEAR = "YEAR";
    public static final String HOUR = "HOUR";
    public static final String MINUTE = "MINUTE";
    public static final String SECOND = "SECOND";
    public static final String WEEK = "WEEK";//    private static final Log log = LogFactory.getLog(Duration.class);
    private int value;
    private String unit;
    private boolean isBusinessTime = true;

    /**
     * 创建时间间隔对象
     * @param value 时间值
     * @param unit 时间单位
     */
    public Duration(int value, String unit) {
        this.value = value;
        this.unit = unit;

//        log.debug("Duration(" + value + ", " + unit + ")");
    }
    
    /**
     * 获取时间值
     * @return
     */
    public int getValue() {
        return value;
    }

    public void setValue(int v) {
        value = v;
    }

    /**
     * 获取时间单位
     * @return
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 设置时间单位
     * @param u
     */
    public void setUnit(String u) {
        unit = u;
    }

    /**
     * 获取时间单位，如果时间单位为null，则返回defaultUnit
     * @param defaultUnit
     * @return
     */
    public String getUnit(String defaultUnit) {
        if (unit == null) {
            return defaultUnit;
        } else {
            return unit;
        }
    }

    /**
     * 获取换算成毫秒的时间间隔值
     * @param defaultUnit
     * @return
     */
    public int getDurationInMilliseconds(String defaultUnit) {
        int value = getValue();
        String unit = getUnit(defaultUnit);

        //log.debug("Duration value: " + value);
        //log.debug("Duration unit: " + unit);
        //log.debug("Unit in MS: " + unit.toMilliseconds());

        if (value == 0) {
            return value;
        } else {
            int duration = value * toMilliseconds(unit);
            //log.debug("Duration in MS: " + duration);
            return duration;
        }
    }

    public int toMilliseconds(String unit) {
        if (unit == null) {
            return 0;
        } else if (unit.equals(SECOND)) {
            return 1 * 1000;
        } else if (unit.equals(MINUTE)) {
            return 60 * 1000;
        } else if (unit.equals(HOUR)) {
            return 60 * 60 * 1000;
        } else if (unit.equals(DAY)) {
            return 24 * 60 * 60 * 1000;
        } else if (unit.equals(MONTH)) {
            return 30 * 24 * 60 * 60 * 1000;
        } else if (unit.equals(YEAR)) {
            return 365 * 30 * 24 * 60 * 60 * 1000;
        } else if (unit.equals(WEEK)) {
            return 7 * 24 * 60 * 60 * 1000;
        } else {
            return 0;
        }
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(value);
        if (unit != null) {
            buffer.append(unit);
        }
        return buffer.toString();
    }

    /**
     * 时间间隔是否指工作时间
     * @return
     */
    public boolean isBusinessTime() {
        return isBusinessTime;
    }

    /**
     * 设置时间间隔的属性，isBusinessTime==true表示时间间隔指工作时间
     * @param isBusinessTime
     */
    public void setBusinessTime(boolean isBusinessTime) {
        this.isBusinessTime = isBusinessTime;
    }
    /** Parse the duration string into a Duration object.
    
    @param durationString The String
    @return The Duration object
    @throws NumberFormatException
     *///    public static Duration parse(String durationString)
//        throws NumberFormatException {
//
//        if (durationString == null) {
//            throw new IllegalArgumentException(
//                "Duration string cannot be null");
//        }
//
//        StringBuffer intBuffer = new StringBuffer();
//        String unit = null;
//        for (int i = 0; i < durationString.length(); i++) {
//            char c = durationString.charAt(i);
//            if (Character.isDigit(c)) {
//                intBuffer.append(c);
//            } else if (Character.isLetter(c)) {
//                char[] cArray = {c};
//                unit = DurationUnit.fromString(new String(cArray));
//            }
//        }
//
//        if (intBuffer.toString().equals("")) {
//            return new Duration(0, unit);
//        } else {
//            return new Duration(Integer.parseInt(intBuffer.toString()), unit);
//        }
//    }
}