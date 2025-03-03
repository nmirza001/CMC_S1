// DatabaseControllerTest.java
package cmc.backend;

import org.junit.After; // Import for @After annotation
import org.junit.Assert; // Import for Assert class
import org.junit.Before; // Import for @Before annotation
import org.junit.Test;  // Import for @Test annotation
import java.util.List;  // Import for List interface
import java.util.Map;   // Import for Map interface

import cmc.CMCException; // Import our custom exception

/**
 *  Hey there! This is our test class for the DatabaseController.
 *  We're going to test all the methods in DatabaseController to make sure they work as expected.
 *  Think of it as a way to double-check if our database interactions are solid.
 */
public class DatabaseControllerTest {
    private DatabaseController dbController; // Instance of DatabaseController we'll be testing
    private static final String TEST_USER = "testUserDB"; // A test username
    private static final String TEST_PASS = "testPass123"; // A test password
    private static final String TEST_FNAME = "TestFirst"; // Test first name
    private static final String TEST_LNAME = "TestLast"; // Test last name
    private static final char TEST_TYPE_USER = 'u'; // Test user type - regular user
    private static final char TEST_TYPE_ADMIN = 'a'; // Test user type - admin
    private static final String TEST_SCHOOL = "YALE UNIVERSITY";  // Using a real school name from the database

    /**
     *  Set up method, runs before each test method.
     *  We'll create a new DatabaseController and clean up any leftover test user from previous runs.
     */
    @Before
    public void setUp() {
        dbController = new DatabaseController(); // Create a fresh DatabaseController before each test
        // Let's make sure our test user is not already in the system from a failed previous test.
        try {
            dbController.removeUser(TEST_USER); // Try to remove the test user
        } catch (CMCException ex) {
            // Ignore exception - it's okay if the user doesn't exist yet.
            // We just want to ensure a clean state for testing.
        }
    }

    /**
     *  Tear down method, runs after each test method.
     *  We'll clean up by removing the test user we created to keep the database clean.
     */
    @After
    public void tearDown() {
        try {
            dbController.removeUser(TEST_USER); // Remove the test user after each test
        } catch (CMCException ex) {
            // Ignore cleanup errors - if removal fails, it's not critical for test integrity in most cases.
        }
    }

    /**
     *  Test case for adding a new user successfully.
     *  We check if the user is added and if we can retrieve their details correctly.
     */
    @Test
    public void testAddUser() throws CMCException {
        boolean result = dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER,
                                            TEST_FNAME, TEST_LNAME);
        Assert.assertTrue("User addition should succeed", result); // Assert that addUser returns true

