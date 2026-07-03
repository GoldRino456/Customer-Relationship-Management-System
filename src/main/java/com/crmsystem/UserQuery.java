package com.crmsystem;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/** Handles all User Queries
 *
 * <p>An abstract class used to retrieve and manage user data including fetching a username, password,
 *  and retrieving the full list of usernames. Username and password validation also occurs here.</p>
 *
 */
public abstract class UserQuery {

    /** Stores the ID of the Currently Signed in User */
    private static int signedInUserID;

    /** Stores the Username of the Currently Signed in User */
    private static String username;

    /** Determines if a given Username exists in the Database
     *
     * <p>Checks the database for the given string, username. If the username does not appear in the User Table,
     * returns false. If the Username does exist, returns true.</p>
     *
     * @param username Username to search for
     * @return True if username is found, False if not found.
     *
     */
    public static boolean findUsername(String username) throws SQLException {

        //SQL Query
        String sql = "SELECT * FROM USERS WHERE User_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();

        //Did it find any results?
        if(rs.next())
        {
            signedInUserID = rs.getInt("User_ID");
            return true;
        }

        return false;
    }

    /** Determines if a given Password exists for the selected Username
     *
     * <p>Must run after findUsername(). Checks the database to see if the given password matches the
     * password linked to the previously entered username.</p>
     *
     * @param password Password to check
     * @return True if password is valid, False if not valid.
     *
     */
    public static boolean findPassword(String password) throws SQLException {

        //SQL Query
        String sql = "SELECT * FROM USERS WHERE User_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, signedInUserID);

        ResultSet rs = ps.executeQuery();

        if(rs.next() && rs.getString("Password").equals(password))
        {
            username = rs.getString("User_Name"); //Only set username variable once login in validated
            return true;
        }

        signedInUserID = -1; //Failed Login, reset signedInUserID
        return false;
    }

    /** Returns the username of the signed in user
     *
     * <p>Returns the username of the currently signed in user.</p>
     *
     * @return Username
     *
     */
    public static String getUsername() {
        return username;
    }

    /** Searches the Database for a username, returns the ID
     *
     * <p>Searches the database for a user with a given name. Returns an integer of that
     * user's id. Returns -1 if not found.</p>
     *
     * @param username Username to search for
     * @return ID of the user
     *
     */
    public static int getUserID(String username) throws SQLException {
        //SQL Query
        String sql = "SELECT * FROM USERS WHERE User_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();

        //Did it find any results?
        if(rs.next())
        {
            return rs.getInt("User_ID");
        }

        return -1;
    }

    /** Searches for a username by a given User ID
     *
     * <p>Queries the database to retrieve a username with a given user ID. If no username is found matching
     *  the user id, Null is returned instead.</p>
     *
     * @param id User ID to search for
     * @return The Username of User with matching User ID, Null if no match is found
     *
     */
    public static String findUsernameByID(int id) throws SQLException {
        //SQL Query
        String sql = "SELECT * FROM USERS WHERE User_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        //Did it find any results?
        if(rs.next())
        {
            return rs.getString("User_Name");
        }

        return null; //Nothing found. Returning Null value
    }

    /** Retrieves a full list of all users in the database
     *
     * <p>Queries the database and returns a list of all usernames in the database.</p>
     *
     * @return ObservableList of Strings containing all Usernames
     *
     */
    public static ObservableList<String> selectAll() throws SQLException {
        ObservableList<String> users = FXCollections.observableList(new ArrayList<>());

        //SQL Query
        String sql = "SELECT * FROM USERS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        //Pull data from Query
        while(rs.next()) {
            users.add(rs.getString("User_Name"));
        }

        return users;
    }
}
