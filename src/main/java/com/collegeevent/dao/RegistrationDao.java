package com.collegeevent.dao;

import com.collegeevent.model.Event;
import com.collegeevent.model.Registration;
import com.collegeevent.model.User;
import com.collegeevent.util.DateUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * All registration rules live here and run inside DataStore.LOCK, so two students can never
 * grab the last seat at the same time and nobody can register twice (single Tomcat instance).
 */
public class RegistrationDao {
    public enum RegisterResult { SUCCESS, EVENT_NOT_FOUND, USER_NOT_FOUND, ALREADY_REGISTERED, EVENT_FULL, DEADLINE_PASSED, EVENT_ENDED }
    public enum CancelResult { SUCCESS, NOT_FOUND, ALREADY_CANCELLED, ATTENDED, CLOSED }

    private final DataStore store = DataStore.get();

    public RegisterResult register(String userId, String eventId) {
        synchronized (DataStore.LOCK) {
            Event event = findEvent(eventId);
            if (event == null) return RegisterResult.EVENT_NOT_FOUND;
            User user = findUser(userId);
            if (user == null || user.isAdmin()) return RegisterResult.USER_NOT_FOUND;

            Registration existing = null;
            int active = 0;
            for (Registration r : store.registrations.items()) {
                if (!eventId.equals(r.getEventId())) continue;
                if (r.isActive()) active++;
                if (userId.equals(r.getUserId())) existing = r;
            }
            if (existing != null && existing.isActive()) return RegisterResult.ALREADY_REGISTERED;

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime end = DateUtil.eventEnd(event);
            if (end != null && now.isAfter(end)) return RegisterResult.EVENT_ENDED;
            LocalDateTime deadline = DateUtil.parseDateTime(event.getRegistrationDeadline());
            if (deadline != null && now.isAfter(deadline)) return RegisterResult.DEADLINE_PASSED;
            if (active >= event.getCapacity()) return RegisterResult.EVENT_FULL;

            if (existing != null) { // student cancelled earlier and is signing up again
                existing.setStatus(Registration.STATUS_REGISTERED);
                existing.setAttended(false);
                existing.setRegisteredAt(DateUtil.nowStamp());
                existing.setCancelledAt(null);
            } else {
                Registration r = new Registration();
                r.setId(UUID.randomUUID().toString());
                r.setEventId(eventId);
                r.setUserId(userId);
                r.setStatus(Registration.STATUS_REGISTERED);
                r.setAttended(false);
                r.setRegisteredAt(DateUtil.nowStamp());
                store.registrations.items().add(r);
            }
            store.registrations.save();
            return RegisterResult.SUCCESS;
        }
    }

    public CancelResult cancel(String userId, String registrationId) {
        synchronized (DataStore.LOCK) {
            Registration target = null;
            for (Registration r : store.registrations.items()) {
                if (r.getId().equals(registrationId) && r.getUserId().equals(userId)) target = r;
            }
            if (target == null) return CancelResult.NOT_FOUND;
            if (!target.isActive()) return CancelResult.ALREADY_CANCELLED;
            if (target.isAttended()) return CancelResult.ATTENDED;
            Event event = findEvent(target.getEventId());
            if (event != null) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime end = DateUtil.eventEnd(event);
                LocalDateTime deadline = DateUtil.parseDateTime(event.getRegistrationDeadline());
                if ((end != null && now.isAfter(end)) || (deadline != null && now.isAfter(deadline))) return CancelResult.CLOSED;
            }
            target.setStatus(Registration.STATUS_CANCELLED);
            target.setCancelledAt(DateUtil.nowStamp());
            store.registrations.save();
            return CancelResult.SUCCESS;
        }
    }

    /** Marks or clears attendance. Only active (not cancelled) registrations can be marked. */
    public boolean setAttendance(String registrationId, boolean attended) {
        synchronized (DataStore.LOCK) {
            for (Registration r : store.registrations.items()) {
                if (r.getId().equals(registrationId) && r.isActive()) {
                    r.setAttended(attended);
                    store.registrations.save();
                    return true;
                }
            }
            return false;
        }
    }

    public List<Registration> findAll() {
        synchronized (DataStore.LOCK) { return store.registrations.snapshot(); }
    }

    public List<Registration> findByUser(String userId) {
        List<Registration> out = new ArrayList<Registration>();
        for (Registration r : findAll()) if (userId.equals(r.getUserId())) out.add(r);
        return out;
    }

    public Map<String, Integer> activeCountsByEvent() {
        synchronized (DataStore.LOCK) {
            Map<String, Integer> counts = new HashMap<String, Integer>();
            for (Registration r : store.registrations.items()) {
                if (!r.isActive()) continue;
                Integer n = counts.get(r.getEventId());
                counts.put(r.getEventId(), n == null ? 1 : n + 1);
            }
            return counts;
        }
    }

    public Set<String> activeEventIdsForUser(String userId) {
        synchronized (DataStore.LOCK) {
            Set<String> ids = new HashSet<String>();
            for (Registration r : store.registrations.items()) {
                if (r.isActive() && userId.equals(r.getUserId())) ids.add(r.getEventId());
            }
            return ids;
        }
    }

    public int countActive() {
        synchronized (DataStore.LOCK) {
            int n = 0;
            for (Registration r : store.registrations.items()) if (r.isActive()) n++;
            return n;
        }
    }

    public int countAttended() {
        synchronized (DataStore.LOCK) {
            int n = 0;
            for (Registration r : store.registrations.items()) if (r.isActive() && r.isAttended()) n++;
            return n;
        }
    }

    private Event findEvent(String id) {
        for (Event e : store.events.items()) if (e.getId().equals(id)) return e;
        return null;
    }

    private User findUser(String id) {
        for (User u : store.users.items()) if (u.getId().equals(id)) return u;
        return null;
    }
}
