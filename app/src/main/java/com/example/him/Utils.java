package com.example.him;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    /* 날자 구하기 */
    public static String getDate(String format, long timeMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date date = new Date(timeMillis);

        return dateFormat.format(date);
    }

}
