/*
 * DisplayClearButtonListener.java Sun Certified Developer for the Java 2
 * Platform Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.props;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * Action listener that responds to a user request to commit properties changes
 * to the underlying properties file.
 * @author Starkie, Michael C.
 * @since Dec 13, 2010:7:46:46 AM
 */
public class PropertiesCommitButtonListener extends
    PropertiesLoadButtonListener {
    /** A reference to the UI */
    protected PropView view = null;

    /**
     * @param v A non-null reference to the GUI must be passed.
     */
    public PropertiesCommitButtonListener(PropView v) {
        super(v);
        this.view = v;
    }

    /**
     * Forces a write of the properties in a properties object to a properties
     * file. Existing properties contained in the file will be overwrite.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JTable propsTable = view.getPropsTable();
        PropertiesTableModel model = (PropertiesTableModel) propsTable
            .getModel();
        TableCellEditor cellEditor = propsTable.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
        propsTable.clearSelection();
        model.commitChanges();
        Properties props = new Properties();
        ArrayList<PropertyRecord> data = model.getData();
        ListIterator<PropertyRecord> i = data.listIterator();
        while (i.hasNext()) {
            PropertyRecord rec = i.next();
            String name = rec.getPropertyName();
            String value = rec.getPropertyValue();
            System.out.println("writing property to file: key=" + name
                + ", value=" + value);
            props.put(name, value);
        }
        try {
            PropConfigLauncher.writeProperties(props);
        } catch (Exception ex) {
            displayPopUpError("There was a communication error saving properties."
                + "  Please contact technical support. Exception ("
                + ex.getMessage() + ")");
            ex.printStackTrace();
        }
        super.actionPerformed(e);
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
