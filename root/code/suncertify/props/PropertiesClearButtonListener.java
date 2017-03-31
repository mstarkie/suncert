/*
 * DisplayClearButtonListener.java Sun Certified Developer for the Java 2
 * Platform Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.props;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

/**
 * Action listener that responds to a user request to clear the display table of
 * properties retrieved from a properties file.
 * @author Starkie, Michael C.
 * @since Dec 13, 2010:7:46:46 AM
 */
public class PropertiesClearButtonListener implements ActionListener {
    /** A reference to the UI */
    protected PropView view = null;

    /**
     * @param v A non-null reference to the GUI must be passed.
     */
    public PropertiesClearButtonListener(PropView v) {
        this.view = v;
    }

    /**
     * Clears the properties display results table.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        JTable propTable = view.getPropsTable();
        TableCellEditor cellEditor = propTable.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
        propTable.clearSelection();
        propTable.setModel(new DefaultTableModel());
    }
}
