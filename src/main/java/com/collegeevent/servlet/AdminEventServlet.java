package com.collegeevent.servlet;

import com.collegeevent.dao.EventDao;
import com.collegeevent.dao.EventDao.UpdateResult;
import com.collegeevent.dao.ViewBuilder;
import com.collegeevent.model.Event;
import com.collegeevent.util.Categories;
import com.collegeevent.util.DateUtil;
import com.collegeevent.util.Flash;
import com.collegeevent.util.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

/** Admin: list, add, edit and delete events. */
@WebServlet(name = "AdminEventServlet",
        urlPatterns = {"/admin/events", "/admin/events/form", "/admin/events/save", "/admin/events/delete"})
public class AdminEventServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getServletPath().endsWith("/form")) {
            showForm(req, resp);
            return;
        }
        String q = param(req, "q");
        String category = param(req, "category");
        req.setAttribute("events", new ViewBuilder().eventViews(new EventDao().search(q, category, false), null));
        req.setAttribute("q", q);
        req.setAttribute("selectedCategory", category);
        addLookups(req);
        forward(req, resp, "admin/manage-events.jsp");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = param(req, "id");
        Event form = new Event();
        if (!id.isEmpty()) {
            form = new EventDao().findById(id);
            if (form == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "That event does not exist.");
                return;
            }
        } else {
            form.setCapacity(50);
        }
        req.setAttribute("form", form);
        addLookups(req);
        forward(req, resp, "admin/event-form.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getServletPath().endsWith("/delete")) {
            delete(req, resp);
        } else {
            save(req, resp);
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (new EventDao().delete(param(req, "id"))) {
            Flash.success(req, "Event and its registrations were deleted.");
        } else {
            Flash.error(req, "That event was already deleted.");
        }
        redirect(req, resp, "/admin/events");
    }

    private void save(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> errors = new LinkedHashMap<String, String>();
        Event e = new Event();
        e.setId(param(req, "id"));
        e.setTitle(param(req, "title"));
        e.setDescription(param(req, "description"));
        e.setCategory(param(req, "category"));
        e.setDate(param(req, "date"));
        e.setStartTime(param(req, "startTime"));
        e.setEndTime(param(req, "endTime"));
        e.setVenue(param(req, "venue"));
        e.setOrganizer(param(req, "organizer"));
        e.setRegistrationDeadline(param(req, "registrationDeadline"));
        boolean isNew = e.getId().isEmpty();

        try {
            int cap = Integer.parseInt(param(req, "capacity"));
            e.setCapacity(cap);
            if (cap < 1 || cap > 100000) errors.put("capacity", "Capacity must be between 1 and 100000.");
        } catch (NumberFormatException ex) {
            errors.put("capacity", "Enter the capacity as a whole number.");
        }
        if (e.getTitle().length() < 3 || e.getTitle().length() > 120) errors.put("title", "Title must be 3 to 120 characters.");
        if (e.getDescription().length() < 10 || e.getDescription().length() > 2000) errors.put("description", "Description must be 10 to 2000 characters.");
        if (!Categories.isValidCategory(e.getCategory())) errors.put("category", "Choose a category.");
        if (e.getVenue().length() < 2 || e.getVenue().length() > 120) errors.put("venue", "Venue must be 2 to 120 characters.");
        if (e.getOrganizer().length() < 2 || e.getOrganizer().length() > 100) errors.put("organizer", "Organizer must be 2 to 100 characters.");

        LocalDate date = DateUtil.parseDate(e.getDate());
        LocalTime start = DateUtil.parseTime(e.getStartTime());
        LocalTime end = DateUtil.parseTime(e.getEndTime());
        LocalDateTime deadline = DateUtil.parseDateTime(e.getRegistrationDeadline());
        if (date == null) errors.put("date", "Choose the event date.");
        else if (isNew && date.isBefore(LocalDate.now())) errors.put("date", "A new event cannot be in the past.");
        if (start == null) errors.put("startTime", "Choose the start time.");
        if (end == null) errors.put("endTime", "Choose the end time.");
        else if (start != null && !end.isAfter(start)) errors.put("endTime", "End time must be after the start time.");
        if (deadline == null) {
            errors.put("registrationDeadline", "Choose the registration deadline.");
        } else {
            e.setRegistrationDeadline(DateUtil.formatDeadline(deadline));
            if (date != null && start != null && deadline.isAfter(LocalDateTime.of(date, start))) {
                errors.put("registrationDeadline", "The deadline must be before the event starts.");
            }
        }

        EventDao dao = new EventDao();
        if (errors.isEmpty()) {
            if (isNew) {
                dao.create(e);
                Flash.success(req, "Event added.");
            } else {
                UpdateResult r = dao.update(e);
                if (r == UpdateResult.CAPACITY_TOO_LOW) {
                    errors.put("capacity", "More students than this are already registered. Raise the capacity or cancel some registrations first.");
                } else if (r == UpdateResult.NOT_FOUND) {
                    Flash.error(req, "That event no longer exists.");
                    redirect(req, resp, "/admin/events");
                    return;
                } else {
                    Flash.success(req, "Event updated.");
                }
            }
        }
        if (!errors.isEmpty()) {
            req.setAttribute("form", e);
            req.setAttribute("errors", errors);
            addLookups(req);
            forward(req, resp, "admin/event-form.jsp");
            return;
        }
        redirect(req, resp, "/admin/events");
    }
}
