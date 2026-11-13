package com.collegeevent.util;

import java.util.Map;
import java.util.regex.Pattern;

/** Server-side validation. Methods add messages to the errors map, keyed by form field name. */
public final class Validator {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern STUDENT_ID = Pattern.compile("^[A-Za-z0-9/_-]{3,20}$");

    private Validator() { }

    public static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    public static void name(String v, Map<String, String> errors) {
        if (isBlank(v) || v.trim().length() < 2 || v.trim().length() > 80)
            errors.put("name", "Enter your full name (2 to 80 characters).");
    }

    public static void email(String v, Map<String, String> errors) {
        if (isBlank(v) || v.length() > 120 || !EMAIL.matcher(v.trim()).matches())
            errors.put("email", "Enter a valid e-mail address.");
    }

    public static void studentId(String v, Map<String, String> errors) {
        if (isBlank(v) || !STUDENT_ID.matcher(v.trim()).matches())
            errors.put("studentId", "Student ID must be 3 to 20 letters, numbers, - or /.");
    }

    public static void department(String v, Map<String, String> errors) {
        if (!Categories.isValidDepartment(v)) errors.put("department", "Choose your department from the list.");
    }

    public static int year(String v, Map<String, String> errors) {
        try {
            int y = Integer.parseInt(v == null ? "" : v.trim());
            if (y >= 1 && y <= 5) return y;
        } catch (NumberFormatException ignored) { /* falls through */ }
        errors.put("year", "Choose your year of study (1 to 5).");
        return 0;
    }

    public static void newPassword(String password, String confirm, Map<String, String> errors) {
        if (password == null || password.length() < 8 || password.length() > 100
                || !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            errors.put("password", "Use at least 8 characters with a letter and a number.");
        } else if (!password.equals(confirm)) {
            errors.put("confirmPassword", "The two passwords do not match.");
        }
    }
}
