/*
 * Data.java Sun Certified Developer for the Java 2 Platform Submission. 2010
 * Bodgitt and Scarper, LLC
 */
package suncertify.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

/**
 * Implements the database access interface requirements. The class supports
 * asynchronous database operations from multiple clients using record locking.
 * This class is thread safe although no methods are synchronized. Deleted
 * records are noted and cached for new record inserts. New record inserts are
 * only appended to end of the underlying database file if no deleted records
 * are found in the cache. Some records may be deleted in the underlying file
 * and not be cached if those records were deleted in a previous run. In this
 * case new record inserts will be appended to the file.
 * @see suncertify.db.DBRecordLocker DBRecordLocker (The record locking
 *      delegate).
 * @author Starkie, Michael C. Oct 4, 2010:7:23:44 PM
 */
public class Data implements DBAccess {
    /** the first field in a record indicating the value of a valid record. */
    public static final short VALID_RECORD = (short) 0x0000;
    /** the first field in a record indicating the value of an invalid record. */
    public static final short DELETED_RECORD = (short) 0x8000;
    /** A reference to the underlying database file */
    private File file = null;
    /** DB header and schema information */
    private DBInfo dbInfo = null;
    /** manages locking/unlocking of records */
    private DBRecordLocker recordLocker = null;
    /** manages i/o operations on the underlying database */
    private DBIo io = null;
    /** a cache of record numbers that are know to be deleted */
    private ConcurrentLinkedQueue<Long> recordCache = new ConcurrentLinkedQueue<Long>();

    /**
     * Constructor which takes a string argument URI path to a database file
     * location used to initialize the system.
     * @param dbFileURI a string representation of a path in URI form of the
     *            underlying database file.
     * @see java.net.URI
     */
    public Data(String dbFileURI) throws FileNotFoundException, IOException {
        init(dbFileURI);
    }

    /**
     * close any OS resources cleanly.
     */
    public void close() {
        io.close();
    }

