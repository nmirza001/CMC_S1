package cmc.backend;

/**
 * Represents a user in the CMC system.
 * A user can be either an admin (type 'a') or regular user (type 'u')
 * and can be either activated ('Y') or deactivated ('N').
 */
public class User {
    // Constants for user types and activation status
    public static final char ADMIN_TYPE = 'a';
    public static final char USER_TYPE = 'u';
    public static final char ACTIVATED = 'Y';
    public static final char DEACTIVATED = 'N';
    
    // Changed to private with getters/setters for better encapsulation
    private String username;
    private String password;
    private char type;     // u or a
    private String firstName;
    private String lastName;
    private char activated; // Y or N

    /**
     * Constructs a new User with the given parameters.
     * New users are always activated by default.
     * 
     * @param username the unique username for this user
     * @param password the user's password
     * @param type the user type (must be 'a' for admin or 'u' for regular user)
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @throws IllegalArgumentException if type is invalid or any parameter is null/empty
     */
    public User(String username, String password, char type, String firstName, String lastName) {
        // Validate input parameters
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (type != ADMIN_TYPE && type != USER_TYPE) {
            throw new IllegalArgumentException("Invalid user type: must be 'a' or 'u'");
        }

        this.username = username.trim();
        this.password = password.trim();
        this.type = type;
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.activated = ACTIVATED; // users always start activated
    }

    /**
     * @return the username (cannot be modified as it's the unique identifier)
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     * @throws IllegalArgumentException if password is null or empty
     */
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = password.trim();
    }

    /**
     * @return the user type ('a' for admin, 'u' for regular user)
     */
    public char getType() {
        return type;
    }

    /**
     * @param type the type to set ('a' for admin, 'u' for regular user)
     * @throws IllegalArgumentException if type is invalid
     */
    public void setType(char type) {
        if (type != ADMIN_TYPE && type != USER_TYPE) {
            throw new IllegalArgumentException("Invalid user type: must be 'a' or 'u'");
        }
        this.type = type;
    }

    /**
     * @return true if the user is an admin ('a' type), false otherwise
     */
    public boolean isAdmin() {
        return type == ADMIN_TYPE;
    }

    /**
     * @return the user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the first name to set
     * @throws IllegalArgumentException if firstName is null or empty
     */
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        this.firstName = firstName.trim();
    }

    /**
     * @return the user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the last name to set
     * @throws IllegalArgumentException if lastName is null or empty
     */
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        this.lastName = lastName.trim();
    }

    /**
     * @return the activation status ('Y' for activated, 'N' for deactivated)
     */
    public char getActivated() {
        return activated;
    }

    /**
     * @param activated the activation status to set ('Y' for activated, 'N' for deactivated)
     * @throws IllegalArgumentException if activation status is invalid
     */
    public void setActivated(char activated) {
        if (activated != ACTIVATED && activated != DEACTIVATED) {
            throw new IllegalArgumentException("Invalid activation status: must be 'Y' or 'N'");
        }
        this.activated = activated;
    }

    /**
     * @return true if the user is currently activated, false otherwise
     */
    public boolean isActivated() {
        return activated == ACTIVATED;
    }

    /**
     * @return string representation of the User object (excluding sensitive information)
     */
    @Override
    public String toString() {
        return String.format("User[username=%s, type=%c, firstName=%s, lastName=%s, activated=%c]",
                           username, type, firstName, lastName, activated);
    }
}