package com.collegeevent.servlet;

import com.collegeevent.dao.EventDao;
import com.collegeevent.dao.RegistrationDao;
import com.collegeevent.dao.UserDao;
import com.collegeevent.dao.ViewBuilder;
import com.collegeevent.model.Event;
import com.collegeevent.model.EventView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Event> upcoming = new EventDao().search(null, null, true);
        List<EventView> views = new ViewBuilder().eventViews(upcoming, currentStudentId(req));
        req.setAttribute("nextEvent", views.isEmpty() ? null : views.get(0));
        req.setAttribute("featured", views.size() > 3 ? views.subList(0, 3) : views);
        req.setAttribute("upcomingCount", upcoming.size());
        req.setAttribute("studentCount", new UserDao().countStudents());
        req.setAttribute("registrationCount", new RegistrationDao().countActive());
        addLookups(req);
        forward(req, resp, "home.jsp");
    }
}
