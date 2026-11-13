package com.collegeevent.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/** One-time notification messages shown after a redirect (see partials/flash.jsp). */
public final class Flash {
    private Flash() { }

    public static void success(HttpServletRequest req, String message) { set(req, "success", message); }

    public static void error(HttpServletRequest req, String message) { set(req, "error", message); }

    private static void set(HttpServletRequest req, String type, String message) {
        HttpSession session = req.getSession(true);
        session.setAttribute("flashType", type);
        session.setAttribute("flashMessage", message);
    }
}
