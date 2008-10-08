
package org.fireflow.model;

import java.io.Serializable;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class Duration implements Serializable {
    public static final String DAY = "DAY";
    public static final String MONTH = "MONTH";
    public static final String YEAR = "YEAR";
    public static final String HOUR = "HOUR";
    public static final String MINUTE = "MINUTE";
    public static final String SECOND = "SECOND";
    public static final String WEEK = "WEEK";

//    private static final Log log = LogFactory.getLog(Duration.class);

    private int value;
    private String unit;
    private boolean isBusinessTime = true;

    /** Construct a new Duration.  A value of 0 signals an unlimited duration.
     The duration unit may be null to specify that the default should be used.
     The default is determined at runtime. 

     @param value The duration value
     @param unit The unit of measurement
     */

    public Duration(int value, String unit) {
        this.value = value;
        this.unit = unit;

//        log.debug("Duration(" + value + ", " + unit + ")");
    }

    /** Construct a new Duration.

     @param durationString The duration value
     @throws NumberFormatException
     */

//    public Duration(String durationString) throws NumberFormatException {
//        Duration d = Duration.parse(durationString);
//
//        this.value = d.getValue();
//        this.unit = d.getUnit();
//    }

    /** The duration value.

     @return The duration value
     */

    public int getValue() {
        return value;
    }
    
    public void setValue(int v ){
        value = v;
    }

    /** Return this duration's unit.  This method may return null if the
     unit is not specified.

     @return The duration unit or null
     */

    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String u){
        unit = u;
    }

    /** Get the duration's unit.  The specified default duration unit is
     used if this duration has no specified duration unit.

     @param defaultUnit The default unit if no unit specified
     @return The duration unit
     */

    public String getUnit(String defaultUnit) {
        if (unit == null) {
            return defaultUnit;
        } else {
            return unit;
        }
    }

    /** Get the duration represented as millseconds.  The specified default
     duration unit is used this duration has no specified duration unit.

     @param defaultUnit The default unit if no unit specified
     @return The number of milliseconds for this duration
     */

    public long getDurationInMilliseconds(String defaultUnit) {
        int value = getValue();
        String unit = getUnit(defaultUnit);

        //log.debug("Duration value: " + value);
        //log.debug("Duration unit: " + unit);
        //log.debug("Unit in MS: " + unit.toMilliseconds());

        if (value == 0) {
            return value;
        } else {
            long duration = value * toMilliseconds(unit);
            //log.debug("Duration in MS: " + duration);
            return duration;
        }
    }
    
    public long toMilliseconds(String unit){
    	if (unit==null)return 0l;
    	else if (unit.equals(SECOND)){
    		return 1*1000;
    	}else if (unit.equals(MINUTE)){
    		return 60*1000;
    	}else if (unit.equals(HOUR)){
    		return 60*60*1000;
    	}else if (unit.equals(DAY)){
    		return 24*60*60*1000;
    	}else if (unit.equals(MONTH)){
    		return 30*24*60*60*1000;
    	}else if (unit.equals(YEAR)){
    		return 365*30*24*60*60*1000;
    	}else{
    		return 0;
    	}
    }

    /** Return a String representation of the Duration.

     @return A string
     */

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(value);
        if (unit != null) {
            buffer.append(unit);
        }
        return buffer.toString();
    }

	public boolean isBusinessTime() {
		return isBusinessTime;
	}

	public void setBusinessTime(boolean isBusinessTime) {
		this.isBusinessTime = isBusinessTime;
	}

    /** Parse the duration string into a Duration object.

     @param durationString The String
     @return The Duration object
     @throws NumberFormatException
     */

//    public static Duration parse(String durationString)
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