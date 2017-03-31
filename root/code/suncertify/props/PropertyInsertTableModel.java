/*
 * PropertiesEditorTableModel.java Sun Certified Developer for the Java 2
 * Platform Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.props;

import javax.swing.table.AbstractTableModel;

/**
 * The data model of the table used to enter new properties.
 * @author Starkie, Michael C.
 * @since Nov 16, 2010:6:16:36 PM
 */
public class PropertyInsertTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1492937192669015157L;
    /** The column names */
    protected final String[] columnNames = new String[] {
        "Property Name", "Property Value" };
    /** The table data */
    protected Object[][] data = null;
    /** The model will always contain 1 row */
    protected int rowCount = 1;

    public PropertyInsertTableModel() {
        rowCount = 1;
        data = new Object[rowCount][getColumnCount()];
        data[rowCount - 1] = new Object[] {
            "", "" };
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        return data.length;
    }

    /**
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    /**
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
     *      int, int)
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
    }

    /**
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /**
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }
}
