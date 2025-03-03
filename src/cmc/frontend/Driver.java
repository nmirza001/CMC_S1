package cmc.frontend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import cmc.CMCException;
import cmc.backend.User;

/**
 * <p>
 *  The <code>Driver</code> class is the entry point of our Choose My College (CMC) application.
 *  It's the conductor of the user interface, orchestrating the flow of menus and user interactions.
 * </p>
 * <p>
 *  This class is responsible for presenting menus to the user, capturing their input,
 *  and delegating actions to the {@link UserInteraction} class, which in turn interacts with the backend.
 *  It manages the overall user experience from login to navigating through different functionalities
 *  of the CMC system.
 * </p>
 *
 * @author Nasir Mirza (Masterfully documenting the user interface driver)
 */
public class Driver {
    /**
     *  Constants for array indices, mirroring those in backend classes.
     *  These constants help in accessing user and school data arrays in a readable and maintainable way.
     */
    private static final int USER_FIRST_NAME_INDEX = 0;
    /** Index for user last name. */
    private static final int USER_LAST_NAME_INDEX = 1;
    /** Index for username. */
    private static final int USER_USERNAME_INDEX = 2;
    /** Index for school name. */
    private static final int SCHOOL_NAME_INDEX = 0;
    /** Index for school state. */
    private static final int SCHOOL_STATE_INDEX = 1;

    /**
     *  A static instance of {@link UserInteraction} that handles all user-initiated actions.
     *  This field establishes the communication bridge to the backend logic of the CMC system.
     */
    private static UserInteraction ui = new UserInteraction();

    /**
     *  <p>
     *   Private constructor to prevent instantiation of <code>Driver</code> class.
     *  </p>
     *  <p>
     *   Since <code>Driver</code> is designed to be used statically, instantiation is not allowed,
     *   enforcing its role as a utility class.
     *  </p>
     *
     * @throws CMCException if there's an attempt to instantiate the Driver class, indicating improper use.
     */
    private Driver() throws CMCException {
        throw new CMCException("Attempt to instantiate a Driver"); // Driver class is not meant to be instantiated.
    }

    /**
     *  <p>
     *   <code>getSingleMenuEntry</code> is a helper method to safely read a single integer input from the user
     *   via the console, ensuring it falls within a specified valid range.
     *  </p>
     *  <p>
     *   It handles potential {@link NumberFormatException} if the user input is not a valid integer
     *   and checks if the integer is within the acceptable menu choice range.
     *  </p>
     *
     * @param s The {@link Scanner} object used to read user input.
     * @param minChoice The minimum valid menu choice.
     * @param maxChoice The maximum valid menu choice.
     * @return The integer representing the user's menu choice if valid, or -1 if the input is invalid or out of range.
     */
    private static int getSingleMenuEntry(Scanner s, int minChoice, int maxChoice) {
        if (s == null) return -1; // Scanner must be valid.

        String choice = s.nextLine(); // Read the entire line of user input.
        try {
            int numChoice = Integer.parseInt(choice.trim()); // Attempt to parse input as integer.
            if (numChoice < minChoice || numChoice > maxChoice) { // Check if choice is within valid range.
                throw new NumberFormatException("Invalid selection"); // Input is out of range.
            }
            return numChoice; // Valid choice.
        } catch (Exception e) {
            return -1; // Input is not a valid integer or parsing failed.
        }
    }

    /**
     *  <p>
     *   <code>getMenuOption</code> displays a list of menu options to the user and retrieves their choice.
     *  </p>
     *  <p>
     *   It presents each option with a number and then uses {@link #getSingleMenuEntry(Scanner, int, int)}
     *   to get and validate the user's numerical choice, ensuring it corresponds to a valid menu option.
     *  </p>
     *
     * @param s The {@link Scanner} object for reading user input.
     * @param options A List of Strings representing the menu options to be displayed.
     * @return The integer representing the user's valid menu choice, or -1 if input is invalid.
     */
    private static int getMenuOption(Scanner s, List<String> options) {
        if (s == null || options == null || options.isEmpty()) { // Validate input parameters.
            System.err.println("Invalid menu parameters");
            return -1; // Invalid parameters provided.
        }

        int choice = -1; // Initialize choice to invalid.
        while (choice == -1) { // Loop until a valid choice is made.
            System.out.println("\nChoose an option:");
            for (int i = 0; i < options.size(); i++) {
                System.out.println((i+1) + ": " + options.get(i)); // Display menu options with numbers.
            }
            choice = getSingleMenuEntry(s, 1, options.size()); // Get and validate user's choice.
            if (choice == -1) {
                System.out.println("Invalid option. Please try again."); // Inform user of invalid input.
            }
        }

        return choice; // Return valid menu choice.
    }

