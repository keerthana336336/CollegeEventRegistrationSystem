package com.collegeevent.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Per-session CSRF token. Every POST form carries it in a hidden field named "_csrf". */
public final class CsrfUtil {
    public static final String SESSION_KEY = "csrfToken";
    public static final String PARAM_NAME = "_csrf";
    private static final SecureRandom RANDOM = new SecureRandom();

    private CsrfUtil() { }

    public static String token(HttpSession session) {
        Object existing = session.getAttribute(SESSION_KEY);
        if (existing instanceof String) return (String) existing;
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(SESSION_KEY, token);
        return token;
    }

    public static boolean isValid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        Object expected = session.getAttribute(SESSION_KEY);
        String actual = request.getParameter(PARAM_NAME);
        if (!(expected instanceof String) || actual == null) return false;
        return MessageDigest.isEqual(((String) expected).getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }
}
