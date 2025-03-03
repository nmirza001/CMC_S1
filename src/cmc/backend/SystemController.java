package cmc.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cmc.CMCException;

/**
 * The SystemController class provides the main business logic for the CMC system,
 * coordinating between the user interface and database operations.
 */
public class SystemController {
    private static final int USER_FIRST_NAME_INDEX = 0;
    private static final int USER_LAST_NAME_INDEX = 1;
    private static final int USER_USERNAME_INDEX = 2;
    private static final int USER_PASSWORD_INDEX = 3;
    private static final int USER_TYPE_INDEX = 4;
    private static final int USER_ACTIVATION_INDEX = 5;
    private static final int SCHOOL_NAME_INDEX = 0;
    private static final int SCHOOL_STATE_INDEX = 1;
    
    private DatabaseController myDBController;
    
    public SystemController() {
        this.myDBController = new DatabaseController();
    }

    public boolean userExists(String username) {
        return this.myDBController.getUser(username) != null;
    }

    public boolean isUserActive(String username) {
        return this.myDBController.isUserActive(username);
    }
    
    public String[] getUser(String username) {
        return this.myDBController.getUser(username);
    }
    
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return null;
        }

        String[] userData = this.myDBController.getUser(username);
        if (userData == null) {
            return null;
        }
        
        User theUser = new User(
            userData[USER_USERNAME_INDEX],
            userData[USER_PASSWORD_INDEX],
            userData[USER_TYPE_INDEX].charAt(0),
            userData[USER_FIRST_NAME_INDEX],
            userData[USER_LAST_NAME_INDEX]
        );
        
        theUser.setActivated(userData[USER_ACTIVATION_INDEX].charAt(0));
        
        if (theUser.getActivated() != 'Y' || !theUser.getPassword().equals(password)) {
            return null;
        }
        
        return theUser;
    }

    public List<String[]> getAllUsers() {
        return this.myDBController.getAllUsers();
    }
    
    public boolean addUser(String username, String password,
            String firstName, String lastName, boolean isAdmin) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty()) {
            System.out.println("Error adding user: All fields must be filled");
            return false;
        }

        char type = (isAdmin ? 'a' : 'u');
        try {
            return this.myDBController.addUser(username, password, type, firstName, lastName);
        } catch (CMCException e) {
            System.out.println("Error adding user: " + e.getMessage());
            return false;
        }
    }
    
    public boolean removeUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Error removing user: Username cannot be empty");
            return false;
        }
        
        try {
            return this.myDBController.removeUser(username);
        } catch (CMCException e) {
            System.out.println("Error removing user: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deactivateUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Error deactivating user: Username cannot be empty");
            return false;
        }
        
        try {
            return this.myDBController.deactivateUser(username);
        } catch (CMCException e) {
            System.out.println("Error deactivating user: " + e.getMessage());
            return false;
        }
    }
    
    public List<String[]> search(String state) {
        List<String[]> schoolList = this.myDBController.getAllSchools();
        
        if (state == null || state.trim().isEmpty()) {
            return schoolList;
        }
        
        List<String[]> filteredList = new ArrayList<String[]>();
        for (String[] school : schoolList) {
            if (school[SCHOOL_STATE_INDEX].equals(state)) {
                filteredList.add(school);
            }
        }
        
        return filteredList;
    }
    
    public boolean saveSchool(String user, String school) {
        if (user == null || user.trim().isEmpty() ||
            school == null || school.trim().isEmpty()) {
            System.out.println("Error saving school: Username and school name must be provided");
            return false;
        }

        try {
            return this.myDBController.saveSchool(user, school);
        } catch (CMCException e) {
            System.out.println("Error saving school: " + e.getMessage());
            return false;
        }
    }
    
    public boolean removeSchool(String user, String school) {
        if (user == null || user.trim().isEmpty() ||
            school == null || school.trim().isEmpty()) {
            System.out.println("Error removing school: Username and school name must be provided");
            return false;
        }

        try {
            return this.myDBController.removeSchool(user, school);
        } catch (CMCException e) {
            System.out.println("Error removing saved school: " + e.getMessage());
            return false;
        }
    }
    
    public List<String> getSavedSchools(String user) {
        if (user == null || user.trim().isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, List<String>> usersToSavedSchools = this.myDBController.getUserSavedSchoolMap();
        List<String> schools = usersToSavedSchools.get(user);
        return schools != null ? schools : new ArrayList<>();
    }
    
    public boolean editUser(String username, String firstName, String lastName, 
                          String password, char type, char activated) {
        if (username == null || username.trim().isEmpty() ||
            firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            System.out.println("Error editing user: All fields must be filled");
            return false;
        }

        try {
            return this.myDBController.editUser(username, firstName, lastName, 
                                              password, type, activated);
        } catch (CMCException e) {
            System.out.println("Error editing user: " + e.getMessage());
            return false;
        }
    }
}