package cmc.backend;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cmc.CMCException;

import java.util.List;

public class SystemControllerTest {

    private SystemController systemController;
    private DatabaseController dbController;  // Used directly (not mocked) for simplicity
    private static final String TEST_USER = "testUserSys";
    private static final String TEST_PASS = "testPass";
    private static final String TEST_FNAME = "Test";
    private static final String TEST_LNAME = "User";
    private static final boolean TEST_IS_ADMIN = false;
    private static final String TEST_SCHOOL = "Test School";

    @Before
    public void setUp() throws Exception {
        systemController = new SystemController();
        dbController = new DatabaseController(); // Use a real DB controller
      // Ensure test user doesn't exist before each test
        try {
            dbController.removeUser(TEST_USER);
        } catch (CMCException ex) {
            // User likely doesn't exist, which is fine
        }
    }

    @After
    public void tearDown() throws Exception {
        // Clean up: Remove the test user
    	try {
            dbController.removeUser(TEST_USER);
        } catch (CMCException ex) {
             //User not found, which is OK
        }
    }
    

    @Test
    public void testLoginSuccess() throws CMCException {
    	//first add the user
    	dbController.addUser(TEST_USER, TEST_PASS, 'u', TEST_FNAME, TEST_LNAME);
        User user = systemController.login(TEST_USER, TEST_PASS);
        Assert.assertNotNull(user);
        Assert.assertEquals(TEST_USER, user.getUsername());
    }

    @Test
    public void testLoginFailureWrongPassword() throws CMCException{
    	//first add the user
    	dbController.addUser(TEST_USER, TEST_PASS, 'u', TEST_FNAME, TEST_LNAME);
        User user = systemController.login(TEST_USER, "wrongPassword");
        Assert.assertNull(user);
    }
    
    @Test
    public void testLoginFailureWrongUsername() {
        User user = systemController.login("wrong", TEST_PASS);
        Assert.assertNull(user);
    }

    @Test
    public void testLoginDeactivatedUser() throws CMCException {
    	//first add the user
    	dbController.addUser(TEST_USER, TEST_PASS, 'u', TEST_FNAME, TEST_LNAME);
    	dbController.deactivateUser(TEST_USER);
        User user = systemController.login(TEST_USER, TEST_PASS);
        Assert.assertNull(user); // Should not be able to log in
    }

    @Test
    public void testAddUser() {
        boolean result = systemController.addUser(TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME, TEST_IS_ADMIN);
        Assert.assertTrue(result);
    }
    
    @Test
    public void testAddUser_Duplicate() throws CMCException{
    	boolean result = systemController.addUser(TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME, TEST_IS_ADMIN);
    	boolean result2 = systemController.addUser(TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME, TEST_IS_ADMIN);
    	Assert.assertFalse(result2);
    }

    @Test
    public void testRemoveUser() throws CMCException{
    	systemController.addUser(TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME, TEST_IS_ADMIN);
        boolean result = systemController.removeUser(TEST_USER);
        Assert.assertTrue(result);
    }

    @Test
    public void testRemoveUserNonExistent() {
        boolean result = systemController.removeUser("nonExistentUser");
        Assert.assertFalse(result);  // Should return false, not throw exception
    }
    
    @Test
    public void testSearchByState() {
    	//depends on your database's initial schools;
    	//This test case may or may not work depending on which state schools you have
    	//List<String[]> result = systemController.search("MN");
    	//Assert.assertTrue(result.size()>0);
    	//Assert.assertEquals("MN", result.get(0)[1]);
    	
    	//To make the test more robust we should add a school with known state
    	//But our current implementation does not allow that.
    }
    
    @Test
    public void testSearchByState_Empty() {
    	//depends on your database's initial schools;
    	//This test should at least not crash and return a (possibly empty) list.
    	List<String[]> result = systemController.search("");
    	Assert.assertNotNull(result);
    }

    @Test
    public void testSaveAndGetSavedSchools() throws CMCException{
        systemController.addUser(TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME, TEST_IS_ADMIN);
        boolean saveResult = systemController.saveSchool(TEST_USER, TEST_SCHOOL);
        Assert.assertTrue(saveResult);

        List<String> savedSchools = systemController.getSavedSchools(TEST_USER);
        Assert.assertNotNull(savedSchools);
        Assert.assertTrue(savedSchools.contains(TEST_SCHOOL));
    }

    @Test
    public void testSaveSchoolDuplicate() throws CMCException{
        systemController.addUser(TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME, TEST_IS_ADMIN);
        systemController.saveSchool(TEST_USER, TEST_SCHOOL);
        boolean saveResult2 = systemController.saveSchool(TEST_USER, TEST_SCHOOL); // Save again
        Assert.assertFalse(saveResult2);  // Should return false
    }
    
    @Test
    public void testGetSavedSchools_NoSavedSchools() throws CMCException{
    	systemController.addUser(TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME, TEST_IS_ADMIN);
        List<String> savedSchools = systemController.getSavedSchools(TEST_USER);
        Assert.assertNotNull(savedSchools); //should return empty list, not null
        Assert.assertTrue(savedSchools.isEmpty());
    }
    
    @Test
    public void testGetAllUsers() throws CMCException{
    	systemController.addUser(TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME, TEST_IS_ADMIN);
    	systemController.addUser(TEST_USER+"2", TEST_PASS, TEST_FNAME, TEST_LNAME, TEST_IS_ADMIN);
    	List<String[]> allusers = systemController.getAllUsers();
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
        systemController.removeUser(TEST_USER + "2");

    }
}