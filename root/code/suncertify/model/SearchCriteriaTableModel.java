/*
 * PropertiesEditorTableModel.java Sun Certified Developer for the Java 2
 * Platform Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.model;

import javax.swing.table.AbstractTableModel;

/**
 * The data model of the table used to enter search criteria.
 * @author Starkie, Michael C.
 * @since Nov 16, 2010:6:16:36 PM
 */
public class SearchCriteriaTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -1781317634536616772L;
    /** The column names */
    protected final String[] columnNames = new String[] {
        "Record #", "Name", "City", "Work", "Size", "Rate", "Customer" };
    /** column index of record number */
    public static final int REC_COL_IDX = 0;
    /** column index of company name */
    public static final int NAM_COL_IDX = 1;
    /** column index of company location */
    public static final int CIT_COL_IDX = 2;
    /** column index of type of work performed */
    public static final int WRK_COL_IDX = 3;
    /** column index of company size */
    public static final int SZE_COL_IDX = 4;
    /** column index of hourly rate */
    public static final int RAT_COL_IDX = 5;
    /** column index of custer number */
    public static final int CUS_COL_IDX = 6;
    /** The table data */
    protected Object[][] data = null;
    /** The model will always contain 1 row */
    protected int rowCount = 1;

    public SearchCriteriaTableModel() {
        rowCount = 1;
        data = new Object[rowCount][getColumnCount()];
        data[rowCount - 1] = new Object[] {
            "", "", "", "", "", "$", "" };
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
     * Ensures that empty cell values are returned as null. This method is used
     * by search engines that treat null values as wildcards.
     * @param rowIndex The index of the row.
     * @param columnIndex The index of the column.
     * @return Either null or the value of the cell.
     */
    public String convertValueAt(int rowIndex, int columnIndex) {
        String val = (String) data[rowIndex][columnIndex];
        if ((val != null) && val.equals("")) {
            val = null;
        }
        return val;
    }

    /**
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
     *      int, int)
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        String v = (String) value;
        if ((col == SearchCriteriaTableModel.RAT_COL_IDX) && !v.contains("$")) {
            value = "$" + value;
        }
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
