/*
 * DBIo.java Sun Certified Developer for the Java 2 Platform Submission. 2010
 * Bodgitt and Scarper, LLC
 */
package suncertify.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This file encapsulates the read and write operations on an underlying file.
 * It is backed by a pool of RandomAccessFiles. It supports multiple concurrent
 * read or write operations to a common underlying file. The class itself is
 * thread safe but there are no guarantees that concurrent requests will not
 * cause data corruption in the underlying file or that corrupt data will not be
 * returned. it is the clients responsibility to ensure that data is protected
 * from corruption from concurrent requests. A pool of file accessors is kept to
 * avoid having to create an instance for every I/O operation. The pool will
 * grow to the size of the greatest number of concurrent requests that have
 * occurred.
 * @author Starkie, Michael C.
 * @since Oct 23, 2010:3:55:12 PM
 */
public class DBIo {
    /** A pool of available db file handles */
    private ConcurrentLinkedQueue<RandomAccessFile> accessCache = new ConcurrentLinkedQueue<RandomAccessFile>();
    /** The database file */
    private File file = null;
    /** database file information including schema and header info */
    private DBInfo dbInfo = null;

    /**
     * @param file The file object upon which the IO is to take place
     * @param dbInfo Static information about the db file included schema,
     *            header and number of records
     */
    public DBIo(File file, DBInfo dbInfo) {
        this.file = file;
        this.dbInfo = dbInfo;
    }

    /**
     * Closes any open file handles and removes them from the pool
     */
    public void close() {
        Iterator<RandomAccessFile> it = accessCache.iterator();
        while (it.hasNext()) {
            RandomAccessFile raf = it.next();
            try {
                raf.close();
                accessCache.remove(raf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        accessCache.clear();
    }

    /**
     * Used to normalize key lookups in a legacy database. Since the key is a
     * text field and subject to manual entry on the part of a CSR, a best
     * effort attempt is made to eliminate distinct keys resulting from
     * differences in case and whitespace. Extra whitespace characters and case
     * discrepancies can be added by CSRs from a user interface without being
     * realized. In such cases comparisons between keys may produce differences.
     * I chose to normalize the keys by eliminating whitespace and shifting all
     * characters to lower case for comparison purposes in an attempt at key
     * uniqueness. A primary (unique) key is based on name and location
     * parameters. Not quite sure where this method belongs at the time of this
     * writing.
     * @param name The company name is the first primary key in a joint primary
     *            key.
     * @param location The company location is the second primary key in the
     *            joint primary key.
     * @return String The key minus whitespace and lower-case shifted.
     */
    public static String normalizeKey(String name, String location) {
        String rep1 = name.trim().toLowerCase().replaceAll("\\s+", "");
        String rep2 = location.trim().toLowerCase().replaceAll("\\s+", "");
        String rep = rep1 + "/" + rep2;
        return rep;
    }

    /**
     * Write a record of data to the underlying db file at a given offset with a
     * given status.
     * @param data one DB record of data
     * @param offset the offset into the underlying file to begin writing the
     *            data at.
     * @param recStatus the associated status of the record.
     * @return true or false in regards to the success of the operation.
     * @throws IOException
     */
    final boolean write(String[] data, long offset, short recStatus)
        throws IOException {
        boolean success = false;
        RandomAccessFile writer = null;
        try {
            writer = open();
            writer.seek(offset);
            writer.writeShort(recStatus);
            for (String element : data) {
                writer.write(element.getBytes(DBInfo.ASCII));
            }
            success = true;
        } finally {
            close(writer);
        }
        return success;
    }

    /**
     * Reads and returns the record from the database file given a record
     * number.
     * @param recNo The record number indicating which record to return
     * @return The record associated with the record number.
     * @throws IOException
     */
    final String[] read(long recNo) throws IOException {
        RandomAccessFile reader = null;
        String[] record = new String[dbInfo.getNumOfFields()];
        long offset = dbInfo.getRecordOffset(recNo);
        try {
            reader = open();
            reader.seek(offset);
            reader.readShort(); // skip the valid/invalid rec ident.
            int f = 0;
            for (DBField field : DBField.values()) {
                byte[] b = new byte[field.getLen()];
                reader.read(b);
                record[f++] = new String(b, DBInfo.ASCII);
            }
        } finally {
            close(reader);
        }
        return record;
    }

    /**
     * Determines if a primary key is already defined and in use within the
     * underlying database.
     * @param key The key to check
     * @return true if the key is already defined.
     * @throws IOException
     */
    final boolean isKeyInUse(String key) throws IOException {
        RandomAccessFile reader = null;
        boolean isFound = false;
        try {
            reader = open();
            for (int i = 0; i < dbInfo.getNumberOfRecords(); i++) {
                long offset = dbInfo.getRecordOffset(i);
                reader.seek(offset);
                short isValid = reader.readShort();
                if (isValid == Data.DELETED_RECORD) {
                    continue;
                }
                byte[] b = new byte[DBField.NAME.getLen()];
                reader.read(b);
                String nameKey = new String(b, DBInfo.ASCII);
                b = new byte[DBField.LOCATION.getLen()];
                reader.read(b);
                String locKey = new String(b, DBInfo.ASCII);
                String normalKey = DBIo.normalizeKey(nameKey, locKey);
                if (normalKey.equals(key)) {
                    isFound = true;
                    break;
                }
            }
        } finally {
            close(reader);
        }
        return isFound;
    }

    /**
     * Returns the status of a record given a record number. The status in a
     * short value defined by the database schema.
     * @param recNo The record number
     * @return the status
     */
    final short readStatus(long recNo) {
        short recStatus = -1;
        long offset = dbInfo.getRecordOffset(recNo);
        RandomAccessFile raf = null;
        try {
            try {
                raf = open();
                raf.seek(offset);
                recStatus = raf.readShort();
            } finally {
                close(raf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recStatus;
    }

    /**
     * updates the status of a record in the database.
     * @param recNo the record number.
     * @param status the status to set
     * @return true if operation succeeded.
     */
    final short writeStatus(long recNo, short status) {
        short recStatus = -1;
        long offset = dbInfo.getRecordOffset(recNo);
        RandomAccessFile raf = null;
        try {
            try {
                raf = open();
                raf.seek(offset);
                raf.writeShort(status);
                recStatus = status;
            } finally {
                close(raf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recStatus;
    }

    /**
     * Encapsulate an open on the underlying db file. A pool of available file
     * handles are checked. A previously opened file handle may be returned from
     * the pool. If not, a new file handle will be created and returned to the
     * pool when available for re-use.
     * @return A free file handle to the underlying database.
     * @throws FileNotFoundException
     */
    private RandomAccessFile open() throws FileNotFoundException {
        RandomAccessFile raf = accessCache.poll();
        if (raf == null) {
            raf = new RandomAccessFile(file, "rws");
        }
        return raf;
    }

    /**
     * returns a file handle to the pool for re-use. Does not actually close the
     * file handle itself. The clean up resources associated with this class use
     * {@link #close()}
     * @param r the file handle
     */
    private void close(RandomAccessFile r) {
        if (r == null) {
            return;
        }
        accessCache.add(r);
    }
}
