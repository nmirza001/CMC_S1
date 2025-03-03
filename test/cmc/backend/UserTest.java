package cmc.backend;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserTest {
	private static User testUser;

	private static String testUname = "testuname";
	private static String testPass = "testpass";
	private static char testType = 'u';
	private static String testFName = "Test";
	private static String testLName = "AUser";

	@Before
	public void setUp() throws Exception {
		testUser = new User(testUname, testPass, testType, testFName, testLName);
	}

	@After
	public void tearDown() throws Exception {
		// No database use for unit testing this class, so nothing to "tear down"!
	}
	
	@Test
	public void testConstructor() {
		Assert.assertEquals(testFName, testUser.getFirstName());
		Assert.assertEquals(testLName, testUser.getLastName());
		// ...
		Assert.assertEquals('Y', testUser.getActivated());
	}

	@Test
	public void testIsAdmin() {
		Assert.assertFalse(testUser.isAdmin());
		testUser.setType('a');
		Assert.assertTrue(testUser.isAdmin());
	}

}