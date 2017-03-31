/*
 * SearchButtonListener.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import suncertify.control.DataAccess;
import suncertify.db.RecordNotFoundException;
import suncertify.model.DataConversionHelper;
import suncertify.model.DisplayRecord;
import suncertify.model.DisplayTableModel;
import suncertify.model.SearchCriteriaTableModel;

/**
 * Reads user-entered field data from the search criteria table and does a
 * lookup on a database given the criteria. Two types of searches are supported:
 * #1-search for a particular record number or #2-search for records whose
 * fields match, at least partially, the values supplied by the user in the
 * remaining cells excluding the record number cell. Empty fields or characters
 * are considered wild cards and will match anything. Supply no data and search
 * will match and return all records. When a record number is entered all other
 * search criteria are ignored and the search will look only for the supplied
 * record number. The actions are invoked in new threads to free the AWT Event
 * Dispatching thread.
 * @author Starkie, Michael C.
 * @since Nov 30, 2010:6:58:23 PM
 */
public class SearchButtonListener implements ActionListener {
    /** only 1 row exists in the search criteria table */
    protected static int ROW = 0;
    /** access to the main GUI */
    protected CRLView view = null;

    /**
     * @param v A non-null reference to the view must be supplied.
     */
    public SearchButtonListener(CRLView v) {
        this.view = v;
    }

    /**
     * Performs the functions mentioned in the class description.
     * @see SearchButtonListener#handleReadRecord(String)
     * @see SearchButtonListener#handleSearchCriteria(SearchCriteriaTableModel)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        JTable searchTable = view.getSearchTable();
        TableCellEditor cellEditor = searchTable.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
        searchTable.clearSelection();
        SearchCriteriaTableModel model = (SearchCriteriaTableModel) searchTable
            .getModel();
        String recordNumEntry = (String) model.getValueAt(
            SearchButtonListener.ROW, 0);
        if ((recordNumEntry == null) || recordNumEntry.equals("")) {
            doSearchCriteria(model);
        } else {
            doReadRecord(recordNumEntry);
        }
    }

    /**
     * FORK this operation to search the underlying or remote database on a new
     * thread and free the AWT Event Dispatch thread. Disables the GUI until
     * results are returned.
     * @param model The search criteria data model.
     * @see suncertify.model.SearchCriteriaTableModel
     */
    private void doSearchCriteria(final SearchCriteriaTableModel model) {
        Thread doSearchCriteria = new Thread() {
            @Override
            public void run() {
                try {
                    view.disableGUI();
                    handleSearchCriteria(model);
                } finally {
                    view.enableGUI();
                }
            }
        };
        doSearchCriteria.start();
    }

    /**
     * FORK this operation to read a single record from the underlying or remote
     * database on a new thread and free the AWT Event Dispatch thread.
     * @param recordNumEntry The record number to search for.
     */
    private void doReadRecord(final String recordNumEntry) {
        Thread doReadRecord = new Thread() {
            @Override
            public void run() {
                handleReadRecord(recordNumEntry);
            }
        };
        doReadRecord.start();
    }

    /**
     * Searches for a single record that matches the user supplied record
     * number. The resulting row is appended to the search results table and
     * displayed.
     * @param recordNumEntry The record number to search for as a string.
     */
    private void handleReadRecord(String recordNumEntry) {
        long recNum = -1;
        try {
            recNum = Long.parseLong(recordNumEntry);
            DataAccess dataAccess = view.getDataAccess();
            String[] record = null;
            record = dataAccess.readRecord(recNum);
            appendRecord(record);
        } catch (RemoteException re) {
            re.printStackTrace();
            displayPopUpError("Communication error reading record " + recNum
                + ".  Please contact technical support. RemoteException ("
                + re.getMessage() + ")");
        } catch (RecordNotFoundException rnf) {
            displayPopUpError("Record not found: "
                + recNum
                + ".  Please contact technical support. RecordNotFoundException ("
                + rnf.getMessage() + ")");
            rnf.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            displayPopUpError("Error reading record: " + recNum
                + ".  Please contact technical support. Exception ("
                + ex.getMessage() + ")");
        }
    }

    /**
     * Searches for records that match the user supplied search criteria. The
     * resulting rows are appended to the display table model and displayed to
     * the user.
     * @param model The search criteria table model that contains the user
     *            supplied data to search for.
     */
    private void handleSearchCriteria(SearchCriteriaTableModel model) {
        int colCount = model.getColumnCount();
        String[] criteria = new String[colCount - 1]; // recNo search is handled
        for (int columnIndex = 1; columnIndex < colCount; columnIndex++) {
            criteria[columnIndex - 1] = model.convertValueAt(
                SearchButtonListener.ROW, columnIndex);
        }
        try {
            DataAccess dataAccess = view.getDataAccess();
            String[][] displayData = null;
            displayData = dataAccess.searchRecords(criteria);
            view.getDisplayTable();
            if (displayData == null) {
                return;
            }
            appendRecords(displayData);
        } catch (RemoteException re) {
            re.printStackTrace();
            displayPopUpError("Communication error while searching"
                + ".  Please contact technical support. SecurityException ("
                + re.getMessage() + ")");
        } catch (Exception ex) {
            ex.printStackTrace();
            displayPopUpError("Error while searching"
                + ".  Please contact technical support. Exception ("
                + ex.getMessage() + ")");
        }
    }

    /**
     * Appends a record to the display table model so that it is displayed for
     * the user in the display results area of the UI.
     * @param record The single record to append.
     * @throws Exception Runtime exceptions are caught and error pop-ups
     *             displayed. Users are notified of runtime exceptions so that
     *             they know an error has occurred and need to contact technical
     *             support.
     */
    private void appendRecord(String[] record) throws Exception {
        DisplayRecord displayRecord = DataConversionHelper
            .DBRecordToDisplayRecord(record);
        DisplayRecord[] displayData = new DisplayRecord[1];
        displayData[0] = displayRecord;
        addDataToTableModel(displayData);
    }

    /**
     * Appends records to the display table model so that they are displayed for
     * the user in the display results area of the UI.
     * @param records An array of records to append.
     * @throws Exception Runtime exceptions are caught and error pop-ups
     *             displayed. Users are notified of runtime exceptions so that
     *             they know an error has occurred and need to contact technical
     *             support.
     */
    private void appendRecords(String[][] records) throws Exception {
        DisplayRecord[] displayData = new DisplayRecord[records.length];
        for (int recIdx = 0; recIdx < records.length; recIdx++) {
            displayData[recIdx] = DataConversionHelper
                .DBRecordToDisplayRecord(records[recIdx]);
        }
        addDataToTableModel(displayData);
    }

    /**
     * Adds a set of records to the display table model for display.
     * @param displayData An array of records to display.
     * @see suncertify.model.DisplayRecord
     */
    private void addDataToTableModel(DisplayRecord[] displayData) {
        TableModel model = view.getDisplayTable().getModel();
        if (model instanceof DefaultTableModel) {
            model = new DisplayTableModel(displayData);
            view.getDisplayTable().setModel(model);
        } else {
            ((DisplayTableModel) model).appendRecords(displayData);
        }
        JTableFactory.setColumnWidths(view.getDisplayTable());
        JTableFactory.setDisplayTableEditorColumnRestrictions(view
            .getDisplayTable());
    }

    /**
     * Display an error pop-up
     * @see suncertify.view.CRLView#showMessageDialog(String, String, int, int)
     */
    private void displayPopUpError(String errorMsg) {
        view.showMessageDialog(errorMsg, "Error", JOptionPane.OK_OPTION,
            JOptionPane.ERROR_MESSAGE);
    }
}
