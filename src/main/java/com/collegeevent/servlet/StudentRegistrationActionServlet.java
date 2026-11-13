package com.collegeevent.servlet;

import com.collegeevent.dao.RegistrationDao;
import com.collegeevent.dao.RegistrationDao.CancelResult;
import com.collegeevent.dao.RegistrationDao.RegisterResult;
import com.collegeevent.util.Flash;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/** Handles the "Register" and "Cancel registration" buttons (POST only, student session required). */
@WebServlet(name = "StudentRegistrationActionServlet", urlPatterns = {"/student/event-register", "/student/event-cancel"})
public class StudentRegistrationActionServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        redirect(req, resp, "/student/registrations");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getServletPath().endsWith("event-cancel")) cancel(req, resp); else register(req, resp);
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String eventId = param(req, "eventId");
        RegisterResult result = new RegistrationDao().register(currentUserId(req), eventId);
        String back = "/event?id=" + URLEncoder.encode(eventId, "UTF-8");
        switch (result) {
            case SUCCESS:
                Flash.success(req, "You are registered. See the details under My registrations.");
                redirect(req, resp, "/student/registrations");
                return;
            case ALREADY_REGISTERED:
                Flash.error(req, "You are already registered for this event.");
                break;
            case EVENT_FULL:
                Flash.error(req, "Sorry, this event is full. No seats are left.");
                break;
            case DEADLINE_PASSED:
                Flash.error(req, "Registration for this event has closed.");
                break;
            case EVENT_ENDED:
                Flash.error(req, "This event has already ended.");
                break;
            case EVENT_NOT_FOUND:
                Flash.error(req, "That event no longer exists.");
                back = "/events";
                break;
            default:
                Flash.error(req, "Your account could not be used to register. Please log in again.");
                break;
        }
        redirect(req, resp, back);
    }

    private void cancel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CancelResult result = new RegistrationDao().cancel(currentUserId(req), param(req, "registrationId"));
        switch (result) {
            case SUCCESS: Flash.success(req, "Your registration was cancelled and the seat was released."); break;
            case ALREADY_CANCELLED: Flash.error(req, "That registration is already cancelled."); break;
            case ATTENDED: Flash.error(req, "Attendance is already recorded, so this registration cannot be cancelled."); break;
            case CLOSED: Flash.error(req, "Cancellation is closed because the registration deadline has passed."); break;
            default: Flash.error(req, "Registration not found."); break;
        }
        redirect(req, resp, "/student/registrations");
    }
}
