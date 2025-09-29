package com.example.electionmanagement.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    // ISO format we use
    public static final String ISO = "yyyy-MM-dd HH:mm";

    public static String format(Date d) {
        return new SimpleDateFormat(ISO, Locale.getDefault()).format(d);
    }

    public static Date parse(String iso) {
        try {
            return new SimpleDateFormat(ISO, Locale.getDefault()).parse(iso);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isNowBetween(String startIso, String endIso) {
        Date s = parse(startIso);
        Date e = parse(endIso);
        Date now = new Date();
        if (s == null || e == null) return false;
        return now.after(s) && now.before(e);
    }

    public static boolean isEnded(String endIso) {
        Date e = parse(endIso);
        Date now = new Date();
        if (e == null) return true;
        return now.after(e) || now.equals(e);
    }

    // New method to get status of election
    public static String getStatus(String startIso, String endIso) {
        Date start = parse(startIso);
        Date end = parse(endIso);
        Date now = new Date();
        if (start == null || end == null) return "Unknown";

        if (now.before(start)) {
            return "Upcoming";
        } else if (now.after(end)) {
            return "Ended";
        } else {
            return "Ongoing";
        }
    }
}
