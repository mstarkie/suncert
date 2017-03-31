/*
 * TestDBAcessImplRead.java Sun Certified Developer for the Java 2 Platform
 * Submission. 2010 Bodgitt and Scarper, LLC
 */
package suncertify.db;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Starkie, Michael C.
 * @since Oct 4, 2010:7:29:50 PM
 */
public class TestDBAccessImpl {
    /** file name and location */
    private static String dbFileURI = null;
    private static String tmpFileURI = null;
    private static File dbFile = null;
    private static Data db = null;
    private static int numLocked = 0;
    private static int numUnlocked = 0;

    @BeforeClass
    public static void dump() throws Exception {
        TestDBAccessImpl.setUpBefore();
        TestDBAccessImpl.db = new Data(TestDBAccessImpl.tmpFileURI);
        TestDBAccessImpl.dumpTableContents();
    }

    public static void dumpTableContents() throws Exception {
        System.out.println("DUMPING FILE CONTENTS");
        TreeMap<String, String[]> map = TestDBAccessImpl.db.dump();
        Iterator<String> keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String[] row = map.get(key);
            for (String cell : row) {
                System.out.print("[" + cell + "]");
            }
            System.out.println();
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBefore() throws Exception {
        // TestDBAccessImpl.dbFileURI =
        // "file:///Users/Michael/Documents/workspace/sun/root/db-2x2.db";
        // TestDBAccessImpl.tmpFileURI =
        // "file:///Users/Michael/Documents/db-2x2.db";
        TestDBAccessImpl.dbFileURI = "file:///C:/Users/mike/workspace/sun/root/db-2x2.db";
        TestDBAccessImpl.tmpFileURI = "file:///C:/Users/mike/db-2x2.db";
        try {
            URI inUri = new URI(TestDBAccessImpl.dbFileURI);
            File inFile = new File(inUri);
            // copy the database file to a tmp location and use the tmp.
            URI outUri = new URI(TestDBAccessImpl.tmpFileURI);
            TestDBAccessImpl.dbFile = new File(outUri);
            FileUtils.copyFile(inFile, TestDBAccessImpl.dbFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * @throws java.lang.Exception
     */
    public static void tearDownAfter() throws Exception {
        TestDBAccessImpl.dbFile.delete();
        // Thread.sleep(5000);
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        TestDBAccessImpl.setUpBefore();
        TestDBAccessImpl.db = new Data(TestDBAccessImpl.tmpFileURI);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        TestDBAccessImpl.db = null;
        TestDBAccessImpl.tearDownAfter();
    }

    /**
     * Test method for
     * {@link suncertify.db.Data#createRecord(java.lang.String[])}.
     */
    @Test
    public void testCreateRecord() {
        // create a record
        DBInfo dbInfo = TestDBAccessImpl.db.getDBInfo();
        String[] record = getTestRecord("Global Forge, LLC", "Morristown, NJ");
        long newRecNo = 0L;
        try {
            newRecNo = TestDBAccessImpl.db.createRecord(record);
        } catch (DuplicateKeyException e) {
            Assert.fail("New record can't have duplicate key");
        }
        String[] readData = null;
        try {
            readData = TestDBAccessImpl.db.readRecord(newRecNo);
        } catch (RecordNotFoundException e) {
            Assert.fail("unable to read a record that was just inserted");
        }
        Assert.assertEquals(readData.length, dbInfo.getNumOfFields());
        int i = 0;
        for (DBField field : DBField.values()) {
            int newLen = readData[i].length();
            int oldLen = record[i].length();
            Assert.assertTrue(newLen == oldLen);
            String newVal = readData[i];
            String oldVal = record[i];
            Assert.assertTrue(newVal.equals(oldVal));
            Assert.assertTrue(readData[i].length() == field.getLen());
            i++;
        }
        try {
            newRecNo = TestDBAccessImpl.db.createRecord(record);
            Assert.fail("duplicate key should fail in createRecord");
        } catch (DuplicateKeyException e) {
            System.out.println("Duplicate Key Exception expected: "
                + e.getMessage());
        }
        record = getRecordWithEmptyLoc("Global Forge, LLC");
        try {
            newRecNo = TestDBAccessImpl.db.createRecord(record);
            Assert.fail();
        } catch (DuplicateKeyException e) {
        }
        record = getRecordWithEmptyName("Morristown, NJ");
        try {
            newRecNo = TestDBAccessImpl.db.createRecord(record);
            Assert.fail();
        } catch (DuplicateKeyException e) {
        }
        record = getRecordWithEmptyNameAndLoc();
        try {
            newRecNo = TestDBAccessImpl.db.createRecord(record);
            Assert.fail();
        } catch (DuplicateKeyException e) {
        }
        record = new String[] {
            "Gardens", "Morristown", "Flowers", "6", "$30.00", "" };
        try {
            newRecNo = TestDBAccessImpl.db.createRecord(record);
            Assert.assertTrue(newRecNo != -1);
        } catch (DuplicateKeyException e) {
            Assert.fail();
        }
        record = new String[] {
            "Books", "Morristown", "", "6", "$30.00", "" };
        try {
            newRecNo = TestDBAccessImpl.db.createRecord(record);
            Assert.assertTrue(newRecNo == -1);
        } catch (DuplicateKeyException e) {
            Assert.fail();
        }
        record = new String[] {
            "Books", "Morristown", null, "6", "$30.00", "" };
        try {
            newRecNo = TestDBAccessImpl.db.createRecord(record);
            Assert.assertTrue(newRecNo == -1);
        } catch (DuplicateKeyException e) {
            Assert.fail();
        }
        record = new String[] {
            "Books", "Morristown", "Flowers", "", "$30.00", "" };
        try {
            newRecNo = TestDBAccessImpl.db.createRecord(record);
            Assert.assertTrue(newRecNo == -1);
        } catch (DuplicateKeyException e) {
            Assert.fail();
        }
        record = new String[] {
            "Books", "Morristown", "Flowers", "6", "", "" };
        try {
            newRecNo = TestDBAccessImpl.db.createRecord(record);
            Assert.assertTrue(newRecNo == -1);
        } catch (DuplicateKeyException e) {
            Assert.fail();
        }
        try {
            TestDBAccessImpl.dumpTableContents();
        } catch (Exception e) {
            Assert.fail();
        }
    }

    /**
     * Test method for {@link suncertify.db.Data#deleteRecord(long, long)}.
     */
    @Test
    public void testDeleteRecord() {
        DBInfo info = TestDBAccessImpl.db.getDBInfo();
        long numOfRecords = info.getNumberOfRecords();
        String[] record = getTestRecord("Global Forge, LLC", "Newark, NJ");
        long recNo = 0;
        try {
            recNo = TestDBAccessImpl.db.createRecord(record);
        } catch (DuplicateKeyException e1) {
            Assert.fail("record number should not be duplicate");
        }
        long n = info.getNumberOfRecords();
        Assert.assertEquals(numOfRecords + 1, n);
        try {
            record = TestDBAccessImpl.db.readRecord(recNo);
            Assert.assertTrue(record[0].trim().equals("Global Forge, LLC"));
            Assert.assertTrue(record[1].trim().equals("Newark, NJ"));
        } catch (RecordNotFoundException e1) {
            Assert.fail("record number should exist");
        }
        long cookie = 0L;
        try {
            cookie = TestDBAccessImpl.db.lockRecord(recNo);
        } catch (RecordNotFoundException e) {
            TestDBAccessImpl.db.unlock(recNo, cookie);
            Assert.fail("record number should exist");
        }
        try {
            TestDBAccessImpl.db.deleteRecord(recNo, cookie);
        } catch (SecurityException e) {
            Assert.fail();
        } catch (RecordNotFoundException e) {
            Assert.fail();
        } finally {
            TestDBAccessImpl.db.unlock(recNo, cookie);
        }
        try {
            record = TestDBAccessImpl.db.readRecord(recNo);
            Assert.fail("record number should not exist");
        } catch (RecordNotFoundException e1) {
        }
        n = info.getNumberOfRecords();
        // because the system didn't reuse any deleted records yet.
        Assert.assertEquals(numOfRecords + 1, n);
        record = getTestRecord("Global Forge, LLC", "Newark, NJ");
        long newRecNo = 0;
        try {
            newRecNo = TestDBAccessImpl.db.createRecord(record);
        } catch (DuplicateKeyException e1) {
            Assert.fail("record number should not be duplicate");
        }
        Assert.assertEquals(newRecNo, recNo);
        n = info.getNumberOfRecords();
        Assert.assertEquals(numOfRecords + 1, n);
    }

    /**
     * Test method for
     * {@link suncertify.db.Data#findByCriteria(java.lang.String[])}.
     */
    @Test
    public void testFindByCriteria() {
        DBInfo info = TestDBAccessImpl.db.getDBInfo();
        String[] record = getTestRecord("Global Forge, LLC", "Morristown, NJ");
        try {
            TestDBAccessImpl.db.createRecord(record);
        } catch (DuplicateKeyException e) {
            Assert.fail("New record can't have duplicate key");
        }
        TestDBAccessImpl.db.dump();
        String[] criteria = new String[] {
            null };
        long[] result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, info.getNumberOfRecords());
        criteria = new String[] {
            "0", "0", "0", null };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 0);
        criteria = new String[] {
            "Global" };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 1);
        criteria = new String[] {
            "global" };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 1);
        criteria = new String[] {
            "Bitter" };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 2);
        criteria = new String[] {
            "B" };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 5);
        criteria = new String[] {
            "Bitter", "EmeraldCity" };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 1);
        criteria = new String[] {
            "Bitter", "EmeraldCity", null };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 1);
        criteria = new String[] {
            "Buonarotti", null, null, null, null, null };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 3);
        criteria = new String[] {
            "Dogs", null, null, null, null, null };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 6);
        criteria = new String[] {
            "Dogs", null, "heat", null, null, null };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 2);
        criteria = new String[] {
            "Dogs", null, "heat", null, "$4", null };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 1);
        criteria = new String[] {
            "Hamner", null, null, null, null, null };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 5);
        criteria = new String[] {
            "Hamner &", null, null, null, null, null };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 5);
        criteria = new String[] {
            "Hamner & Tongs", null, null, null, null, null };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 0);
        criteria = new String[] {
            "Hamner & Tong", null, "Electrical", null, null, null };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 1);
        criteria = new String[] {
            "Hamner & Tong", null, "drywall", null, null, null };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 2);
        criteria = new String[] {
            "Hamner & Tong", null, "drywall", null, "$75.00", null };
        result = TestDBAccessImpl.db.findByCriteria(criteria);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.length, 1);
        try {
            result = TestDBAccessImpl.db.findByCriteria(new String[] {
                "hamner & tong" });
            Assert.assertNotNull(result);
            Assert.assertEquals(result.length, 5);
            long recNo = 12;
            String[] rec = TestDBAccessImpl.db.readRecord(recNo);
            System.out.println("deleting record: "
                + DBIo.normalizeKey(rec[0], rec[1]));
            long cookie = TestDBAccessImpl.db.lockRecord(recNo);
            TestDBAccessImpl.db.deleteRecord(recNo, cookie);
            TestDBAccessImpl.db.unlock(recNo, cookie);
            result = TestDBAccessImpl.db.findByCriteria(new String[] {
                "hamner & tong" });
            Assert.assertNotNull(result);
            Assert.assertEquals(result.length, 4);
            result = TestDBAccessImpl.db.findByCriteria(new String[] {
                "hamner&tong" });
            Assert.assertNotNull(result);
            Assert.assertEquals(result.length, 4);
            criteria = new String[] {
                null, null, "plumbing", null, null, null };
            result = TestDBAccessImpl.db.findByCriteria(criteria);
            Assert.assertNotNull(result);
            Assert.assertEquals(result.length, 3);
        } catch (RecordNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testLockRecord1() {
        try {
            long cookie = TestDBAccessImpl.db.lockRecord(0);
            TestDBAccessImpl.db.unlock(0, cookie);
        } catch (RecordNotFoundException e) {
            Assert.fail("testLockRecord: 0 - FAIL");
        }
    }

    @Test(expected = RecordNotFoundException.class)
    public void testLockRecord2() throws Exception {
        TestDBAccessImpl.db.lockRecord(-1);
    }

    @Test(expected = RecordNotFoundException.class)
    public void testLockRecord3() throws Exception {
        TestDBAccessImpl.db.lockRecord(TestDBAccessImpl.db.getDBInfo()
            .getNumberOfRecords());
    }

    @Test
    public void testLockRecord5() {
        try {
            System.out.println("Multiple lock on single record test");
            int lockAttempts = 5000;
            ExecutorService e = Executors.newFixedThreadPool(lockAttempts);
            CompletionService<Long> ecs = new ExecutorCompletionService<Long>(e);
            long recNo = 4;
            for (int i = 0; i < lockAttempts; i++) {
                ecs.submit(new LockTask(recNo));
            }
            for (int i = 0; i < lockAttempts; i++) {
                System.out.println("before take: [numLocked="
                    + TestDBAccessImpl.numLocked + ", numUnlocked="
                    + TestDBAccessImpl.numUnlocked + "]");
                long c = ecs.take().get();
                System.out.println("after take: [numLocked="
                    + TestDBAccessImpl.numLocked + ", numUnlocked="
                    + TestDBAccessImpl.numUnlocked + "]");
                TestDBAccessImpl.db.unlock(4, c);
                TestDBAccessImpl.numUnlocked++;
                // Thread.sleep(100);
                // int waitCount = TestDBAccessImpl.db.getRecordLocker()
                // .getLockTable().getNumOfWaiting(4);
                // Assert.assertTrue(waitCount == (4 - i));
            }
            // long c = ecs.take().get();
            // TestDBAccessImpl.db.unlock(4, c);
            // Integer waitCount = TestDBAccessImpl.db.getRecordLocker()
            // .getLockTable().getNumOfWaiting(4);
            // Assert.assertTrue(waitCount == null);
            DBLock lock = TestDBAccessImpl.db.getRecordLocker().getLockTable()
                .get(4);
            // Assert.assertNull(lock);
            Assert.assertTrue(TestDBAccessImpl.numLocked == lockAttempts);
            Assert
                .assertTrue(TestDBAccessImpl.numLocked == TestDBAccessImpl.numUnlocked);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Test method for {@link suncertify.db.Data#readRecord(long)}.
     */
    @Test
    public void testReadRecord() {
        DBInfo dbInfo = TestDBAccessImpl.db.getDBInfo();
        TreeMap<String, String[]> keys = new TreeMap<String, String[]>();
        TreeMap<String, String[]> dump = TestDBAccessImpl.db.dump();
        Iterator<String> it = dump.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String[] rec = dump.get(key);
            keys.put(key, rec);
        }
        Iterator<String> names = keys.keySet().iterator();
        int i = 0;
        while (names.hasNext()) {
            String[] rec = keys.get(names.next());
            String name = rec[0];
            String location = rec[1];
            String specialties = rec[2];
            String size = rec[3];
            String rate = rec[4];
            String owner = rec[5];
            System.out.println(i++ + ":" + name + location + specialties + size
                + rate + owner);
            // Assert.assertTrue(!keys.contains(key));
            // keys.add(key);
        }
        Assert.assertTrue(i == dbInfo.getNumberOfRecords());
        try {
            String[] rec = TestDBAccessImpl.db.readRecord(0);
            Assert.assertNotNull(rec);
            Assert.assertEquals(rec.length, dbInfo.getNumOfFields());
            rec = TestDBAccessImpl.db.readRecord(12);
            Assert.assertNotNull(rec);
            Assert.assertEquals(rec.length, dbInfo.getNumOfFields());
            long numOfRecords = dbInfo.getNumberOfRecords();
            rec = TestDBAccessImpl.db.readRecord(numOfRecords - 1);
            Assert.assertNotNull(rec);
            Assert.assertEquals(rec.length, dbInfo.getNumOfFields());
            try {
                rec = TestDBAccessImpl.db.readRecord(numOfRecords);
                Assert.fail("Can not read record number = numOfRecords");
            } catch (RecordNotFoundException e) {
            }
            try {
                rec = TestDBAccessImpl.db.readRecord(-1);
                Assert.fail("Can not read record number = -1");
            } catch (RecordNotFoundException e) {
            }
        } catch (RecordNotFoundException e) {
            e.printStackTrace();
            Assert.fail("Valid record #1 expected");
        }
    }

    /**
     * Test method for
     * {@link suncertify.db.Data#updateRecord(long, java.lang.String[], long)} .
     */
    @Test
    public void testUpdateRecord() {
        TestDBAccessImpl.db.getDBInfo();
        String[] record = getTestRecord("Global Forge, LLC", "Newark, NJ");
        long recNo = 0;
        try {
            recNo = TestDBAccessImpl.db.createRecord(record);
        } catch (DuplicateKeyException e1) {
            Assert.fail("record number should not be duplicate");
        }
        short len = DBField.LOCATION.getLen();
        String newField = new String(Arrays.copyOf("Peapack, NJ"
            .getBytes(DBInfo.ASCII), len), DBInfo.ASCII);
        record[1] = newField;
        long cookie = 0;
        try {
            cookie = TestDBAccessImpl.db.lockRecord(recNo);
            TestDBAccessImpl.db.updateRecord(recNo, record, cookie);
            TestDBAccessImpl.db.unlock(recNo, cookie);
        } catch (SecurityException e) {
            e.printStackTrace();
            Assert.fail();
        } catch (RecordNotFoundException e) {
            e.printStackTrace();
            Assert.fail();
        }
        String[] newRecord = null;
        try {
            newRecord = TestDBAccessImpl.db.readRecord(recNo);
        } catch (RecordNotFoundException e1) {
            e1.printStackTrace();
            Assert.fail();
        }
        String n1 = record[0];
        Assert.assertTrue(n1.trim().equals("Global Forge, LLC"));
        String n2 = newRecord[0];
        Assert.assertTrue(n2.trim().equals("Global Forge, LLC"));
        Assert.assertTrue(n1.equals(n2));
        String c2 = newRecord[1];
        Assert.assertTrue(c2.trim().equals("Peapack, NJ"));
        long newCookie = 0;
        try {
            newCookie = TestDBAccessImpl.db.lockRecord(recNo);
            TestDBAccessImpl.db.updateRecord(recNo, record, cookie);
            Assert.fail("Should throw SecurityException");
        } catch (SecurityException e) {
            TestDBAccessImpl.db.unlock(recNo, newCookie);
        } catch (RecordNotFoundException e) {
            e.printStackTrace();
            Assert.fail();
        }
        try {
            newCookie = TestDBAccessImpl.db.lockRecord(recNo);
            TestDBAccessImpl.db.updateRecord(42, record, newCookie);
            Assert.fail("Should throw SecurityException");
        } catch (SecurityException e) {
            Assert.fail();
        } catch (RecordNotFoundException e) {
            TestDBAccessImpl.db.unlock(recNo, newCookie);
        }
        len = DBField.LOCATION.getLen();
        newField = new String(Arrays.copyOf("Hoboken, NJ"
            .getBytes(DBInfo.ASCII), len), DBInfo.ASCII);
        record[1] = newField;
        try {
            cookie = TestDBAccessImpl.db.lockRecord(recNo);
            TestDBAccessImpl.db.updateRecord(recNo, record, cookie);
            TestDBAccessImpl.db.unlock(recNo, cookie);
        } catch (SecurityException e) {
            e.printStackTrace();
            Assert.fail();
        } catch (RecordNotFoundException e) {
            e.printStackTrace();
            Assert.fail();
        }
        try {
            newRecord = TestDBAccessImpl.db.readRecord(recNo);
        } catch (RecordNotFoundException e1) {
            e1.printStackTrace();
            Assert.fail();
        }
        n1 = record[0];
        Assert.assertTrue(n1.trim().equals("Global Forge, LLC"));
        n2 = newRecord[0];
        Assert.assertTrue(n2.trim().equals("Global Forge, LLC"));
        Assert.assertTrue(n1.equals(n2));
        c2 = newRecord[1];
        Assert.assertTrue(c2.trim().equals("Hoboken, NJ"));
        // delete the record
        try {
            cookie = TestDBAccessImpl.db.lockRecord(recNo);
            TestDBAccessImpl.db.deleteRecord(recNo, cookie);
            TestDBAccessImpl.db.unlock(recNo, cookie);
        } catch (SecurityException e) {
            Assert.fail();
        } catch (RecordNotFoundException e) {
            Assert.fail();
        }
        try {
            record = TestDBAccessImpl.db.readRecord(recNo);
            Assert.fail("record number should not exist");
        } catch (RecordNotFoundException e1) {
        }
        // update the record
        try {
            cookie = TestDBAccessImpl.db.lockRecord(recNo);
            TestDBAccessImpl.db.updateRecord(recNo, record, cookie);
            Assert.fail();
        } catch (SecurityException e) {
            e.printStackTrace();
            Assert.fail();
        } catch (RecordNotFoundException e) {
        } finally {
            TestDBAccessImpl.db.unlock(recNo, cookie);
        }
    }

    private String[] getTestRecord(String name, String city) {
        DBInfo dbInfo = TestDBAccessImpl.db.getDBInfo();
        String[] record = new String[dbInfo.getNumOfFields()];
        short len = DBField.NAME.getLen();
        record[0] = String.format("%1$-" + len + "s", name);
        Assert.assertTrue(record[0].length() == len);
        len = DBField.LOCATION.getLen();
        record[1] = String.format("%1$-" + len + "s", city);
        Assert.assertTrue(record[1].length() == len);
        len = DBField.SPECIALTIES.getLen();
        record[2] = String.format("%1$-" + len + "s",
            "Computer Consulting Services");
        Assert.assertTrue(record[2].length() == len);
        len = DBField.SIZE.getLen();
        record[3] = String.format("%1$-" + len + "s", "3");
        Assert.assertTrue(record[3].length() == len);
        len = DBField.RATE.getLen();
        record[4] = String.format("%1$-" + len + "s", "75.00");
        Assert.assertTrue(record[4].length() == len);
        len = DBField.OWNER.getLen();
        record[5] = String.format("%1$-" + len + "s", " ");
        Assert.assertTrue(record[5].length() == len);
        return record;
    }

    private String[] getRecordWithEmptyLoc(String name) {
        DBInfo dbInfo = TestDBAccessImpl.db.getDBInfo();
        String[] record = new String[dbInfo.getNumOfFields()];
        short len = DBField.NAME.getLen();
        record[0] = String.format("%1$-" + len + "s", name);
        Assert.assertTrue(record[0].length() == len);
        len = DBField.LOCATION.getLen();
        record[1] = String.format("%1$-" + len + "s", ""); // location is empty
        Assert.assertTrue(record[1].length() == len);
        len = DBField.SPECIALTIES.getLen();
        record[2] = String.format("%1$-" + len + "s",
            "Computer Consulting Services");
        Assert.assertTrue(record[2].length() == len);
        len = DBField.SIZE.getLen();
        record[3] = String.format("%1$-" + len + "s", "3");
        Assert.assertTrue(record[3].length() == len);
        len = DBField.RATE.getLen();
        record[4] = String.format("%1$-" + len + "s", "75.00");
        Assert.assertTrue(record[4].length() == len);
        len = DBField.OWNER.getLen();
        record[5] = String.format("%1$-" + len + "s", " ");
        Assert.assertTrue(record[5].length() == len);
        return record;
    }

    private String[] getRecordWithEmptyName(String loc) {
        DBInfo dbInfo = TestDBAccessImpl.db.getDBInfo();
        String[] record = new String[dbInfo.getNumOfFields()];
        short len = DBField.NAME.getLen();
        record[0] = String.format("%1$-" + len + "s", "");
        Assert.assertTrue(record[0].length() == len);
        len = DBField.LOCATION.getLen();
        record[1] = String.format("%1$-" + len + "s", loc);
        Assert.assertTrue(record[1].length() == len);
        len = DBField.SPECIALTIES.getLen();
        record[2] = String.format("%1$-" + len + "s",
            "Computer Consulting Services");
        Assert.assertTrue(record[2].length() == len);
        len = DBField.SIZE.getLen();
        record[3] = String.format("%1$-" + len + "s", "3");
        Assert.assertTrue(record[3].length() == len);
        len = DBField.RATE.getLen();
        record[4] = String.format("%1$-" + len + "s", "75.00");
        Assert.assertTrue(record[4].length() == len);
        len = DBField.OWNER.getLen();
        record[5] = String.format("%1$-" + len + "s", " ");
        Assert.assertTrue(record[5].length() == len);
        return record;
    }

    private String[] getRecordWithEmptyNameAndLoc() {
        DBInfo dbInfo = TestDBAccessImpl.db.getDBInfo();
        String[] record = new String[dbInfo.getNumOfFields()];
        short len = DBField.NAME.getLen();
        record[0] = String.format("%1$-" + len + "s", "");
        Assert.assertTrue(record[0].length() == len);
        len = DBField.LOCATION.getLen();
        record[1] = String.format("%1$-" + len + "s", "");
        Assert.assertTrue(record[1].length() == len);
        len = DBField.SPECIALTIES.getLen();
        record[2] = String.format("%1$-" + len + "s",
            "Computer Consulting Services");
        Assert.assertTrue(record[2].length() == len);
        len = DBField.SIZE.getLen();
        record[3] = String.format("%1$-" + len + "s", "3");
        Assert.assertTrue(record[3].length() == len);
        len = DBField.RATE.getLen();
        record[4] = String.format("%1$-" + len + "s", "75.00");
        Assert.assertTrue(record[4].length() == len);
        len = DBField.OWNER.getLen();
        record[5] = String.format("%1$-" + len + "s", " ");
        Assert.assertTrue(record[5].length() == len);
        return record;
    }

    private class LockTask implements Callable<Long> {
        private long recNo = -1;
        public long cookie = 0L;

        public LockTask(long recNo) {
            TestDBAccessImpl.numLocked++;
            this.recNo = recNo;
        }

        /**
         * @see java.util.TimerTask#run()
         */
        @Override
        public Long call() {
            try {
                // recNo must not be locked otherwise this thread will not
                // return
                cookie = TestDBAccessImpl.db.lockRecord(recNo);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                // System.exit(-1);
            }
            return cookie;
        }
    }
}
