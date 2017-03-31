/*
 * DisplayTableModel.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.props;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.table.AbstractTableModel;

import suncertify.model.CellIdx;

/**
 * The data model used to hold properties to be edited or deleted by a user in a
 * client view.
 * @author Starkie, Michael C.
 * @since Dec 4, 2010:4:45:30 PM
 */
public class PropertiesTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1474367168380659960L;
    /** Property Name */
    private static final String NAME_COL = "Property Name";
    /** Property Value */
    private static final String VAL_COL = "Property Value";
    /** property has been marked for update */
    private static final String UPD_COL = "Update";
    /** property has been marked for deletion */
    private static final String DEL_COL = "Delete";
    /** column index of record number */
    public static final int NAME_COL_IDX = 0;
    /** column index of copany name */
    public static final int VAL_COL_IDX = 1;
    /** column index of is record marked for update */
    public static final int UPD_COL_IDX = 2;
    /** column index of is record marked for delete */
    public static final int DEL_COL_IDX = 3;
    /** array of column names */
    protected final String[] columnNames = new String[] {
        PropertiesTableModel.NAME_COL, PropertiesTableModel.VAL_COL,
        PropertiesTableModel.UPD_COL, PropertiesTableModel.DEL_COL };
    /** used to hold records */
    protected ArrayList<PropertyRecord> data = new ArrayList<PropertyRecord>();
    /**
     * a cache of original data for any modified field. Allows users to revert
     * changes and restore the original value. Referenced by cell index.
     */
    private HashMap<CellIdx, Object> dataCache = new HashMap<CellIdx, Object>();

    /**
     * @param d an array of {@link suncertify.props.PropertyRecord}
     */
    public PropertiesTableModel(List<PropertyRecord> d) {
        for (int i = 0; i < d.size(); i++) {
            data.add(i, d.get(i));
        }
    }

    /**
     * Appends records to the model.
     * @param d An array of {@link suncertify.model.DisplayRecord}
     */
    public void appendRecords(List<PropertyRecord> d) {
        int numOfRecords = getRowCount();
        for (PropertyRecord rec : d) {
            if (data.contains(rec)) {
                continue;
            }
            int idx = numOfRecords++;
            data.add(idx, rec);
        }
        fireTableDataChanged();
    }

    /**
     * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == PropertiesTableModel.NAME_COL_IDX) {
            return false;
        }
        if ((column == PropertiesTableModel.UPD_COL_IDX)
            || (column == PropertiesTableModel.DEL_COL_IDX)) {
            return true;
        }
        Boolean updCell = (Boolean) getValueAt(row,
            PropertiesTableModel.UPD_COL_IDX);
        if (updCell.booleanValue() == true) {
            return true;
        }
        return false;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        return data.size();
    }

    /**
     * Given a {@link suncertify.props.PropertyRecord} return the value at the
     * given column index.
     * @param record The record for which the column requested is contained.
     * @param columnIndex The index of the column value to return.
     * @return The value at the column index.
     */
    public Object getValueAtColumn(PropertyRecord record, int columnIndex) {
        switch (columnIndex) {
            case NAME_COL_IDX: {
                return record.getPropertyName();
            }
            case VAL_COL_IDX: {
                return record.getPropertyValue();
            }
            case UPD_COL_IDX: {
                return record.getIsUpdate();
            }
            case DEL_COL_IDX: {
                return record.getIsDelete();
            }
        }
        return null;
    }

    /**
     * Given a {@link suncertify.props.PropertyRecord} set the value at the
     * given column index.
     * @param record The record for which the column value is to be set.
     * @param columnIndex The index of the column value to set.
     */
    private void setValueAtColumn(PropertyRecord record, int columnIndex,
        Object aValue) {
        switch (columnIndex) {
            case NAME_COL_IDX: {
                record.setPropertyName((String) aValue);
                break;
            }
            case VAL_COL_IDX: {
                record.setPropertyValue((String) aValue);
                break;
            }
            case UPD_COL_IDX: {
                record.setIsUpdate((Boolean) aValue);
                break;
            }
            case DEL_COL_IDX: {
                record.setIsDelete((Boolean) aValue);
                break;
            }
        }
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (data.size() == 0) {
            return null;
        }
        PropertyRecord record = data.get(rowIndex);
        return getValueAtColumn(record, columnIndex);
    }

    /**
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int c) {
        switch (c) {
            case NAME_COL_IDX:
                return String.class;
            case VAL_COL_IDX:
                return String.class;
            case UPD_COL_IDX:
                return Boolean.class;
            case DEL_COL_IDX:
                return Boolean.class;
        }
        return null;
    }

    /**
     * Returns a list of records marked for delete by a user.
     */
    public ArrayList<String> getDeleted() {
        int rowCnt = getRowCount();
        ArrayList<String> dl = new ArrayList<String>(rowCnt);
        for (int i = 0; i < rowCnt; i++) {
            PropertyRecord row = data.get(i);
            String propName = row.getPropertyName();
            if (!(Boolean) getValueAtColumn(row,
                PropertiesTableModel.DEL_COL_IDX)) {
                continue;
            }
            dl.add(propName);
        }
        return dl;
    }

    /**
     * Commits changes to the model. The semantics of change are as follows.
     * Rows marked for deletion are removed from the model. Rows marked as
     * updated are set to false. The data cache of original values is cleared.
     * The state of the model after commit is equivalent to the model having
     * been initialized with the state of the data after a commit.
     */
    public void commitChanges() {
        dataCache.clear();
        ListIterator<PropertyRecord> idr = data.listIterator();
        while (idr.hasNext()) {
            PropertyRecord rec = idr.next();
            if ((Boolean) getValueAtColumn(rec,
                PropertiesTableModel.DEL_COL_IDX)) {
                idr.remove();
                continue;
            }
            if ((Boolean) getValueAtColumn(rec,
                PropertiesTableModel.UPD_COL_IDX)) {
                setValueAtColumn(rec, PropertiesTableModel.UPD_COL_IDX, false);
                continue;
            }
        }
        fireTableDataChanged();
    }

    /**
     * @return a list of {@link suncertify.props.PropertyRecord} that have been
     *         updated since the model was initialized.
     */
    public ArrayList<PropertyRecord> getUpdated() {
        int rowCnt = getRowCount();
        ArrayList<PropertyRecord> dl = new ArrayList<PropertyRecord>(rowCnt);
        for (int i = 0; i < rowCnt; i++) {
            PropertyRecord row = data.get(i);
            if (!(Boolean) getValueAtColumn(row,
                PropertiesTableModel.UPD_COL_IDX)) {
                continue;
            }
            dl.add(data.get(i));
        }
        return dl;
    }

    /**
     * Retrieves all of the data contained in this model
     * @return A list of Property Records.
     */
    public ArrayList<PropertyRecord> getData() {
        int rowCnt = getRowCount();
        ArrayList<PropertyRecord> dl = new ArrayList<PropertyRecord>(rowCnt);
        for (int i = 0; i < rowCnt; i++) {
            data.get(i);
            dl.add(data.get(i));
        }
        return dl;
    }

    /**
     * @param row the row index.
     * @param col the column index.
     * @return true if the value at the index has been modified.
     */
    public boolean hasValueChanged(int row, int col) {
        CellIdx idx = new CellIdx(row, col);
        if (dataCache.containsKey(idx)) {
            return true;
        }
        return false;
    }

    /**
     * reverts a row to the original values of the fields.
     * @param row the row to revert.
     */
    private void revertRow(int row) {
        Iterator<CellIdx> keySet = dataCache.keySet().iterator();
        while (keySet.hasNext()) {
            CellIdx idx = keySet.next();
            if (idx.getRow() == row) {
                Object obj = dataCache.get(idx);
                keySet.remove();
                PropertyRecord record = data.get(row);
                this.setValueAtColumn(record, idx.getCol(), obj);
            }
        }
    }

    /**
     * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object,
     *      int, int)
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        PropertyRecord rec = data.get(rowIndex);
        switch (columnIndex) {
            case UPD_COL_IDX:
                if (!(Boolean) getValueAtColumn(rec,
                    PropertiesTableModel.DEL_COL_IDX)) {
                    boolean newValue = ((Boolean) aValue).booleanValue();
                    setValueAtColumn(rec, PropertiesTableModel.UPD_COL_IDX,
                        aValue);
                    if (newValue == false) {
                        revertRow(rowIndex);
                    }
                }
                break;
            case DEL_COL_IDX:
                if (!(Boolean) getValueAtColumn(rec,
                    PropertiesTableModel.UPD_COL_IDX)) {
                    setValueAtColumn(rec, PropertiesTableModel.DEL_COL_IDX,
                        aValue);
                }
                break;
            case NAME_COL_IDX:
            default:
                if ((aValue != null) && !(aValue.toString().equals(""))) {
                    cacheValue(rowIndex, columnIndex, aValue);
                }
        }
        fireTableDataChanged();
    }

    /**
     * Caches a field value and replaces it with the value given. Cache will
     * always contain the original value no matter how many times this method is
     * called.
     * @param rowIndex The row index of the value to cache.
     * @param columnIndex The column index of the value to cache.
     * @param aValue The new value to store at the cell index.
     */
    private void cacheValue(int rowIndex, int columnIndex, Object aValue) {
        CellIdx index = new CellIdx(rowIndex, columnIndex);
        Object origValue = null;
        if (!dataCache.containsKey(index)) {
            origValue = getValueAt(rowIndex, columnIndex);
            dataCache.put(index, origValue);
        }
        origValue = dataCache.get(index);
        if (origValue.equals(aValue)) {
            dataCache.remove(index);
        }
        PropertyRecord rec = data.get(rowIndex);
        setValueAtColumn(rec, columnIndex, aValue);
    }
}
