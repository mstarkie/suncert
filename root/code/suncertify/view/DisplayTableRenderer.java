package suncertify.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import suncertify.model.DisplayTableModel;

/**
 * Used to render cells in the display table. Cells that are updated by the user
 * will turn green to indicate they have been modified and are pending commit.
 * Rows that have been selected to be deleted will turn red in the display to
 * indicate deletion is pending commit by the user..
 * @author Starkie, Michael C.
 * @since Jan 31, 2011:6:26:49 PM
 */
public class DisplayTableRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = -5533330702993783273L;
    /** used to hold the original default background color of a cell */
    private Color defaultBackgroundColor = null;

    /**
     * Cells that are updated by the user will turn green to indicate they have
     * been modified and are pending commit. Rows that have been selected to be
     * deleted will turn red in the display to indicate deletion is pending
     * commit by the user.
     * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, boolean, int, int)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int col) {
        JLabel comp = (JLabel) super.getTableCellRendererComponent(table,
            value, isSelected, hasFocus, row, col);
        DisplayTableModel model = (DisplayTableModel) table.getModel();
        if (defaultBackgroundColor == null) {
            defaultBackgroundColor = comp.getBackground();
        }
        int rowIdx = table.convertRowIndexToModel(row);
        int colIdx = table.convertColumnIndexToModel(col);
        Boolean del = (Boolean) table.getModel().getValueAt(rowIdx,
            DisplayTableModel.DEL_COL_IDX);
        Boolean upd = (Boolean) table.getModel().getValueAt(rowIdx,
            DisplayTableModel.UPD_COL_IDX);
        if (del == true) {
            comp.setBackground(Color.RED);
        } else
            if ((upd == true) && model.hasValueChanged(rowIdx, colIdx)) {
                comp.setBackground(Color.GREEN);
            } else {
                comp.setBackground(defaultBackgroundColor);
            }
        return comp;
    }
}
