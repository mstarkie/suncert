/*
 * DataAccessImpl.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.control;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import suncertify.db.DBAccess;
import suncertify.db.Data;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.model.DataConversionHelper;

/**
 * The concrete implementation of the interface which defines the remote
 * database operations available to a networked client.
 * @see suncertify.control.DataAccess
 * @author Starkie, Michael C.
 * @since Nov 8, 2010:8:14:00 AM
 */
public class DataAccessImpl extends UnicastRemoteObject implements DataAccess {
    /** The static serial version ID */
    private static final long serialVersionUID = 4685640070991322679L;
    /**
     * The delegate which implements the operations defined by the database
     * access interface
     * @see suncertify.db.Data
     */
    private DBAccess data = null;

    /**
     * @param d the implementation of the data access interface. Maybe locally
     *            or remotely defined.
     * @exception RemoteException if the object handle cannot be constructed.
     * @see suncertify.db.DBAccess
     */
    public DataAccessImpl(DBAccess d) throws RemoteException {
        setDBAccess(d);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutting down cleanly");
                close();
            }
        });
    }

    /**
     * Sets the Database access delegate.
     * @param d The database access delegate.
     * @see suncertify.db.DBAccess
     */
    public void setDBAccess(DBAccess d) {
        this.data = d;
    }

    /**
     * Used to close all IO channels to the database.
     */
    public void close() {
        ((Data) data).close();
    }

    /**
     * @see suncertify.control.DataAccess#setLocatorURI(String)
     */
    @Override
    public Boolean setLocatorURI(String fileURI) throws RemoteException,
        URISyntaxException, IOException {
        ((Data) data).close();
        ((Data) data).init(fileURI);
        setDBAccess(data);
        System.out.println("Set remote file URI to: " + fileURI);
        return true;
    }

    /**
     * @see suncertify.control.DataAccess#getLocatorURI()
     */
    @Override
    public String getLocatorURI() throws RemoteException {
        return data.toString();
    }

    /**
     * @see suncertify.control.DataAccess#readRecord(long)
     */
    @Override
    public String[] readRecord(long recNo) throws RemoteException,
        RecordNotFoundException {
        String[] record = data.readRecord(recNo);
        return DataConversionHelper.addRecordNumberToDBRecord(record, recNo);
    }

    /**
     * @see suncertify.control.DataAccess#searchRecords(java.lang.String[])
     */
    @Override
    public String[][] searchRecords(String[] criteria) throws RemoteException {
        long[] matchedRecords = data.findByCriteria(criteria);
        if ((matchedRecords == null) || (matchedRecords.length == 0)) {
            return null;
        }
        ArrayList<String[]> recordData = new ArrayList<String[]>();
        for (long recordNum : matchedRecords) {
            try {
                String[] rec = data.readRecord(recordNum);
                recordData.add(DataConversionHelper.addRecordNumberToDBRecord(
                    rec, recordNum));
            } catch (RecordNotFoundException e) {
                e.printStackTrace();
                System.out
                    .println("This should not be happening.  Please investigate me.");
                String[] errorRecord = new String[] {
                    "Internal ERROR: Contact Support" };
                recordData.add(errorRecord);
            }
        }
        return recordData.toArray(new String[recordData.size()][]);
    }

    /**
     * @see suncertify.control.DataAccess#deleteRecords(java.lang.Long[])
     */
    @Override
    public void deleteRecords(Long[] records) throws RemoteException,
        RecordNotFoundException, SecurityException {
        if ((records == null) || (records.length == 0)) {
            return;
        }
        for (Long r : records) {
            long recNo = r.longValue();
            long cookie = data.lockRecord(recNo);
            data.deleteRecord(recNo, cookie);
            data.unlock(recNo, cookie);
        }
    }

    /**
     * @see suncertify.control.DataAccess#insertRecord(java.lang.String[])
     */
    @Override
    public long insertRecord(String[] insertData) throws RemoteException,
        DuplicateKeyException {
        return data.createRecord(insertData);
    }

    /**
     * @see suncertify.control.DataAccess#updateRecords(java.util.ArrayList)
     */
    @Override
    public void updateRecords(ArrayList<String[]> records)
        throws RemoteException, RecordNotFoundException, SecurityException {
        for (String[] r : records) {
            String[] d = DataConversionHelper.removeRecordNumberFromDBRecord(r);
            long recNo = Long.parseLong(r[0]);
            long cookie = data.lockRecord(recNo);
            data.updateRecord(recNo, d, cookie);
            data.unlock(recNo, cookie);
        }
    }
}
