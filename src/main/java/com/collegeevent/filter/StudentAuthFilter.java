package com.collegeevent.filter;

import com.collegeevent.dao.UserDao;
import com.collegeevent.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

/** Protects /student/* - only a logged-in STUDENT session may pass. */
public class StudentAuthFilter implements Filter {
    @Override public void init(FilterConfig filterConfig) { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String userId = session == null ? null : (String) session.getAttribute("userId");
        String role = session == null ? null : (String) session.getAttribute("userRole");

        if (userId == null) {
            String target = "";
            if ("GET".equalsIgnoreCase(req.getMethod())) {
                String path = req.getRequestURI().substring(req.getContextPath().length());
                if (req.getQueryString() != null) path += "?" + req.getQueryString();
                target = "?next=" + URLEncoder.encode(path, "UTF-8");
            }
            resp.sendRedirect(req.getContextPath() + "/login" + target);
            return;
        }
        if (User.ROLE_ADMIN.equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }
        User user = new UserDao().findById(userId);
        if (user == null || user.isAdmin()) { // account removed from users.json while logged in
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override public void destroy() { }
}
