/*
 * DisplayTableModel.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC Starkie, Michael C.
 */
package suncertify.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

/**
 * @author Starkie, Michael C.
 * @since Dec 4, 2010:4:45:30 PM
 */
public class DisplayTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 4640759568475202260L;
    /** record number */
    private static final String REC_COL = "Record #";
    /** company name */
    private static final String NAM_COL = "Name";
    /** company location */
    private static final String CIT_COL = "City";
    /** work performed */
    private static final String WRK_COL = "Work";
    /** company size */
    private static final String SZE_COL = "Size";
    /** hourly rate */
    private static final String RAT_COL = "Rate";
    /** customer holding a record */
    private static final String CUS_COL = "Customer";
    /** 1 or more fields have been updated */
    private static final String UPD_COL = "Update";
    /** record has been marked for deletion */
    private static final String DEL_COL = "Delete";
    /** column index of record number */
    public static final int REC_COL_IDX = 0;
    /** column index of copany name */
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
    /** column index of is record marked for update */
    public static final int UPD_COL_IDX = 7;
    /** column index of is record marked for delete */
    public static final int DEL_COL_IDX = 8;
    /** array of column names */
    protected final String[] columnNames = new String[] {
        DisplayTableModel.REC_COL, DisplayTableModel.NAM_COL,
        DisplayTableModel.CIT_COL, DisplayTableModel.WRK_COL,
        DisplayTableModel.SZE_COL, DisplayTableModel.RAT_COL,
        DisplayTableModel.CUS_COL, DisplayTableModel.UPD_COL,
        DisplayTableModel.DEL_COL };
    /** used to hold records */
    protected ArrayList<DisplayRecord> data = new ArrayList<DisplayRecord>();
    /**
     * a cache of original data for any modified field. Allows users to revert
     * changes and restore the original value. Referenced by cell index.
     */
    private HashMap<CellIdx, Object> dataCache = new HashMap<CellIdx, Object>();

    /**
     * @param d an array of {@link suncertify.model.DisplayRecord}
     */
    public DisplayTableModel(DisplayRecord[] d) {
        if (d == null) {
            return;
        }
        for (int i = 0; i < d.length; i++) {
            data.add(i, d[i]);
        }
    }

    /**
     * Returns a list of all record numbers.
     * @return The set of record numbers.
     */
    private Set<Long> getRecordNumberSet() {
        Set<Long> recNoSet = new HashSet<Long>();
        ListIterator<DisplayRecord> idr = data.listIterator();
        while (idr.hasNext()) {
            recNoSet.add(idr.next().getRecNo());
        }
        return recNoSet;
    }

    /**
     * Appends records to the model.
     * @param d An array of {@link suncertify.model.DisplayRecord}
     */
    public void appendRecords(DisplayRecord[] d) {
        int numOfRecords = getRowCount();
        Set<Long> recNoSet = getRecordNumberSet();
        for (DisplayRecord rec : d) {
            if (recNoSet.contains(rec.getRecNo())) {
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
        if (column == DisplayTableModel.REC_COL_IDX) {
            return false;
        }
        if ((column == DisplayTableModel.UPD_COL_IDX)
            || (column == DisplayTableModel.DEL_COL_IDX)) {
            return true;
        }
        Boolean updCell = (Boolean) getValueAt(row,
            DisplayTableModel.UPD_COL_IDX);
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
     * Given a {@link suncertify.model.DisplayRecord} return the value at the
     * given column index.
     * @param record The record for which the column requested is contained.
     * @param columnIndex The index of the column value to return.
     * @return The value at the column index.
     */
    public Object getValueAtColumn(DisplayRecord record, int columnIndex) {
        switch (columnIndex) {
            case REC_COL_IDX: {
                return record.getRecNo();
            }
            case NAM_COL_IDX: {
                return record.getCompanyName();
            }
            case CIT_COL_IDX: {
                return record.getCity();
            }
            case WRK_COL_IDX: {
                return record.getWorkType();
            }
            case SZE_COL_IDX: {
                return record.getCompanySize();
            }
            case RAT_COL_IDX: {
                return record.getCompanyRate();
            }
            case CUS_COL_IDX: {
                return record.getCustomerNumber();
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
     * Given a {@link suncertify.model.DisplayRecord} set the value at the given
     * column index.
     * @param record The record for which the column value is to be set.
     * @param columnIndex The index of the column value to set.
     */
    private void setValueAtColumn(DisplayRecord record, int columnIndex,
        Object aValue) {
        switch (columnIndex) {
            case REC_COL_IDX: {
                record.setRecNo((Long) aValue);
                break;
            }
            case NAM_COL_IDX: {
                record.setCompanyName((String) aValue);
                break;
            }
            case CIT_COL_IDX: {
                record.setCity((String) aValue);
                break;
            }
            case WRK_COL_IDX: {
                record.setWorkType((String) aValue);
                break;
            }
            case SZE_COL_IDX: {
                record.setCompanySize((Integer) aValue);
                break;
            }
            case RAT_COL_IDX: {
                record.setCompanyRate((Rate) aValue);
                break;
            }
            case CUS_COL_IDX: {
                record.setCustomerNumber((String) aValue);
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
        DisplayRecord record = data.get(rowIndex);
        return getValueAtColumn(record, columnIndex);
    }

    /**
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class<?> getColumnClass(int c) {
        switch (c) {
            case REC_COL_IDX:
                return Long.class;
            case NAM_COL_IDX:
                return String.class;
            case CIT_COL_IDX:
                return String.class;
            case WRK_COL_IDX:
                return String.class;
            case SZE_COL_IDX:
                return Integer.class;
            case RAT_COL_IDX:
                return Rate.class;
            case CUS_COL_IDX:
                return String.class;
            case UPD_COL_IDX:
                return Boolean.class;
            case DEL_COL_IDX:
                return Boolean.class;
        }
        return null;
    }

    /**
     * Returns a list of record numbers marked for deletion.
     */
    public ArrayList<Long> getDeleted() {
        int rowCnt = getRowCount();
        ArrayList<Long> dl = new ArrayList<Long>(rowCnt);
        for (int i = 0; i < rowCnt; i++) {
            DisplayRecord row = data.get(i);
            Long recNo = row.getRecNo();
            if (!(Boolean) getValueAtColumn(row, DisplayTableModel.DEL_COL_IDX)) {
                continue;
            }
            dl.add(recNo);
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
        ListIterator<DisplayRecord> idr = data.listIterator();
        while (idr.hasNext()) {
            DisplayRecord rec = idr.next();
            if ((Boolean) getValueAtColumn(rec, DisplayTableModel.DEL_COL_IDX)) {
                idr.remove();
                continue;
            }
            if ((Boolean) getValueAtColumn(rec, DisplayTableModel.UPD_COL_IDX)) {
                setValueAtColumn(rec, DisplayTableModel.UPD_COL_IDX, false);
                continue;
            }
        }
        fireTableDataChanged();
    }

    /**
     * @return a list of {@link suncertify.model.DisplayRecord} that have been
     *         updated since the model was initialized.
     */
    public ArrayList<DisplayRecord> getUpdated() {
        int rowCnt = getRowCount();
        ArrayList<DisplayRecord> dl = new ArrayList<DisplayRecord>(rowCnt);
        for (int i = 0; i < rowCnt; i++) {
            DisplayRecord row = data.get(i);
            if (!(Boolean) getValueAtColumn(row, DisplayTableModel.UPD_COL_IDX)) {
                continue;
            }
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
                DisplayRecord record = data.get(row);
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
        DisplayRecord rec = data.get(rowIndex);
        switch (columnIndex) {
            case UPD_COL_IDX:
                if (!(Boolean) getValueAtColumn(rec,
                    DisplayTableModel.DEL_COL_IDX)) {
                    boolean newValue = ((Boolean) aValue).booleanValue();
                    setValueAtColumn(rec, DisplayTableModel.UPD_COL_IDX, aValue);
                    if (newValue == false) {
                        revertRow(rowIndex);
                    }
                }
                break;
            case DEL_COL_IDX:
                if (!(Boolean) getValueAtColumn(rec,
                    DisplayTableModel.UPD_COL_IDX)) {
                    setValueAtColumn(rec, DisplayTableModel.DEL_COL_IDX, aValue);
                }
                break;
            case RAT_COL_IDX:
                Rate rVal = null;
                try {
                    rVal = new Rate((String) aValue);
                } catch (Exception e) {
                    return; // N.A.N.
                }
                cacheValue(rowIndex, columnIndex, rVal);
                break;
            case REC_COL_IDX:
            case SZE_COL_IDX:
                Integer nVal = null;
                try {
                    nVal = Integer.valueOf((String) aValue);
                } catch (Exception e) {
                    return; // N.A.N.
                }
                cacheValue(rowIndex, columnIndex, nVal);
                break;
            case CUS_COL_IDX:
                String newValue = ((String) aValue);
                newValue = newValue.trim();
                if (newValue.equals("")) {
                    cacheValue(rowIndex, columnIndex, aValue);
                    return;
                }
                try {
                    Integer.valueOf((String) aValue);
                } catch (Exception e) {
                    return; // N.A.N.
                }
                cacheValue(rowIndex, columnIndex, aValue);
                break;
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
        DisplayRecord rec = data.get(rowIndex);
        setValueAtColumn(rec, columnIndex, aValue);
    }
}
