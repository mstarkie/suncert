/*
 * DisplayClearButtonListener.java Sun Certified Developer for the Java 2
 * Platform Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.view;

import java.awt.event.ActionEvent;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

/**
 * Action listener that responds to a user request to clear the display table of
 * records retrieved from a database.
 * @author Starkie, Michael C.
 * @since Dec 13, 2010:7:46:46 AM
 */
public class DisplayClearButtonListener extends SearchClearButtonListener {
    /**
     * @param v A non-null reference to the GUI must be passed.
     */
    public DisplayClearButtonListener(CRLView v) {
        super(v);
    }

    /**
     * Clears the display results table.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JTable displayTable = view.getDisplayTable();
        TableCellEditor cellEditor = displayTable.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
        displayTable.clearSelection();
        displayTable.setModel(new DefaultTableModel());
    }
}
