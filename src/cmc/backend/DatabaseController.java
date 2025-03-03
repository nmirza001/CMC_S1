package cmc.backend;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmc.CMCException;
import dblibrary.project.csci230.*;

/**
 * The DatabaseController class is the primary interaction class with the
 * database library. It currently just calls the lower-level methods and
 * forwards the result (possibly throwing some exceptions along the way).
 * 
 * @author Sally Sparrow
 */
public class DatabaseController {
    // Constants for array indices to improve maintainability
    private static final int USER_FIRST_NAME_INDEX = 0;
    private static final int USER_LAST_NAME_INDEX = 1;
    private static final int USER_USERNAME_INDEX = 2;
    private static final int USER_PASSWORD_INDEX = 3;
    private static final int USER_TYPE_INDEX = 4;
    private static final int USER_ACTIVATION_INDEX = 5;
    private static final int SCHOOL_NAME_INDEX = 0;
    private static final int SCHOOL_STATE_INDEX = 1;

    private UniversityDBLibrary database;

    // The default constructor that connects to the underlying
    // UniversityDBLibrary object using your team's info.
    public DatabaseController() {
        this.database = new UniversityDBLibrary("csci230", "Csci230$");
    }

    /**
     * Validates user input for adding/editing users
     */
    private void validateUserInput(String username, String password, 
                                 String firstName, String lastName) throws CMCException {
        if (username == null || username.trim().isEmpty()) {
            throw new CMCException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new CMCException("Password cannot be empty");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new CMCException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new CMCException("Last name cannot be empty");
        }
    }

    /**
     * Checks if a user is currently active in the system
     */
    public boolean isUserActive(String username) {
        String[] user = getUser(username);
        if (user == null) {
            return false;
        }
        return user[USER_ACTIVATION_INDEX].charAt(0) == 'Y';
    }

    /**
     * Add a user to the database with input validation
     */
    public boolean addUser(String username, String password, char type,
            String firstName, String lastName) throws CMCException {
        // Validate input
        validateUserInput(username, password, firstName, lastName);
        
        // Check if user already exists
        if (getUser(username) != null) {
            throw new CMCException("User already exists");
        }
        
        int result = this.database.user_addUser(firstName, lastName, username, password, type);
        
        if (result == -1) {
            throw new CMCException("Error adding user to the DB");
        }
        return true;
    }
    
    // Rest of your existing methods with updated array index references...
    public boolean removeUser(String username) throws CMCException {
        // First remove all saved schools for this user
        Map<String, List<String>> savedSchools = getUserSavedSchoolMap();
        if (savedSchools.containsKey(username)) {
            for (String school : savedSchools.get(username)) {
                this.database.user_removeSchool(username, school);
            }
        }
        
        int result = this.database.user_deleteUser(username);
        if (result != 1) {
            throw new CMCException("Error removing user from the DB");
        }
        
        return true;
    }
    
    public String[] getUser(String username) {
        String[][] databaseUserStrings = this.database.user_getUsers();
        
        for (String[] singleUser : databaseUserStrings) {
            if (singleUser[USER_USERNAME_INDEX].equals(username)) {
                return singleUser;
            }
        }
        
        return null;
    }
    
    public List<String[]> getAllUsers() {
        String[][] dbUserList = this.database.user_getUsers();
        
        ArrayList<String[]> result = new ArrayList<String[]>();
        for (String[] user : dbUserList) {
            result.add(user);
        }
        
        return result;
    }

    public boolean deactivateUser(String username) throws CMCException {
        String[] theUser = getUser(username);
        if (theUser == null)
            return false;

        int result = this.database.user_editUser(
            theUser[USER_USERNAME_INDEX],
            theUser[USER_FIRST_NAME_INDEX],
            theUser[USER_LAST_NAME_INDEX],
            theUser[USER_PASSWORD_INDEX],
            theUser[USER_TYPE_INDEX].charAt(0),
            'N'
        );

        if (result == -1) {
            throw new CMCException("Error deactivating user in the DB");
        }
        
        return true;
    }

    public List<String[]> getAllSchools() {
        String[][] dbUniversityList = this.database.university_getUniversities();

        ArrayList<String[]> result = new ArrayList<String[]>();
        for (String[] school : dbUniversityList) {
            result.add(school);
        }

        return result;
    }
    
    public boolean saveSchool(String username, String schoolName) throws CMCException {
        if (username == null || username.trim().isEmpty()) {
            throw new CMCException("Username cannot be empty");
        }
        if (schoolName == null || schoolName.trim().isEmpty()) {
            throw new CMCException("School name cannot be empty");
        }

        Map<String, List<String>> savedSchools = getUserSavedSchoolMap();
        if (savedSchools.containsKey(username) && 
            savedSchools.get(username).contains(schoolName)) {
            throw new CMCException("School already saved for this user");
        }

        boolean schoolExists = false;
        List<String[]> allSchools = getAllSchools();
        for (String[] school : allSchools) {
            if (school[SCHOOL_NAME_INDEX].equals(schoolName)) {
                schoolExists = true;
                break;
            }
        }
        
        if (!schoolExists) {
            throw new CMCException("School does not exist in the database");
        }
        
        int result = this.database.user_saveSchool(username, schoolName);
        if (result != 1) {
            throw new CMCException("Error saving school to user in the DB");
        }
        
        return true;
    }
    
    public boolean removeSchool(String username, String schoolName) throws CMCException {
        if (username == null || username.trim().isEmpty()) {
            throw new CMCException("Username cannot be empty");
        }
        if (schoolName == null || schoolName.trim().isEmpty()) {
            throw new CMCException("School name cannot be empty");
        }

        int result = this.database.user_removeSchool(username, schoolName);
        if (result != 1) {
            throw new CMCException("Error removing school from user's saved list");
        }
        return true;
    }
    
    public Map<String, List<String>> getUserSavedSchoolMap() {
        String[][] dbMapping = this.database.user_getUsernamesWithSavedSchools();

        HashMap<String, List<String>> result = new HashMap<String, List<String>>();
        
        for (String[] entry : dbMapping) {
            String user = entry[0];
            String school = entry[1];
            
            if (!result.containsKey(user))
                result.put(user, new ArrayList<String>());
            
            result.get(user).add(school);
        }

        return result;
    }
    
    public boolean editUser(String username, String firstName, String lastName, 
                          String password, char type, char activated) throws CMCException {
        validateUserInput(username, password, firstName, lastName);
        
        int result = this.database.user_editUser(username, firstName, lastName, 
                                               password, type, activated);
        if (result == -1) {
            throw new CMCException("Error editing user in the DB");
        }
        return true;
    }
}