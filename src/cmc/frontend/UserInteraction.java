package cmc.frontend;

import java.util.ArrayList; // Explicitly importing ArrayList
import java.util.List;
import java.util.Scanner;

import cmc.backend.SystemController;
import cmc.backend.User;

/**

Alright, listen up! This UserInteraction class is like the middleman
between the user (you!) and the system behind the scenes. It takes what you
want to do, talks to the SystemController to make it happen, and then gives
you feedback.  It's all about making the system user-friendly.
*/
public class UserInteraction {
private User loggedInUser; // Keeps track of who's logged in right now
private SystemController theSystemController; // We need this to talk to the backend logic
// Constructor - when we create a UserInteraction object, it sets up the SystemController.
public UserInteraction() {
this.theSystemController = new SystemController(); // Let's get our system brain ready
this.loggedInUser = null; // No user logged in when we start up
}
/**
Handles the login process. Takes a username and password, checks them,
and if they're good, sets the loggedInUser.
*/
public boolean login(String username, String password) {
User result = this.theSystemController.login(username, password); // Try to login via SystemController
if (result != null) { // Login was successful!
System.out.println("Login successful!");
this.loggedInUser = result; // Remember who's logged in
return true; // Yay, login success!
} else { // Login failed :(
if (this.theSystemController.userExists(username)) { // User exists, but login failed
if (!this.theSystemController.isUserActive(username)) { // Account deactivated?
System.out.println("Login failed! Account is deactivated.");
} else { // Wrong password?
System.out.println("Login failed! Incorrect password.");
}
} else { // User doesn't even exist
System.out.println("Login failed! User not found.");
}
this.loggedInUser = null; // Clear loggedInUser as login failed
return false; // Nope, login failed.
}
}
/**
Logs the current user out. Just clears the loggedInUser variable.
*/
public boolean logout() {
if (this.loggedInUser == null) { // Nobody logged in? Can't log out.
return false; // Logout failed (but maybe nobody was logged in anyway?)
}
this.loggedInUser = null; // Forget who's logged in - logout successful!
return true; // Logout success!
}
/**
Gets a list of all users in the system. Admin-only function.
*/
public List getAllUsers() { // Raw List type for older Java
if (!isAdmin()) { // Only admins can do this
System.out.println("Error: Admin privileges required");
return new ArrayList(); // Empty list if not admin
}
return this.theSystemController.getAllUsers(); // Get the list from SystemController
}
/**
Adds a new user to the system. Admin-only. Gets user details from the Scanner.
*/
public boolean addUser(Scanner s) {
if (!isAdmin()) { // Admin check again
System.out.println("Error: Admin privileges required");
return false; // Can't add user if not admin
}
System.out.print("Username: ");
String username = s.nextLine().trim(); // Get username from user input
System.out.print("Password: ");
String password = s.nextLine().trim(); // Get password
System.out.print("First Name: ");
String firstName = s.nextLine().trim(); // Get first name
System.out.print("Last Name: ");
String lastName = s.nextLine().trim(); // Get last name
System.out.print("Admin? (Y or N): ");
boolean isAdmin = s.nextLine().trim().equalsIgnoreCase("y"); // Is this user an admin?
return this.theSystemController.addUser(username, password, firstName, lastName, isAdmin); // Add user via SystemController
}
/**
Removes a user from the system. Admin-only. Gets username from Scanner.
*/
public boolean removeUser(Scanner s) {
if (!isAdmin()) { // Admin check once more
System.out.println("Error: Admin privileges required");
return false; // Not admin, can't remove user
}
System.out.print("Username: ");
String username = s.nextLine().trim(); // Get username to remove
return this.theSystemController.removeUser(username); // Remove user via SystemController
}
/**
Deactivates a user. Admin-only. Takes username as input.
*/
public boolean deactivateUser(String username) {
if (!isAdmin()) { // Still need to be admin for deactivation
System.out.println("Error: Admin privileges required");
return false; // Not admin, can't deactivate
}
if (username == null || username.trim().length() == 0) { // Username can't be empty
System.out.println("Error: Username cannot be empty");
return false; // Can't deactivate without a username
}
return this.theSystemController.deactivateUser(username.trim()); // Deactivate via SystemController
}
/**
Searches for schools based on state. Gets state from Scanner.
If state is blank, it searches all states (gets all schools).
*/
public List search(Scanner s) { // Raw List type for older Java
System.out.print("State (leave blank to see all schools): ");
String state = s.nextLine().trim(); // Get state input
return this.theSystemController.search(state); // Search via SystemController
}
/**
Saves a school to the current logged-in user's saved school list.
Gets school name from Scanner.
*/
public boolean saveSchool(Scanner s) {
if (this.loggedInUser == null) { // Must be logged in to save schools
System.out.println("Error: Must be logged in to save schools");
return false; // Can't save if not logged in
}
System.out.print("School Name: ");
String schoolName = s.nextLine().trim(); // Get school name to save
return this.theSystemController.saveSchool(this.loggedInUser.getUsername(), schoolName); // Save via SystemController
}
/**
Removes a school from the current user's saved school list.
Takes school name as input.
*/
public boolean removeSchool(String schoolName) {
if (this.loggedInUser == null) { // Need to be logged in to remove saved schools
System.out.println("Error: Must be logged in to remove schools");
return false; // Can't remove if not logged in
}
if (schoolName == null || schoolName.trim().length() == 0) { // School name can't be empty
System.out.println("Error: School name cannot be empty");
return false; // Need a school name to remove
}
return this.theSystemController.removeSchool(
this.loggedInUser.getUsername(), schoolName.trim()); // Remove via SystemController
}
/**
Gets a list of schools saved by the current logged-in user.
*/
public List getSavedSchools() { // Raw List type for older Java
if (this.loggedInUser == null) { // Need to be logged in to see saved schools
System.out.println("Error: Must be logged in to view saved schools");
return new ArrayList(); // Empty list if not logged in
}
List schools = this.theSystemController.getSavedSchools( // Get saved schools from SystemController
this.loggedInUser.getUsername());
return schools != null ? schools : new ArrayList(); // Return the list or empty list if none saved
}
/**
Gets the currently logged-in User object. Can be null if no user is logged in.
*/
public User getLoggedInUser() {
return this.loggedInUser; // Just return the current loggedInUser
}
/**
Helper method to check if there's a logged-in user and if they are an admin.
*/
private boolean isAdmin() {
return this.loggedInUser != null && this.loggedInUser.isAdmin(); // Check if loggedInUser is not null AND is admin
}
}
