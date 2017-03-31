/*
 * DBInfo.java Sun Certified Developer for the Java 2 Platform Submission. 2010
 * Bodgitt and Scarper, LLC
 */
package suncertify.db;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An encapsulation of all static information concerning the database. Includes
 * header and schema related information and calculates the number of records.
 * @author Starkie, Michael C.
 * @since Oct 9, 2010:8:36:25 AM
 */
final class DBInfo {
    /** A static reference to an ASICC character set */
    public static final Charset ASCII = Charset.forName("US-ASCII");
    /** length of field indicating valid/invalid record */
    public static final short VALID_RECORD_FIELD_LEN = 2;
    /** the expected number of fields in a record */
    public static final short NUM_OF_FIELDS = 6;
    /** the expected number of fields in a record */
    public static int RECORD_SIZE = 0; // includes valid/invalid field
    /** the db file header info */
    private DBHeader dbHeader = null;
    /** the db file input stream reader */
    private DataInputStream dis = null;
    /** the size of the db file */
    private long fileSize = 0;
    /** the byte offset indicating the start of data */
    private int startOfRecords = 0;
    /** the number of fields in one record */
    private short numOfFields = 0;
    /** the number of records present in the file */
    private AtomicLong numOfRecords = null;;
    /** the schema info for serialization */
    private String[][] schema = null;

    /**
     * @param dbFile The database file.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public DBInfo(File dbFile) throws FileNotFoundException, IOException {
        fileSize = dbFile.length();
        System.out.println("File size: " + fileSize);
        dis = new DataInputStream(new FileInputStream(dbFile));
        init();
    }

    /**
     * Initialize the data before the class can be used.
     * @throws IOException when file access is corrupted.
     */
    private void init() throws IOException {
        readHeader();
        readSchema();
        calc();
    }

    /**
     * Reads the expected header
     * @throws IOException When parsing file header info fails
     */
    private void readHeader() throws IOException {
        int magicCookie = dis.readInt();
        System.out.println("Magic cookie: " + magicCookie);
        startOfRecords = dis.readInt();
        numOfFields = dis.readShort();
        dbHeader = new DBHeader(magicCookie, startOfRecords, numOfFields);
    }

    /**
     * Reads the schema description from a database file;
     * @throws IOException When parsing file fails.
     */
    private void readSchema() throws IOException {
        int recordLength = 0;
        ArrayList<Short> fieldList = new ArrayList<Short>(dbHeader
            .getNumOfFields());
        schema = new String[dbHeader.getNumOfFields()][];
        for (int i = 0; i < dbHeader.getNumOfFields(); i++) {
            short fieldNameLen = dis.readShort();
            byte[] b = new byte[fieldNameLen];
            dis.read(b);
            String fieldName = new String(b, Charset.forName("US-ASCII"));
            short fieldLen = dis.readShort();
            System.out.println("field name[" + fieldName + "], field len["
                + fieldLen + "]");
            recordLength += fieldLen;
            fieldList.add(fieldLen);
            schema[i] = new String[] {
                fieldName, Short.toString(fieldLen) };
        }
        Short[] fLengths = fieldList.toArray(new Short[fieldList.size()]);
        boolean numOfFieldsCheck = fLengths.length == DBInfo.NUM_OF_FIELDS ? true
            : false;
        if (!numOfFieldsCheck) {
            System.out
                .println("Unexpected number of fields in schema.  Expected = "
                    + DBInfo.NUM_OF_FIELDS + ", read = " + fieldList.size());
            System.exit(-1);
        }
        int fieldLen = 0;
        for (DBField field : DBField.values()) {
            if (field.getLen() != fLengths[fieldLen]) {
                System.out.println("Unexpected field length.  Expected = "
                    + field.getLen() + ", read = " + fLengths[fieldLen]
                    + " for " + field);
                System.exit(-1);
            }
            fieldLen += 1;
        }
        // add 2 extra bytes because each record also contains a valid/invalid
        // record field.
        DBInfo.RECORD_SIZE = recordLength + DBInfo.VALID_RECORD_FIELD_LEN;
    }

    /**
     * Calculate any useful information available from the static file info
     * contained in the header or schema descriptions.
     */
    private void calc() {
        long dataLength = fileSize - startOfRecords;
        numOfRecords = new AtomicLong(dataLength / getRecordLength());
        System.out.println("Number of records in file: " + numOfRecords);
    }

    /**
     * @see DBHeader#getNumOfFields()
     */
    public short getNumOfFields() {
        return dbHeader.getNumOfFields();
    }

    /**
     * @return the static length of each individual record.
     */
    public long getRecordLength() {
        return DBInfo.RECORD_SIZE;
    }

    /**
     * @see DBHeader#getStartOfRecords()
     */
    public long getStartOfRecords() {
        return dbHeader.getStartOfRecords();
    }

    /**
     * Returns the byte offset of a given record number.
     * @param recNumber record numbers start at 0
     * @return long the byte offset of recNumber.
     */
    public long getRecordOffset(long recNumber) {
        return (recNumber * getRecordLength()) + getStartOfRecords();
    }

    /**
     * Returns the number of records contained in the db file.
     * @return long the number of records which is also the next available
     *         record number for use when creating new records.
     */
    public long getNumberOfRecords() {
        return numOfRecords.get();
    }

    /**
     * Enables clients to ensure unique record numbers when creating new
     * records.
     * @return The next available record number.
     */
    public long getNumberOfRecordsAndIncrement() {
        return numOfRecords.getAndIncrement();
    }

    /**
     * Returns a 2D array of fieldName, fieldLength for the number of fields in
     * the underlying database. This is so the data may be serialized.
     * @return String[][] the field schema.
     */
    public String[][] getSchema() {
        return schema;
    }
}
