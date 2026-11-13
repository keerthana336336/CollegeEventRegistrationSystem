package com.collegeevent.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Locks a login key (e-mail + IP) for a few minutes after repeated failures. */
public final class LoginThrottle {
    private static final int MAX_FAILURES = 5;
    private static final long LOCK_MILLIS = 5 * 60 * 1000L;
    private static final ConcurrentMap<String, long[]> STATE = new ConcurrentHashMap<String, long[]>();

    private LoginThrottle() { }

    /** @return seconds left on the lock, or 0 when login is allowed. */
    public static long secondsLocked(String key) {
        long[] s = STATE.get(key);
        if (s == null) return 0;
        long left = s[1] - System.currentTimeMillis();
        return left > 0 ? (left + 999) / 1000 : 0;
    }

    public static void recordFailure(String key) {
        long now = System.currentTimeMillis();
        STATE.compute(key, (k, s) -> {
            if (s == null || (s[1] != 0 && s[1] < now)) s = new long[]{0, 0};
            s[0]++;
            if (s[0] >= MAX_FAILURES) { s[1] = now + LOCK_MILLIS; s[0] = 0; }
            return s;
        });
    }

    public static void reset(String key) { STATE.remove(key); }
}
