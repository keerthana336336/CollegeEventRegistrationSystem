package com.collegeevent.model;

import com.collegeevent.util.Categories;
import com.collegeevent.util.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

/** An event plus live numbers (seats left, open/closed) so JSP pages stay simple. */
public class EventView {
    private final Event event;
    private final int registered;
    private final boolean userRegistered;
    private final boolean deadlinePassed;
    private final boolean ended;

    public EventView(Event event, int registered, boolean userRegistered) {
        this.event = event;
        this.registered = registered;
        this.userRegistered = userRegistered;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = DateUtil.parseDateTime(event.getRegistrationDeadline());
        LocalDateTime end = DateUtil.eventEnd(event);
        this.deadlinePassed = deadline != null && now.isAfter(deadline);
        this.ended = end != null && now.isAfter(end);
    }

    public Event getEvent() { return event; }
    public int getRegistered() { return registered; }
    public boolean isUserRegistered() { return userRegistered; }
    public boolean isDeadlinePassed() { return deadlinePassed; }
    public boolean isEnded() { return ended; }

    public int getRemaining() { return Math.max(0, event.getCapacity() - registered); }
    public boolean isFull() { return registered >= event.getCapacity(); }
    /** True when a student may still sign up (ignores whether this student is already registered). */
    public boolean isOpen() { return !ended && !deadlinePassed && !isFull(); }
    public int getPercentFilled() {
        return event.getCapacity() <= 0 ? 100 : Math.min(100, registered * 100 / event.getCapacity());
    }

    public String getStatusLabel() {
        if (ended) return "Ended";
        if (isFull()) return "Full";
        if (deadlinePassed) return "Registration closed";
        return "Open";
    }

    public String getStatusClass() {
        if (ended) return "badge-muted";
        if (isFull()) return "badge-danger";
        if (deadlinePassed) return "badge-warn";
        return "badge-ok";
    }

    public String getCategoryClass() { return Categories.cssClass(event.getCategory()); }
    public String getDateDisplay() { return DateUtil.formatDate(event.getDate()); }
    public String getTimeDisplay() {
        return DateUtil.formatTime(event.getStartTime()) + " - " + DateUtil.formatTime(event.getEndTime());
    }
    public String getDeadlineDisplay() { return DateUtil.formatDateTime(event.getRegistrationDeadline()); }

    public String getDay() {
        LocalDate d = DateUtil.parseDate(event.getDate());
        return d == null ? "" : String.valueOf(d.getDayOfMonth());
    }

    public String getMonth() {
        LocalDate d = DateUtil.parseDate(event.getDate());
        return d == null ? "" : d.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }
}
