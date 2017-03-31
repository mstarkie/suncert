package suncertify.control;

import java.rmi.Naming;

class DataAccessClient {
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
            Naming.lookup("///DataAccess");
        } catch (Exception e) {
            System.out.println("DataAccessClient exception: " + e);
        }
    }
    // -------------------- INNER CLASSES -------------------- //
}
