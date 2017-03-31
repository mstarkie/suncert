/*
 * InsertPropertyButtonListener.java Sun Certified Developer for the Java 2
 * Platform Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.props;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * @author Starkie, Michael C.
 * @since Feb 6, 2011:6:28:02 PM
 */
public class InsertPropertyButtonListener extends
    PropertiesCommitButtonListener {
    /** only 1 row exists in the property insert table */
    protected static int ROW = 0;

    /**
     * @param v
     */
    public InsertPropertyButtonListener(PropView v) {
        super(v);
    }

    /**
     * Inserts a new property into the property file
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JTable propsTable = view.getPropInsertTable();
        PropertyInsertTableModel model = (PropertyInsertTableModel) propsTable
            .getModel();
        TableCellEditor cellEditor = propsTable.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
        String nameEntry = (String) model.getValueAt(
            InsertPropertyButtonListener.ROW, 0);
        if (nameEntry == null) {
            displayPopUpError("Property name can not be empty");
            return;
        }
        nameEntry = nameEntry.trim();
        if (nameEntry.equals("")) {
            displayPopUpError("Property name can not be empty");
            return;
        }
        String valueEntry = (String) model.getValueAt(
            InsertPropertyButtonListener.ROW, 1);
        if (valueEntry == null) {
            displayPopUpError("Property value can not be empty");
            return;
        }
        valueEntry = valueEntry.trim();
        if (valueEntry.equals("")) {
            displayPopUpError("Property value can not be empty");
            return;
        }
        PropertyRecord newRecord = new PropertyRecord();
        newRecord.setPropertyName(nameEntry);
        newRecord.setPropertyValue(valueEntry);
        propsTable.clearSelection();
        ArrayList<PropertyRecord> records = getPropertiesFromFile();
        records.add(newRecord);
        view.getPropsTable().setModel(new PropertiesTableModel(records));
        super.actionPerformed(e);
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
