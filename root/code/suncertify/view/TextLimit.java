package suncertify.view;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Limits the width of an text when entered into the model from the view by the
 * user.
 * @author Starkie, Michael C.
 * @since Feb 3, 2011:8:03:04 AM
 */
public class TextLimit extends PlainDocument {
    private static final long serialVersionUID = 678546601633454789L;
    /** the max size of a text document */
    private int limit;

    /**
     * @param limit the maximum size of the document.
     */
    public TextLimit(int limit) {
        super();
        this.limit = limit;
    }

    /**
     * @return the max size of this document.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Will only insert a string at the current offset if the offset is less
     * than the limit. Exceptions are ignored.
     * @see javax.swing.text.PlainDocument#insertString(int, java.lang.String,
     *      javax.swing.text.AttributeSet)
     */
    @Override
    public void insertString(int offset, String s, AttributeSet attributeSet)
        throws BadLocationException {
        try {
            if (offset < limit) {
                super.insertString(offset, s, attributeSet);
            }
        } catch (Exception e) {
        }
    }
}