    /**
     *  <p>
     *   <code>printHeader</code> prints a formatted header to the console for each menu.
     *  </p>
     *  <p>
     *   This method enhances the user interface by providing clear visual separation and titles for different menus,
     *   making the console output more readable and user-friendly.
     *  </p>
     *
     * @param title The title of the menu to be displayed in the header.
     */
    private static void printHeader(String title) {
        if (title == null || title.length() == 0) { // Use default title if none provided.
            title = "Menu";
        }

        StringBuilder sb = new StringBuilder(); // Use StringBuilder for efficient string manipulation.
        for (int i = 0; i < title.length(); i++) {
            sb.append("-"); // Create a separator line of dashes.
        }
        String separator = sb.toString(); // Convert StringBuilder to String.

        System.out.println("\n" + separator); // Print separator line.
        System.out.println(title); // Print menu title.
        System.out.println(separator); // Print separator line again.
    }

    /**
     *  <p>
     *   <code>adminUserListMenu</code> displays a submenu for administrators to view and manage users.
     *  </p>
     *  <p>
     *   It shows a list of all users in the system and provides options to add new users, remove existing users,
     *   or go back to the main admin menu. This menu is part of the administrative functionalities.
     *  </p>
     *
     * @param s The {@link Scanner} object for reading user input.
     */
    private static void adminUserListMenu(Scanner s) {
        printHeader("Admin User List"); // Print header for admin user list menu.

        List<String[]> allUsers = ui.getAllUsers(); // Get list of all users from UserInteraction.
        if (allUsers == null || allUsers.isEmpty()) { // Check if user list is empty or null.
            System.out.println("No users found in the system.");
            return; // No users to display.
        }

        for (String[] user : allUsers) { // Iterate through each user in the list.
            System.out.printf("%s | %s %s%n", // Print user details in a formatted manner.
                user[USER_USERNAME_INDEX],
                user[USER_FIRST_NAME_INDEX],
                user[USER_LAST_NAME_INDEX]);
        }
        System.out.println();

        while (true) { // Menu loop for admin user list options.
            List<String> optionsList = new ArrayList<>(Arrays.asList("Add User", "Remove User", "Go Back")); // Define menu options.
            int choice = getMenuOption(s, optionsList); // Get user's menu choice.

            switch(choice) {
                case 1: // Add User option.
                    if (!ui.addUser(s)) { // Attempt to add a user via UserInteraction.
                        System.out.println("Failed to add new user. Username may already exist.");
                    }
                    return; // Return to previous menu after add operation.
                case 2: // Remove User option.
                    if (!ui.removeUser(s)) { // Attempt to remove a user via UserInteraction.
                        System.out.println("Failed to remove user. Username may be invalid.");
                    }
                    return; // Return to previous menu after remove operation.
                case 3: // Go Back option.
                    return; // Return to the admin menu.
                default:
                    System.err.println("Invalid option selected."); // Handle invalid option selection.
                    return;
            }
        }
    }

