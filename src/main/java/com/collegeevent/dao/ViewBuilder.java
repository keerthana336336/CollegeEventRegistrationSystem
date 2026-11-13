package com.collegeevent.dao;

import com.collegeevent.model.Event;
import com.collegeevent.model.EventView;
import com.collegeevent.model.Registration;
import com.collegeevent.model.RegistrationView;
import com.collegeevent.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Joins events, users and registrations into objects that are easy to show in JSP pages. */
public class ViewBuilder {
    private final EventDao events = new EventDao();
    private final UserDao users = new UserDao();
    private final RegistrationDao registrations = new RegistrationDao();

    /** @param userId logged-in student (marks events they joined) or null for visitors/admins */
    public List<EventView> eventViews(List<Event> list, String userId) {
        Map<String, Integer> counts = registrations.activeCountsByEvent();
        Set<String> mine = userId == null ? Collections.<String>emptySet() : registrations.activeEventIdsForUser(userId);
        List<EventView> out = new ArrayList<EventView>();
        for (Event e : list) {
            Integer n = counts.get(e.getId());
            out.add(new EventView(e, n == null ? 0 : n, mine.contains(e.getId())));
        }
        return out;
    }

    public EventView eventView(Event e, String userId) {
        return eventViews(Collections.singletonList(e), userId).get(0);
    }

    /** A student's registrations (including cancelled ones), newest first. */
    public List<RegistrationView> userRegistrations(String userId) {
        User student = users.findById(userId);
        List<RegistrationView> out = new ArrayList<RegistrationView>();
        if (student == null) return out;
        Map<String, Event> eventsById = eventMap();
        for (Registration r : registrations.findByUser(userId)) {
            Event e = eventsById.get(r.getEventId());
            if (e != null) out.add(new RegistrationView(r, e, student));
        }
        sortNewestFirst(out);
        return out;
    }

    /** Registrations for the admin pages, optionally for one event and/or matching a search text. */
    public List<RegistrationView> adminRegistrations(String eventId, String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        Map<String, Event> eventsById = eventMap();
        Map<String, User> usersById = new HashMap<String, User>();
        for (User u : users.findStudents()) usersById.put(u.getId(), u);

        List<RegistrationView> out = new ArrayList<RegistrationView>();
        for (Registration r : registrations.findAll()) {
            if (eventId != null && !eventId.isEmpty() && !eventId.equals(r.getEventId())) continue;
            Event e = eventsById.get(r.getEventId());
            User u = usersById.get(r.getUserId());
            if (e == null || u == null) continue;
            if (!q.isEmpty() && !matches(q, e, u, r)) continue;
            out.add(new RegistrationView(r, e, u));
        }
        sortNewestFirst(out);
        return out;
    }

    private static boolean matches(String q, Event e, User u, Registration r) {
        return has(u.getName(), q) || has(u.getEmail(), q) || has(u.getStudentId(), q) || has(u.getDepartment(), q)
                || has(e.getTitle(), q) || has(r.getStatus(), q);
    }

    private static boolean has(String s, String q) { return s != null && s.toLowerCase().contains(q); }

    private Map<String, Event> eventMap() {
        Map<String, Event> map = new HashMap<String, Event>();
        for (Event e : events.findAll()) map.put(e.getId(), e);
        return map;
    }

    private static void sortNewestFirst(List<RegistrationView> list) {
        Collections.sort(list, new Comparator<RegistrationView>() {
            @Override public int compare(RegistrationView a, RegistrationView b) {
                return String.valueOf(b.getRegistration().getRegisteredAt()).compareTo(String.valueOf(a.getRegistration().getRegisteredAt()));
            }
        });
    }
}
