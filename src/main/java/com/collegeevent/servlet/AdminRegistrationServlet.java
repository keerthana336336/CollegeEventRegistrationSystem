package com.collegeevent.servlet;

import com.collegeevent.dao.EventDao;
import com.collegeevent.dao.RegistrationDao;
import com.collegeevent.dao.ViewBuilder;
import com.collegeevent.model.Event;
import com.collegeevent.model.EventView;
import com.collegeevent.model.RegistrationView;
import com.collegeevent.model.User;
import com.collegeevent.util.CsvUtil;
import com.collegeevent.util.Flash;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;

/** Admin: see who registered, mark attendance, export CSV. */
@WebServlet(name = "AdminRegistrationServlet",
        urlPatterns = {"/admin/registrations", "/admin/registrations/attendance", "/admin/registrations/export"})
public class AdminRegistrationServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String eventId = param(req, "eventId");
        String q = param(req, "q");
        ViewBuilder vb = new ViewBuilder();
        List<RegistrationView> regs = vb.adminRegistrations(eventId, q);

        if (req.getServletPath().endsWith("/export")) {
            export(resp, regs, eventId);
            return;
        }
        Event selected = eventId.isEmpty() ? null : new EventDao().findById(eventId);
        EventView selectedView = selected == null ? null : vb.eventView(selected, null);
        req.setAttribute("regs", regs);
        req.setAttribute("allEvents", new EventDao().findAll());
        req.setAttribute("selectedEvent", selectedView);
        req.setAttribute("eventId", selected == null ? "" : eventId);
        req.setAttribute("q", q);
        forward(req, resp, "admin/event-registrations.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.getServletPath().endsWith("/attendance")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        boolean attended = "true".equals(param(req, "attended"));
        if (new RegistrationDao().setAttendance(param(req, "registrationId"), attended)) {
            Flash.success(req, attended ? "Attendance marked." : "Attendance cleared.");
        } else {
            Flash.error(req, "That registration is cancelled or no longer exists.");
        }
        redirect(req, resp, "/admin/registrations?eventId=" + URLEncoder.encode(param(req, "eventId"), "UTF-8")
                + "&q=" + URLEncoder.encode(param(req, "q"), "UTF-8"));
    }

    private void export(HttpServletResponse resp, List<RegistrationView> regs, String eventId) throws IOException {
        String label = "all-events";
        if (!eventId.isEmpty()) {
            Event e = new EventDao().findById(eventId);
            if (e != null) label = e.getTitle().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
            if (label.length() > 40) label = label.substring(0, 40);
            if (label.isEmpty()) label = "event";
        }
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"registrations-" + label + "-" + LocalDate.now() + ".csv\"");
        PrintWriter out = resp.getWriter();
        out.write('\uFEFF'); // lets Excel detect UTF-8
        out.write(CsvUtil.row("Event", "Event date", "Student name", "Student ID", "Email", "Department",
                "Year", "Status", "Attended", "Registered at"));
        for (RegistrationView r : regs) {
            User s = r.getStudent();
            out.write(CsvUtil.row(r.getEvent().getTitle(), r.getEvent().getDate(), s.getName(), s.getStudentId(),
                    s.getEmail(), s.getDepartment(), String.valueOf(s.getYear()), r.getStatusLabel(),
                    r.getRegistration().isAttended() ? "Yes" : "No", r.getRegistration().getRegisteredAt()));
        }
        out.flush();
    }
}