    /**
     *  <p>
     *   <code>adminMenu</code> presents the main menu for administrator users.
     *  </p>
     *  <p>
     *   It offers options to view the list of users, deactivate a user, or logout.
     *   This is the primary navigation menu for administrative tasks within the CMC system.
     *  </p>
     *
     * @param s The {@link Scanner} object for reading user input.
     */
    private static void adminMenu(Scanner s) {
        printHeader("Admin Menu"); // Print header for admin menu.

        while (true) { // Main admin menu loop.
            List<String> optionsList = new ArrayList<>(Arrays.asList("View List of Users", "Deactivate User", "Logout")); // Define admin menu options.
            int choice = getMenuOption(s, optionsList); // Get user's menu choice.

            switch(choice) {
                case 1: // View List of Users option.
                    adminUserListMenu(s); // Show the admin user list submenu.
                    return; // Return to admin menu after user list submenu.
                case 2: // Deactivate User option.
                    System.out.print("Enter username to deactivate: ");
                    String username = s.nextLine().trim(); // Prompt for username to deactivate.
                    if (ui.deactivateUser(username)) { // Attempt to deactivate user via UserInteraction.
                        System.out.println("User successfully deactivated.");
                    } else {
                        System.out.println("Failed to deactivate user.");
                    }
                    return; // Return to admin menu after deactivate operation.
                case 3: // Logout option.
                    ui.logout(); // Perform logout via UserInteraction.
                    return; // Return to top-level menu (login menu).
                default:
                    System.err.println("Invalid option selected."); // Handle invalid option selection.
                    return;
            }
        }
    }

    /**
     *  <p>
     *   <code>searchResultsMenu</code> displays the results of a school search to the user.
     *  </p>
     *  <p>
     *   It presents a list of schools found and offers options to save a school to their saved list
     *   or go back to the previous menu. This menu is shown after a user performs a school search.
     *  </p>
     *
     * @param s The {@link Scanner} object for reading user input.
     * @param results A List of String arrays, where each array represents a school's information.
     */
    private static void searchResultsMenu(Scanner s, List<String[]> results) {
        printHeader("Search Results"); // Print header for search results menu.

        if (results == null || results.isEmpty()) { // Check if search results are empty or null.
            System.out.println("No matching schools found.");
            return; // No schools found, return to previous menu.
        }

        for (String[] school : results) { // Iterate through each school in the search results.
            System.out.printf("%s | %s%n", // Print school details in a formatted manner.
                school[SCHOOL_NAME_INDEX],
                school[SCHOOL_STATE_INDEX]);
        }
        System.out.println();

        while (true) { // Menu loop for search results options.
            List<String> optionsList = new ArrayList<>(Arrays.asList("Save School", "Go Back")); // Define search results menu options.
            int choice = getMenuOption(s, optionsList); // Get user's menu choice.

            switch(choice) {
                case 1: // Save School option.
                    if (!ui.saveSchool(s)) { // Attempt to save school via UserInteraction.
                        System.out.println("Failed to save school. It may already be in your saved list.");
                    }
                    return; // Return to previous menu after save operation.
                case 2: // Go Back option.
                    return; // Return to the user menu.
                default:
                    System.err.println("Invalid option selected."); // Handle invalid option selection.
                    return;
            }
        }
    }

    /**
     *  <p>
     *   <code>userSavedSchoolListMenu</code> displays a list of schools saved by the user.
     *  </p>
     *  <p>
     *   It shows the user their saved schools and provides options to remove a school from the list
     *   or go back to the previous menu. This menu allows users to manage their saved schools.
     *  </p>
     *
     * @param s The {@link Scanner} object for reading user input.
     */
    private static void userSavedSchoolListMenu(Scanner s) {
        printHeader("User Saved School List"); // Print header for saved school list menu.

        List<String> schools = ui.getSavedSchools(); // Get list of saved schools from UserInteraction.
        if (schools == null || schools.isEmpty()) { // Check if saved schools list is empty or null.
            System.out.println("No saved schools found.");
            return; // No saved schools to display.
        }

        for (String school : schools) { // Iterate through each saved school.
            System.out.println(school); // Print the name of each saved school.
        }
        System.out.println();

        while (true) { // Menu loop for saved school list options.
            List<String> optionsList = new ArrayList<>(Arrays.asList("Remove School", "Go Back")); // Define saved school list menu options.
            int choice = getMenuOption(s, optionsList); // Get user's menu choice.

            switch(choice) {
                case 1: // Remove School option.
                    System.out.print("Enter school name to remove: ");
                    String schoolName = s.nextLine().trim(); // Prompt for school name to remove.
                    if (ui.removeSchool(schoolName)) { // Attempt to remove school via UserInteraction.
                        System.out.println("School successfully removed from saved list.");
                    } else {
                        System.out.println("Failed to remove school.");
                    }
                    return; // Return to previous menu after remove operation.
                case 2: // Go Back option.
                    return; // Return to the user menu.
                default:
                    System.err.println("Invalid option selected."); // Handle invalid option selection.
                    return;
            }
        }
    }

