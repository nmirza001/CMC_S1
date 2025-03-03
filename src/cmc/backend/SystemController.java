package cmc.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cmc.CMCException;

/**
 * <p>
 *  The <code>SystemController</code> class is the central orchestrator of the CMC system's business logic.
 *  Think of it as the conductor of an orchestra, coordinating interactions between the user interface
 *  and the data management layer ({@link DatabaseController}).
 * </p>
 * <p>
 *  It's responsible for processing user requests, applying business rules, and ensuring data consistency
 *  by delegating database operations to the <code>DatabaseController</code>. This class is where decisions are made
 *  based on the application's requirements, making it a critical component of the backend system.
 * </p>
 *
 * @author Nasir Mirza (Crafting professional documentation for a robust system)
 */
public class SystemController {
    /**
     *  For internal array handling, these constants define the indices for user-related data.
     *  Using constants enhances code readability and maintainability, acting like named positions in a record.
     */
    private static final int USER_FIRST_NAME_INDEX = 0;
    /** Index for the user's last name. */
    private static final int USER_LAST_NAME_INDEX = 1;
    /** Index for the username. */
    private static final int USER_USERNAME_INDEX = 2;
    /** Index for the password. */
    private static final int USER_PASSWORD_INDEX = 3;
    /** Index for the user type (e.g., admin or regular user). */
    private static final int USER_TYPE_INDEX = 4;
    /** Index for the user's activation status. */
    private static final int USER_ACTIVATION_INDEX = 5;
    /** Index for the school name. */
    private static final int SCHOOL_NAME_INDEX = 0;
    /** Index for the school state. */
    private static final int SCHOOL_STATE_INDEX = 1;

    /**
     *  A reference to the {@link DatabaseController} instance, allowing <code>SystemController</code>
     *  to communicate with the database layer for data retrieval and manipulation.
     */
    private DatabaseController myDBController;

    /**
     *  <p>
     *   Constructs a new <code>SystemController</code>.
     *   Upon creation, it initializes its connection to the database by instantiating a {@link DatabaseController}.
     *  </p>
     *  <p>
     *   This setup ensures that the <code>SystemController</code> is ready to handle business logic
     *   and interact with the database from the moment it's created.
     *  </p>
     */
    public SystemController() {
        this.myDBController = new DatabaseController(); // Get the database interaction layer ready.
    }

    /**
     *  <p>
     *   <code>userExists</code> checks if a user with the given username is present in the system.
     *  </p>
     *  <p>
     *   It delegates this check to the {@link DatabaseController}, effectively querying the database
     *   to determine the existence of the user.
     *  </p>
     *
     * @param username The username to check for existence.
     * @return <code>true</code> if a user with the given username exists, <code>false</code> otherwise.
     */
    public boolean userExists(String username) {
        return this.myDBController.getUser(username) != null; // Let DatabaseController handle the check.
    }

    /**
     *  <p>
     *   <code>isUserActive</code> determines if a user account is currently active.
     *  </p>
     *  <p>
     *   It utilizes the {@link DatabaseController} to retrieve the user's information and then
     *   checks the activation status. An active user is typically allowed to log in and use system features.
     *  </p>
     *
     * @param username The username of the user to check for active status.
     * @return <code>true</code> if the user is active, <code>false</code> otherwise.
     */
    public boolean isUserActive(String username) {
        return this.myDBController.isUserActive(username); // Delegate the activity check to DatabaseController.
    }

    /**
     *  <p>
     *   <code>getUser</code> retrieves the complete user information for a given username.
     *  </p>
     *  <p>
     *   This method acts as a proxy to the {@link DatabaseController}'s method of the same name,
     *   simply forwarding the request to fetch user data from the database.
     *  </p>
     *
     * @param username The username of the user whose information is to be retrieved.
     * @return An array of strings containing the user's data if found, or <code>null</code> if the user does not exist.
     */
    public String[] getUser(String username) {
        return this.myDBController.getUser(username); // Forward the user data request to DatabaseController.
    }

    /**
     *  <p>
     *   <code>login</code> attempts to authenticate a user with provided credentials.
     *  </p>
     *  <p>
     *   It first validates that both username and password are provided. Then, it retrieves the user's data
     *   and verifies if the provided password matches the stored password and if the user account is active.
     *   If both conditions are met, a {@link User} object is returned, representing a successful login.
     *  </p>
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return A {@link User} object if login is successful, <code>null</code> otherwise (e.g., invalid credentials, inactive account).
     */
    public User login(String username, String password) {
        if (username == null || username.trim().length() == 0 || // Input validation: username required
            password == null || password.trim().length() == 0) { // Input validation: password required
            return null; // Cannot login without username or password.
        }

        String[] userData = this.myDBController.getUser(username); // Retrieve user data from the database.
        if (userData == null) {
            return null; // User not found in the database, login fails.
        }

        User theUser = new User( // Create a User object from the database data.
            userData[USER_USERNAME_INDEX],
            userData[USER_PASSWORD_INDEX],
            userData[USER_TYPE_INDEX].charAt(0),
            userData[USER_FIRST_NAME_INDEX],
            userData[USER_LAST_NAME_INDEX]
        );

        theUser.setActivated(userData[USER_ACTIVATION_INDEX].charAt(0)); // Set the activation status from the database.

        if (theUser.getActivated() != 'Y' || !theUser.getPassword().equals(password)) { // Check activation status and password match.
            return null; // Login fails if not active or password doesn't match.
        }

        return theUser; // Login successful, return the User object.
    }

