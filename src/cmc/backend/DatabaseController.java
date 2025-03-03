package cmc.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmc.CMCException;
import dblibrary.project.csci230.*;

/**
 * <p>
 *  The <code>DatabaseController</code> class serves as the central hub for interacting with
 *  our underlying database library. Think of it as the friendly face that speaks "database"
 *  on behalf of the rest of our application.
 * </p>
 *
 * <p>
 *  Currently, its role is quite direct: it takes requests, translates them into commands
 *  understood by the <code>UniversityDBLibrary</code>, and then neatly packages up the responses
 *  to send back.  It's like a diligent messenger, ensuring smooth communication.
 * </p>
 *
 * <p>
 *  In cases where the database encounters an issue, this class is responsible for catching
 *  those hiccups and turning them into more understandable <code>CMCException</code>s,
 *  making error handling in the upper layers of our application much cleaner.
 * </p>
 *
 * @author Sally Sparrow (Original Architect - We're just making the docs shine!)
 * @author Nasir Mirza (Documentation Enhancements - Giving Sally's brilliance the words it deserves 😉)
 */
public class DatabaseController {
    /**
     *  For clarity and maintainability, we use these constants to refer to the index
     *  of each piece of user information within our arrays. It's like having named columns
     *  instead of just remembering positions!
     */
    private static final int USER_FIRST_NAME_INDEX = 0;
    /** Index for user's last name in user info arrays. */
    private static final int USER_LAST_NAME_INDEX = 1;
    /** Index for username in user info arrays. */
    private static final int USER_USERNAME_INDEX = 2;
    /** Index for password in user info arrays. */
    private static final int USER_PASSWORD_INDEX = 3;
    /** Index for user type (admin/regular) in user info arrays. */
    private static final int USER_TYPE_INDEX = 4;
    /** Index for user activation status (Y/N) in user info arrays. */
    private static final int USER_ACTIVATION_INDEX = 5;
    /** Index for school name in school info arrays. */
    private static final int SCHOOL_NAME_INDEX = 0;
    /** Index for school state in school info arrays. */
    private static final int SCHOOL_STATE_INDEX = 1;

    /**
     *  This is our connection to the external database library. Think of it as
     *  the actual phone line that connects us to the database server.
     */
    private UniversityDBLibrary database;

    /**
     *  <p>
     *   Constructs a new <code>DatabaseController</code>.
     *   This constructor automatically establishes a connection to the database
     *   using our team's designated credentials.
     *  </p>
     *  <p>
     *   It's like plugging in the phone line – ready to make calls to the database!
     *   The credentials ("dei", "Csci230$") are used to authenticate our application
     *   with the database.
     *  </p>
     */
    public DatabaseController() {
        // "dei" is our team identifier, and "Csci230$" is our, ahem, 'highly secure' password. 😉
        this.database = new UniversityDBLibrary("dei", "Csci230$");
    }

    /**
     *  <p>
     *   <code>validateUserInput</code> is a gatekeeper method. Before we proceed to add or modify
     *   user data, this method ensures that the essential input fields are not empty or null.
     *  </p>
     *  <p>
     *   It checks for the presence of a username, password, first name, and last name.
     *   If any of these are missing, it bravely throws a {@link CMCException} to halt the operation
     *   and signal that we need complete user information.
     *  </p>
     *
     * @param username The username provided for validation.
     * @param password The password provided for validation.
     * @param firstName The first name provided for validation.
     * @param lastName The last name provided for validation.
     * @throws CMCException If any of the input parameters (username, password, firstName, lastName) are null or empty after trimming whitespace.
     */
    private void validateUserInput(String username, String password,
                                 String firstName, String lastName) throws CMCException {
        if (username == null || username.trim().length() == 0) {
            throw new CMCException("Username cannot be empty"); // Username is essential!
        }
        if (password == null || password.trim().length() == 0) {
            throw new CMCException("Password cannot be empty"); // Password too, of course.
        }
        if (firstName == null || firstName.trim().length() == 0) {
            throw new CMCException("First name cannot be empty"); // Need to know who they are.
        }
        if (lastName == null || lastName.trim().length() == 0) {
            throw new CMCException("Last name cannot be empty"); // And their last name.
        }
    }

