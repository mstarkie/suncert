/*
 * SearchButtonListener.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import suncertify.model.SearchCriteriaTableModel;

/**
 * Clears the search criteria entry table.
 * @author Starkie, Michael C.
 * @since Nov 30, 2010:6:58:23 PM
 */
public class SearchClearButtonListener implements ActionListener {
    /** A reference to the UI */
    protected CRLView view = null;

    /**
     * @param v A non-null reference to the UI.
     */
    public SearchClearButtonListener(CRLView v) {
        this.view = v;
    }

    /**
     * clears the search table by simply replacing the table model with a new
     * instance.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JTable searchTable = view.getSearchTable();
        TableCellEditor cellEditor = searchTable.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
        searchTable.clearSelection();
        SearchCriteriaTableModel tm = new SearchCriteriaTableModel();
        searchTable.setModel(tm);
        JTableFactory.setColumnWidths(searchTable);
        JTableFactory.setDisplayTableEditorColumnRestrictions(searchTable);
    }
}
