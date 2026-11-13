package com.collegeevent.servlet;

import com.collegeevent.dao.EventDao;
import com.collegeevent.dao.RegistrationDao;
import com.collegeevent.dao.UserDao;
import com.collegeevent.dao.ViewBuilder;
import com.collegeevent.model.EventView;
import com.collegeevent.model.RegistrationView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ViewBuilder vb = new ViewBuilder();
        RegistrationDao regs = new RegistrationDao();
        List<EventView> events = vb.eventViews(new EventDao().findAll(), null);
        int full = 0;
        for (EventView v : events) if (v.isFull() && !v.isEnded()) full++;

        List<RegistrationView> recent = vb.adminRegistrations(null, null);
        req.setAttribute("totalStudents", new UserDao().countStudents());
        req.setAttribute("totalEvents", events.size());
        req.setAttribute("totalRegistrations", regs.countActive());
        req.setAttribute("attendedCount", regs.countAttended());
        req.setAttribute("fullCount", full);
        req.setAttribute("events", events);
        req.setAttribute("recent", recent.size() > 6 ? recent.subList(0, 6) : recent);
        forward(req, resp, "admin/dashboard.jsp");
    }
}
