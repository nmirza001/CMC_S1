// UserInteractionTest.java
package cmc.frontend;

import org.junit.Assert;    // Import for Assert class
import org.junit.Before;   // Import for @Before annotation
import org.junit.After;    // Import for @After annotation
import org.junit.Test;    // Import for @Test annotation
import java.util.List;    // Import for List interface
import java.util.Scanner; // Import for Scanner class
import java.io.ByteArrayInputStream; // Import for ByteArrayInputStream
import cmc.backend.User;    // Import for User class

/**
 *  Hey there! This is our test class for UserInteraction.
 *  We're going to test all the user interface level operations in UserInteraction.
 *  This includes things like user login, managing users (adding, getting all),
 *  and operations related to saved schools. We want to make sure the user interface
 *  works smoothly and correctly.
 */
public class UserInteractionTest {
    private UserInteraction userInteraction; // Instance of UserInteraction we're testing
    private static final String TEST_USER = "testUIUser";   // A test username for regular user
    private static final String TEST_PASS = "testPass123";   // A test password for regular user
    private static final String TEST_FNAME = "TestFirst";   // Test first name
    private static final String TEST_LNAME = "TestLast";    // Test last name
    private static final String TEST_SCHOOL = "YALE UNIVERSITY"; // A real school name from the database
    private static final String ADMIN_USER = "admin";      // Username for admin user
    private static final String ADMIN_PASS = "adminpass";      // Password for admin user

    /**
     *  Set up method, runs before each test method.
     *  We'll create a new UserInteraction object and clean up any existing test users
     *  to start each test in a clean state.
     */
    @Before
    public void setUp() {
        userInteraction = new UserInteraction(); // Create a new UserInteraction before each test
        cleanupTestUser(); // Ensure test user and admin user are cleaned up before test
    }

    /**
     *  Tear down method, runs after each test method.
     *  We'll clean up again after each test to remove any test users created during the test,
     *  ensuring that our tests don't leave behind any garbage data.
     */
    @After
    public void tearDown() {
        cleanupTestUser(); // Clean up test user after each test
    }

    /**
     *  Helper method to clean up test users (both regular and admin).
     *  It logs out any current user, logs in as admin, and then attempts to remove
     *  the test users we use for testing.
     */
    private void cleanupTestUser() {
        // First, make sure we are logged out, just in case.
        userInteraction.logout();

        // We need to be admin to remove users, so let's login as admin temporarily.
        createAndLoginAdmin();

        // Now, let's try to remove our test users. It's okay if they don't exist - it will just do nothing.
        userInteraction.removeUser(createScannerWithInput(TEST_USER + "\n")); // Try to remove regular test user
        userInteraction.removeUser(createScannerWithInput(ADMIN_USER + "\n")); // Try to remove admin test user

        userInteraction.logout(); // Logout admin after cleanup.
    }