    /**
     *  <p>
     *   <code>isUserActive</code> checks if a given user is currently marked as 'active' in the system.
     *  </p>
     *  <p>
     *   It retrieves the user's record using their username and then inspects the activation flag.
     *   A 'Y' in the activation status field means the user is active and ready to go!
     *  </p>
     *
     * @param username The username of the user to check for activity.
     * @return <code>true</code> if the user is found and their activation status is 'Y', <code>false</code> otherwise.
     */
    public boolean isUserActive(String username) {
        String[] user = getUser(username); // First, let's get the user's details
        if (user == null) {
            return false; // No user found? Definitely not active.
        }
        // We look at the activation index and check if it starts with 'Y' (for Yes).
        return user[USER_ACTIVATION_INDEX].charAt(0) == 'Y';
    }

    /**
     *  <p>
     *   <code>addUser</code> is the method for introducing a new user into our system.
     *  </p>
     *  <p>
     *   It takes all the necessary user details, first performing input validation using
     *   {@link #validateUserInput(String, String, String, String)} to ensure data integrity.
     *   It then checks if a user with the given username already exists to prevent duplicates.
     *   If all checks pass, it calls the underlying database library to persist the new user.
     *  </p>
     *
     * @param username The desired username for the new user.
     * @param password The password for the new user.
     * @param type The user type, typically 'u' for regular user or 'a' for admin.
     * @param firstName The first name of the new user.
     * @param lastName The last name of the new user.
     * @return <code>true</code> if the user was successfully added to the database, <code>false</code> otherwise.
     * @throws CMCException If input validation fails (e.g., empty fields) or if there's an error during database operation (e.g., user already exists).
     */
    public boolean addUser(String username, String password, char type,
            String firstName, String lastName) throws CMCException {
        validateUserInput(username, password, firstName, lastName); // Let's ensure the input is squeaky clean

        if (getUser(username) != null) {
            throw new CMCException("User already exists"); // Oops, someone's already using that name!
        }

        int result = this.database.user_addUser(firstName, lastName, username, password, type);

        if (result == -1) {
            throw new CMCException("Error adding user to the DB"); // Uh oh, database didn't like that.
        }
        return true; // Welcome aboard, new user!
    }

    /**
     *  <p>
     *   <code>removeUser</code> is the method for permanently deleting a user from the system.
     *  </p>
     *  <p>
     *   Before deleting the user record itself, it tidies up by removing any saved schools
     *   associated with this user. This ensures we don't leave any orphaned data behind.
     *   After cleaning up saved schools, it proceeds to delete the user from the database.
     *  </p>
     *
     * @param username The username of the user to be removed.
     * @return <code>true</code> if the user was successfully removed, <code>false</code> otherwise.
     * @throws CMCException If there is an error removing the user from the database.
     */
    public boolean removeUser(String username) throws CMCException {
        Map<String, List<String>> savedSchools = getUserSavedSchoolMap(); // First, let's see if they have any saved schools
        if (savedSchools.containsKey(username)) {
            for (String school : savedSchools.get(username)) {
                this.database.user_removeSchool(username, school); // Detach them from their saved schools
            }
        }

        int result = this.database.user_deleteUser(username); // Now, vaporize the user!
        if (result != 1) {
            throw new CMCException("Error removing user from the DB"); // Database says "Nope, can't delete them."
        }

        return true; // User is gone, like they were never there... *spooky sound effect*
    }

    /**
     *  <p>
     *   <code>getUser</code> retrieves the information for a single user based on their username.
     *  </p>
     *  <p>
     *   It queries the database for all users and then iterates through the list to find the user
     *   matching the provided username. If found, it returns an array containing the user's details.
     *  </p>
     *
     * @param username The username of the user to retrieve.
     * @return An array of strings containing the user's information if found, or <code>null</code> if no user with the given username exists.
     */
    public String[] getUser(String username) {
        String[][] databaseUserStrings = this.database.user_getUsers(); // Fetch all users - like casting a wide net

        for (String[] singleUser : databaseUserStrings) { // Let's sift through them one by one
            if (singleUser[USER_USERNAME_INDEX].equals(username)) {
                return singleUser; // Aha! Found our user, here are their details.
            }
        }

        return null; // User not found? They must be hiding... or maybe they don't exist.
    }

