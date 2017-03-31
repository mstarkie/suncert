/*
 * JTableFactory.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.view;

import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.PlainDocument;

import suncertify.db.DBField;
import suncertify.model.DisplayTableModel;
import suncertify.model.Rate;
import suncertify.model.SearchCriteriaTableModel;

/**
 * Factory to create search criteria, display results tables and associated
 * helper methods. Both tables share a similar look and feel and contain column
 * elements.
 * @author Starkie, Michael C.
 * @since Nov 17, 2010:7:28:36 AM
 */
final class JTableFactory {
    /** The height of a row for both the display and search table. */
    public static final int ROW_HEIGHT = 20;
    /** The wide of the record number column */
    private static final int RECORD_NUM_WIDTH = 20;
    /** The wide of the company name column */
    private static final int NAME_WIDTH = 150;
    /** The wide of the company location column */
    private static final int LOCATION_WIDTH = 100;
    /** The wide of the work performed column */
    private static final int INDUSTRY_WIDTH = 200;
    /** The wide of the company size column */
    private static final int NUM_OF_EMPLOYEES_WIDTH = 10;
    /** The wide of the hourly rate column */
    private static final int HOURLY_RATE_WIDTH = 20;
    /** The wide of the customer id column */
    private static final int CUSTOMER_ID_WIDTH = 20;
    /** The wide of the is delete column */
    private static final int DELETE_WIDTH = 5;
    /** The wide of the is update column */
    private static final int UPDATE_WIDTH = 5;
    /** A lookup table of column index vs. column width */
    private static HashMap<Integer, Integer> colWidths = new HashMap<Integer, Integer>();
    static {
        JTableFactory.colWidths.put(DisplayTableModel.REC_COL_IDX,
            JTableFactory.RECORD_NUM_WIDTH);
        JTableFactory.colWidths.put(DisplayTableModel.NAM_COL_IDX,
            JTableFactory.NAME_WIDTH);
        JTableFactory.colWidths.put(DisplayTableModel.CIT_COL_IDX,
            JTableFactory.LOCATION_WIDTH);
        JTableFactory.colWidths.put(DisplayTableModel.WRK_COL_IDX,
            JTableFactory.INDUSTRY_WIDTH);
        JTableFactory.colWidths.put(DisplayTableModel.SZE_COL_IDX,
            JTableFactory.NUM_OF_EMPLOYEES_WIDTH);
        JTableFactory.colWidths.put(DisplayTableModel.RAT_COL_IDX,
            JTableFactory.HOURLY_RATE_WIDTH);
        JTableFactory.colWidths.put(DisplayTableModel.CUS_COL_IDX,
            JTableFactory.CUSTOMER_ID_WIDTH);
        JTableFactory.colWidths.put(DisplayTableModel.UPD_COL_IDX,
            JTableFactory.DELETE_WIDTH);
        JTableFactory.colWidths.put(DisplayTableModel.DEL_COL_IDX,
            JTableFactory.UPDATE_WIDTH);
    }

    /**
     * Returns an instance of a search criteria table.
     * @see javax.swing.JTable
     * @return The search criteria entry table.
     */
    static SearchCriteriaTable getSearchCriteriaTable() {
        SearchCriteriaTableModel searchModel = new SearchCriteriaTableModel();
        SearchCriteriaTable searchTable = new SearchCriteriaTable(searchModel);
        searchTable.setRowHeight(JTableFactory.ROW_HEIGHT);
        searchTable.setCellSelectionEnabled(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        searchTable.setDefaultRenderer(String.class, renderer);
        searchTable.setBorder(null);
        searchTable.setFillsViewportHeight(true);
        JTableFactory.setColumnWidths(searchTable);
        JTableFactory.setDisplayTableEditorColumnRestrictions(searchTable);
        return searchTable;
    }

    /**
     * Returns an instance of the display table used to display results of a
     * database search based on values entered in the search criteria table.
     * @see javax.swing.JTable
     * @return The display results table.
     */
    static DisplayTable getDisplayTable() {
        DisplayTable displayTable = new DisplayTable();
        DisplayTableRenderer renderer = new DisplayTableRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        displayTable.setDefaultRenderer(Long.class, renderer);
        displayTable.setDefaultRenderer(String.class, renderer);
        displayTable.setDefaultRenderer(Integer.class, renderer);
        displayTable.setDefaultRenderer(Rate.class, renderer);
        displayTable.setRowHeight(JTableFactory.ROW_HEIGHT);
        // setColumnWidths(displayTable);
        displayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        displayTable.setCellSelectionEnabled(true);
        displayTable.setFillsViewportHeight(true);
        displayTable.setAutoCreateRowSorter(true);
        displayTable.setModel(new DefaultTableModel());
        return displayTable;
    }

    /**
     * Sets the column widths of a table based on predefined static column
     * widths associated with this application.
     * @param table The table whose column widths are being changed.
     */
    public static void setColumnWidths(JTable table) {
        int columnCount = table.getModel().getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            int width = JTableFactory.colWidths.get(i);
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(width);
        }
    }

