package suncertify.view;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Limits the width of an Integer when entered into the model from the view by
 * the user.
 * @author Starkie, Michael C.
 * @since Jan 31, 2011:6:55:18 PM
 */
public class IntegerLimit extends PlainDocument {
    private static final long serialVersionUID = 7270408015224823897L;
    /** Tha maximum size of an integer text field */
    protected int limit;

    /**
     * @param limit Maximum width of an integer text field.
     */
    public IntegerLimit(int limit) {
        super();
        this.limit = limit;
    }

    /**
     * @return the maximum limit defined.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Only inserts a value into the document if the character is a number and
     * offset into the document is less than the limit.
     * @see javax.swing.text.PlainDocument#insertString(int, java.lang.String,
     *      javax.swing.text.AttributeSet)
     */
    @Override
    public void insertString(int offset, String s, AttributeSet attributeSet)
        throws BadLocationException {
        try {
            Long.parseLong(s);
            if (offset < limit) {
                super.insertString(offset, s, attributeSet);
            }
        } catch (Exception e) {
        }
    }
}