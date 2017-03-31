/*
 * DBField.java Sun Certified Developer for the Java 2 Platform Submission. 2010
 * Bodgitt and Scarper, LLC
 */
package suncertify.control;

/**
 * Enumeration of legal run modes. This allows to refer to the operation run
 * mode by name.
 * @author Starkie, Michael C.
 * @since Oct 25, 2010:4:30:00 PM
 */
public enum RunMode {
    /** Both the client and server are running in the same local JVM */
    ALONE,
    /** Only the network client UI in running (networked to a remote server) */
    CLIENT,
    /** Only the network server is running */
    SERVER,
    /** interpreted as an uninitialized system */
    NONE;
}