    /**
     *  Helper method to create a Scanner that reads input from a String.
     *  This is useful for simulating user input in our tests without needing actual user typing.
     */
    private Scanner createScannerWithInput(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes())); // Create Scanner from input string
    }

    /**
     *  Helper method to create an admin user and log in as that admin user.
     *  We use a Scanner to simulate providing input for adding the admin user.
     */
    private void createAndLoginAdmin() {
        userInteraction.addUser(createScannerWithInput(String.format(
            "%s\n%s\nAdmin\nUser\nY\n", ADMIN_USER, ADMIN_PASS))); // Simulate input for adding admin user
        userInteraction.login(ADMIN_USER, ADMIN_PASS); // Login as the admin user we just created.
    }

    /**
     *  Helper method to create a regular test user and log in as that user.
     *  Similar to createAndLoginAdmin, but for a regular user.
     */
    private void createAndLoginTestUser() {
        userInteraction.addUser(createScannerWithInput(String.format(
            "%s\n%s\n%s\n%s\nN\n", TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME))); // Simulate input for adding regular user
        userInteraction.login(TEST_USER, TEST_PASS); // Login as the regular test user.
    }

    /**
     *  Test case for successful user login.
     *  We first add a regular user, then try to log in with the correct username and password,
     *  and verify that login succeeds and the logged-in user details are correct.
     */
    @Test
    public void testLoginSuccess() {
        // First, we need to add a regular user to the system for testing login. Let's use admin to add the user.
        createAndLoginAdmin();
        userInteraction.addUser(createScannerWithInput(String.format(
            "%s\n%s\n%s\n%s\nN\n", TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME))); // Add a regular test user
        userInteraction.logout(); // Logout admin after adding the test user.

        // Now, let's test login with the correct credentials for the regular user.
        boolean loginResult = userInteraction.login(TEST_USER, TEST_PASS);
        Assert.assertTrue("Login should succeed with correct credentials", loginResult); // Assert that login returns true for success

        User loggedInUser = userInteraction.getLoggedInUser(); // Get the logged-in user object
        Assert.assertNotNull("Should have logged in user", loggedInUser); // Make sure we got a user object
        Assert.assertEquals("Username should match", TEST_USER, loggedInUser.getUsername()); // Check if the username of logged-in user is correct
        Assert.assertTrue("User should be active", loggedInUser.isActivated()); // Verify that the logged-in user is marked as active.
    }

    /**
     *  Test case for login failures due to wrong password and wrong username.
     *  We test both scenarios and verify that login fails in each case, and no user is logged in.
     */
    @Test
    public void testLoginFailures() {
        // First, add a test user to the system so we have someone to try to login as (and fail).
        createAndLoginAdmin();
        userInteraction.addUser(createScannerWithInput(String.format(
            "%s\n%s\n%s\n%s\nN\n", TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME))); // Add a regular test user
        userInteraction.logout(); // Logout admin after setup.

        // Test login with a wrong password.
        Assert.assertFalse("Login should fail with wrong password",
                          userInteraction.login(TEST_USER, "wrongpass")); // Login should return false for failure
        Assert.assertNull("No user should be logged in after failed login",
                         userInteraction.getLoggedInUser()); // Verify that no user is logged in after failed attempt.

        // Test login with a wrong username.
        Assert.assertFalse("Login should fail with wrong username",
                          userInteraction.login("wronguser", TEST_PASS)); // Should also return false for wrong username
        Assert.assertNull("No user should be logged in after failed login",
                         userInteraction.getLoggedInUser()); // Again, no user should be logged in.
    }

    /**
     *  Test case for user logout functionality.
     *  We test logging out when a user is logged in, and also trying to log out when no user is logged in.
     */
    @Test
    public void testLogout() {
        // Setup: create and log in as a test user first.
        createAndLoginAdmin();
        createAndLoginTestUser(); // Login as a regular test user

        // Test logout when a user is actually logged in.
        Assert.assertTrue("Logout should succeed when logged in", userInteraction.logout()); // Logout should return true for success
        Assert.assertNull("No user should be logged in after logout",
                         userInteraction.getLoggedInUser()); // Verify that no user is logged in after logout.

        // Test trying to logout again when already logged out - this should ideally fail or do nothing gracefully.
        Assert.assertFalse("Logout should fail when not logged in", userInteraction.logout()); // Logout when already logged out should return false.
    }

    /**
     *  Test case for adding a new user as an admin.
     *  We log in as an admin, try to add a new user, and then verify if the new user can log in.
     */
    @Test
    public void testAddUserAsAdmin() {
        // First, login as an admin user - we need admin rights to add new users.
        createAndLoginAdmin();

        // Test adding a new user using UserInteraction's addUser method (as admin).
        Scanner input = createScannerWithInput(String.format(
            "%s\n%s\n%s\n%s\nN\n", TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME)); // Simulate input for new user details
        Assert.assertTrue("Should successfully add user", userInteraction.addUser(input)); // Adding user as admin should return true.

        // After adding, logout admin and try to login as the newly added user to verify.
        userInteraction.logout();
        Assert.assertTrue("Should be able to login as new user",
                         userInteraction.login(TEST_USER, TEST_PASS)); // New user should be able to login with provided credentials.
    }

    /**
     *  Test case for verifying that a non-admin user cannot add new users.
     *  We log in as a regular user and attempt to add another user, which should fail.
     */
    @Test
    public void testAddUserNotAdmin() {
        // Create and login as a regular user - regular users shouldn't be able to add other users.
        createAndLoginAdmin();
        createAndLoginTestUser(); // Login as a regular test user

        // Try to add another user using UserInteraction (as a non-admin user).
        Scanner input = createScannerWithInput("newuser\npass\nNew\nUser\nN\n"); // Simulate input for adding a different user
        Assert.assertFalse("Non-admin should not be able to add users",
                          userInteraction.addUser(input)); // Adding user as non-admin should return false.
    }

    /**
     *  Test case for getting a list of all users in the system.
     *  We add an admin user and a regular user, then retrieve the list of all users and verify
     *  that both admin and regular users are present in the list.
     */
    @Test
    public void testGetAllUsers() {
        // First, create an admin user and a regular user in the system.
        createAndLoginAdmin();
        userInteraction.addUser(createScannerWithInput(String.format(
            "%s\n%s\n%s\n%s\nN\n", TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME))); // Add a regular test user

        // Now, get the list of all users using UserInteraction's getAllUsers method.
        List users = userInteraction.getAllUsers();
        Assert.assertNotNull("User list should not be null", users); // The list of users should not be null
        Assert.assertTrue("Should find multiple users", users.size() >= 2); // We expect at least 2 users (admin and test user)

        boolean foundAdmin = false, foundTestUser = false; // Flags to track if we found admin and test user in the list.
        for (int i = 0; i < users.size(); i++) {
            String[] user = (String[]) users.get(i);
            if (ADMIN_USER.equals(user[2])) foundAdmin = true; // Check if username matches admin user
            if (TEST_USER.equals(user[2])) foundTestUser = true; // Check if username matches regular test user
        }
        Assert.assertTrue("Should find admin user", foundAdmin); // Assert that admin user was found in the list
        Assert.assertTrue("Should find test user", foundTestUser); // Assert that regular test user was found in the list.
    }

    /**
     *  Test case for saving a school to a user's saved school list and then retrieving the saved schools.
     *  We test saving a school as a regular user and then verify that the school is in the saved list.
     */
    @Test
    public void testSaveAndGetSavedSchools() {
        // Create and login as a regular user - regular users are the ones who save schools.
        createAndLoginAdmin();
        createAndLoginTestUser(); // Login as a regular test user

        // Test saving a school using UserInteraction's saveSchool method.
        Scanner input = createScannerWithInput(TEST_SCHOOL + "\n"); // Simulate input of school name to save
        Assert.assertTrue("Should save school successfully", userInteraction.saveSchool(input)); // Saving school should return true for success

        // Now, let's retrieve the list of saved schools for this user and verify if our school is there.
        List savedSchools = userInteraction.getSavedSchools();
        Assert.assertNotNull("Saved schools list should not be null", savedSchools); // Saved schools list should not be null
        Assert.assertTrue("Should find saved school", savedSchools.contains(TEST_SCHOOL)); // The school we saved should be in the list.
    }

    /**
     *  Test case for removing a school from a user's saved school list.
     *  We set up by adding a user, logging in, and saving a school. Then we test removing the school
     *  and verify that it's no longer in the saved schools list.
     */
    @Test
    public void testRemoveSchool() {
        // Setup: Create a user, login as that user, and save a school to their list.
        createAndLoginAdmin();
        createAndLoginTestUser(); // Login as a regular test user
        userInteraction.saveSchool(createScannerWithInput(TEST_SCHOOL + "\n")); // Save a school first, so we can remove it later.

        // Test removing the saved school using UserInteraction's removeSchool method.
        Assert.assertTrue("Should remove school successfully",
                         userInteraction.removeSchool(TEST_SCHOOL)); // Removing school should return true for success

        // After removal, retrieve the saved schools list again and verify that the school is no longer there.
        List savedSchools = userInteraction.getSavedSchools();
        Assert.assertFalse("School should no longer be in saved list",
                          savedSchools.contains(TEST_SCHOOL)); // The school we removed should not be in the list anymore.
    }

    /**
     *  Test case for deactivating a user.
     *  We set up by creating an admin and a test user. Then, as admin, we deactivate the test user
     *  and verify that the deactivated user can no longer log in.
     */
    @Test
    public void testDeactivateUser() {
        // Setup: Create an admin user and a regular test user.
        createAndLoginAdmin();
        createAndLoginTestUser(); // Create and login as a regular test user
        userInteraction.logout(); // Logout the regular user, so we can test deactivation effect on login.

        // Login as admin again - only admins can deactivate users.
        userInteraction.login(ADMIN_USER, ADMIN_PASS);

        // Test deactivating the test user using UserInteraction's deactivateUser method.
        Assert.assertTrue("Should deactivate user successfully",
                         userInteraction.deactivateUser(TEST_USER)); // Deactivating user should return true for success

        // After deactivation, try to login as the deactivated user and verify that login fails.
        Assert.assertFalse("Deactivated user should not be able to login",
                          userInteraction.login(TEST_USER, TEST_PASS)); // Login for deactivated user should return false.
    }
}