    /**
     *  <p>
     *   <code>getAllUsers</code> retrieves a list of all users in the system.
     *  </p>
     *  <p>
     *   This method is typically used by administrators to manage user accounts. It simply forwards
     *   the request to the {@link DatabaseController} to fetch all user records.
     *  </p>
     *
     * @return A List of String arrays, where each array represents a user's information. Returns an empty List if no users are in the system.
     */
    public List<String[]> getAllUsers() {
        return this.myDBController.getAllUsers(); // Delegate to DatabaseController to get all users.
    }

    /**
     *  <p>
     *   <code>addUser</code> adds a new user to the system.
     *  </p>
     *  <p>
     *   This administrative function validates user input, determines the user type (admin or regular),
     *   and then utilizes the {@link DatabaseController} to persist the new user in the database.
     *  </p>
     *
     * @param username The username for the new user.
     * @param password The password for the new user.
     * @param firstName The first name of the new user.
     * @param lastName The last name of the new user.
     * @param isAdmin A boolean indicating if the user should be created as an administrator.
     * @return <code>true</code> if the user was successfully added, <code>false</code> otherwise (e.g., input validation failure, database error).
     */
    public boolean addUser(String username, String password,
            String firstName, String lastName, boolean isAdmin) {
        if (username == null || username.trim().length() == 0 || // Input validation: username required
            password == null || password.trim().length() == 0 || // Input validation: password required
            firstName == null || firstName.trim().length() == 0 || // Input validation: first name required
            lastName == null || lastName.trim().length() == 0) {  // Input validation: last name required
            System.out.println("Error adding user: All fields must be filled"); // Inform user about input error.
            return false; // User addition failed due to input error.
        }

        char type = (isAdmin ? 'a' : 'u'); // Determine user type based on isAdmin flag.
        try {
            return this.myDBController.addUser(username, password, type, firstName, lastName); // Delegate user addition to DatabaseController.
        } catch (CMCException e) {
            System.out.println("Error adding user: " + e.getMessage()); // Display any CMCException message.
            return false; // User addition failed due to exception.
        }
    }

    /**
     *  <p>
     *   <code>removeUser</code> removes a user from the system.
     *  </p>
     *  <p>
     *   This administrative function ensures that a username is provided and then delegates the actual removal
     *   process to the {@link DatabaseController}.
     *  </p>
     *
     * @param username The username of the user to be removed.
     * @return <code>true</code> if the user was successfully removed, <code>false</code> otherwise (e.g., username not provided, database error).
     */
    public boolean removeUser(String username) {
        if (username == null || username.trim().length() == 0) { // Input validation: username required for removal
            System.out.println("Error removing user: Username cannot be empty"); // Inform user about input error.
            return false; // User removal failed due to input error.
        }

        try {
            return this.myDBController.removeUser(username); // Delegate user removal to DatabaseController.
        } catch (CMCException e) {
            System.out.println("Error removing user: " + e.getMessage()); // Display any CMCException message.
            return false; // User removal failed due to exception.
        }
    }

    /**
     *  <p>
     *   <code>deactivateUser</code> deactivates a user account, preventing future logins.
     *  </p>
     *  <p>
     *   Similar to user removal, this administrative function requires a username and then delegates
     *   the deactivation operation to the {@link DatabaseController}.
     *  </p>
     *
     * @param username The username of the user to be deactivated.
     * @return <code>true</code> if the user was successfully deactivated, <code>false</code> otherwise (e.g., username not provided, database error).
     */
    public boolean deactivateUser(String username) {
        if (username == null || username.trim().length() == 0) { // Input validation: username required for deactivation
            System.out.println("Error deactivating user: Username cannot be empty"); // Inform user about input error.
            return false; // User deactivation failed due to input error.
        }

        try {
            return this.myDBController.deactivateUser(username); // Delegate user deactivation to DatabaseController.
        } catch (CMCException e) {
            System.out.println("Error deactivating user: " + e.getMessage()); // Display any CMCException message.
            return false; // User deactivation failed due to exception.
        }
    }

    /**
     *  <p>
     *   <code>search</code> searches for universities, optionally filtering by state.
     *  </p>
     *  <p>
     *   If no state is provided (or is blank), it retrieves all universities. Otherwise, it filters the results
     *   to only include universities in the specified state.
     *  </p>
     *
     * @param state The state to filter universities by, or <code>null</code> or blank to retrieve all universities.
     * @return A List of String arrays, where each array represents a university's information. Returns an empty List if no universities match the criteria.
     */
    public List<String[]> search(String state) {
        List<String[]> schoolList = this.myDBController.getAllSchools(); // Get the full list of schools from DatabaseController.

        if (state == null || state.trim().length() == 0) { // If no state is specified, return all schools.
            return schoolList;
        }

        List<String[]> filteredList = new ArrayList<>(); // Prepare a list for filtered schools.
        for (String[] school : schoolList) {
            if (school[SCHOOL_STATE_INDEX].equals(state)) { // Check if the school's state matches the search state.
                filteredList.add(school); // Add matching school to the filtered list.
            }
        }

        return filteredList; // Return the filtered list of schools.
    }

