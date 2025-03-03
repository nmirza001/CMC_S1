// SystemControllerTest.java
package cmc.backend;

import org.junit.After;  // Import for @After annotation
import org.junit.Assert;  // Import for Assert class
import org.junit.Before; // Import for @Before annotation
import org.junit.Test;  // Import for @Test annotation
import java.util.List;  // Import for List interface

/**
 *  Hey again! This is our test class for the SystemController.
 *  We're testing the methods in SystemController, which is responsible for the main business logic.
 *  This helps us ensure the core system functions are working correctly.
 */
public class SystemControllerTest {
    private SystemController systemController; // Instance of SystemController we're testing
    private static final String TEST_USER = "testUserSys"; // Test username
    private static final String TEST_PASS = "testPass123"; // Test password
    private static final String TEST_FNAME = "TestFirst"; // Test first name
    private static final String TEST_LNAME = "TestLast"; // Test last name
    private static final String TEST_SCHOOL = "YALE UNIVERSITY"; // A real school name from the database

    /**
     *  Set up method, runs before each test.
     *  We create a new SystemController and clean up any leftover test user.
     */
    @Before
    public void setUp() {
        systemController = new SystemController(); // Create a new SystemController before each test
        // Clean up any test user from previous tests to ensure a clean environment.
        systemController.removeUser(TEST_USER); // Try to remove test user - ignore if user doesn't exist.
    }

    /**
     *  Tear down method, runs after each test.
     *  We clean up by removing the test user.
     */
    @After
    public void tearDown() {
        systemController.removeUser(TEST_USER); // Remove the test user after each test.
    }

    /**
     *  Test case for successful login.
     *  We add a user, then try to log in with correct credentials and verify login success.
     */
    @Test
    public void testLogin() {
        // First, we need to add a user to the system for login testing.
        Assert.assertTrue(systemController.addUser(TEST_USER, TEST_PASS,
                                                 TEST_FNAME, TEST_LNAME, false)); // Add a test user

        // Now let's test successful login with the correct username and password.
        User user = systemController.login(TEST_USER, TEST_PASS);
        Assert.assertNotNull("Login should succeed with correct credentials", user); // Login should return a User object if successful
        Assert.assertEquals("Username should match", TEST_USER, user.getUsername()); // Check if the logged-in username is correct
        Assert.assertEquals("First name should match", TEST_FNAME, user.getFirstName()); // Check if the first name is correct
        Assert.assertTrue("New user should be active", user.isActivated()); // New user should be active by default.
    }

    /**
     *  Test case for various login failure scenarios.
     *  We test login with wrong password, wrong username, and for a deactivated user.
     */
    @Test
    public void testLoginFailures() {
        // First, add a user to the system for testing login failures.
        systemController.addUser(TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME, false); // Add a test user

        // Test login with a wrong password - should fail.
        Assert.assertNull("Login should fail with wrong password",
                         systemController.login(TEST_USER, "wrongpass")); // Login should return null for failure

        // Test login with a wrong username - should also fail.
        Assert.assertNull("Login should fail with wrong username",
                         systemController.login("wronguser", TEST_PASS)); // Should return null

        // Test login for a deactivated user. First, deactivate the user.
        systemController.deactivateUser(TEST_USER);
        Assert.assertNull("Login should fail for deactivated user",
                         systemController.login(TEST_USER, TEST_PASS)); // Deactivated users should not be able to login.
    }

    /**
     *  Test case for the search functionality.
     *  We test searching for all schools (empty state) and searching by a specific state.
     */
    @Test
    public void testSearch() {
        // Test searching for all schools by providing an empty state string.
        List allSchools = systemController.search("");
        Assert.assertNotNull("Search result should not be null", allSchools); // Search result should be a list
        Assert.assertTrue("Should find some schools", allSchools.size() > 0); // We expect to find schools in the system

        // Now, let's test searching by a specific state. We'll pick the state of the first school in the list.
        String firstSchoolState = ((String[])allSchools.get(0))[1]; // Get the state of the first school
        List stateSchools = systemController.search(firstSchoolState); // Search for schools in that state
        Assert.assertTrue("Should find schools in state", stateSchools.size() > 0); // We should find at least one school in that state.
        for (int i = 0; i < stateSchools.size(); i++) {
            String[] school = (String[]) stateSchools.get(i);
            Assert.assertEquals("All schools should be from searched state",
                              firstSchoolState, school[1]); // Verify each school is from the correct state.
        }
    }

    /**
     *  Test case for saving a school for a user and retrieving the saved schools.
     */
    @Test
    public void testSaveAndGetSavedSchools() {
        // First, add a user to the system to save schools for.
        systemController.addUser(TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME, false); // Add a test user

        // Test saving a school for the user.
        Assert.assertTrue("Should save school successfully",
                         systemController.saveSchool(TEST_USER, TEST_SCHOOL)); // Saving should return true for success

        // Now, let's retrieve the list of saved schools for this user.
        List savedSchools = systemController.getSavedSchools(TEST_USER);
        Assert.assertNotNull("Saved schools list should not be null", savedSchools); // We should get a list back
        Assert.assertTrue("Should find saved school",
                         savedSchools.contains(TEST_SCHOOL)); // The saved school should be in the list.
    }

    /**
     *  Test case for removing a saved school from a user's saved list.
     */
    @Test
    public void testRemoveSchool() {
        // Set up: Add a user and save a school for them first.
        systemController.addUser(TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME, false); // Add a test user
        systemController.saveSchool(TEST_USER, TEST_SCHOOL); // Save a school for the user

        // Test removing the saved school.
        Assert.assertTrue("Should remove school successfully",
                         systemController.removeSchool(TEST_USER, TEST_SCHOOL)); // Removal should return true for success

        // Verify that the school is no longer in the saved schools list.
        List savedSchools = systemController.getSavedSchools(TEST_USER);
        Assert.assertFalse("School should no longer be in saved list",
                          savedSchools.contains(TEST_SCHOOL)); // The removed school should not be in the list anymore.
    }
}