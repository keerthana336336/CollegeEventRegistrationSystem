package com.collegeevent.dao;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Keeps one JSON array file in memory and writes it back after every change.
 * Not thread-safe by itself: callers synchronise on {@link DataStore#LOCK}.
 */
public class JsonRepository<T> {
    private static final Logger LOG = Logger.getLogger(JsonRepository.class.getName());

    private final Path file;
    private final Class<T[]> arrayType;
    private final Class<T> itemType;
    private final Gson gson;
    private final List<T> items = new ArrayList<T>();
    private boolean createdOnLoad;

    @SuppressWarnings("unchecked")
    public JsonRepository(Path file, Class<T[]> arrayType, Gson gson) {
        this.file = file;
        this.arrayType = arrayType;
        this.itemType = (Class<T>) arrayType.getComponentType();
        this.gson = gson;
    }

    /** Reads the file; creates it with [] when missing; sets aside a malformed file and starts empty. */
    public void load() throws IOException {
        items.clear();
        createdOnLoad = false;
        if (!Files.exists(file)) {
            Files.createDirectories(file.getParent());
            write();
            createdOnLoad = true;
            return;
        }
        String text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        if (text.length() > 0 && text.charAt(0) == '\uFEFF') text = text.substring(1); // Notepad adds a BOM
        text = text.trim();
        if (text.isEmpty()) return;
        try {
            T[] loaded = gson.fromJson(text, arrayType);
            if (loaded != null) {
                for (T item : loaded) if (item != null) items.add(item);
            }
        } catch (JsonParseException ex) {
            Path backup = file.resolveSibling(file.getFileName() + ".corrupt-" + System.currentTimeMillis());
            LOG.log(Level.SEVERE, "Malformed JSON in " + file + ". Moving it to " + backup + " and starting empty.", ex);
            items.clear();
            Files.move(file, backup, StandardCopyOption.REPLACE_EXISTING);
            write();
        }
    }

    public boolean wasCreatedOnLoad() { return createdOnLoad; }

    /** The live list - only touch it while holding DataStore.LOCK. */
    public List<T> items() { return items; }

    public T copy(T item) {
        return item == null ? null : gson.fromJson(gson.toJson(item), itemType);
    }

    public List<T> snapshot() {
        List<T> out = new ArrayList<T>(items.size());
        for (T item : items) out.add(copy(item));
        return out;
    }

    /** Writes to disk; if that fails the in-memory list is reloaded so memory and disk stay equal. */
    public void save() {
        try {
            write();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Could not write " + file, e);
            try { load(); } catch (IOException reloadError) { LOG.log(Level.SEVERE, "Could not reload " + file, reloadError); }
            throw new DataAccessException("Could not save data to " + file + ". Check that the folder is writable.", e);
        }
    }

    private void write() throws IOException {
        byte[] bytes = gson.toJson(items).getBytes(StandardCharsets.UTF_8);
        Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
        Files.write(tmp, bytes);
        try {
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException | java.nio.file.AccessDeniedException e) {
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
