/*
 * SearchButtonListener.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.props;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * Clears the property entry section of the properties editor.
 * @author Starkie, Michael C.
 * @since Nov 30, 2010:6:58:23 PM
 */
public class ClearInsertPropertyButtonListener implements ActionListener {
    /** A reference to the UI */
    protected PropView view = null;

    /**
     * @param v A non-null reference to the UI.
     */
    public ClearInsertPropertyButtonListener(PropView v) {
        this.view = v;
    }

    /**
     * clears the insert table by simply replacing the table model with a new
     * instance.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JTable propInsertTable = view.getPropInsertTable();
        TableCellEditor cellEditor = propInsertTable.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
        propInsertTable.clearSelection();
        PropertyInsertTableModel tm = new PropertyInsertTableModel();
        propInsertTable.setModel(tm);
    }
}
