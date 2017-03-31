/*
 * DisplayClearButtonListener.java Sun Certified Developer for the Java 2
 * Platform Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.view;

import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import suncertify.control.DataAccess;
import suncertify.db.RecordNotFoundException;
import suncertify.model.DataConversionHelper;
import suncertify.model.DisplayRecord;
import suncertify.model.DisplayTableModel;

/**
 * Action Listener which performs the function of committing changes from the
 * display to the database. Users make updates to fields and mark rows for
 * delete in the display. All changes, both update and delete, are committed in
 * one-shot when the the commit button is pressed. The actions are invoked in
 * new threads to free the AWT Event Dispatching thread.
 * @author Starkie, Michael C.
 * @since Dec 13, 2010:7:46:46 AM
 */
public class CommitButtonListener extends SearchButtonListener {
    /**
     * @param v A non-null handle to the GUI must be passed.
     */
    public CommitButtonListener(CRLView v) {
        super(v);
    }

    /**
     * Rows marked for deletion are collected first from the data model and
     * committed to the database followed by updates. When all database
     * operations are successful, the changes are then committed in the model.
     * There is a possibility that changes might be partially committed in the
     * database and not the model. This situation is presently not handled.
     * @see suncertify.model.DisplayTableModel#commitChanges()
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        JTable displayTable = view.getDisplayTable();
        TableCellEditor cellEditor = displayTable.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
        displayTable.clearSelection();
        doCommit();
    }

    /**
     * FORK the commit operation onto a new thread and free the AWT Event
     * Dispatching thread.
     */
    private void doCommit() {
        Thread doCommit = new Thread() {
            @Override
            public void run() {
                try {
                    view.disableGUI();
                    handleCommitChanges();
                } finally {
                    view.enableGUI();
                }
            }
        };
        doCommit.start();
    }

    /**
     * Retrieves rows marked for deletion and modification from the Display
     * Table Model. Rows marked for deletion are deleted from the remote or
     * underlying database. Rows marked for update are updated in the remote or
     * underlying database. If the operation is successful the rows are
     * committed in the table model which means that any original cached values
     * are cleared. A single exception will abort the entire operation.
     */
    private void handleCommitChanges() {
        JTable displayTable = view.getDisplayTable();
        TableModel tm = displayTable.getModel();
        if (!(tm instanceof DisplayTableModel)) {
            return;
        }
        DisplayTableModel model = (DisplayTableModel) displayTable.getModel();
        DataAccess dataAccess = view.getDataAccess();
        try {
            // get all the rows where delete is checked
            ArrayList<Long> rowsToDelete = model.getDeleted();
            Long[] delArray = rowsToDelete
                .toArray(new Long[rowsToDelete.size()]);
            dataAccess.deleteRecords(delArray);
            ArrayList<DisplayRecord> rowsToUpdate = model.getUpdated();
            ArrayList<String[]> formattedRows = DataConversionHelper
                .DisplayRecordToDBRecord(rowsToUpdate);
            dataAccess.updateRecords(formattedRows);
            model.commitChanges();
        } catch (RemoteException re) {
            re.printStackTrace();
            displayPopUpError("There was a communication error."
                + "  Please contact technical support.  RemoteException ("
                + re.getMessage() + ")");
        } catch (RecordNotFoundException rnf) {
            rnf.printStackTrace();
            displayPopUp("Record Not Found (" + rnf.getMessage() + ")");
        } catch (SecurityException se) {
            se.printStackTrace();
            displayPopUpError("There was a communication error."
                + "  Please contact technical support. SecurityException ("
                + se.getMessage() + ")");
        } catch (Exception ex) {
            ex.printStackTrace();
            displayPopUpError("There was a communication error."
                + "  Please contact technical support. Exception ("
                + ex.getMessage() + ")");
        }
    }

    /**
     * Display an error pop-up
     * @see suncertify.view.CRLView#showMessageDialog(String, String, int, int)
     */
    private void displayPopUpError(String errorMsg) {
        view.showMessageDialog(errorMsg, "Error", JOptionPane.OK_OPTION,
            JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Display an alert pop-up
     * @see suncertify.view.CRLView#showMessageDialog(String, String, int, int)
     */
    private void displayPopUp(String errorMsg) {
        view.showMessageDialog(errorMsg, "Alert", JOptionPane.OK_OPTION,
            JOptionPane.INFORMATION_MESSAGE);
    }
}
