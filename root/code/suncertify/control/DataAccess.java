/*
 * DBRemote.java Sun Certified Developer for the Java 2 Platform Submission.
 */
package suncertify.control;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;

/**
 * The interface that defines all remote operations available to the network
 * client user. It was written to perform bulk operations whenever possible so
 * as to offline any iterative processing on the server side. This serves to
 * save time on reducing the number network calls. Included in this interface
 * are two methods to read and set the remote location of the underlying
 * database. Changing the location will force a re-initialization of the network
 * server.
 * @author Starkie, Michael C.
 * @since Nov 6, 2010:8:41:16 PM
 */
public interface DataAccess extends Remote {
    /**
     * Sets the location of the remote DB file.
     * @param fileURI The location specified as a URI.
     * @return Boolean Indicates success or failure with the operation.
     * @throws RemoteException A problem associate with RMI prevents his.
     * @throws URISyntaxException Syntax of URI is incorrect.
     * @throws IOException A problem with the physical file on the remote host.
     */
    public Boolean setLocatorURI(String fileURI) throws RemoteException,
        URISyntaxException, IOException;

    /**
     * Retrieves the location of the remote file specified as a URI representing
     * the path and filename of the file as it exists on the remote host.
     * @return The URI as a String
     * @throws RemoteException A problem associated with RMI prevents this.
     */
    public String getLocatorURI() throws RemoteException;

    /**
     * Retrieves a record from the remote server given a record number.
     * @param recNo The record number to retrieve.
     * @return The fields of the record in column order.
     * @throws RemoteException A problem associated with RMI prevents this.
     * @throws RecordNotFoundException The record number does not exist in the
     *             remote table.
     */
    public String[] readRecord(long recNo) throws RemoteException,
        RecordNotFoundException;

    /**
     * Performs a case insensitive search on the remote table based upon a set
     * of search strings specified as an argument to the method. Each field in
     * the array argument represents a column in the table (in column order) and
     * may be empty in which case that particular field will match all records.
     * Partial strings may be also be specified. A limitation exists in that the
     * cells being matched in the underlying table must start with the criteria
     * specified in the search criteria array for any given column.
     * @param criteria An array of column values to search for.
     * @return An array of Records that match the criteria.
     * @throws RemoteException When method fails due to an RMI condition.
     */
    public String[][] searchRecords(String[] criteria) throws RemoteException;

    /**
     * Inserts a new record into the table. The user interface view does not
     * currently support bulk inserts.
     * @param data A record to insert.
     * @return The record number assigned by the database server that identifies
     *         the new record.
     * @throws RemoteException When method fails due to an RMI condition.
     * @throws DuplicateKeyException An attempt to insert a record with a
     *             non-unique key. Fields assigned as primary keys may very with
     *             each data base schema.
     */
    public long insertRecord(String[] data) throws RemoteException,
        DuplicateKeyException;

    /**
     * Deletes records from the remote database.
     * @param records An array of record numbers to delete.
     * @throws RemoteException When method fails due to an RMI condition.
     * @throws RecordNotFoundException When an attempt is made to delete a
     *             record that is unknown to the remote database the method will
     *             halt. Some valid record numbers may not be deleted as a
     *             result. User should retry the operation after removing the
     *             offending record number.
     * @throws SecurityException An illegal attempt to delete records from the
     *             table has been attempted.
     */
    public void deleteRecords(Long[] records) throws RemoteException,
        RecordNotFoundException, SecurityException;

    /**
     * Update a set of records in a remote database.
     * @param records An array of records to update. The argument should include
     *            the record number of the records being updated as the first
     *            field in each array followed by the remaining database fields
     *            in column order.
     * @throws RemoteException When method fails due to an RMI condition.
     * @throws RecordNotFoundException When an attempt is made to delete a
     *             record that is unknown to the remote database the method will
     *             halt. Some valid record numbers may not be deleted as a
     *             result. User should retry the operation after removing the
     *             offending record number.
     * @throws SecurityException An illegal attempt to delete records from the
     *             table has been attempted.
     */
    public void updateRecords(ArrayList<String[]> records)
        throws RemoteException, RecordNotFoundException, SecurityException;
}
