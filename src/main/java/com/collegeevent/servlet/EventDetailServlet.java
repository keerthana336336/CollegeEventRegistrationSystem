package com.collegeevent.servlet;

import com.collegeevent.dao.EventDao;
import com.collegeevent.dao.ViewBuilder;
import com.collegeevent.model.Event;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "EventDetailServlet", urlPatterns = {"/event"})
public class EventDetailServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Event event = new EventDao().findById(param(req, "id"));
        if (event == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "That event does not exist or was removed.");
            return;
        }
        req.setAttribute("ev", new ViewBuilder().eventView(event, currentStudentId(req)));
        forward(req, resp, "event-details.jsp");
    }
}
