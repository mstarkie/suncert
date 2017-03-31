/*
 * DBAccess.java Sun Certified Developer for the Java 2 Platform Submission.
 * 2010 Bodgitt and Scarper, LLC
 */
package suncertify.db;

/**
 * The interface <code>DBAccess</code> defines the data access methods for the
 * database.
 * @author Sun Microsystems
 */
public interface DBAccess {
    /**
     * Reads a record from the file. Returns an array of String where each
     * element contains a field of the corresponding recNo.
     * @param recNo The record number to read
     * @return the row corresponding to the supplied record number.
     * @throws RecordNotFoundException when the supplied record number is not
     *             found.
     */
    public String[] readRecord(long recNo) throws RecordNotFoundException;

    /**
     * Modifies the fields of a record. The new value for field n appears in
     * data[n]. Throws SecurityException if the record is locked with a cookie
     * other than lockCookie.
     * @param recNo The record number
     * @param data The updated fields
     * @param lockCookie The database lock cookie.
     * @throws RecordNotFoundException when reading a non-existent record number
     * @throws SecurityException when trying to modify a record which is
     *             currently locked by another operation.
     * @see #lockRecord(long)
     */
    public void updateRecord(long recNo, String[] data, long lockCookie)
        throws RecordNotFoundException, SecurityException;

    /**
     * Deletes a record, making the record number and associated disk storage
     * available for reuse. Throws SecurityException if the record is locked
     * with a cookie other than lockCookie.
     * @param recNo The record number
     * @param lockCookie The database lock cookie.
     * @throws RecordNotFoundException when reading a non-existent record number
     * @throws SecurityException when trying to modify a record which is
     *             currently locked by another operation.
     * @see #lockRecord(long)
     */
    public void deleteRecord(long recNo, long lockCookie)
        throws RecordNotFoundException, SecurityException;

    /**
     * Returns an array of record numbers that match the specified criteria.
     * Field n in the database file is described by criteria[n]. A null value in
     * criteria[n] matches any field value. A non-null value in criteria[n]
     * matches any field value that begins with criteria[n]. (For example,
     * "Fred" matches "Fred" or "Freddy".)
     * @param criteria The criteria to match.
     * @return an array of record numbers matching the criteria.
     * @see #lockRecord(long)
     */
    public long[] findByCriteria(String[] criteria);

    /**
     * Creates a new record in the database (possibly reusing a deleted entry).
     * Inserts the given data, and returns the record number of the new record.
     * @param data the data to be inserted into the table.
     * @return a record number identifying the newly inserted row.
     * @throws DuplicateKeyException when trying to insert a non-unique record.
     */
    public long createRecord(String[] data) throws DuplicateKeyException;

    /**
     * Locks a record so that it can only be updated or deleted by this client.
     * Returned value is a cookie that must be used when the record is unlocked,
     * updated, or deleted. If the specified record is already locked by a
     * different client, the current thread gives up the CPU and consumes no CPU
     * cycles until the record is unlocked.
     * @param recNo The record number to lock.
     * @return A unique number called a lockCookie identifying the row that is
     *         locked.
     * @throws RecordNotFoundException when trying to lock a non-existing
     *             record.
     */
    public long lockRecord(long recNo) throws RecordNotFoundException;

    /**
     * Releases the lock on a record. Cookie must be the cookie returned when
     * the record was locked; otherwise throws SecurityException.
     * @param recNo The record number to lock.
     * @param cookie A unique number confirming the row has been locked for the
     *            this operation.
     * @throws SecurityException when the row has not been locked for this
     *             operation.
     * @see #lockRecord(long)
     */
    public void unlock(long recNo, long cookie) throws SecurityException;
}
