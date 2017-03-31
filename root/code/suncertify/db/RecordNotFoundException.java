/*
 * RecordNotFoundException.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.db;

/**
 * The class <code>RecordNotFoundException</code> extends <code>Exception</code>
 * and is used to indicate that an attempt was made to access an invalid recored
 * from a database.
 * @author Michael Starkie
 * @see java.lang.Exception
 */
public class RecordNotFoundException extends Exception {
    private static final long serialVersionUID = 9172845648588845215L;

    /**
     * Constructs a new exception with <code>null</code> as its detail message.
     */
    public RecordNotFoundException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message.
     * @see java.lang.Exception#getMessage()
     */
    public RecordNotFoundException(String message) {
        super(message);
    }
}
