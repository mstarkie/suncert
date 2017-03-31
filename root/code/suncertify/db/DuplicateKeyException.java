/*
 * DuplicateKeyException.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.db;

/**
 * The class <code>DuplicateKeyException</code> extends <code>Exception</code>
 * and is used to indicate that an attempt was made to insert a duplicate
 * recored into a database.
 * @author Michael Starkie
 * @see java.lang.Exception
 */
public class DuplicateKeyException extends Exception {
    private static final long serialVersionUID = -6689165809485807888L;

    /**
     * Constructs a new exception with <code>null</code> as its detail message.
     */
    public DuplicateKeyException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message.
     * @see java.lang.Exception#getMessage()
     */
    public DuplicateKeyException(String message) {
        super(message);
    }
}
