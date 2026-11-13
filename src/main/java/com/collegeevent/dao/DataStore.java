package com.collegeevent.dao;

import com.collegeevent.model.Event;
import com.collegeevent.model.Registration;
import com.collegeevent.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Holds the three JSON repositories. Created once at startup by AppInitListener. */
public final class DataStore {
    /** Every read-modify-write on the JSON data happens while holding this lock. */
    public static final Object LOCK = new Object();

    private static volatile DataStore instance;

    public final Path dataDir;
    public final JsonRepository<User> users;
    public final JsonRepository<Event> events;
    public final JsonRepository<Registration> registrations;
    private final boolean firstRun;

    private DataStore(Path dir) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        Files.createDirectories(dir);
        if (!Files.isWritable(dir)) throw new IOException("Data folder is not writable: " + dir);
        this.dataDir = dir;
        this.users = new JsonRepository<User>(dir.resolve("users.json"), User[].class, gson);
        this.events = new JsonRepository<Event>(dir.resolve("events.json"), Event[].class, gson);
        this.registrations = new JsonRepository<Registration>(dir.resolve("registrations.json"), Registration[].class, gson);
        users.load();
        events.load();
        registrations.load();
        this.firstRun = events.wasCreatedOnLoad();
    }

    public static synchronized void init(Path dir) throws IOException {
        instance = new DataStore(dir);
    }

    public static DataStore get() {
        DataStore s = instance;
        if (s == null) throw new IllegalStateException("Data store is not initialised - check the Tomcat console for startup errors.");
        return s;
    }

    /** True when events.json did not exist before this startup. */
    public boolean isFirstRun() { return firstRun; }
}
