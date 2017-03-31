/*
 * DisplayClearButtonListener.java Sun Certified Developer for the Java 2
 * Platform Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.props;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * Action listener that responds to a user request to load properties retrieved
 * from a properties file.
 * @author Starkie, Michael C.
 * @since Dec 13, 2010:7:46:46 AM
 */
public class PropertiesLoadButtonListener implements ActionListener {
    /** A reference to the UI */
    protected PropView view = null;

    /**
     * @param v A non-null reference to the GUI must be passed.
     */
    public PropertiesLoadButtonListener(PropView v) {
        this.view = v;
    }

    /**
     * Retrieves properties from a properties file.
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        JTable propsTable = view.getPropsTable();
        TableCellEditor cellEditor = propsTable.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
        propsTable.clearSelection();
        ArrayList<PropertyRecord> records = getPropertiesFromFile();
        propsTable.setModel(new PropertiesTableModel(records));
        PropTableFactory.setPropertiesColumnWidths(propsTable);
    }

    /**
     * Reads properties from a file and converts them into a list of
     * PropertyRecords.
     * @return A list of PropertyRecords read from a properties file.
     */
    public ArrayList<PropertyRecord> getPropertiesFromFile() {
        Properties props = PropConfigLauncher.readProperties();
        Iterator<Entry<Object, Object>> i = props.entrySet().iterator();
        ArrayList<PropertyRecord> records = new ArrayList<PropertyRecord>();
        while (i.hasNext()) {
            Entry<Object, Object> entry = i.next();
            PropertyRecord rec = new PropertyRecord();
            rec.setPropertyName((String) entry.getKey());
            rec.setPropertyValue((String) entry.getValue());
            records.add(rec);
        }
        return records;
    }
}
