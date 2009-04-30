/*
 * Copyright 2007-2009 非也
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

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */

package org.fireflow.engine.calendar;

import java.util.Date;
import java.util.Properties;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.model.Duration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on 2009-03-11
 */
public class DefaultCalendarServiceTest {

    public DefaultCalendarServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of dateAfter method, of class DefaultCalendarService.
     */
    @Test
    public void testDateAfter() {
        System.out.println("dateAfter");
        Date fromDate = null;
        Duration duration = null;
        DefaultCalendarService instance = new DefaultCalendarService();
        Date expResult = null;
        Date result = instance.dateAfter(fromDate, duration);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of businessDateAfter method, of class DefaultCalendarService.
     */
    @Test
    public void testBusinessDateAfter() {
        System.out.println("businessDateAfter");
        Date fromDate = null;
        int totalDurationInMillseconds = 0;
        DefaultCalendarService instance = new DefaultCalendarService();
        Date expResult = null;
        Date result = instance.businessDateAfter(fromDate, totalDurationInMillseconds);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTotalWorkingTime method, of class DefaultCalendarService.
     */
    @Test
    public void testGetTotalWorkingTime() {
        System.out.println("getTotalWorkingTime");
        Date date = null;
        DefaultCalendarService instance = new DefaultCalendarService();
        int expResult = 0;
        int result = instance.getTotalWorkingTime(date);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isBusinessDay method, of class DefaultCalendarService.
     */
    @Test
    public void testIsBusinessDay() {
        System.out.println("isBusinessDay");
        Date d = null;
        DefaultCalendarService instance = new DefaultCalendarService();
        boolean expResult = false;
        boolean result = instance.isBusinessDay(d);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBusinessCalendarProperties method, of class DefaultCalendarService.
     */
    @Test
    public void testGetBusinessCalendarProperties() {
        System.out.println("getBusinessCalendarProperties");
        DefaultCalendarService instance = new DefaultCalendarService();
        Properties expResult = null;
        Properties result = instance.getBusinessCalendarProperties();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setBusinessCalendarProperties method, of class DefaultCalendarService.
     */
    @Test
    public void testSetBusinessCalendarProperties() {
        System.out.println("setBusinessCalendarProperties");
        Properties props = null;
        DefaultCalendarService instance = new DefaultCalendarService();
        instance.setBusinessCalendarProperties(props);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSysDate method, of class DefaultCalendarService.
     */
    @Test
    public void testGetSysDate() {
        System.out.println("getSysDate");
        DefaultCalendarService instance = new DefaultCalendarService();
        Date expResult = null;
        Date result = instance.getSysDate();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setRuntimeContext method, of class DefaultCalendarService.
     */
    @Test
    public void testSetRuntimeContext() {
        System.out.println("setRuntimeContext");
        RuntimeContext ctx = null;
        DefaultCalendarService instance = new DefaultCalendarService();
        instance.setRuntimeContext(ctx);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRuntimeContext method, of class DefaultCalendarService.
     */
    @Test
    public void testGetRuntimeContext() {
        System.out.println("getRuntimeContext");
        DefaultCalendarService instance = new DefaultCalendarService();
        RuntimeContext expResult = null;
        RuntimeContext result = instance.getRuntimeContext();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}