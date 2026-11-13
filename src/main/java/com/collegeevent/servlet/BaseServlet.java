package com.collegeevent.servlet;

import com.collegeevent.model.User;
import com.collegeevent.util.Categories;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** Shared helpers for all servlets: forwarding to JSPs, redirects and session handling. */
public abstract class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected static final String VIEWS = "/WEB-INF/views/";
    protected static final String SESSION_USER_ID = "userId";
    protected static final String SESSION_USER_NAME = "userName";
    protected static final String SESSION_USER_EMAIL = "userEmail";
    protected static final String SESSION_USER_ROLE = "userRole";

    protected void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher(VIEWS + view).forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    /** Trimmed request parameter, never null. */
    protected String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }

    protected String currentUserId(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (String) s.getAttribute(SESSION_USER_ID);
    }

    protected String currentRole(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (String) s.getAttribute(SESSION_USER_ROLE);
    }

    /** The id of the logged-in student, or null for visitors and admins. */
    protected String currentStudentId(HttpServletRequest req) {
        return User.ROLE_STUDENT.equals(currentRole(req)) ? currentUserId(req) : null;
    }

    protected boolean isLoggedIn(HttpServletRequest req) { return currentUserId(req) != null; }

    /** Starts a brand-new session for the user (prevents session fixation). */
    protected void startSession(HttpServletRequest req, User user) {
        HttpSession old = req.getSession(false);
        if (old != null) old.invalidate();
        HttpSession s = req.getSession(true);
        s.setAttribute(SESSION_USER_ID, user.getId());
        s.setAttribute(SESSION_USER_NAME, user.getName());
        s.setAttribute(SESSION_USER_EMAIL, user.getEmail());
        s.setAttribute(SESSION_USER_ROLE, user.getRole());
        s.setMaxInactiveInterval(8 * 60 * 60);   // stay logged in for 8 hours of inactivity
    }

    private static final String EMAIL_COOKIE = "ce_email";

    /** Stores the last student e-mail in a cookie (1 year) so the login form is pre-filled. Never the password. */
    protected void rememberEmail(HttpServletResponse resp, HttpServletRequest req, String email) {
        try {
            javax.servlet.http.Cookie c = new javax.servlet.http.Cookie(EMAIL_COOKIE,
                    java.net.URLEncoder.encode(email, "UTF-8"));
            c.setMaxAge(365 * 24 * 60 * 60);
            c.setHttpOnly(true);
            c.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
            resp.addCookie(c);
        } catch (java.io.UnsupportedEncodingException ignored) { }
    }

    /** The e-mail remembered by {@link #rememberEmail}, or "" when there is none. */
    protected String rememberedEmail(HttpServletRequest req) {
        javax.servlet.http.Cookie[] cookies = req.getCookies();
        if (cookies == null) return "";
        for (javax.servlet.http.Cookie c : cookies) {
            if (EMAIL_COOKIE.equals(c.getName())) {
                try { return java.net.URLDecoder.decode(c.getValue(), "UTF-8"); }
                catch (java.io.UnsupportedEncodingException | IllegalArgumentException e) { return ""; }
            }
        }
        return "";
    }

    protected void addLookups(HttpServletRequest req) {
        req.setAttribute("categories", Categories.EVENT_CATEGORIES);
        req.setAttribute("departments", Categories.DEPARTMENTS);
        req.setAttribute("years", Categories.YEARS);
    }

    /** Only allow redirects to paths inside this application. */
    protected static String safeNext(String next) {
        if (next == null) return null;
        String n = next.trim();
        if (n.isEmpty() || !n.startsWith("/") || n.startsWith("//") || n.contains("\\")
                || n.contains("://") || n.contains("\r") || n.contains("\n")) return null;
        return n;
    }

    protected String clientKey(HttpServletRequest req, String email) {
        return email.toLowerCase() + "|" + req.getRemoteAddr();
    }
}
