package suncertify.view;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import suncertify.db.DBField;

/**
 * Imposes limits on user entered characters before submitting to the data model
 * from the view on behalf of the user. The restrictions are that the only
 * characters allowed are numbers and the '.' char. Also a maximum of six
 * numbers can be entered with a minimum of 2 reserved for after the decimal
 * place.
 * @author Starkie, Michael C.
 * @since Jan 31, 2011:6:55:18 PM
 */
public class RateLimit extends PlainDocument {
    private static final long serialVersionUID = 6425952922749489641L;
    /** the maximum char limit of this document */
    protected int limit;

    /**
     * @param limit The maximum number of digits allowed including the decimal
     *            place.
     */
    public RateLimit(int limit) {
        super();
        this.limit = limit;
    }

    /**
     * @return The maximum number of digits allowed including the decimal place.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Only inserts a value into the document if the character is a number or a
     * decimal and offset into the document is less than the limit. A minimum of
     * 2 places are always reserved at the end for the decimal value.
     * @see javax.swing.text.PlainDocument#insertString(int, java.lang.String,
     *      javax.swing.text.AttributeSet)
     */
    @Override
    public void insertString(int offset, String s, AttributeSet attributeSet)
        throws BadLocationException {
        if (offset >= limit) {
            return;
        }
        String content = getContent().getString(0, getContent().length());
        boolean containsDecimal = content.contains(".");
        if (s.equals(".")) {
            if (containsDecimal) {
                return; // only one decimal allowed
            } else {
                // Subtract for '$' + 2 (scale)
                if (offset < (DBField.RATE.getLen() - 3)) {
                    super.insertString(offset, s, attributeSet);
                    return;
                }
            }
        }
        try {
            Long.parseLong(s);
        } catch (Exception e) {
            return; // only integrals allowed
        }
        if (!containsDecimal) {
            // subtract for '$' + 2 (scale) + '.'
            if (offset < (DBField.RATE.getLen() - 4)) {
                // insert number
                super.insertString(offset, s, attributeSet);
                return;
            }
        } else {
            // just go ahead and insert since we're not at the limit and data
            // model will take care of rounding before rendering.
            super.insertString(offset, s, attributeSet);
        }
    }
}