    /**
     *  <p>
     *   <code>saveSchool</code> allows a user to save a school to their personal saved schools list.
     *  </p>
     *  <p>
     *   It validates that both username and school name are provided and then delegates the save operation
     *   to the {@link DatabaseController}.
     *  </p>
     *
     * @param user The username of the user saving the school.
     * @param school The name of the school to be saved.
     * @return <code>true</code> if the school was successfully saved, <code>false</code> otherwise (e.g., input validation failure, database error).
     */
    public boolean saveSchool(String user, String school) {
        if (user == null || user.trim().length() == 0 || // Input validation: username required
            school == null || school.trim().length() == 0) { // Input validation: school name required
            System.out.println("Error saving school: Username and school name must be provided"); // Inform user about input error.
            return false; // School saving failed due to input error.
        }

        try {
            return this.myDBController.saveSchool(user, school); // Delegate school saving to DatabaseController.
        } catch (CMCException e) {
            System.out.println("Error saving school: " + e.getMessage()); // Display any CMCException message.
            return false; // School saving failed due to exception.
        }
    }

    /**
     *  <p>
     *   <code>removeSchool</code> removes a school from a user's saved schools list.
     *  </p>
     *  <p>
     *   This method validates that both username and school name are provided and then delegates
     *   the removal operation to the {@link DatabaseController}.
     *  </p>
     *
     * @param user The username of the user from whose saved list the school should be removed.
     * @param school The name of the school to be removed.
     * @return <code>true</code> if the school was successfully removed, <code>false</code> otherwise (e.g., input validation failure, database error).
     */
    public boolean removeSchool(String user, String school) {
        if (user == null || user.trim().length() == 0 || // Input validation: username required
            school == null || school.trim().length() == 0) { // Input validation: school name required
            System.out.println("Error removing school: Username and school name must be provided"); // Inform user about input error.
            return false; // School removal failed due to input error.
        }

        try {
            return this.myDBController.removeSchool(user, school); // Delegate school removal to DatabaseController.
        } catch (CMCException e) {
            System.out.println("Error removing saved school: " + e.getMessage()); // Display any CMCException message.
            return false; // School removal failed due to exception.
        }
    }

    /**
     *  <p>
     *   <code>getSavedSchools</code> retrieves a list of schools saved by a specific user.
     *  </p>
     *  <p>
     *   It validates that a username is provided and then uses the {@link DatabaseController} to fetch
     *   the list of saved schools for that user.
     *  </p>
     *
     * @param user The username of the user whose saved schools are to be retrieved.
     * @return A List of school names saved by the user. Returns an empty List if the user has not saved any schools or if the username is not provided.
     */
    public List<String> getSavedSchools(String user) {
        if (user == null || user.trim().length() == 0) { // Input validation: username required
            return new ArrayList<>(); // Cannot get saved schools without a username.
        }

        Map<String, List<String>> usersToSavedSchools = this.myDBController.getUserSavedSchoolMap(); // Get the mapping of users to saved schools from DatabaseController.
        List<String> schools = usersToSavedSchools.get(user); // Retrieve saved schools for the given user.
        return schools != null ? schools : new ArrayList<>(); // Return the list of schools or an empty list if none saved.
    }

    /**
     *  <p>
     *   <code>editUser</code> allows administrators to modify details of an existing user account.
     *  </p>
     *  <p>
     *   This administrative function validates all input parameters and then delegates the user editing
     *   operation to the {@link DatabaseController}.
     *  </p>
     *
     * @param username The username of the user to be edited.
     * @param firstName The new first name for the user.
     * @param lastName The new last name for the user.
     * @param password The new password for the user.
     * @param type The new user type (e.g., 'u' for regular, 'a' for admin).
     * @param activated The new activation status ('Y' for active, 'N' for inactive).
     * @return <code>true</code> if the user details were successfully updated, <code>false</code> otherwise (e.g., input validation failure, database error).
     */
    public boolean editUser(String username, String firstName, String lastName,
                          String password, char type, char activated) {
        if (username == null || username.trim().length() == 0 || // Input validation: username required
            firstName == null || firstName.trim().length() == 0 || // Input validation: first name required
            lastName == null || lastName.trim().length() == 0 ||  // Input validation: last name required
            password == null || password.trim().length() == 0) {  // Input validation: password required
            System.out.println("Error editing user: All fields must be filled"); // Inform user about input error.
            return false; // User editing failed due to input error.
        }

        try {
            return this.myDBController.editUser(username, firstName, lastName, // Delegate user editing to DatabaseController.
                                              password, type, activated);
        } catch (CMCException e) {
            System.out.println("Error editing user: " + e.getMessage()); // Display any CMCException message.
            return false; // User editing failed due to exception.
        }
    }
}