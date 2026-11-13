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

/** Protects /admin/* - only an ADMIN session may pass (the admin login page itself is open). */
public class AdminAuthFilter implements Filter {
    @Override public void init(FilterConfig filterConfig) { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());
        if ("/admin/login".equals(path)) {
            chain.doFilter(request, response);
            return;
        }
        HttpSession session = req.getSession(false);
        String userId = session == null ? null : (String) session.getAttribute("userId");
        String role = session == null ? null : (String) session.getAttribute("userRole");

        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }
        if (!User.ROLE_ADMIN.equals(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "This area is for administrators only.");
            return;
        }
        User user = new UserDao().findById(userId);
        if (user == null || !user.isAdmin()) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override public void destroy() { }
}