    /**
     *  <p>
     *   <code>getAllUsers</code> fetches a list of information for all users in the system.
     *  </p>
     *  <p>
     *   It directly calls the database library to retrieve all user records and then
     *   packages them into a List of String arrays for easy access.
     *  </p>
     *
     * @return A List where each element is a String array containing the information for a single user. Returns an empty List if there are no users in the system.
     */
    public List<String[]> getAllUsers() {
        String[][] dbUserList = this.database.user_getUsers(); // Grab 'em all, folks!

        ArrayList<String[]> result = new ArrayList<>(); // Let's get ready to compile our list
        for (String[] user : dbUserList) {
            result.add(user); // One by one, add each user to our result list
        }

        return result; // Ta-da! A list of all users, ready for action.
    }

    /**
     *  <p>
     *   <code>deactivateUser</code> marks a user as inactive, preventing them from logging into the system.
     *  </p>
     *  <p>
     *   It retrieves the user's record and then calls the {@link #editUser} method to update their activation
     *   status to 'N', while keeping all other user details unchanged.
     *  </p>
     *
     * @param username The username of the user to deactivate.
     * @return <code>true</code> if the user was successfully deactivated, <code>false</code> if the user was not found.
     * @throws CMCException If there is an error deactivating the user in the database.
     */
    public boolean deactivateUser(String username) throws CMCException {
        String[] theUser = getUser(username); // First, who are we deactivating?
        if (theUser == null) {
            return false; // Can't deactivate someone who isn't there.
        }

        int result = this.database.user_editUser(
            theUser[USER_USERNAME_INDEX],
            theUser[USER_FIRST_NAME_INDEX],
            theUser[USER_LAST_NAME_INDEX],
            theUser[USER_PASSWORD_INDEX],
            theUser[USER_TYPE_INDEX].charAt(0),
            'N' // Flip the activation switch to 'N' for 'No, not active!'
        );

        if (result == -1) {
            throw new CMCException("Error deactivating user in the DB"); // Database is resisting deactivation!
        }

        return true; // User deactivated. *sound of door slamming shut*
    }

    /**
     *  <p>
     *   <code>getAllSchools</code> retrieves a list of information for all universities in the system.
     *  </p>
     *  <p>
     *   It directly interfaces with the database library to fetch all university records and
     *   returns them as a List of String arrays.
     *  </p>
     *
     * @return A List where each element is a String array containing the information for a single university. Returns an empty List if there are no universities in the system.
     */
    public List<String[]> getAllSchools() {
        String[][] dbUniversityList = this.database.university_getUniversities(); // Unleash the university getter!

        ArrayList<String[]> result = new ArrayList<>(); // Time to gather all those schools
        for (String[] school : dbUniversityList) {
            result.add(school); // Add each school to our growing list
        }

        return result; // Behold, a list of all the universities!
    }

    /**
     *  <p>
     *   <code>saveSchool</code> allows a user to save a university to their personal list of saved schools.
     *  </p>
     *  <p>
     *   It validates the input (username and school name), checks if the school is already saved by the user
     *   to prevent duplicates, and also verifies if the school actually exists in the database.
     *   If all validations pass, it proceeds to save the school association in the database.
     *  </p>
     *
     * @param username The username of the user saving the school.
     * @param schoolName The name of the school to be saved.
     * @return <code>true</code> if the school was successfully saved for the user, <code>false</code> otherwise.
     * @throws CMCException If input validation fails, if the school is already saved, or if the school does not exist in the database, or if there's a database error during saving.
     */
    public boolean saveSchool(String username, String schoolName) throws CMCException {
        if (username == null || username.trim().length() == 0) {
            throw new CMCException("Username cannot be empty"); // Need a user to save for!
        }
        if (schoolName == null || schoolName.trim().length() == 0) {
            throw new CMCException("School name cannot be empty"); // And a school to save!
        }

        Map<String, List<String>> savedSchools = getUserSavedSchoolMap(); // Let's see what they've already saved
        if (savedSchools.containsKey(username) &&
            savedSchools.get(username).contains(schoolName)) {
            throw new CMCException("School already saved for this user"); // No double-dipping on schools!
        }

        boolean schoolExists = false;
        List<String[]> allSchools = getAllSchools(); // Is this school even real? Let's check!
        for (String[] school : allSchools) {
            if (school[SCHOOL_NAME_INDEX].equals(schoolName)) {
                schoolExists = true; // Found it! It's a real school.
                break; // No need to keep searching.
            }
        }

        if (!schoolExists) {
            throw new CMCException("School does not exist in the database"); // Whoops, school's not in our records.
        }

        int result = this.database.user_saveSchool(username, schoolName); // Database, please save this school for this user!
        if (result != 1) {
            throw new CMCException("Error saving school to user in the DB"); // Database is having trouble saving.
        }

        return true; // School saved! User's college dreams are one step closer.
    }

