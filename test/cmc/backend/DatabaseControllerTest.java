// DatabaseControllerTest.java
package cmc.backend;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Map;
import cmc.CMCException;

public class DatabaseControllerTest {
    private DatabaseController dbController;
    private static final String TEST_USER = "testUserDB";
    private static final String TEST_PASS = "testPass123";
    private static final String TEST_FNAME = "TestFirst";
    private static final String TEST_LNAME = "TestLast";
    private static final char TEST_TYPE_USER = 'u';
    private static final char TEST_TYPE_ADMIN = 'a';
    private static final String TEST_SCHOOL = "YALE UNIVERSITY";  // Using a real school from DB

    @Before
    public void setUp() {
        dbController = new DatabaseController();
        // Clean up any existing test user
        try {
            dbController.removeUser(TEST_USER);
        } catch (CMCException ex) {
            // Ignore - user might not exist
        }
    }

    @After
    public void tearDown() {
        try {
            dbController.removeUser(TEST_USER);
        } catch (CMCException ex) {
            // Ignore cleanup errors
        }
    }

    @Test
    public void testAddUser() throws CMCException {
        boolean result = dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, 
                                            TEST_FNAME, TEST_LNAME);
        Assert.assertTrue("User addition should succeed", result);

        String[] user = dbController.getUser(TEST_USER);
        Assert.assertNotNull("Added user should be retrievable", user);
        Assert.assertEquals("First name should match", TEST_FNAME, user[0]);
        Assert.assertEquals("Last name should match", TEST_LNAME, user[1]);
        Assert.assertEquals("Username should match", TEST_USER, user[2]);
        Assert.assertEquals("Password should match", TEST_PASS, user[3]);
        Assert.assertEquals("User type should match", String.valueOf(TEST_TYPE_USER), user[4]);
        Assert.assertEquals("New user should be active", "Y", user[5]);
    }

    @Test(expected = CMCException.class)
    public void testAddUserWithEmptyUsername() throws CMCException {
        dbController.addUser("", TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
    }

    @Test(expected = CMCException.class)
    public void testAddUserWithNullUsername() throws CMCException {
        dbController.addUser(null, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
    }

    @Test(expected = CMCException.class)
    public void testAddDuplicateUser() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
    }

    @Test
    public void testIsUserActive() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
        Assert.assertTrue("New user should be active", dbController.isUserActive(TEST_USER));
        
        dbController.deactivateUser(TEST_USER);
        Assert.assertFalse("Deactivated user should be inactive", dbController.isUserActive(TEST_USER));
    }

    @Test
    public void testDeactivateUser() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
        Assert.assertTrue("Deactivation should succeed", dbController.deactivateUser(TEST_USER));
        
        String[] user = dbController.getUser(TEST_USER);
        Assert.assertEquals("User should be marked as inactive", "N", user[5]);
    }

    @Test
    public void testGetAllUsers() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
        dbController.addUser(TEST_USER + "2", TEST_PASS, TEST_TYPE_ADMIN, TEST_FNAME, TEST_LNAME);

        List<String[]> users = dbController.getAllUsers();
        Assert.assertNotNull("User list should not be null", users);
        Assert.assertTrue("User list should not be empty", users.size() > 0);

        boolean foundUser1 = false, foundUser2 = false;
        for (String[] user : users) {
            if (TEST_USER.equals(user[2])) foundUser1 = true;
            if ((TEST_USER + "2").equals(user[2])) foundUser2 = true;
        }
        Assert.assertTrue("Should find first test user", foundUser1);
        Assert.assertTrue("Should find second test user", foundUser2);

        // Cleanup second test user
        dbController.removeUser(TEST_USER + "2");
    }

    @Test
    public void testSaveAndGetSavedSchools() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
        Assert.assertTrue("Should save school successfully", 
                         dbController.saveSchool(TEST_USER, TEST_SCHOOL));

        Map<String, List<String>> savedSchools = dbController.getUserSavedSchoolMap();
        Assert.assertTrue("User should exist in saved schools map", 
                         savedSchools.containsKey(TEST_USER));
        Assert.assertTrue("School should be in user's saved list", 
                         savedSchools.get(TEST_USER).contains(TEST_SCHOOL));
    }

    @Test(expected = CMCException.class)
    public void testSaveNonexistentSchool() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
        dbController.saveSchool(TEST_USER, "NONEXISTENT UNIVERSITY");
    }

    @Test
    public void testRemoveSchool() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
        dbController.saveSchool(TEST_USER, TEST_SCHOOL);
        
        Assert.assertTrue("Should remove school successfully", 
                         dbController.removeSchool(TEST_USER, TEST_SCHOOL));
        
        Map<String, List<String>> savedSchools = dbController.getUserSavedSchoolMap();
        List<String> userSchools = savedSchools.get(TEST_USER);
        Assert.assertTrue("User's saved schools should be empty or not contain removed school",
                         userSchools == null || !userSchools.contains(TEST_SCHOOL));
    }

    @Test
    public void testGetAllSchools() {
        List<String[]> schools = dbController.getAllSchools();
        Assert.assertNotNull("School list should not be null", schools);
        Assert.assertTrue("School list should not be empty", schools.size() > 0);
        
        // Verify school data structure
        String[] firstSchool = schools.get(0);
        Assert.assertNotNull("School record should not be null", firstSchool);
        Assert.assertTrue("School name should not be empty", 
                         firstSchool[0] != null && !firstSchool[0].isEmpty());
        Assert.assertTrue("School state should not be empty", 
                         firstSchool[1] != null && !firstSchool[1].isEmpty());
    }
}