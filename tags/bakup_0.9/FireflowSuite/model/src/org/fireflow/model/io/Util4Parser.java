package org.fireflow.model.io;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.QName;
import org.fireflow.model.Duration;



public class Util4Parser {

    //private static final DateFormat STANDARD_DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Util4Parser() {
        // no op
    }

    /** Return the child element with the given name.  The element must be in
     the same name space as the parent element.

     @param element The parent element
     @param name The child element name
     @return The child element
     */

    public static Element child(Element element, String name) {
        return element.element(new QName(name, element.getNamespace()));
    }

    /** Return the child elements with the given name.  The elements must be in
     the same name space as the parent element.

     @param element The parent element
     @param name The child element name
     @return The child elements
     */

    public static List<Element> children(Element element, String name) {
        if (element==null){
            return null;
        }
        return element.elements(new QName(name, element.getNamespace()));
    }

    // Conversion

    /** Return the value of the child element with the given name.  The element
     must be in the same name space as the parent element.

     @param element The parent element
     @param name The child element name
     @return The child element value
     */

    public static String elementAsString(Element element, String name) {
        String s = element.elementTextTrim(
            new QName(name, element.getNamespace()));
        return (s == null || s.length() == 0) ? null : s;
    }

    public static Date elementAsDate(Element element, String name) throws
        FPDLParserException {
        String text = elementAsString(element, name);
        if (text == null) {
            return null;
        }

        try {
            return DateUtilities.getInstance().parse(text);
            //return STANDARD_DF.parse(text);
        } catch (ParseException e) {
            throw new FPDLParserException("Error parsing date: " + text, e);
        }
    }

    public static int elementAsInteger(Element element, String name) {
        String text = elementAsString(element, name);
        if (text == null) {
            return 0;
        }

        return Integer.parseInt(text);
    }

    public static boolean elementAsBoolean(Element element, String name) {
        String text = elementAsString(element, name);
        if (text == null) {
            return false;
        }

        return new Boolean(text).booleanValue();
    }

    public static URL elementAsURL(Element element, String name) throws
        FPDLParserException {
        String text = elementAsString(element, name);
        if (text == null) {
            return null;
        }

        try {
            return new URL(text);
        } catch (MalformedURLException e) {
            throw new FPDLParserException("Invalid URL: " + text, e);
        }
    }


}