    /**
     *  <p>
     *   <code>regularUserMenu</code> is the main menu for regular (non-admin) users.
     *  </p>
     *  <p>
     *   It provides options for regular users to search schools, view their saved schools, or logout.
     *   This menu is tailored to the functionalities available to standard users of the CMC system.
     *  </p>
     *
     * @param s The {@link Scanner} object for reading user input.
     */
    private static void regularUserMenu(Scanner s) {
        printHeader("User Menu"); // Print header for regular user menu.

        while (true) { // Main regular user menu loop.
            List<String> optionsList = new ArrayList<>(Arrays.asList("Search Schools", "View Saved Schools", "Logout")); // Define regular user menu options.
            int choice = getMenuOption(s, optionsList); // Get user's menu choice.

            switch(choice) {
                case 1: // Search Schools option.
                    List<String[]> searchResult = ui.search(s); // Perform school search via UserInteraction.
                    searchResultsMenu(s, searchResult); // Show search results submenu.
                    return; // Return to user menu after search actions.
                case 2: // View Saved Schools option.
                    userSavedSchoolListMenu(s); // Show saved school list submenu.
                    return; // Return to user menu after saved schools actions.
                case 3: // Logout option.
                    ui.logout(); // Perform logout via UserInteraction.
                    return; // Return to top-level menu (login menu).
                default:
                    System.err.println("Invalid option selected."); // Handle invalid option selection.
                    return;
            }
        }
    }

    /**
     *  <p>
     *   <code>topMenu</code> is the initial menu presented to users when the CMC application starts.
     *  </p>
     *  <p>
     *   It prompts the user to login by asking for their username and password.
     *   Upon successful login, the control is passed to either the {@link #adminMenu(Scanner)} or
     *   {@link #regularUserMenu(Scanner)}, depending on the user's role.
     *  </p>
     *
     * @param s The {@link Scanner} object for reading user input.
     */
    private static void topMenu(Scanner s) {
        printHeader("Welcome to Choose My College (CMC)!"); // Print welcome header.
        System.out.println("Please log in.");

        String username = ""; // Initialize username input variable.
        while (username.trim().length() == 0) { // Loop until a non-empty username is entered.
            System.out.print("Username: ");
            username = s.nextLine().trim(); // Prompt for and read username input.
        }

        System.out.print("Password: ");
        String password = s.nextLine().trim(); // Prompt for and read password input.

        boolean success = ui.login(username, password); // Attempt login via UserInteraction.
        if (!success) {
            System.out.println("Login failed. Please try again."); // Inform user about login failure.
        }
    }

    /**
     *  <p>
     *   <code>main</code> is the primary method that starts the CMC application.
     *  </p>
     *  <p>
     *   It initializes the application, presents the top-level login menu, and then navigates
     *   users through different menus based on their role and actions. The application continues
     *   to run in a loop, presenting menus until a fatal error occurs or the application is terminated.
     *  </p>
     *
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in); // Initialize Scanner for user input.
        System.out.println("CMC System Starting..."); // Startup message.

        try {
            while (true) { // Main application loop.
                User currentUser = ui.getLoggedInUser(); // Get currently logged-in user.
                if (currentUser == null) { // No user logged in.
                    topMenu(s); // Show the top-level login menu.
                } else if (currentUser.isAdmin()) { // Admin user logged in.
                    adminMenu(s); // Show the admin menu.
                } else { // Regular user logged in.
                    regularUserMenu(s); // Show the regular user menu.
                }
            }
        } catch (Exception e) {
            System.err.println("Fatal error encountered: " + e.getMessage()); // Print error message for fatal exceptions.
            e.printStackTrace(); // Print stack trace for debugging.
        } finally {
            s.close(); // Ensure Scanner is closed to prevent resource leaks.
        }
    }
}