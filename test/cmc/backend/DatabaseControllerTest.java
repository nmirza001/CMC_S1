package cmc.backend;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import cmc.CMCException;

public class DatabaseControllerTest {

    private DatabaseController dbController;
    private static final String TEST_USER = "testUserDB";
    private static final String TEST_PASS = "testPass";
    private static final String TEST_FNAME = "Test";
    private static final String TEST_LNAME = "User";
    private static final char TEST_TYPE_USER = 'u';
    private static final char TEST_TYPE_ADMIN = 'a';
    private static final String TEST_SCHOOL = "TEST_SCHOOL";

    @Before
    public void setUp() throws Exception {
        dbController = new DatabaseController();
        // Ensure test user doesn't exist before each test
        try {
            dbController.removeUser(TEST_USER);
        } catch (CMCException ex) {
            // User likely doesn't exist, which is fine
        }
    }

    @After
    public void tearDown() throws Exception {
        // Clean up: Remove the test user after each test
        try {
            dbController.removeUser(TEST_USER);
        } catch (CMCException ex) {
             //User not found, which is OK
        }
    }

    @Test
    public void testAddUser() throws CMCException {
        boolean result = dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
        Assert.assertTrue(result);

        String[] userData = dbController.getUser(TEST_USER);
        Assert.assertNotNull(userData);
        Assert.assertEquals(TEST_FNAME, userData[0]);
        Assert.assertEquals(TEST_LNAME, userData[1]);
        Assert.assertEquals(TEST_USER, userData[2]);
        Assert.assertEquals(TEST_PASS, userData[3]);
        Assert.assertEquals(String.valueOf(TEST_TYPE_USER), userData[4]); //Important: Convert char to String for comparison
    }

    @Test(expected = CMCException.class)
    public void testAddUserDuplicate() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
        // Attempt to add the same user again - should throw an exception
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
    }


    @Test
    public void testRemoveUser() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
        boolean result = dbController.removeUser(TEST_USER);
        Assert.assertTrue(result);
        Assert.assertNull(dbController.getUser(TEST_USER));  // Verify user is gone
    }

    @Test(expected = CMCException.class)
    public void testRemoveUserNonExistent() throws CMCException {
        dbController.removeUser("nonExistentUser"); // Should throw exception
    }

    @Test
    public void testGetUser() throws CMCException{
    	dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
        String[] user = dbController.getUser(TEST_USER);
        Assert.assertNotNull(user);
        Assert.assertEquals(TEST_USER, user[2]);
    }

    @Test
    public void testGetUserNonExistent() {
        String[] user = dbController.getUser("nonExistentUser");
        Assert.assertNull(user);
    }

    @Test
    public void testGetAllUsers() throws CMCException{
    	//ensure the added users are being returned
    	dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
    	dbController.addUser(TEST_USER + "2", TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
    	List<String[]> allusers = dbController.getAllUsers();
    	boolean foundUser1 = false;
        boolean foundUser2 = false;
        for(String[] user : allusers) {
            if(user[2].equals(TEST_USER)) {
                foundUser1 = true;
            }
            if(user[2].equals(TEST_USER+"2")) {
            	foundUser2 = true;
            }
        }
        Assert.assertTrue(foundUser1);
        Assert.assertTrue(foundUser2);
        dbController.removeUser(TEST_USER + "2");

    }

    @Test
    public void testDeactivateUser() throws CMCException{
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
        boolean result = dbController.deactivateUser(TEST_USER);
        Assert.assertTrue(result);
        String[] deactivated = dbController.getUser(TEST_USER);
        Assert.assertEquals(deactivated[5], "N");
    }
    
    @Test
    public void testDeactivateUserNonExistent() throws CMCException{
    	boolean result = dbController.deactivateUser("nonExistentUser");
    	Assert.assertFalse(result);
    }

    @Test
    public void testGetAllSchools() {
        List<String[]> schools = dbController.getAllSchools();
        Assert.assertNotNull(schools);  // At least *some* schools should exist
        Assert.assertTrue(schools.size() > 0); // Check for non-empty list
    }

    @Test
    public void testSaveAndGetSavedSchools() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME);
        boolean saveResult = dbController.saveSchool(TEST_USER, TEST_SCHOOL);
        Assert.assertTrue(saveResult);
        
        boolean saveResult2 = dbController.saveSchool(TEST_USER, TEST_SCHOOL+"2");
        Assert.assertTrue(saveResult2);

        List<String> savedSchools = dbController.getUserSavedSchoolMap().get(TEST_USER);
        Assert.assertNotNull(savedSchools);
        Assert.assertTrue(savedSchools.contains(TEST_SCHOOL));
        Assert.assertTrue(savedSchools.contains(TEST_SCHOOL+"2"));
        
        dbController.removeUser(TEST_USER);
    }
    
    @Test (expected = Error.class)
    public void testSaveSchoolNonExistentUser() throws CMCException{
    	boolean saveResult = dbController.saveSchool("NonExistent User", TEST_SCHOOL);

    }
}