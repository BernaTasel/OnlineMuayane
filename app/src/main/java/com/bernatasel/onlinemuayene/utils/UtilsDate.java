package com.bernatasel.onlinemuayene.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UtilsDate {
    private static final String DEFAULT_DATETIME_FORMAT = "dd.MM.yyyy HH:mm:ss";
    private static final String DEFAULT_DATETIME_FORMAT_DD_MM_YYYY = "dd.MM.yyyy";

    public static String timestampToHumanReadable(long millis) {
        Date date = new Date(millis);
        return new SimpleDateFormat(DEFAULT_DATETIME_FORMAT, Locale.getDefault()).format(date);
    }

    public static String timestampToHumanReadable_DD_MM_YYYY(long millis) {
        Date date = new Date(millis);
        return new SimpleDateFormat(DEFAULT_DATETIME_FORMAT_DD_MM_YYYY, Locale.getDefault()).format(date);
    }

    public static String timestampToHumanReadable1(long millis) {
        Date date = new Date(millis);
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
    }
}
