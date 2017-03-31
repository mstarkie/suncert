/*
 * DisplayRecord.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.model;

/**
 * An encapsulation of a record and associated fields as understood by the view
 * and data model.
 * @author Starkie, Michael C.
 * @since Jan 10, 2011:7:41:27 AM
 */
public class DisplayRecord {
    /** number of fields in the view */
    public static final int NUM_OF_FIELDS = 9;
    /** first field is record number */
    private Long recNo = -1L;
    /** company name */
    private String companyName = null;
    /** company location */
    private String city = null;
    /** type of work performed */
    private String workType = null;
    /** size of company */
    private Integer companySize = null;
    /** hourly rate of charge */
    private Rate companyRate = null;
    /** customer holding this record */
    private String customerNumber = null;
    /** indicates whether a field has been updated */
    private boolean isUpdate = false;
    /** indicates whether the record is marked for deletion */
    private boolean isDelete = false;

    /**
     * @return the recNo
     */
    public Long getRecNo() {
        return recNo;
    }

    /**
     * @param recNo the recNo to set
     */
    public void setRecNo(Long recNo) {
        this.recNo = recNo;
    }

    /**
     * @return the companyName
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * @param companyName the companyName to set
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the workType
     */
    public String getWorkType() {
        return workType;
    }

    /**
     * @param workType the workType to set
     */
    public void setWorkType(String workType) {
        this.workType = workType;
    }

    /**
     * @return the companySize
     */
    public Integer getCompanySize() {
        return companySize;
    }

    /**
     * @param companySize the companySize to set
     */
    public void setCompanySize(Integer companySize) {
        this.companySize = companySize;
    }

    /**
     * @return the companyRate
     */
    public Rate getCompanyRate() {
        return companyRate;
    }

    /**
     * @param companyRate the companyRate to set
     */
    public void setCompanyRate(Rate companyRate) {
        this.companyRate = companyRate;
    }

    /**
     * @return the customerNumber
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * @param customerNumber the customer holding this record.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
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
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return recNo.hashCode();
    }

    /**
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
        if (!(obj instanceof DisplayRecord)) {
            return false;
        }
        DisplayRecord other = (DisplayRecord) obj;
        if (!recNo.equals(other.recNo)) {
            return false;
        }
        return true;
    }
}
