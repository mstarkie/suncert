/*
 * Rate.java Sun Certified Developer for the Java 2 Platform Submission. 2010
 * Bodgitt and Scarper, LLC
 */
package suncertify.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * An encapsulation of a floating number representing a monetary rate. It
 * accepts a '$' in the constructor and prepends a '$' before returning from
 * toString(). The rest of the semantics are consistent with BigDecimal. This
 * class offers the ability of a table to display a $ when showing this value
 * yet have columns of this type sort on the floating point number ignoring the
 * '$'.
 * @author Starkie, Michael C.
 * @since Jan 9, 2011:3:32:05 PM
 */
public class Rate implements Comparable<Rate> {
    private static final long serialVersionUID = 1518981817816434713L;
    /** The underlying value */
    private BigDecimal val = null;

    /**
     * @param v A monetary rate including the '$' sign.
     */
    public Rate(String v) {
        v = v.replaceFirst("\\$", "").trim();
        val = new BigDecimal(v);
        val = val.setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * @return the numerical value.
     */
    public BigDecimal getValue() {
        return val;
    }

    /**
     * Compares the underlying numerical value.
     * @see java.math.BigDecimal#compareTo(BigDecimal)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Rate o) {
        return getValue().compareTo(o.getValue());
    }

    /**
     * Returns the string representation of the value with a '$' prepended.
     * @see java.lang.Object#toString()
     * @see java.math.BigDecimal#toString()
     */
    @Override
    public String toString() {
        return "$" + val;
    }

    /**
     * @see java.math.BigDecimal#hashCode()
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return val.hashCode();
    }

    /**
     * @see java.math.BigDecimal#equals(Object)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object x) {
        if ((x == null) || !(x instanceof Rate)) {
            return false;
        }
        return val.equals(((Rate) x).getValue());
    }

    public static void main(String[] args) {
        new Rate("45.456");
        new Rate("$6.485739");
        new Rate("0");
        new Rate("0.34");
        new Rate(".45");
        new Rate("100.75");
    }
}
