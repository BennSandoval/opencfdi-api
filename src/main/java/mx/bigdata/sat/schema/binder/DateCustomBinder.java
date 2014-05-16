package mx.bigdata.sat.schema.binder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: benn_sandoval
 * Date: 3/21/14
 * Time: 6:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateCustomBinder {

    public static Date parseDate(String s) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String printDate(Date dt) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return (dt == null) ? null : formatter.format(dt);
    }

    public static Date parseDateTime(String s) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return formatter.parse(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String printDateTime(Date dt) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return (dt == null) ? null : formatter.format(dt);
    }

    public static Date parseTime(String s) {
        try {
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            return formatter.parse(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String printTime(Date dt) {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return (dt == null) ? null : formatter.format(dt);
    }

}
