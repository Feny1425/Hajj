package feny.job.hajj.Utils;

import java.util.Calendar;

public class DateUtils {

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Format the date as desired
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public static String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        // Format the date and time as desired
        return String.format("%04d_%02d_%02d %02d_%02d_%02d", year, month, day, hour, minute, second);
    }
}
