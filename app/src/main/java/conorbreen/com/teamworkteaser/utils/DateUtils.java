package conorbreen.com.teamworkteaser.utils;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by Conor Breen on 29/01/2018.
 */

public class DateUtils {
    public static String formatDate(Date date) {
        return DateFormat.format("dd/MM/yyyy", date).toString();
    }
    public static String formatTime(Date date) {
        return DateFormat.format("HH:mm", date).toString();
    }

    public static Date stripTimeFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.MONTH);
    }

    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.YEAR);
    }

    public static String getMonth3LetterFormat(Date date) {
        return DateFormat.format("MMM", date).toString();
    }
}
