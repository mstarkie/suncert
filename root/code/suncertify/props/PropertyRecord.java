/*
 * DisplayRecord.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.props;

/**
 * An encapsulation of a record and associated fields as understood by the view
 * and data model. Used to contain property name and value entries.
 * @author Starkie, Michael C.
 * @since Jan 10, 2011:7:41:27 AM
 */
public class PropertyRecord {
    /** number of fields in the view */
    public static final int NUM_OF_FIELDS = 4;
    /** property name */
    private String propertyName = null;
    /** property value */
    private String propertyValue = null;
    /** indicates whether a field has been updated */
    private boolean isUpdate = false;
    /** incates whether the property is marked for deletion */
    private boolean isDelete = false;

    /**
     * @return the property name
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @param propertyName the property nameto set
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * @return the property name
     */
    public String getPropertyValue() {
        return propertyValue;
    }

    /**
     * @param propertyValue the property alue to set
     */
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    /**
     * @return the isUpdate
     */
    public Boolean getIsUpdate() {
        return isUpdate;
    }

    /**
     * @param isUpdate the isUpdate to set
     */
    public void setIsUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    /**
     * @return the isDelete
     */
    public Boolean getIsDelete() {
        return isDelete;
    }

    /**
     * @param isDelete the isDelete to set
     */
    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    /**
     * The value returned is the hashCode of the String value of the property
     * name.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return propertyName.hashCode();
    }

    /**
     * Equality is based upon the String value of the property name field.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PropertyRecord)) {
            return false;
        }
        PropertyRecord other = (PropertyRecord) obj;
        if (!propertyName.equals(other.propertyName)) {
            return false;
        }
        return true;
    }
}
