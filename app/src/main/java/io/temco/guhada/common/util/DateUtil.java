package io.temco.guhada.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import io.temco.guhada.common.Type;

public class DateUtil {

    // 오늘 일자
    public static Calendar getToday() {
        return Calendar.getInstance(TimeZone.getDefault());
    }

    public static String getTodayToString(Type.DateFormat type) {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        return getCalendarToString(type, c);
    }

    // Calendar String 변환
    public static String getCalendarToString(Type.DateFormat type, Calendar c) {
        SimpleDateFormat f = new SimpleDateFormat(Type.DateFormat.get(type));
        return f.format(c.getTime());
    }




}
