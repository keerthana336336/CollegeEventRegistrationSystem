package com.collegeevent.util;

/** Builds CSV lines that open safely in Excel. */
public final class CsvUtil {
    private CsvUtil() { }

    public static String row(String... cells) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(cells[i]));
        }
        return sb.append("\r\n").toString();
    }

    public static String escape(String value) {
        if (value == null) return "";
        String v = value;
        // Stop spreadsheet formula injection (cells starting with = + - @)
        if (!v.isEmpty() && "=+-@\t\r".indexOf(v.charAt(0)) >= 0) v = "'" + v;
        if (v.indexOf(',') >= 0 || v.indexOf('"') >= 0 || v.indexOf('\n') >= 0 || v.indexOf('\r') >= 0) {
            v = "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }
}
