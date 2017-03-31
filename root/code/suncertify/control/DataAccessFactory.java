/*
 * RemoteFileListener.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.control;

import java.io.FileNotFoundException;
import java.io.IOException;

import suncertify.db.DBAccess;
import suncertify.db.Data;

/**
 * Factory responsible for creating instances of DataAccess. DataAccess defines
 * the database operations available to the view. It is the same class used for
 * this purpose in both networked mode and non-networked mode.
 * @see suncertify.control.DataAccess
 * @author Starkie, Michael C.
 * @since Dec 26, 2010:4:50:29 PM
 */
public class DataAccessFactory {
    /**
     * Returns a concrete implementation of DataAccess - the class used to
     * access the underlying database by clients.
     * @param locatorURI The location of the database file in URI string form.
     * @return suncertify.control.DataAccess
     * @throws FileNotFoundException When the given file does not exist or there
     *             is a problem opening the file for reading and writing.
     * @throws IOException When there is an IO problem writing or reading the
     *             given database file.
     */
    public static DataAccess getDataAccess(String locatorURI)
        throws FileNotFoundException, IOException {
        DBAccess dbAccess = new Data(locatorURI);
        return new DataAccessImpl(dbAccess);
    }
}