    /**
     * Returns the URI of the backing database file.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return file.toURI().toString();
    }

    /**
     * The first two fields are considered joint primary keys. If any one of
     * them is either null or empty the method will throw a
     * DuplicateKeyException. Empty or null values are generally considered
     * wildcards and this method will interpret an attempt to insert a new
     * record with a null or empty primary key a match on all records.
     * <p />
     * The primary keys are normalized by removing all whitespace and converting
     * to lower case before being compared.
     * <p />
     * Fields are formatted to the correct width before being inserted into the
     * database.
     * <p />
     * An attempt is made to reuse a known deleted record before appending the
     * new record to the database.
     * <p />
     * If the data to be written does not pass a format check, -1 will be
     * returned as the record number. A better solution would be to throw an
     * exception but doing so would make the method incompatible with the throws
     * clause of the DBAccess interface.
     * @see suncertify.db.DBAccess#createRecord(java.lang.String[])
     * @see suncertify.db.DBIo#normalizeKey(String, String)
     * @see suncertify.db.Data#formatRecord(String[])
     */
    @Override
    public long createRecord(String[] data) throws DuplicateKeyException {
        if ((data[0] == null) || data[0].trim().equals("")) {
            // duplicate key is appropriate because an empty value matches
            // everything.
            throw new DuplicateKeyException(
                "Can not create record with empty primary key [0].");
        }
        if ((data[1] == null) || data[1].trim().equals("")) {
            // duplicate key ex is appropriate because an empty value matches
            // everything.
            throw new DuplicateKeyException(
                "Can not create record with empty primary key [1].");
        }
        String normalKey = DBIo.normalizeKey(data[0], data[1]);
        try {
            if (io.isKeyInUse(normalKey)) {
                throw new DuplicateKeyException("key = [" + normalKey + "]");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            return -1L;
        }
        data = formatRecord(data);
        if (data == null) {
            System.out
                .println("ERROR: Attempt to create a new record with bad data");
            // duplicate key exception not acceptable. Need to inform user some
            // other way.
            return -1;
        }
        Long delRec = recordCache.poll();
        if (delRec == null) {
            delRec = dbInfo.getNumberOfRecordsAndIncrement();
        }
        long offset = -1L;
        offset = dbInfo.getRecordOffset(delRec);
        try {
            io.write(data, offset, Data.VALID_RECORD);
        } catch (IOException e) {
            e.printStackTrace();
            delRec = -1L;
        }
        // dump();
        Data.printMsg("record inserted: " + delRec);
        return delRec;
    }

    /**
     * Record number is checked for validity. The systems assumes the user has a
     * lock on the record before attempting this operation. The associated
     * cookie is checked to make sure it is the same cookie that is holding the
     * lock on the record number. If successful, the newly deleted record number
     * is cached for reuse on the next new record insert.
     * @see suncertify.db.DBAccess#deleteRecord(long, long)
     * @see suncertify.db.DBRecordLocker#checkLock(long, long)
     */
    @Override
    public void deleteRecord(long recNo, long lockCookie)
        throws RecordNotFoundException, SecurityException {
        if (!isValidRecNo(recNo)) {
            throw new RecordNotFoundException("recNo: " + recNo + " not found.");
        }
        recordLocker.checkLock(recNo, lockCookie);
        short status = io.writeStatus(recNo, Data.DELETED_RECORD);
        if (status != Data.DELETED_RECORD) {
            String m = "Error deleting record: " + recNo + ", with handle: "
                + lockCookie;
            throw new RecordNotFoundException(m);
        }
        recordCache.offer(recNo);
        Data.printMsg("record deleted: " + recNo);
    }

    /**
     * Searches each record in the underlying database for a match. Each record
     * is locked before being compared to avoid asynchronous writes on the same
     * record during a comparison. In order for a match to occur a database
     * value must start with the string in the corresponding field of the search
     * criteria array argument. Searches are not case sensitive. Null values in
     * the search criteria will match every cell in the corresponding column.
     * @see suncertify.db.DBAccess#findByCriteria(java.lang.String[])
     */
    @Override
    public long[] findByCriteria(String[] criteria) {
        if ((criteria == null) || (criteria.length == 0)
            || (criteria.length > dbInfo.getNumOfFields())) {
            return null;
        }
        ArrayList<Long> resultSet = new ArrayList<Long>();
        for (long i = 0; i < dbInfo.getNumberOfRecords(); i++) {
            if (!isValidRecNo(i)) {
                continue;
            }
            long cookie = Long.MIN_VALUE;
            try {
                cookie = lockRecord(i);
                String[] result = io.read(i);
                unlock(i, cookie);
                BitSet matchSet = new BitSet(criteria.length);
                for (int j = 0; j < criteria.length; j++) {
                    String c = criteria[j];
                    if ((c == null) || c.isEmpty()) {
                        matchSet.set(j);
                    } else {
                        c = c.replaceAll("\\s+", "");
                        Pattern p = Pattern.compile("\\Q" + c + "\\E" + "(.*)",
                            Pattern.CASE_INSENSITIVE);
                        String r = result[j].replaceAll("\\s+", "");
                        if (p.matcher(r).matches()) {
                            matchSet.set(j);
                        }
                    }
                }
                if (matchSet.cardinality() == criteria.length) {
                    resultSet.add(i);
                }
            } catch (Exception e) {
                unlock(i, cookie);
                e.printStackTrace();
            }
        }
        long[] result = new long[resultSet.size()];
        for (int i = 0; i < resultSet.size(); i++) {
            result[i] = resultSet.get(i);
        }
        return result;
    }

    /**
     * Record validity is first checked before locking the record.
     * @see suncertify.db.Data#isValidRecNo(long)
     * @see suncertify.db.DBAccess#lockRecord(long)
     */
    @Override
    public long lockRecord(long recNo) throws RecordNotFoundException {
        if (!isValidRecNo(recNo)) {
            throw new RecordNotFoundException("recNo: " + recNo + " not found.");
        }
        try {
            return recordLocker.lock(recNo);
        } catch (Exception e) {
            String msg = "Error locking record: " + recNo;
            throw new RecordNotFoundException(msg);
        }
    }

    /**
     * Records are locked before reading to avoid asynchronous write operations
     * on the same record during a read operation.
     * @see suncertify.db.DBAccess#readRecord(long)
     * @see suncertify.db.Data#lockRecord(long)
     */
    @Override
    public String[] readRecord(long recNo) throws RecordNotFoundException {
        long cookie = Long.MIN_VALUE;
        String[] record = null;
        try {
            cookie = lockRecord(recNo);
            record = io.read(recNo);
            Data.printMsg("record read: " + recNo);
        } catch (IOException e) {
            String m = "Error reading record: " + recNo;
            throw new RecordNotFoundException(m);
        } finally {
            unlock(recNo, cookie);
        }
        return record;
    }

    /**
     * Unlocks a record and disassociates the record number from the cookie.
     * @see suncertify.db.DBAccess#unlock(long, long)
     * @see suncertify.db.DBRecordLocker#unlock(long, long)
     */
    @Override
    public void unlock(long recNo, long cookie) throws SecurityException {
        recordLocker.unlock(recNo, cookie);
    }

    /**
     * The first two fields are considered joint primary keys. If any one of
     * them is either null or empty the method will throw a
     * RecordNotFoundException. Empty or null values are generally considered
     * wildcards and this method will interpret an attempt to update a new
     * record with a wild card a violation of the implicit contract that only 1
     * record is to be updated. A better solution would be to throw an exception
     * but doing so would make the method incompatible with the throws clause of
     * the DBAccess interface.
     * <p/>
     * Data is formatted prior to updating the database.
     * <p/>
     * The cookie argument is checked to make sure that it matches the cookie in
     * the lock holding this record.
     * @see suncertify.db.DBAccess#updateRecord(long, java.lang.String[], long)
     * @see suncertify.db.DBRecordLocker#checkLock(long, long)
     * @see suncertify.db.Data#formatRecord(String[])
     */
    @Override
    public void updateRecord(long recNo, String[] data, long lockCookie)
        throws RecordNotFoundException, SecurityException {
        if (!isValidRecNo(recNo)) {
            throw new RecordNotFoundException("recNo: " + recNo + " not found.");
        }
        if ((data[0] == null) || data[0].trim().equals("")) {
            // record not found is appropriate because an empty value matches
            // everything.
            throw new RecordNotFoundException(
                "Can not update a record with an empty primary key [0].");
        }
        if ((data[1] == null) || data[1].trim().equals("")) {
            // record not found is appropriate because an empty value matches
            // everything.
            throw new RecordNotFoundException(
                "Can not update a record with an empty primary key [1].");
        }
        data = formatRecord(data);
        if (data == null) {
            String msg = "ERROR: Attempt to update a record with bad data, recNo="
                + recNo;
            System.out.println(msg);
            // This exception may not be the best but we need
            // to inform the user. It in inconsistent with createRecord and
            // represents a best effort as there is no return value defined in
            // the interface.
            throw new RecordNotFoundException(msg);
        }
        recordLocker.checkLock(recNo, lockCookie);
        long offset = -1L;
        offset = dbInfo.getRecordOffset(recNo);
        try {
            io.write(data, offset, Data.VALID_RECORD);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RecordNotFoundException("IO Exception updating record: "
                + recNo + ". Message: " + e.getMessage());
        }
        Data.printMsg("record updated: " + recNo);
    }

    /*
     * FOR UNIT TESTING ONLY
     */
    final DBInfo getDBInfo() {
        return this.dbInfo;
    }

    /*
     * FOR UNIT TESTING ONLY
     */
    final DBRecordLocker getRecordLocker() {
        return this.recordLocker;
    }

    /*
     * FOR UNIT TESTING ONLY
     * @return Map of all the records in the underlying database index by a
     * normalized key string.
     */
    final TreeMap<String, String[]> dump() {
        TreeMap<String, String[]> d = new TreeMap<String, String[]>();
        for (int i = 0; i < dbInfo.getNumberOfRecords(); i++) {
            try {
                String[] rec = io.read(i);
                String name = rec[0];
                String location = rec[1];
                String key = DBIo.normalizeKey(name, location);
                d.put(key, rec);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return d;
    }

    /**
     * Valid record checker. Checks to make sure the recNo is within bounds and,
     * if so, whether the record being accessed is available for modification.
     * @param recNo The record number.
     * @return boolean true if recNo is active.
     */
    private final boolean isValidRecNo(long recNo) {
        if ((recNo < 0) || (recNo >= dbInfo.getNumberOfRecords())) {
            return false;
        }
        short recStatus = io.readStatus(recNo);
        if (recStatus != Data.VALID_RECORD) {
            return false;
        }
        return true;
    }

    /**
     * Initializes the class by verifying the path to the database file is valid
     * @param dbFileURI the file object.
     */
    public void init(String dbFileURI) throws FileNotFoundException,
        IOException {
        URI uri = null;
        try {
            uri = new URI(dbFileURI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            close();
            System.exit(-1);
        }
        this.file = new File(uri);
        dbInfo = new DBInfo(file);
        io = new DBIo(file, dbInfo);
        recordLocker = new DBRecordLocker((int) dbInfo.getNumberOfRecords());
    }

    /**
     * Ensures that fields of a record to be inserted or updated into the
     * database are the appropriate length. Null fields not allowed. The method
     * will halt and return null when encountering a field where nulls are not
     * allowed.
     * @param rec A record of data to format
     * @return The formatted data. Null if proper formatting could not complete.
     */
    private String[] formatRecord(String[] rec) {
        if ((rec == null) || (rec.length != dbInfo.getNumOfFields())) {
            return null;
        }
        for (int i = 0; i < (rec.length - 1); i++) { // The last column can be
            // empty
            String tmp = rec[i];
            if ((tmp == null) || tmp.trim().equals("")) {
                return null;
            }
        }
        // Record # is excluded because it is not user defined.
        String[] record = new String[dbInfo.getNumOfFields()];
        short len = DBField.NAME.getLen();
        record[0] = String.format("%1$-" + len + "s", rec[0]);
        len = DBField.LOCATION.getLen();
        record[1] = String.format("%1$-" + len + "s", rec[1]);
        len = DBField.SPECIALTIES.getLen();
        record[2] = String.format("%1$-" + len + "s", rec[2]);
        len = DBField.SIZE.getLen();
        record[3] = String.format("%1$-" + len + "s", rec[3]);
        len = DBField.RATE.getLen();
        record[4] = String.format("%1$-" + len + "s", rec[4]);
        len = DBField.OWNER.getLen();
        record[5] = String.format("%1$-" + len + "s", rec[5]);
        return record;
    }

    /**
     * prints out a message including the thread name.
     * @param msg The message to print.
     */
    public static void printMsg(String msg) {
        System.out.println("Thread[" + Thread.currentThread().getName() + "]: "
            + msg);
    }
}
