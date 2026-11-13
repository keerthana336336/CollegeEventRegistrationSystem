package com.collegeevent.servlet;

import com.collegeevent.dao.UserDao;
import com.collegeevent.model.User;
import com.collegeevent.util.AppConfig;
import com.collegeevent.util.LoginThrottle;
import com.collegeevent.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Separate login page for administrators. Students are rejected here. */
@WebServlet(name = "AdminLoginServlet", urlPatterns = {"/admin/login"})
public class AdminLoginServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (User.ROLE_ADMIN.equals(currentRole(req))) { redirect(req, resp, "/admin/dashboard"); return; }
        req.setAttribute("email", AppConfig.adminEmail());
        forward(req, resp, "admin-login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = param(req, "email").toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String key = "admin|" + clientKey(req, email);

        long locked = LoginThrottle.secondsLocked(key);
        if (locked > 0) {
            fail(req, resp, email, "Too many failed attempts. Try again in " + ((locked + 59) / 60) + " minute(s).");
            return;
        }
        User user = new UserDao().findByEmail(email);
        boolean ok = user != null && user.isAdmin() && PasswordUtil.verify(password, user.getPasswordHash());
        if (user == null || !user.isAdmin()) PasswordUtil.verifyDummy(password);
        if (!ok) {
            LoginThrottle.recordFailure(key);
            fail(req, resp, email, "Administrator e-mail or password is incorrect.");
            return;
        }
        LoginThrottle.reset(key);
        startSession(req, user);
        redirect(req, resp, "/admin/dashboard");
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, String email, String message)
            throws ServletException, IOException {
        req.setAttribute("formError", message);
        req.setAttribute("email", AppConfig.adminEmail());
        forward(req, resp, "admin-login.jsp");
    }
}
