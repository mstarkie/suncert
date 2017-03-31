/*
 * BodgittHeader.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.db;

/**
 * An encapsulation of database header information for Bodgitt & Scarper, LLC.
 * @author Starkie, Michael C.
 * @since Oct 7, 2010:7:24:42 AM
 */
final class DBHeader {
    /** Magic Cookie */
    private int magicCookie = 0;
    /** Start of records offset */
    private int startOfRecords = 0;
    /** Number of fields per record */
    private short numOfFields = 0;

    /**
     * An encapsulation of database header information
     * @param mCookie The magic cookie
     * @param sRecords The offset to the first record in the file.
     * @param nFields The number of fields in each record.
     */
    DBHeader(int mCookie, int sRecords, short nFields) {
        this.magicCookie = mCookie;
        this.startOfRecords = sRecords;
        this.numOfFields = nFields;
    }

    /**
     * @return the magicCookie
     */
    int getMagicCookie() {
        return magicCookie;
    }

    /**
     * @return the start of records offset into the underlying database file
     */
    int getStartOfRecords() {
        return startOfRecords;
    }

    /**
     * @return the number of fields defined in the schema
     */
    short getNumOfFields() {
        return numOfFields;
    }
}
