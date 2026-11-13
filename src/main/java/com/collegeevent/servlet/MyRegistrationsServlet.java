package com.collegeevent.servlet;

import com.collegeevent.dao.ViewBuilder;
import com.collegeevent.model.RegistrationView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "MyRegistrationsServlet", urlPatterns = {"/student/registrations"})
public class MyRegistrationsServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String status = param(req, "status").toLowerCase();
        if (!"registered".equals(status) && !"attended".equals(status) && !"cancelled".equals(status)) status = "all";

        List<RegistrationView> shown = new ArrayList<RegistrationView>();
        for (RegistrationView r : new ViewBuilder().userRegistrations(currentUserId(req))) {
            String label = r.getStatusLabel().toLowerCase();
            if ("all".equals(status) || label.equals(status)) shown.add(r);
        }
        req.setAttribute("regs", shown);
        req.setAttribute("status", status);
        forward(req, resp, "student/my-registrations.jsp");
    }
}
