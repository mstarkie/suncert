package suncertify.control;

import java.io.File;
import java.net.URI;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import suncertify.db.DBAccess;
import suncertify.db.Data;
import suncertify.db.FileUtils;

class DataAccessServer {
    // -------------------- STATIC FIELDS -------------------- //
    // -------------------- MEMBER FIELDS -------------------- //
    // --------------------- CONSTRUCTORS -------------------- //
    // ------------------------ PUBLIC ----------------------- //
    // ----------------------- DEFAULT ----------------------- //
    // ---------------------- PROTECTED ---------------------- //
    // ----------------------- PRIVATE ----------------------- //
    // ----------------------- STATIC ------------------------ //
    public static void main(String[] args) {
        try {
            /**
             * String dbFileURI =
             * "file:///Users/Michael/Documents/workspace/sun/root/db-2x2.db";
             * String tmpFileURI = "file:///Users/Michael/Documents/db-2x2.db";
             */
            String dbFileURI = "file:///C:/Users/mike/workspace/sun/root/db-2x2.db";
            String tmpFileURI = "file:///C:/Users/mike/db-2x2.db";
            URI inUri = new URI(dbFileURI);
            File inFile = new File(inUri);
            // copy the database file to a tmp location and use the tmp.
            URI outUri = new URI(tmpFileURI);
            File dbFile = new File(outUri);
            FileUtils.copyFile(inFile, dbFile);
            DBAccess data = new Data(tmpFileURI);
            LocateRegistry.createRegistry(1099);
            Naming.rebind("DataAccess", new DataAccessImpl(data));
            System.out.println("DataAccessServer is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // -------------------- INNER CLASSES -------------------- //
}
