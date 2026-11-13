package com.collegeevent.dao;

import com.collegeevent.model.Event;
import com.collegeevent.model.Registration;
import com.collegeevent.util.DateUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class EventDao {
    public enum UpdateResult { OK, NOT_FOUND, CAPACITY_TOO_LOW }

    private static final Comparator<Event> BY_START = new Comparator<Event>() {
        @Override public int compare(Event a, Event b) {
            String x = String.valueOf(a.getDate()) + " " + a.getStartTime();
            String y = String.valueOf(b.getDate()) + " " + b.getStartTime();
            return x.compareTo(y);
        }
    };

    private final DataStore store = DataStore.get();

    public List<Event> findAll() {
        synchronized (DataStore.LOCK) {
            List<Event> out = store.events.snapshot();
            Collections.sort(out, BY_START);
            return out;
        }
    }

    public Event findById(String id) {
        if (id == null) return null;
        synchronized (DataStore.LOCK) {
            for (Event e : store.events.items()) if (id.equals(e.getId())) return store.events.copy(e);
            return null;
        }
    }

    public int count() {
        synchronized (DataStore.LOCK) { return store.events.items().size(); }
    }

    /** Search by text and/or category. When upcomingOnly is true, events that already ended are left out. */
    public List<Event> search(String query, String category, boolean upcomingOnly) {
        String q = query == null ? "" : query.trim().toLowerCase();
        boolean anyCategory = category == null || category.trim().isEmpty() || "all".equalsIgnoreCase(category.trim());
        LocalDateTime now = LocalDateTime.now();
        List<Event> out = new ArrayList<Event>();
        for (Event e : findAll()) {
            if (upcomingOnly) {
                LocalDateTime end = DateUtil.eventEnd(e);
                if (end != null && now.isAfter(end)) continue;
            }
            if (!anyCategory && !category.trim().equalsIgnoreCase(e.getCategory())) continue;
            if (!q.isEmpty() && !matches(e, q)) continue;
            out.add(e);
        }
        return out;
    }

    private static boolean matches(Event e, String q) {
        return contains(e.getTitle(), q) || contains(e.getDescription(), q) || contains(e.getVenue(), q)
                || contains(e.getOrganizer(), q) || contains(e.getCategory(), q);
    }

    private static boolean contains(String s, String q) { return s != null && s.toLowerCase().contains(q); }

    public Event create(Event e) {
        synchronized (DataStore.LOCK) {
            e.setId(UUID.randomUUID().toString());
            e.setCreatedAt(DateUtil.nowStamp());
            store.events.items().add(store.events.copy(e));
            store.events.save();
            return e;
        }
    }

    public UpdateResult update(Event changes) {
        synchronized (DataStore.LOCK) {
            Event target = null;
            for (Event e : store.events.items()) if (changes.getId().equals(e.getId())) target = e;
            if (target == null) return UpdateResult.NOT_FOUND;
            int active = 0;
            for (Registration r : store.registrations.items()) {
                if (r.isActive() && target.getId().equals(r.getEventId())) active++;
            }
            if (changes.getCapacity() < active) return UpdateResult.CAPACITY_TOO_LOW;
            target.setTitle(changes.getTitle());
            target.setDescription(changes.getDescription());
            target.setCategory(changes.getCategory());
            target.setDate(changes.getDate());
            target.setStartTime(changes.getStartTime());
            target.setEndTime(changes.getEndTime());
            target.setVenue(changes.getVenue());
            target.setOrganizer(changes.getOrganizer());
            target.setCapacity(changes.getCapacity());
            target.setRegistrationDeadline(changes.getRegistrationDeadline());
            store.events.save();
            return UpdateResult.OK;
        }
    }

    /** Deletes the event and every registration that belongs to it. */
    public boolean delete(String id) {
        synchronized (DataStore.LOCK) {
            boolean removed = false;
            for (Iterator<Event> it = store.events.items().iterator(); it.hasNext(); ) {
                if (it.next().getId().equals(id)) { it.remove(); removed = true; }
            }
            if (!removed) return false;
            store.events.save();
            boolean regsChanged = false;
            for (Iterator<Registration> it = store.registrations.items().iterator(); it.hasNext(); ) {
                if (id.equals(it.next().getEventId())) { it.remove(); regsChanged = true; }
            }
            if (regsChanged) store.registrations.save();
            return true;
        }
    }
}