    /**
     *  <p>
     *   <code>removeSchool</code> removes a university from a user's saved schools list.
     *  </p>
     *  <p>
     *   It validates the input (username and school name) and then calls the database library
     *   to remove the association between the user and the specified school.
     *  </p>
     *
     * @param username The username of the user from whose saved list the school should be removed.
     * @param schoolName The name of the school to be removed from the saved list.
     * @return <code>true</code> if the school was successfully removed from the user's saved list, <code>false</code> otherwise.
     * @throws CMCException If input validation fails or if there's an error during database operation.
     */
    public boolean removeSchool(String username, String schoolName) throws CMCException {
        if (username == null || username.trim().length() == 0) {
            throw new CMCException("Username cannot be empty"); // Need to know whose list to remove from!
        }
        if (schoolName == null || schoolName.trim().length() == 0) {
            throw new CMCException("School name cannot be empty"); // Which school are we removing?
        }

        int result = this.database.user_removeSchool(username, schoolName); // Database, unsave this school for this user!
        if (result != 1) {
            throw new CMCException("Error removing school from user's saved list"); // Database says "Nope, can't unsave it."
        }
        return true; // School removed from saved list. User's preferences updated.
    }

    /**
     *  <p>
     *   <code>getUserSavedSchoolMap</code> retrieves a mapping of users to their saved schools.
     *  </p>
     *  <p>
     *   It queries the database to get all user-school save relationships and then organizes this
     *   data into a Map where each key is a username, and the value is a List of school names
     *   that the user has saved.
     *  </p>
     *
     * @return A Map where keys are usernames and values are Lists of school names saved by each user. Returns an empty Map if no users have saved any schools.
     */
    public Map<String, List<String>> getUserSavedSchoolMap() {
        String[][] dbMapping = this.database.user_getUsernamesWithSavedSchools(); // Get the raw mapping data from the database

        HashMap<String, List<String>> result = new HashMap<>(); // We'll use a HashMap to organize user -> saved schools

        for (String[] entry : dbMapping) { // Let's process each entry from the database
            String user = entry[0]; // Username is the first part of the entry
            String school = entry[1]; // School name is the second part

            if (!result.containsKey(user)) {
                result.put(user, new ArrayList<>()); // If we haven't started a list for this user, start one now!
            }

            result.get(user).add(school); // Add this saved school to the user's list
        }

        return result; // Here's the map of users and their treasured saved schools!
    }

    /**
     *  <p>
     *   <code>editUser</code> allows modification of an existing user's details, such as name, password, or type.
     *  </p>
     *  <p>
     *   It performs input validation using {@link #validateUserInput(String, String, String, String)} and then
     *   calls the database library to update the user's record with the new information.
     *  </p>
     *
     * @param username The username of the user to edit.
     * @param firstName The new first name for the user.
     * @param lastName The new last name for the user.
     * @param password The new password for the user.
     * @param type The new user type (e.g., 'u' or 'a').
     * @param activated The new activation status ('Y' for active, 'N' for inactive).
     * @return <code>true</code> if the user details were successfully updated, <code>false</code> otherwise.
     * @throws CMCException If input validation fails or if there's an error during database operation.
     */
    public boolean editUser(String username, String firstName, String lastName,
                          String password, char type, char activated) throws CMCException {
        validateUserInput(username, password, firstName, lastName); // Gotta make sure the input is still good

        int result = this.database.user_editUser(username, firstName, lastName,
                                               password, type, activated); // Database, update this user with the new details!
        if (result == -1) {
            throw new CMCException("Error editing user in the DB"); // Database is resisting the edit!
        }
        return true; // User details updated. Fresh coat of paint!
    }
}