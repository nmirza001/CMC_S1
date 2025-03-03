package cmc.frontend;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import cmc.backend.SystemController;
import cmc.backend.User;

public class UserInteraction {
    private User loggedInUser;
    private SystemController theSystemController;
    
    public UserInteraction() {
        this.theSystemController = new SystemController();
        this.loggedInUser = null;
    }

    public boolean login(String username, String password) {
        User result = this.theSystemController.login(username, password);
        if (result != null) {
            System.out.println("Login successful!");
            this.loggedInUser = result;
            return true;
        } else {
            if (this.theSystemController.userExists(username)) {
                if (!this.theSystemController.isUserActive(username)) {
                    System.out.println("Login failed! Account is deactivated.");
                } else {
                    System.out.println("Login failed! Incorrect password.");
                }
            } else {
                System.out.println("Login failed! User not found.");
            }
            this.loggedInUser = null;
            return false;
        }
    }
    
    public boolean logout() {
        if (this.loggedInUser == null) {
            return false;
        }
        this.loggedInUser = null;
        return true;
    }
    
    public List<String[]> getAllUsers() {
        if (!isAdmin()) {
            System.out.println("Error: Admin privileges required");
            return new ArrayList<>();
        }
        return this.theSystemController.getAllUsers();
    }
    
    public boolean addUser(Scanner s) {
        if (!isAdmin()) {
            System.out.println("Error: Admin privileges required");
            return false;
        }
        
        System.out.print("Username: ");
        String username = s.nextLine().trim();
        System.out.print("Password: ");
        String password = s.nextLine().trim();
        System.out.print("First Name: ");
        String firstName = s.nextLine().trim();
        System.out.print("Last Name: ");
        String lastName = s.nextLine().trim();
        System.out.print("Admin? (Y or N): ");
        boolean isAdmin = s.nextLine().trim().equalsIgnoreCase("y");
        
        return this.theSystemController.addUser(username, password, firstName, lastName, isAdmin);
    }
    
    public boolean removeUser(Scanner s) {
        if (!isAdmin()) {
            System.out.println("Error: Admin privileges required");
            return false;
        }
        
        System.out.print("Username: ");
        String username = s.nextLine().trim();
        return this.theSystemController.removeUser(username);
    }

    public boolean deactivateUser(String username) {
        if (!isAdmin()) {
            System.out.println("Error: Admin privileges required");
            return false;
        }
        
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Error: Username cannot be empty");
            return false;
        }
        
        return this.theSystemController.deactivateUser(username.trim());
    }
    
    public List<String[]> search(Scanner s) {
        System.out.print("State (leave blank to see all schools): ");
        String state = s.nextLine().trim();
        return this.theSystemController.search(state);
    }
    
    public boolean saveSchool(Scanner s) {
        if (this.loggedInUser == null) {
            System.out.println("Error: Must be logged in to save schools");
            return false;
        }
        
        System.out.print("School Name: ");
        String schoolName = s.nextLine().trim();
        return this.theSystemController.saveSchool(this.loggedInUser.getUsername(), schoolName);
    }
    
    public boolean removeSchool(String schoolName) {
        if (this.loggedInUser == null) {
            System.out.println("Error: Must be logged in to remove schools");
            return false;
        }
        
        if (schoolName == null || schoolName.trim().isEmpty()) {
            System.out.println("Error: School name cannot be empty");
            return false;
        }
        
        return this.theSystemController.removeSchool(
            this.loggedInUser.getUsername(), schoolName.trim());
    }
    
    public List<String> getSavedSchools() {
        if (this.loggedInUser == null) {
            System.out.println("Error: Must be logged in to view saved schools");
            return new ArrayList<>();
        }
        
        List<String> schools = this.theSystemController.getSavedSchools(
            this.loggedInUser.getUsername());
        return schools != null ? schools : new ArrayList<>();
    }

    public User getLoggedInUser() {
        return this.loggedInUser;
    }

    private boolean isAdmin() {
        return this.loggedInUser != null && this.loggedInUser.isAdmin();
    }
}