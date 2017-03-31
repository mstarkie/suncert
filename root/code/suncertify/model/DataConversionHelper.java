/*
 * DataConversionHelper.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.model;

import java.util.ArrayList;

/**
 * Helper class that consolidates functions used to convert a record as
 * understood by a database into a record as understood by the display and vice
 * versa. There is a tight coupling here between the underly schema and table
 * model code that should be addressed in a future release. One solution would
 * have the table model constructed from an external xml file which describes
 * the database schema. If the xml file could be regenerated after changes to
 * the schema and reflected automatically in the table that would remove the
 * coupling.
 * @author Starkie, Michael C.
 * @since Jan 9, 2011:4:39:27 PM
 */
public class DataConversionHelper {
    /**
     * Converts a database record into a display record.
     * @param dbRecord One database record in the form of a String array.
     * @return Once display record in the form of a DisplayRecord.
     * @see suncertify.model.DisplayRecord
     */
    public static DisplayRecord DBRecordToDisplayRecord(String[] dbRecord) {
        DisplayRecord data = new DisplayRecord();
        data.setRecNo(Long.valueOf(dbRecord[DisplayTableModel.REC_COL_IDX]));
        data.setCompanyName(dbRecord[DisplayTableModel.NAM_COL_IDX]);
        data.setCity(dbRecord[DisplayTableModel.CIT_COL_IDX]);
        data.setWorkType(dbRecord[DisplayTableModel.WRK_COL_IDX]);
        data.setCompanySize(Integer
            .valueOf(dbRecord[DisplayTableModel.SZE_COL_IDX]));
        data.setCompanyRate(new Rate(dbRecord[DisplayTableModel.RAT_COL_IDX]));
        data.setCustomerNumber(dbRecord[DisplayTableModel.CUS_COL_IDX]);
        data.setIsUpdate(new Boolean(false));
        data.setIsDelete(new Boolean(false));
        return data;
    }

    /**
     * Converts an array of DisplayRecords into an array of db records.
     * @param records An ArrayList of DisplayRecords to convert.
     * @return An ArrayList of db records. Each db record is a String[].
     * @see suncertify.model.DisplayRecord
     */
    public static ArrayList<String[]> DisplayRecordToDBRecord(
        ArrayList<DisplayRecord> records) {
        ArrayList<String[]> updRecs = new ArrayList<String[]>(records.size());
        for (DisplayRecord obj : records) {
            String[] row = new String[DisplayRecord.NUM_OF_FIELDS - 2]; // remove
            row[DisplayTableModel.REC_COL_IDX] = obj.getRecNo().toString();
            row[DisplayTableModel.NAM_COL_IDX] = obj.getCompanyName();
            row[DisplayTableModel.CIT_COL_IDX] = obj.getCity();
            row[DisplayTableModel.WRK_COL_IDX] = obj.getWorkType();
            row[DisplayTableModel.SZE_COL_IDX] = obj.getCompanySize()
                .toString();
            row[DisplayTableModel.RAT_COL_IDX] = obj.getCompanyRate()
                .toString();
            row[DisplayTableModel.CUS_COL_IDX] = obj.getCustomerNumber()
                .toString();
            updRecs.add(row);
        }
        return updRecs;
    }

    /**
     * Used when updating records in a database from records in the display.
     * This is needed because a database record does not include the record
     * number.
     * @param record The fields of a db record to be updated.
     * @return The field replacements minus the record number.
     */
    public static String[] removeRecordNumberFromDBRecord(String[] record) {
        for (int i = 0; i < record.length; i++) {
            record[i] = record[i].trim();
        }
        int len = record.length - 1;// remove the record number
        String[] data = new String[len];
        System.arraycopy(record, 1, data, 0, record.length - 1);
        return data;
    }

    /**
     * Adds in a record number to the fields of a database record read from the
     * database. Database records do not contain record number but the table
     * model of the display does.
     * @param record A database record.
     * @param recNo The record number used to retrieve the database record.
     * @return A row of data that will be used by the Display data model.
     */
    public static String[] addRecordNumberToDBRecord(String[] record, long recNo) {
        for (int i = 0; i < record.length; i++) {
            record[i] = record[i].trim();
        }
        int len = record.length + 1;// add in the record number
        String[] data = new String[len];
        data[0] = Long.toString(recNo);
        System.arraycopy(record, 0, data, 1, record.length);
        return data;
    }
}
