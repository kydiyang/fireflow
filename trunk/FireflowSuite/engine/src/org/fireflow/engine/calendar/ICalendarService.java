/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fireflow.engine.calendar;

import java.util.Date;
import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.model.Duration;


/**
 *
 * @author chennieyun
 */
public interface ICalendarService extends IRuntimeContextAware{

    /**
     * Get the date after the duration
     * @param duration
     * @return
     */
    public Date dateAfter(Date fromDate, Duration duration);

    /**
     * 判断给定的日期是否为工作日
     * @param d
     * @return
     */
    public boolean isBusinessDay(Date d);

    public Date getSysDate();
}
