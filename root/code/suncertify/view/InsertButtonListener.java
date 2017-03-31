/*
 * DisplayClearButtonListener.java Sun Certified Developer for the Java 2
 * Platform Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.view;

import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import suncertify.control.DataAccess;
import suncertify.db.DuplicateKeyException;
import suncertify.model.Rate;
import suncertify.model.SearchCriteriaTableModel;

/**
 * Listens for a request by the user to insert a new record. This is a sub-class
 * of the search button listener and takes advantage of that base class to do a
 * search for the newly entered record from the search table so that it shows up
 * in the display table as if the user did a search on the record. The actions
 * are invoked in new threads to free the AWT Event Dispatching thread.
 * @see suncertify.view.SearchButtonListener
 * @author Starkie, Michael C.
 * @since Dec 13, 2010:7:46:46 AM
 */
public class InsertButtonListener extends SearchButtonListener {
    /**
     * @param v A non-null reference to the GUI must be passed.
     */
    public InsertButtonListener(CRLView v) {
        super(v);
    }

    /**
     * Validates each field's data before committing the values to the database.
     * Bad data values will result in an error pop-up dialogue for the user
     * indicating which field contains bad data. After validation the operation
     * is forked on a new thread and the the AWT Event Dispatching thread is
     * freed.
     * @see suncertify.view.SearchButtonListener#actionPerformed(ActionEvent)
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
        model.setValueAt("", 0, 0);
        String nameEntry = (String) model.getValueAt(SearchButtonListener.ROW,
            1);
        if (nameEntry == null) {
            displayPopUpError("Name field can not be empty");
            return;
        }
        nameEntry = nameEntry.trim();
        if (nameEntry.equals("")) {
            displayPopUpError("Name field can not be empty");
            return;
        }
        String cityEntry = (String) model.getValueAt(SearchButtonListener.ROW,
            2);
        if (cityEntry == null) {
            displayPopUpError("City field can not be empty");
            return;
        }
        cityEntry = cityEntry.trim();
        if (cityEntry.equals("")) {
            displayPopUpError("City field can not be empty");
            return;
        }
        String workEntry = (String) model.getValueAt(SearchButtonListener.ROW,
            3);
        if (workEntry == null) {
            displayPopUpError("Work field can not be empty");
            return;
        }
        workEntry = workEntry.trim();
        if (workEntry.equals("")) {
            displayPopUpError("Work field can not be empty");
            return;
        }
        String sizeEntry = (String) model.getValueAt(SearchButtonListener.ROW,
            4);
        if (sizeEntry == null) {
            displayPopUpError("Size field can not be empty");
            return;
        }
        sizeEntry = sizeEntry.trim();
        if (sizeEntry.equals("")) {
            displayPopUpError("Size field can not be empty");
            return;
        }
        try {
            Integer.valueOf(sizeEntry);
        } catch (Exception e) {
            displayPopUpError("Company Size field must be a number");
            return;
        }
        String rateEntry = (String) model.getValueAt(SearchButtonListener.ROW,
            5);
        if (rateEntry == null) {
            displayPopUpError("Rate field can not be empty");
            return;
        }
        rateEntry = rateEntry.trim();
        if (rateEntry.equals("") || rateEntry.equals("$")) {
            displayPopUpError("Rate field can not be empty");
            return;
        }
        try {
            new Rate(rateEntry);
        } catch (Exception e) {
            displayPopUpError("Company Rate field must be a number");
            return;
        }
        String custEntry = (String) model.getValueAt(SearchButtonListener.ROW,
            6);
        if (custEntry == null) {
            custEntry = "";
        }
        custEntry = custEntry.trim();
        if (!custEntry.equals("")) {
            try {
                Integer.valueOf(custEntry);
            } catch (Exception e) {
                displayPopUpError("Customer field must be a number");
                return;
            }
        }
        String[] data = new String[] {
            nameEntry, cityEntry, workEntry, sizeEntry, rateEntry, custEntry };
        doInsertRecord(data, event);
    }

    /**
     * FORK the operation onto a new thread.
     * @param data The validated data to insert.
     * @param event The original ActionEvent received by this listener.
     */
    private void doInsertRecord(final String[] data, final ActionEvent event) {
        Thread doInsertRecord = new Thread() {
            @Override
            public void run() {
                try {
                    view.disableGUI();
                    handleInsertRecord(data, event);
                } finally {
                    view.enableGUI();
                }
            }
        };
        doInsertRecord.start();
    }

    /**
     * Inserts a new record into the remote or underlying database.
     * @param data
     * @param event
     */
    private void handleInsertRecord(String[] data, ActionEvent event) {
        DataAccess dataAccess = view.getDataAccess();
        try {
            long newRecNo = dataAccess.insertRecord(data);
            if (newRecNo == -1) {
                displayPopUpError("There was an internal error on the server"
                    + " side.  Please contact technical support.");
                return;
            }
        } catch (RemoteException re) {
            re.printStackTrace();
            displayPopUpError("Problem inserting record " + data[0]
                + ".  Please contact technical support. RemoteException ("
                + re.getMessage() + ")");
            return;
        } catch (DuplicateKeyException rnf) {
            System.out.println("Duplicate Record");
            displayPopUpError("Attempt to insert record with duplicate key ("
                + rnf.getMessage() + ")");
            rnf.printStackTrace();
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
            displayPopUpError("There was an unrecognized error"
                + ".  Please contact technical support. Exception ("
                + ex.getMessage() + ")");
            return;
        }
        super.actionPerformed(event);
    }

    /**
     * Display an error pop-up
     * @see suncertify.view.CRLView#showMessageDialog(String, String, int, int)
     */
    private void displayPopUpError(String errorMsg) {
        view.showMessageDialog(errorMsg, "Insert Error", JOptionPane.OK_OPTION,
            JOptionPane.ERROR_MESSAGE);
    }
}
