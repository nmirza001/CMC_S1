// UserInteractionTest.java
package cmc.frontend;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.util.List;
import java.util.Scanner;
import java.io.ByteArrayInputStream;
import cmc.backend.User;

/**
 * Test class for UserInteraction
 * Tests user interface level operations including login, user management,
 * and school operations
 */
public class UserInteractionTest {
    private UserInteraction userInteraction;
    private static final String TEST_USER = "testUIUser";
    private static final String TEST_PASS = "testPass123";
    private static final String TEST_FNAME = "TestFirst";
    private static final String TEST_LNAME = "TestLast";
    private static final String TEST_SCHOOL = "YALE UNIVERSITY";
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "adminpass";

    @Before
    public void setUp() {
        userInteraction = new UserInteraction();
        cleanupTestUser();
    }

    @After
    public void tearDown() {
        cleanupTestUser();
    }

    private void cleanupTestUser() {
        // Ensure logged out state and clean up test users if they exist
        userInteraction.logout();
        
        // Login as admin to clean up
        createAndLoginAdmin();
        
        // Try to remove test users
        userInteraction.removeUser(createScannerWithInput(TEST_USER + "\n"));
        userInteraction.removeUser(createScannerWithInput(ADMIN_USER + "\n"));
        
        userInteraction.logout();
    }

    private Scanner createScannerWithInput(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes()));
    }

    private void createAndLoginAdmin() {
        userInteraction.addUser(createScannerWithInput(String.format(
            "%s\n%s\nAdmin\nUser\nY\n", ADMIN_USER, ADMIN_PASS)));
        userInteraction.login(ADMIN_USER, ADMIN_PASS);
    }

    private void createAndLoginTestUser() {
        userInteraction.addUser(createScannerWithInput(String.format(
            "%s\n%s\n%s\n%s\nN\n", TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME)));
        userInteraction.login(TEST_USER, TEST_PASS);
    }

    @Test
    public void testLoginSuccess() {
        // First add a regular user
        createAndLoginAdmin();
        userInteraction.addUser(createScannerWithInput(String.format(
            "%s\n%s\n%s\n%s\nN\n", TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME)));
        userInteraction.logout();

        // Test login
        boolean loginResult = userInteraction.login(TEST_USER, TEST_PASS);
        Assert.assertTrue("Login should succeed with correct credentials", loginResult);
        
        User loggedInUser = userInteraction.getLoggedInUser();
        Assert.assertNotNull("Should have logged in user", loggedInUser);
        Assert.assertEquals("Username should match", TEST_USER, loggedInUser.getUsername());
        Assert.assertTrue("User should be active", loggedInUser.isActivated());
    }

    @Test
    public void testLoginFailures() {
        // First add a test user
        createAndLoginAdmin();
        userInteraction.addUser(createScannerWithInput(String.format(
            "%s\n%s\n%s\n%s\nN\n", TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME)));
        userInteraction.logout();

        // Test wrong password
        Assert.assertFalse("Login should fail with wrong password",
                          userInteraction.login(TEST_USER, "wrongpass"));
        Assert.assertNull("No user should be logged in after failed login",
                         userInteraction.getLoggedInUser());

        // Test wrong username
        Assert.assertFalse("Login should fail with wrong username",
                          userInteraction.login("wronguser", TEST_PASS));
        Assert.assertNull("No user should be logged in after failed login",
                         userInteraction.getLoggedInUser());
    }

    @Test
    public void testLogout() {
        // Setup: create and login as test user
        createAndLoginAdmin();
        createAndLoginTestUser();
        
        // Test logout
        Assert.assertTrue("Logout should succeed when logged in", userInteraction.logout());
        Assert.assertNull("No user should be logged in after logout",
                         userInteraction.getLoggedInUser());
        
        // Test logout when already logged out
        Assert.assertFalse("Logout should fail when not logged in", userInteraction.logout());
    }

    @Test
    public void testAddUserAsAdmin() {
        // Login as admin
        createAndLoginAdmin();

        // Test adding a new user as admin
        Scanner input = createScannerWithInput(String.format(
            "%s\n%s\n%s\n%s\nN\n", TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME));
        Assert.assertTrue("Should successfully add user", userInteraction.addUser(input));

        // Verify user was added by trying to login
        userInteraction.logout();
        Assert.assertTrue("Should be able to login as new user",
                         userInteraction.login(TEST_USER, TEST_PASS));
    }

    @Test
    public void testAddUserNotAdmin() {
        // Create and login as regular user
        createAndLoginAdmin();
        createAndLoginTestUser();

        // Try to add another user
        Scanner input = createScannerWithInput("newuser\npass\nNew\nUser\nN\n");
        Assert.assertFalse("Non-admin should not be able to add users",
                          userInteraction.addUser(input));
    }

    @Test
    public void testGetAllUsers() {
        // Create an admin user and some regular users
        createAndLoginAdmin();
        userInteraction.addUser(createScannerWithInput(String.format(
            "%s\n%s\n%s\n%s\nN\n", TEST_USER, TEST_PASS, TEST_FNAME, TEST_LNAME)));
        
        List<String[]> users = userInteraction.getAllUsers();
        Assert.assertNotNull("User list should not be null", users);
        Assert.assertTrue("Should find multiple users", users.size() >= 2);
        
        boolean foundAdmin = false, foundTestUser = false;
        for (String[] user : users) {
            if (ADMIN_USER.equals(user[2])) foundAdmin = true;
            if (TEST_USER.equals(user[2])) foundTestUser = true;
        }
        Assert.assertTrue("Should find admin user", foundAdmin);
        Assert.assertTrue("Should find test user", foundTestUser);
    }

    @Test
    public void testSaveAndGetSavedSchools() {
        // Create and login as regular user
        createAndLoginAdmin();
        createAndLoginTestUser();

        // Test saving school
        Scanner input = createScannerWithInput(TEST_SCHOOL + "\n");
        Assert.assertTrue("Should save school successfully", userInteraction.saveSchool(input));

        // Test getting saved schools
        List<String> savedSchools = userInteraction.getSavedSchools();
        Assert.assertNotNull("Saved schools list should not be null", savedSchools);
        Assert.assertTrue("Should find saved school", savedSchools.contains(TEST_SCHOOL));
    }

    @Test
    public void testRemoveSchool() {
        // Setup: Create user, login, and save a school
        createAndLoginAdmin();
        createAndLoginTestUser();
        userInteraction.saveSchool(createScannerWithInput(TEST_SCHOOL + "\n"));

        // Test removal
        Assert.assertTrue("Should remove school successfully",
                         userInteraction.removeSchool(TEST_SCHOOL));

        // Verify removal
        List<String> savedSchools = userInteraction.getSavedSchools();
        Assert.assertFalse("School should no longer be in saved list",
                          savedSchools.contains(TEST_SCHOOL));
    }
    
    @Test
    public void testDeactivateUser() {
        // Setup: Create admin and test user
        createAndLoginAdmin();
        createAndLoginTestUser();
        userInteraction.logout();
        
        // Login as admin to deactivate
        userInteraction.login(ADMIN_USER, ADMIN_PASS);
        
        // Test deactivation
        Assert.assertTrue("Should deactivate user successfully",
                         userInteraction.deactivateUser(TEST_USER));
                         
        // Verify deactivation by trying to login
        Assert.assertFalse("Deactivated user should not be able to login",
                          userInteraction.login(TEST_USER, TEST_PASS));
    }
}