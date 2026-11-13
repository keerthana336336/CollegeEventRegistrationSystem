package com.collegeevent.servlet;

import com.collegeevent.dao.UserDao;
import com.collegeevent.model.User;
import com.collegeevent.util.LoginThrottle;
import com.collegeevent.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Student login. Admin accounts must use /admin/login. */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (isLoggedIn(req)) { redirect(req, resp, "/student/dashboard"); return; }
        req.setAttribute("next", safeNext(req.getParameter("next")));
        req.setAttribute("email", rememberedEmail(req));
        forward(req, resp, "login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = param(req, "email").toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String next = safeNext(req.getParameter("next"));
        String key = clientKey(req, email);

        long locked = LoginThrottle.secondsLocked(key);
        if (locked > 0) {
            fail(req, resp, email, next, "Too many failed attempts. Try again in " + ((locked + 59) / 60) + " minute(s).");
            return;
        }
        User user = new UserDao().findByEmail(email);
        boolean ok = user != null && PasswordUtil.verify(password, user.getPasswordHash());
        if (user == null) PasswordUtil.verifyDummy(password);

        if (!ok) {
            LoginThrottle.recordFailure(key);
            fail(req, resp, email, next, "That e-mail and password do not match.");
            return;
        }
        if (user.isAdmin()) {
            fail(req, resp, email, next, "This is an administrator account. Use the admin login page instead.");
            return;
        }
        LoginThrottle.reset(key);
        startSession(req, user);
        rememberEmail(resp, req, user.getEmail());
        redirect(req, resp, next != null ? next : "/student/dashboard");
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, String email, String next, String message)
            throws ServletException, IOException {
        req.setAttribute("formError", message);
        req.setAttribute("email", email);
        req.setAttribute("next", next);
        forward(req, resp, "login.jsp");
    }
}
