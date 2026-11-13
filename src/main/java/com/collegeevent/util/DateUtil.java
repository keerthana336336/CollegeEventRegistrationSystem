package com.collegeevent.util;

import com.collegeevent.model.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Small helpers for the string based dates used in the JSON files. */
public final class DateUtil {
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter DEADLINE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter DATE_LONG = DateTimeFormatter.ofPattern("EEE, d MMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_12H = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a", Locale.ENGLISH);

    private DateUtil() { }

    public static LocalDate parseDate(String s) {
        try { return s == null ? null : LocalDate.parse(s.trim()); } catch (Exception e) { return null; }
    }

    public static LocalTime parseTime(String s) {
        try { return s == null ? null : LocalTime.parse(s.trim()); } catch (Exception e) { return null; }
    }

    /** Accepts "yyyy-MM-ddTHH:mm" (HTML datetime-local) with or without seconds. */
    public static LocalDateTime parseDateTime(String s) {
        try { return s == null ? null : LocalDateTime.parse(s.trim()); } catch (Exception e) { return null; }
    }

    public static String formatDeadline(LocalDateTime dt) { return dt.format(DEADLINE); }

    public static String nowStamp() { return LocalDateTime.now().format(STAMP); }

    public static LocalDateTime eventStart(Event e) {
        LocalDate d = parseDate(e.getDate());
        LocalTime t = parseTime(e.getStartTime());
        return (d == null || t == null) ? null : LocalDateTime.of(d, t);
    }

    public static LocalDateTime eventEnd(Event e) {
        LocalDate d = parseDate(e.getDate());
        LocalTime t = parseTime(e.getEndTime());
        return (d == null || t == null) ? null : LocalDateTime.of(d, t);
    }

    public static String formatDate(String iso) {
        LocalDate d = parseDate(iso);
        return d == null ? "" : d.format(DATE_LONG);
    }

    public static String formatTime(String hhmm) {
        LocalTime t = parseTime(hhmm);
        return t == null ? "" : t.format(TIME_12H);
    }

    public static String formatDateTime(String iso) {
        LocalDateTime dt = parseDateTime(iso);
        return dt == null ? "" : dt.format(DATE_TIME);
    }
}
