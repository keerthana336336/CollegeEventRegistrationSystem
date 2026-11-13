package com.collegeevent.util;

import javax.servlet.ServletContext;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Where the JSON files live and the first-run admin account. */
public final class AppConfig {
    public static final String DATA_DIR_KEY = "collegeevent.data.dir";
    public static final String ADMIN_EMAIL_KEY = "collegeevent.admin.email";
    public static final String ADMIN_PASSWORD_KEY = "collegeevent.admin.password";

    public static final String DEFAULT_ADMIN_EMAIL = "admin@college.edu";
    public static final String DEFAULT_ADMIN_PASSWORD = "Admin@123";

    private AppConfig() { }

    /**
     * Order: JVM property -Dcollegeevent.data.dir, environment variable COLLEGE_EVENT_DATA_DIR,
     * web.xml context-param, then C:\Users\&lt;you&gt;\CollegeEventRegistrationSystem\data (user.home).
     * This folder is outside Tomcat's deployment directory so redeploying never erases data.
     */
    public static Path resolveDataDir(ServletContext ctx) {
        String v = System.getProperty(DATA_DIR_KEY);
        if (isBlank(v)) v = System.getenv("COLLEGE_EVENT_DATA_DIR");
        if (isBlank(v) && ctx != null) v = ctx.getInitParameter(DATA_DIR_KEY);
        if (isBlank(v)) return Paths.get(System.getProperty("user.home"), "CollegeEventRegistrationSystem", "data");
        return Paths.get(v.trim());
    }

    public static String adminEmail() {
        String v = System.getProperty(ADMIN_EMAIL_KEY);
        return isBlank(v) ? DEFAULT_ADMIN_EMAIL : v.trim().toLowerCase();
    }

    public static String adminPassword() {
        String v = System.getProperty(ADMIN_PASSWORD_KEY);
        return isBlank(v) ? DEFAULT_ADMIN_PASSWORD : v;
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
