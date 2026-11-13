package com.collegeevent.servlet;

import com.collegeevent.dao.UserDao;
import com.collegeevent.model.User;
import com.collegeevent.util.Flash;
import com.collegeevent.util.PasswordUtil;
import com.collegeevent.util.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Student sign-up. The role is never read from the form - new accounts are always STUDENT. */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (isLoggedIn(req)) { redirect(req, resp, "/"); return; }
        addLookups(req);
        forward(req, resp, "register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> errors = new LinkedHashMap<String, String>();
        String name = param(req, "name");
        String email = param(req, "email").toLowerCase();
        String studentId = param(req, "studentId");
        String department = param(req, "department");
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String confirm = req.getParameter("confirmPassword") == null ? "" : req.getParameter("confirmPassword");

        Validator.name(name, errors);
        Validator.email(email, errors);
        Validator.studentId(studentId, errors);
        Validator.department(department, errors);
        int year = Validator.year(param(req, "year"), errors);
        Validator.newPassword(password, confirm, errors);

        if (errors.isEmpty()) {
            User u = new User();
            u.setName(name);
            u.setEmail(email);
            u.setStudentId(studentId);
            u.setDepartment(department);
            u.setYear(year);
            u.setPasswordHash(PasswordUtil.hash(password));
            String clash = new UserDao().createStudent(u);
            if ("email".equals(clash)) errors.put("email", "An account with this e-mail already exists.");
            if ("studentId".equals(clash)) errors.put("studentId", "This student ID is already registered.");
        }

        if (!errors.isEmpty()) {
            User form = new User();   // echo back everything except the passwords
            form.setName(name);
            form.setEmail(email);
            form.setStudentId(studentId);
            form.setDepartment(department);
            form.setYear(year);
            req.setAttribute("form", form);
            req.setAttribute("errors", errors);
            addLookups(req);
            forward(req, resp, "register.jsp");
            return;
        }
        // Signed in straight away - no second e-mail/password prompt after registering.
        User created = new UserDao().findByEmail(email);
        if (created != null) {
            startSession(req, created);
            rememberEmail(resp, req, created.getEmail());
            Flash.success(req, "Welcome, " + created.getName() + "! Your account is ready.");
            redirect(req, resp, "/student/dashboard");
            return;
        }
        Flash.success(req, "Your account is ready. Log in to start registering for events.");
        redirect(req, resp, "/login");
    }
}