        String[] user = dbController.getUser(TEST_USER); // Get the user's info we just added
        Assert.assertNotNull("Added user should be retrievable", user); // Make sure we found the user
        Assert.assertEquals("First name should match", TEST_FNAME, user[0]); // Check first name
        Assert.assertEquals("Last name should match", TEST_LNAME, user[1]); // Check last name
        Assert.assertEquals("Username should match", TEST_USER, user[2]);   // Check username
        Assert.assertEquals("Password should match", TEST_PASS, user[3]);   // Check password
        Assert.assertEquals("User type should match", String.valueOf(TEST_TYPE_USER), user[4]); // Check user type
        Assert.assertEquals("New user should be active", "Y", user[5]); // Check if user is active by default
    }

    /**
     *  Test case for trying to add a user with an empty username.
     *  We expect a CMCException to be thrown because usernames cannot be empty.
     */
    @Test(expected = CMCException.class) // We expect this test to throw CMCException
    public void testAddUserWithEmptyUsername() throws CMCException {
        dbController.addUser("", TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME); // Try to add with empty username
    }

    /**
     *  Test case for trying to add a user with a null username.
     *  We expect a CMCException, similar to the empty username case.
     */
    @Test(expected = CMCException.class) // Expect CMCException
    public void testAddUserWithNullUsername() throws CMCException {
        dbController.addUser(null, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME); // Try to add with null username
    }

    /**
     *  Test case for trying to add a user with a username that already exists.
     *  We expect a CMCException because usernames must be unique.
     */
    @Test(expected = CMCException.class) // Expect CMCException
    public void testAddDuplicateUser() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME); // Add user once
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME); // Try to add again with same username - should fail
    }

    /**
     *  Test case to verify the isUserActive method.
     *  We add a user, check if they are active, then deactivate them and check again.
     */
    @Test
    public void testIsUserActive() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME); // Add a test user
        Assert.assertTrue("New user should be active", dbController.isUserActive(TEST_USER)); // Newly added users should be active

        dbController.deactivateUser(TEST_USER); // Deactivate the user
        Assert.assertFalse("Deactivated user should be inactive", dbController.isUserActive(TEST_USER)); // After deactivation, should be inactive
    }

    /**
     *  Test case to verify the deactivateUser method.
     *  We add a user, deactivate them, and then check if their activation status is updated in the database.
     */
    @Test
    public void testDeactivateUser() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME); // Add a user
        Assert.assertTrue("Deactivation should succeed", dbController.deactivateUser(TEST_USER)); // Deactivation should return true (success)

        String[] user = dbController.getUser(TEST_USER); // Get the user's info after deactivation
        Assert.assertEquals("User should be marked as inactive", "N", user[5]); // Activation status in DB should be 'N'
    }

    /**
     *  Test case to verify the getAllUsers method.
     *  We add two test users, retrieve all users, and check if both our test users are in the list.
     */
    @Test
    public void testGetAllUsers() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME); // Add first test user
        dbController.addUser(TEST_USER + "2", TEST_PASS, TEST_TYPE_ADMIN, TEST_FNAME, TEST_LNAME); // Add second test user

        List users = dbController.getAllUsers(); // Get all users from the database
        Assert.assertNotNull("User list should not be null", users); // Make sure we got a list
        Assert.assertTrue("User list should not be empty", users.size() > 0); // List should contain users

        boolean foundUser1 = false, foundUser2 = false; // Flags to check if we found our test users
        for (int i = 0; i < users.size(); i++) {
            String[] user = (String[]) users.get(i); // Get each user from the list
            if (TEST_USER.equals(user[2])) foundUser1 = true; // Check if username matches first test user
            if ((TEST_USER + "2").equals(user[2])) foundUser2 = true; // Check if username matches second test user
        }
        Assert.assertTrue("Should find first test user", foundUser1); // Assert that first user was found
        Assert.assertTrue("Should find second test user", foundUser2); // Assert that second user was found

        // Cleanup second test user - we only need to keep the first one for other tests
        dbController.removeUser(TEST_USER + "2");
    }

    /**
     *  Test case for saving a school for a user and then retrieving the saved schools.
     *  We check if the school is saved correctly and shows up in the user's saved school list.
     */
    @Test
    public void testSaveAndGetSavedSchools() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME); // Add a test user
        Assert.assertTrue("Should save school successfully",
                         dbController.saveSchool(TEST_USER, TEST_SCHOOL)); // Try to save a school

        Map savedSchools = dbController.getUserSavedSchoolMap(); // Get the map of saved schools
        Assert.assertTrue("User should exist in saved schools map",
                         savedSchools.containsKey(TEST_USER)); // Check if our test user is in the map
        List userSchools = (List) savedSchools.get(TEST_USER); // Get the list of schools saved by our user
        Assert.assertTrue("School should be in user's saved list",
                         userSchools.contains(TEST_SCHOOL)); // Check if our test school is in the user's list
    }

    /**
     *  Test case for trying to save a school that does not exist in the database.
     *  We expect a CMCException to be thrown because you can only save existing schools.
     */
    @Test(expected = CMCException.class) // Expect CMCException
    public void testSaveNonexistentSchool() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME); // Add a test user
        dbController.saveSchool(TEST_USER, "NONEXISTENT UNIVERSITY"); // Try to save a school that doesn't exist - should fail
    }

    /**
     *  Test case for removing a saved school from a user's saved school list.
     *  We save a school, then remove it, and check if it's no longer in the saved list.
     */
    @Test
    public void testRemoveSchool() throws CMCException {
        dbController.addUser(TEST_USER, TEST_PASS, TEST_TYPE_USER, TEST_FNAME, TEST_LNAME); // Add a test user
        dbController.saveSchool(TEST_USER, TEST_SCHOOL); // Save a school for the user

        Assert.assertTrue("Should remove school successfully",
                         dbController.removeSchool(TEST_USER, TEST_SCHOOL)); // Try to remove the saved school

        Map savedSchools = dbController.getUserSavedSchoolMap(); // Get the map of saved schools again
        List userSchools = (List) savedSchools.get(TEST_USER); // Get the user's saved school list
        Assert.assertTrue("User's saved schools should be empty or not contain removed school",
                         userSchools == null || !userSchools.contains(TEST_SCHOOL)); // List should be null or not contain the removed school
    }

    /**
     *  Test case to verify the getAllSchools method.
     *  We retrieve all schools and check if the list is not empty and contains valid school data.
     */
    @Test
    public void testGetAllSchools() {
        List schools = dbController.getAllSchools(); // Get all schools from the database
        Assert.assertNotNull("School list should not be null", schools); // Make sure we got a list
        Assert.assertTrue("School list should not be empty", schools.size() > 0); // List should contain schools

        // Let's check the structure of the first school in the list to make sure it looks right.
        String[] firstSchool = (String[]) schools.get(0); // Get the first school
        Assert.assertNotNull("School record should not be null", firstSchool); // Make sure it's not null
        Assert.assertTrue("School name should not be empty",
                         firstSchool[0] != null && firstSchool[0].length() > 0); // School name should be there
        Assert.assertTrue("School state should not be empty",
                         firstSchool[1] != null && firstSchool[1].length() > 0); // School state should be there
    }
}