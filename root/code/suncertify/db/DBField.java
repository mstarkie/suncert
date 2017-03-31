/*
 * DBField.java Sun Certified Developer for the Java 2 Platform Submission. 2010
 * Bodgitt and Scarper, LLC
 */
package suncertify.db;

/**
 * Enumeration of the user-defined fields in a database record and their
 * expected lengths.
 * @author Starkie, Michael C.
 * @since Oct 25, 2010:4:30:00 PM
 */
public enum DBField {
    /** Length of the company name column in the database */
    NAME((short) 32),
    /** Length of the company location column in the database */
    LOCATION((short) 64),
    /** Length of the work performed column in the database */
    SPECIALTIES((short) 64),
    /** Length of the number of employees column in the database */
    SIZE((short) 6),
    /** Length of the hourly rate column in the database */
    RATE((short) 8),
    /** Length of the customer record column in the database */
    OWNER((short) 8);
    /** absolute fixed length of field */
    private final short fieldLen;

    /**
     * @param len the required length of this field.
     */
    DBField(short len) {
        this.fieldLen = len;
    }

    /**
     * @return the length of this field.
     */
    public short getLen() {
        return this.fieldLen;
    }
}
