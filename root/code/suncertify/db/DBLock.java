/*
 * DBLock.java Sun Certified Developer for the Java 2 Platform Submission. 2010
 * Bodgitt and Scarper, LLC
 */
package suncertify.db;

/**
 * A class used to associate a unique key called a cookie with an object
 * representing a lock (this). The cookie represents an association between a
 * unique number and a DBLock object. An optimization has been added to allow a
 * client to re-use a DBLock object by modifying the value of the cookie via a
 * setter method. Care must be taken to avoid corrupting any contract binding a
 * cookie to an instance of DBLock.
 * @author Starkie, Michael C.
 * @since Oct 11, 2010:7:38:23 PM
 */
public class DBLock {
    /** A unique value associated with a lock */
    private Long cookie = null;
    /** true if the cookie is currently active (i.e., holding the lock) */
    private boolean locked = true;

    /**
     * @param c A unique cookie to bind with this instance
     */
    public DBLock(long c) {
        setCookie(c);
    }

    /**
     * It is assumed that the caller of this method takes ownership of
     * synchronizing this object if needed. This method is not thread safe.
     * @return the cookie
     */
    public Long getCookie() {
        return cookie;
    }

    /**
     * Replace the lock with a new cookie. Allows objects of this class to be
     * reused. It is assumed that the caller of this method takes ownership of
     * synchronizing this object if needed. This method is not thread safe.
     * @param c
     */
    public void setCookie(long c) {
        this.cookie = c;
    }

    /**
     * It is assumed that the caller of this method takes ownership of
     * synchronizing this object if needed. This method is not thread safe.
     * @return true if the cookie has locked a record.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * It is assumed that the caller of this method takes ownership of
     * synchronizing this object if needed. This method is not thread safe.
     * locks the {@link #cookie}
     */
    public void lock() {
        locked = true;
    }

    /**
     * It is assumed that the caller of this method takes ownership of
     * synchronizing this object if needed. This method is not thread safe.
     * unlocks the {@link #cookie}
     */
    public void unlock() {
        locked = false;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DBLock)) {
            return false;
        }
        DBLock that = (DBLock) obj;
        return this.getCookie() == that.getCookie();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return cookie.hashCode();
    }

    /**
     * A string representation of this lock object.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String msg = "[hashcode:" + hashCode() + ", cookie:" + cookie
            + ", locked: " + locked + "]";
        return msg;
    }
}
