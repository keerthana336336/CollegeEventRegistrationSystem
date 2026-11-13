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

/** View and update the logged-in student's profile, or change the password. */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/student/profile"})
public class ProfileServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        show(req, resp, new UserDao().findById(currentUserId(req)), null, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDao dao = new UserDao();
        User user = dao.findById(currentUserId(req));
        if (user == null) { resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Account not found."); return; }
        Map<String, String> errors = new LinkedHashMap<String, String>();

        if ("password".equals(param(req, "action"))) {
            String current = req.getParameter("currentPassword") == null ? "" : req.getParameter("currentPassword");
            String next = req.getParameter("newPassword") == null ? "" : req.getParameter("newPassword");
            String confirm = req.getParameter("confirmNewPassword") == null ? "" : req.getParameter("confirmNewPassword");
            if (!PasswordUtil.verify(current, user.getPasswordHash())) {
                errors.put("currentPassword", "Your current password is not correct.");
            }
            Map<String, String> pwErrors = new LinkedHashMap<String, String>();
            Validator.newPassword(next, confirm, pwErrors);
            if (pwErrors.containsKey("password")) errors.put("newPassword", pwErrors.get("password"));
            if (pwErrors.containsKey("confirmPassword")) errors.put("confirmNewPassword", pwErrors.get("confirmPassword"));
            if (errors.isEmpty()) {
                dao.updatePasswordHash(user.getId(), PasswordUtil.hash(next));
                Flash.success(req, "Your password was changed.");
                redirect(req, resp, "/student/profile");
                return;
            }
            show(req, resp, user, errors, "password");
            return;
        }

        String name = param(req, "name");
        String studentId = param(req, "studentId");
        String department = param(req, "department");
        Validator.name(name, errors);
        Validator.studentId(studentId, errors);
        Validator.department(department, errors);
        int year = Validator.year(param(req, "year"), errors);
        if (errors.isEmpty()) {
            String clash = dao.updateProfile(user.getId(), name, studentId, department, year);
            if ("studentId".equals(clash)) errors.put("studentId", "This student ID belongs to another account.");
            else if (clash != null) errors.put("name", "Your account could not be found.");
        }
        if (errors.isEmpty()) {
            req.getSession().setAttribute(SESSION_USER_NAME, name);
            Flash.success(req, "Profile updated.");
            redirect(req, resp, "/student/profile");
            return;
        }
        user.setName(name);
        user.setStudentId(studentId);
        user.setDepartment(department);
        user.setYear(year);
        show(req, resp, user, errors, "details");
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, User profile,
                      Map<String, String> errors, String failedForm) throws ServletException, IOException {
        req.setAttribute("profile", profile);
        req.setAttribute("errors", errors);
        req.setAttribute("failedForm", failedForm);
        addLookups(req);
        forward(req, resp, "student/profile.jsp");
    }
}
