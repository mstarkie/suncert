/*
 * CellIdx.java Sun Certified Developer for the Java 2 Platform Submission. 2010
 * Bodgitt and Scarper, LLC
 */
package suncertify.model;

/**
 * A class that represents a cell index. The hash code and equals methods are
 * based on the string concatenation of the row index followed by the cell index
 * values.
 * @author Starkie, Michael C.
 * @since Feb 4, 2011:10:24:37 PM
 */
public class CellIdx {
    /** column index */
    private int col = 0;
    /** row index */
    private int row = 0;

    /**
     * Objects can only be constructed given a row and column index.
     * @param r The row index.
     * @param c The column Index.
     */
    public CellIdx(int r, int c) {
        row = r;
        col = c;
    }

    /**
     * @return The row index.
     */
    public int getRow() {
        return row;
    }

    /**
     * @return The column index.
     */
    public int getCol() {
        return col;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CellIdx)) {
            return false;
        }
        return hashCode() == obj.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[" + row + "," + col + "]";
    }
}
