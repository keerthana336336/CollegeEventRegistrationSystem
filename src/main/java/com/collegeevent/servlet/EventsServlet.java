package com.collegeevent.servlet;

import com.collegeevent.dao.EventDao;
import com.collegeevent.dao.ViewBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Public event list with search and category filter. */
@WebServlet(name = "EventsServlet", urlPatterns = {"/events"})
public class EventsServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = param(req, "q");
        String category = param(req, "category");
        req.setAttribute("events", new ViewBuilder().eventViews(
                new EventDao().search(q, category, true), currentStudentId(req)));
        req.setAttribute("q", q);
        req.setAttribute("selectedCategory", category);
        addLookups(req);
        forward(req, resp, "events.jsp");
    }
}
