/*
 * DBRecordLocker.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.db;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Encapsulates the handling of record locking, unlocking, and assigning cookies
 * into 3 basic operations. #1 locking, #2 unlocking, and #3 checking to make
 * sure that, given a unique identifier called a 'cookie' and a record number,
 * the record is locked by the cookie.
 * @author Starkie, Michael C.
 * @since Oct 13, 2010:7:20:04 AM
 */
public class DBRecordLocker {
    /** An invalid cookie */
    public static long INVALID_COOKIE = Long.MIN_VALUE;
    /** A record to DBLock table */
    private ConcurrentHashMap<Long, DBLock> lockTable = null;
    /** Random cookie generator */
    private Random cookieGen = new Random();

    /**
     * @param initialCapacity
     */
    DBRecordLocker(int initialCapacity) {
        lockTable = new ConcurrentHashMap<Long, DBLock>(initialCapacity);
    }

    /**
     * Assigns a cookie to the record number and locks the record on the cookie.
     * The method is thread-safe and will block/wait if the record is currently
     * locked. A new cookie will be assigned and returned when the record
     * becomes available and is re-locked by the waiting thread.
     * @param recNo The record number to lock.
     * @return long A randomly generated cookie that owns the lock on the
     *         record.
     */
    final long lock(long recNo) {
        long cookie = DBRecordLocker.INVALID_COOKIE;
        while (cookie == DBRecordLocker.INVALID_COOKIE) {
            cookie = cookieGen.nextLong();
        }
        // returns current value from table if present.
        DBLock curLock = lockTable.putIfAbsent(recNo, new DBLock(cookie));
        if (curLock != null) {
            synchronized (curLock) {
                while (curLock.isLocked()) {
                    try {
                        Data.printMsg("cookie[" + cookie
                            + "] waiting for lock on recNo: " + recNo);
                        curLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
                curLock.lock();
                curLock.setCookie(cookie);
            }
        }
        Data.printMsg("state of record after locking: " + recNo + "="
            + lockTable.get(recNo));
        return cookie;
    }

    /**
     * Unlocks a record. If the record is not currently locked the method will
     * return after printing a warning. The record must be locked by the same
     * cookie used to unlock the record or a SecurityException will be thrown
     * and the record will remain locked.
     * @param recNo The record to unlock.
     * @param cookie The cookie that the record is currently locked on.
     * @throws SecurityException When the cookie the record is locked on does
     *             not match the cookie argument passed to the method.
     */
    final void unlock(long recNo, long cookie) throws SecurityException {
        if (cookie == DBRecordLocker.INVALID_COOKIE) { // locking incomplete
            return;
        }
        String msg = "[recNo:" + recNo + "], [cookie:" + cookie + "]";
        DBLock lock;
        lock = lockTable.get(recNo);
        if (lock == null) {
            Data.printMsg("WARNING: Attempt to unlock an unlocked record: "
                + recNo);
            return;
        }
        synchronized (lock) {
            if (lock.getCookie() != cookie) {
                throw new SecurityException(
                    "Attempt to unlock a record with an invalid cookie: " + msg
                        + ". Record valid for " + lock);
            }
            lock.unlock();
            lock.notifyAll();
            Data
                .printMsg("state of record after unlock: " + recNo + "=" + lock);
        }
    }

    /**
     * Validates two conditions: #1 - The given record is indeed locked, #2 -
     * The cookie passed as an argument is the same cookie that owns the lock on
     * the record. The is method does not return true or false but will throw
     * exceptions if any of the 2 conditions are false. The caller assumes the
     * conditions hold when calling this method and should be used as a check.
     * False conditions are considered exceptions.
     * @param recNo The record number to check.
     * @param cookie The cookie expected to own the lock on the record.
     * @throws SecurityException When a record is not locked or the lock is not
     *             owned by the cookie.
     */
    final void checkLock(long recNo, long cookie) throws SecurityException {
        DBLock lock = lockTable.get(recNo);
        if (lock == null) {
            throw new SecurityException("record is not locked: " + recNo);
        }
        if (lock.getCookie() != cookie) {
            throw new SecurityException("invalid lock handle: " + cookie
                + " for record: " + recNo);
        }
    }

    /*
     * For Unit Testing only. Not thread safe!!
     */
    final ConcurrentHashMap<Long, DBLock> getLockTable() {
        return this.lockTable;
    }
}
