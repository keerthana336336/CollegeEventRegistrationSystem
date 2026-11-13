package com.collegeevent.dao;

/** Thrown when a JSON file cannot be read or written. */
public class DataAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}
