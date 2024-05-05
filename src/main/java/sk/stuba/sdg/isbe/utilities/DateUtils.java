package sk.stuba.sdg.isbe.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {

    public static String getFormattedDateTime(long time) {
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        return formatter.format(date);
    }
}
