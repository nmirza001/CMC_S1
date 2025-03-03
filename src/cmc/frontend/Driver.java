package cmc.frontend;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import cmc.CMCException;
import cmc.backend.User;

/**
 * The Driver class provides the main command-line interface for the CMC system.
 * It handles user interaction through menus and delegates operations to UserInteraction.
 */
public class Driver {
    // Constants for array indices (matching other classes)
    private static final int USER_FIRST_NAME_INDEX = 0;
    private static final int USER_LAST_NAME_INDEX = 1;
    private static final int USER_USERNAME_INDEX = 2;
    private static final int SCHOOL_NAME_INDEX = 0;
    private static final int SCHOOL_STATE_INDEX = 1;

    private static UserInteraction ui = new UserInteraction();

    private Driver() throws CMCException {
        throw new CMCException("Attempt to instantiate a Driver");
    }

    private static int getSingleMenuEntry(Scanner s, int minChoice, int maxChoice) {
        if (s == null) return -1;

        String choice = s.nextLine();
        try {
            int numChoice = Integer.parseInt(choice.trim());
            if (numChoice < minChoice || numChoice > maxChoice) {
                throw new NumberFormatException("Invalid selection");
            }
            return numChoice;
        } catch (Exception e) {
            return -1;
        }
    }

    private static int getMenuOption(Scanner s, List<String> options) {
        if (s == null || options == null || options.isEmpty()) {
            System.err.println("Invalid menu parameters");
            return -1;
        }

        int choice = -1;
        while (choice == -1) {
            System.out.println("\nChoose an option:");
            for (int i = 0; i < options.size(); i++) {
                System.out.println((i+1) + ": " + options.get(i));
            }
            choice = getSingleMenuEntry(s, 1, options.size());
            if (choice == -1) {
                System.out.println("Invalid option. Please try again.");
            }
        }

        return choice;
    }

    private static void printHeader(String title) {
        if (title == null || title.isEmpty()) {
            title = "Menu";
        }

        // Fix for String.repeat() for older Java versions
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < title.length(); i++) {
            sb.append("-");
        }
        String separator = sb.toString();


        System.out.println("\n" + separator);
        System.out.println(title);
        System.out.println(separator);
    }

    private static void adminUserListMenu(Scanner s) {
        printHeader("Admin User List");

        List<String[]> allUsers = ui.getAllUsers();
        if (allUsers == null || allUsers.isEmpty()) {
            System.out.println("No users found in the system.");
            return;
        }

        for (String[] user : allUsers) {
            System.out.printf("%s | %s %s%n",
                user[USER_USERNAME_INDEX],
                user[USER_FIRST_NAME_INDEX],
                user[USER_LAST_NAME_INDEX]);
        }
        System.out.println();

        while (true) {
            int choice = getMenuOption(s, Arrays.asList("Add User", "Remove User", "Go Back"));

            switch(choice) {
                case 1:
                    if (!ui.addUser(s)) {
                        System.out.println("Failed to add new user. Username may already exist.");
                    }
                    return;
                case 2:
                    // Assuming removeUser and addUser are correctly implemented in UserInteraction
                    if (!ui.removeUser(s)) {
                        System.out.println("Failed to remove user. Username may be invalid.");
                    }
                    return;
                case 3:
                    return;
                default:
                    System.err.println("Invalid option selected.");
                    return;
            }
        }
    }

    private static void adminMenu(Scanner s) {
        printHeader("Admin Menu");

        while (true) {
            int choice = getMenuOption(s, Arrays.asList(
                "View List of Users",
                "Deactivate User", // Assuming deactivateUser is implemented in UserInteraction
                "Logout"
            ));

            switch(choice) {
                case 1:
                    adminUserListMenu(s);
                    return;
                case 2:
                    System.out.print("Enter username to deactivate: ");
                    String username = s.nextLine().trim();
                    // Assuming deactivateUser is implemented in UserInteraction
                    if (ui.deactivateUser(username)) {
                        System.out.println("User successfully deactivated.");
                    } else {
                        System.out.println("Failed to deactivate user.");
                    }
                    return;
                case 3:
                    ui.logout();
                    return;
                default:
                    System.err.println("Invalid option selected.");
                    return;
            }
        }
    }

    private static void searchResultsMenu(Scanner s, List<String[]> results) {
        printHeader("Search Results");

        if (results == null || results.isEmpty()) {
            System.out.println("No matching schools found.");
            return;
        }

        for (String[] school : results) {
            System.out.printf("%s | %s%n",
                school[SCHOOL_NAME_INDEX],
                school[SCHOOL_STATE_INDEX]);
        }
        System.out.println();

        while (true) {
            int choice = getMenuOption(s, Arrays.asList("Save School", "Go Back"));

            switch(choice) {
                case 1:
                    if (!ui.saveSchool(s)) {
                        System.out.println("Failed to save school. It may already be in your saved list.");
                    }
                    return;
                case 2:
                    return;
                default:
                    System.err.println("Invalid option selected.");
                    return;
            }
        }
    }

    private static void userSavedSchoolListMenu(Scanner s) {
        printHeader("User Saved School List");

        List<String> schools = ui.getSavedSchools();
        if (schools == null || schools.isEmpty()) {
            System.out.println("No saved schools found.");
            return;
        }

        for (String school : schools) {
            System.out.println(school);
        }
        System.out.println();

        while (true) {
            int choice = getMenuOption(s, Arrays.asList("Remove School", "Go Back"));

            switch(choice) {
                case 1:
                    System.out.print("Enter school name to remove: ");
                    String schoolName = s.nextLine().trim();
                    // Assuming removeSchool is implemented in UserInteraction
                    if (ui.removeSchool(schoolName)) {
                        System.out.println("School successfully removed from saved list.");
                    } else {
                        System.out.println("Failed to remove school.");
                    }
                    return;
                case 2:
                    return;
                default:
                    System.err.println("Invalid option selected.");
                    return;
            }
        }
    }

    private static void regularUserMenu(Scanner s) {
        printHeader("User Menu");

        while (true) {
            int choice = getMenuOption(s, Arrays.asList(
                "Search Schools",
                "View Saved Schools",
                "Logout"
            ));

            switch(choice) {
                case 1:
                    List<String[]> searchResult = ui.search(s);
                    searchResultsMenu(s, searchResult);
                    return;
                case 2:
                    userSavedSchoolListMenu(s);
                    return;
                case 3:
                    ui.logout();
                    return;
                default:
                    System.err.println("Invalid option selected.");
                    return;
            }
        }
    }

    private static void topMenu(Scanner s) {
        printHeader("Welcome to Choose My College (CMC)!");
        System.out.println("Please log in.");

        String username = "";
        while (username.trim().isEmpty()) {
            System.out.print("Username: ");
            username = s.nextLine().trim();
        }

        System.out.print("Password: ");
        String password = s.nextLine().trim();

        boolean success = ui.login(username, password);
        if (!success) {
            System.out.println("Login failed. Please try again.");
        }
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("CMC System Starting...");

        try {
            while (true) {
                User currentUser = ui.getLoggedInUser();
                if (currentUser == null) {
                    topMenu(s);
                } else if (currentUser.isAdmin()) {
                    adminMenu(s);
                } else {
                    regularUserMenu(s);
                }
            }
        } catch (Exception e) {
            System.err.println("Fatal error encountered: " + e.getMessage());
            e.printStackTrace();
        } finally {
            s.close();
        }
    }
}