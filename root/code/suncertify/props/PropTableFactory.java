/*
 * JTableFactory.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.props;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * Factory to create the property entry and display tables and associated helper
 * methods. Both tables share a similar look and feel and contain column
 * elements.
 * @author Starkie, Michael C.
 * @since Nov 17, 2010:7:28:36 AM
 */
final class PropTableFactory {
    /** The height of a row for both the entry and display tables. */
    public static final int ROW_HEIGHT = 20;
    /** The wide of the is delete column */
    private static final int DELETE_WIDTH = 5;
    /** The wide of the is update column */
    private static final int UPDATE_WIDTH = 5;

    /**
     * Returns an instance of the property table used to display and modify the
     * system properties.
     * @see javax.swing.JTable
     * @return The property editor table.
     */
    static PropertiesTable getPropertyDisplayTable() {
        PropertiesTable propsTable = new PropertiesTable();
        PropertyTableRenderer renderer = new PropertyTableRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        propsTable.setDefaultRenderer(String.class, renderer);
        propsTable.setRowHeight(PropTableFactory.ROW_HEIGHT);
        propsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        propsTable.setCellSelectionEnabled(true);
        propsTable.setFillsViewportHeight(true);
        propsTable.setAutoCreateRowSorter(true);
        propsTable.setModel(new DefaultTableModel());
        return propsTable;
    }

    /**
     * Returns an instance of the property table used to enter new system
     * properties.
     * @see javax.swing.JTable
     * @return The property editor table.
     */
    static PropertyInsertTable getPropertyInsertTable() {
        PropertyInsertTableModel propertyInsertModel = new PropertyInsertTableModel();
        PropertyInsertTable propInsertTable = new PropertyInsertTable(
            propertyInsertModel);
        propInsertTable.setRowHeight(PropTableFactory.ROW_HEIGHT);
        propInsertTable.setCellSelectionEnabled(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        propInsertTable.setDefaultRenderer(String.class, renderer);
        propInsertTable.setBorder(null);
        propInsertTable.setFillsViewportHeight(true);
        return propInsertTable;
    }

    /**
     * Sets the column widths of both the property display and entry tables so
     * that they align with each other vertically in the view.
     * @param propsTable Either the properties entry or display table.
     */
    public static void setPropertiesColumnWidths(JTable propsTable) {
        TableColumn col = propsTable.getColumnModel().getColumn(
            PropertiesTableModel.UPD_COL_IDX);
        col.setPreferredWidth(PropTableFactory.UPDATE_WIDTH);
        col = propsTable.getColumnModel().getColumn(
            PropertiesTableModel.DEL_COL_IDX);
        col.setPreferredWidth(PropTableFactory.DELETE_WIDTH);
        col = propsTable.getColumnModel().getColumn(
            PropertiesTableModel.NAME_COL_IDX);
        col.setPreferredWidth(100);
        col = propsTable.getColumnModel().getColumn(
            PropertiesTableModel.VAL_COL_IDX);
        col.setPreferredWidth(400);
    }

    /**
     * The table used to display the system properties.
     * @author Starkie, Michael C.
     * @since Feb 1, 2011:7:54:08 AM
     */
    public static class PropertiesTable extends JTable {
        private static final long serialVersionUID = 7255114612196394420L;
        /** specify the column header tool-tips */
        protected String[] columnToolTips = {
            "Property Name", "Property Value", };

        /**
         * @see javax.swing.JTable#createDefaultTableHeader()
         */
        @Override
        protected JTableHeader createDefaultTableHeader() {
            return new JTableHeader(columnModel) {
                private static final long serialVersionUID = 13L;

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
     * The table used by administrative users to enter new systems properties.
     * @author Starkie, Michael C.
     * @since Feb 1, 2011:7:55:48 AM
     */
    public static class PropertyInsertTable extends JTable {
        private static final long serialVersionUID = -2596740716866537115L;

        public PropertyInsertTable(TableModel model) {
            super(model);
        }

        /** specify the cell tool tips */
        protected String[] columnToolTips = {
            "Specify a new property name", "Specify a new property value" };

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
