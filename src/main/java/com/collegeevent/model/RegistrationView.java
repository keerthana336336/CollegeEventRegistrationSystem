package com.collegeevent.model;

import com.collegeevent.util.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

/** A registration joined with its event and student, for tables in the JSP pages. */
public class RegistrationView {
    private final Registration registration;
    private final Event event;
    private final User student;
    private final boolean canCancel;
    private final boolean eventEnded;

    public RegistrationView(Registration registration, Event event, User student) {
        this.registration = registration;
        this.event = event;
        this.student = student;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = DateUtil.parseDateTime(event.getRegistrationDeadline());
        LocalDateTime end = DateUtil.eventEnd(event);
        this.eventEnded = end != null && now.isAfter(end);
        this.canCancel = registration.isActive() && !registration.isAttended()
                && (deadline == null || !now.isAfter(deadline))
                && (end == null || !now.isAfter(end));
    }

    public Registration getRegistration() { return registration; }
    public Event getEvent() { return event; }
    public User getStudent() { return student; }
    public boolean isCanCancel() { return canCancel; }
    public boolean isEventEnded() { return eventEnded; }

    public String getStatusLabel() {
        if (!registration.isActive()) return "Cancelled";
        return registration.isAttended() ? "Attended" : "Registered";
    }

    public String getStatusClass() {
        if (!registration.isActive()) return "badge-muted";
        return registration.isAttended() ? "badge-ok" : "badge-info";
    }

    public String getDateDisplay() { return DateUtil.formatDate(event.getDate()); }
    public String getTimeDisplay() {
        return DateUtil.formatTime(event.getStartTime()) + " - " + DateUtil.formatTime(event.getEndTime());
    }
    public String getRegisteredAtDisplay() { return DateUtil.formatDateTime(registration.getRegisteredAt()); }

    public String getDay() {
        LocalDate d = DateUtil.parseDate(event.getDate());
        return d == null ? "" : String.valueOf(d.getDayOfMonth());
    }

    public String getMonth() {
        LocalDate d = DateUtil.parseDate(event.getDate());
        return d == null ? "" : d.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }
}