    /**
     * Sets the limitations on the values that can be entered in cells according
     * to column restrictions. The restrictions are application dependent. For
     * example, some columns may only allow numerical values, a decimal point, a
     * '$' sign and can not exceed a maximum number of chars. This method is
     * used to ensure that the underlying database is not corrupted with values
     * Inconsistent with the table schema.
     * @param table
     */
    public static void setDisplayTableEditorColumnRestrictions(JTable table) {
        TableColumn col = table.getColumnModel().getColumn(
            DisplayTableModel.CUS_COL_IDX);
        PlainDocument fieldDoc = new IntegerLimit(DBField.OWNER.getLen());
        JTextField textField = new JTextField();
        textField.setDocument(fieldDoc);
        col.setCellEditor(new DefaultCellEditor(textField));
        // /
        col = table.getColumnModel().getColumn(DisplayTableModel.REC_COL_IDX);
        fieldDoc = new IntegerLimit(Integer.MAX_VALUE);
        textField = new JTextField();
        textField.setDocument(fieldDoc);
        col.setCellEditor(new DefaultCellEditor(textField));
        // /
        col = table.getColumnModel().getColumn(DisplayTableModel.SZE_COL_IDX);
        fieldDoc = new IntegerLimit(DBField.SIZE.getLen());
        textField = new JTextField();
        textField.setDocument(fieldDoc);
        col.setCellEditor(new DefaultCellEditor(textField));
        // /
        col = table.getColumnModel().getColumn(DisplayTableModel.RAT_COL_IDX);
        fieldDoc = new RateLimit(DBField.RATE.getLen() - 1); // $
        textField = new JTextField();
        textField.setDocument(fieldDoc);
        col.setCellEditor(new DefaultCellEditor(textField));
        // /
        col = table.getColumnModel().getColumn(DisplayTableModel.WRK_COL_IDX);
        fieldDoc = new TextLimit(DBField.SPECIALTIES.getLen());
        textField = new JTextField();
        textField.setDocument(fieldDoc);
        col.setCellEditor(new DefaultCellEditor(textField));
        // /
        col = table.getColumnModel().getColumn(DisplayTableModel.CIT_COL_IDX);
        fieldDoc = new TextLimit(DBField.LOCATION.getLen());
        textField = new JTextField();
        textField.setDocument(fieldDoc);
        col.setCellEditor(new DefaultCellEditor(textField));
        // /
        col = table.getColumnModel().getColumn(DisplayTableModel.NAM_COL_IDX);
        fieldDoc = new TextLimit(DBField.NAME.getLen());
        textField = new JTextField();
        textField.setDocument(fieldDoc);
        col.setCellEditor(new DefaultCellEditor(textField));
    }

    /**
     * The table used to display the results of a search.
     * @author Starkie, Michael C.
     * @since Feb 1, 2011:7:54:08 AM
     */
    public static class DisplayTable extends JTable {
        private static final long serialVersionUID = -1650063379735237378L;
        /** specify the column header tool-tips */
        protected String[] columnToolTips = {
            "Record number (cannot be modified)", "Subcontractor name",
            "City where located", "Types of work performed",
            "Number of staff in organization (numbers only)",
            "Hourly charge - (numbers and decimal only)",
            "Customer holding this record (numbers only or blank)",
            "Check to allow updates / uncheck to revert all changes",
            "Check to delete row from database" };

        /**
         * @see javax.swing.JTable#createDefaultTableHeader()
         */
        @Override
        protected JTableHeader createDefaultTableHeader() {
            return new JTableHeader(columnModel) {
                private static final long serialVersionUID = 1L;

                /**
                 * @see javax.swing.table.JTableHeader#getToolTipText(java.awt.event.MouseEvent)
                 */
                @Override
                public String getToolTipText(MouseEvent e) {
                    java.awt.Point p = e.getPoint();
                    int index = columnModel.getColumnIndexAtX(p.x);
                    int realIndex = columnModel.getColumn(index)
                        .getModelIndex();
                    return columnToolTips[realIndex];
                }
            };
        }
    }

    /**
     * The table used by users to enter search criteria to be searched for in
     * the underlying database.
     * @author Starkie, Michael C.
     * @since Feb 1, 2011:7:55:48 AM
     */
    public static class SearchCriteriaTable extends JTable {
        private static final long serialVersionUID = -1507834368080194312L;

        public SearchCriteriaTable(TableModel model) {
            super(model);
        }

        /** specify the cell tool tips */
        protected String[] columnToolTips = {
            "Specifying a Record # will override all other search criteria",
            "Subcontractor name stats with...",
            "City where located starts with...",
            "Types of work performed starts with...",
            "Number of staff in organization starts with...",
            "Hourly charge starts with...",
            "Customer holding this record starts with...", };

        /**
         * @see javax.swing.JTable#getToolTipText(java.awt.event.MouseEvent)
         */
        @Override
        public String getToolTipText(MouseEvent e) {
            java.awt.Point p = e.getPoint();
            int colIndex = columnAtPoint(p);
            int realColumnIndex = convertColumnIndexToModel(colIndex);
            return columnToolTips[realColumnIndex];
        }
    }
}
