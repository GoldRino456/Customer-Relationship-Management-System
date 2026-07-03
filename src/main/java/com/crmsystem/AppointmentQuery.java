package com.crmsystem;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

/** Handles all Appointment Queries
 *
 * <p>An abstract class used to retrieve and manage appointments and most other data involved in their
 *  creation, manipulation, and removal from the database (including contacts).</p>
 *
 */
public abstract class AppointmentQuery {

    /** Queries the Database to retrieve the full Appointment List
     *
     * <p>Selects all appointments from the appointment table and returns them as a ObservableList of
     * Appointment objects.</p>
     *
     */
    public static ObservableList<Appointment> selectAll() throws SQLException {
        ObservableList<Appointment> appointments = FXCollections.observableList(new ArrayList<>());

        //SQL Query
        String sql = "SELECT * FROM APPOINTMENTS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        //Pull data from Query
        while(rs.next()) {
            //Create new Appointment object, add to list
            Appointment temp = new Appointment(
                    rs.getInt("Appointment_ID"),
                    rs.getString("Title"),
                    rs.getString("Description"),
                    rs.getString("Location"),
                    rs.getString("Type"),
                    rs.getTimestamp("Start"),
                    rs.getTimestamp("End"),
                    rs.getInt("Customer_ID"),
                    rs.getInt("User_ID"),
                    rs.getInt("Contact_ID"),
                    getContactNameByID(rs.getInt("Contact_ID"))
            );

            appointments.add(temp);
        }

        return appointments;
    }

    /** Queries the Database to retrieve the contact ID by searching for a Contact Name
     *
     * <p>Selects all contacts that match the given name and returns the contact ID. If no
     * ID is found, returns -1.</p>
     *
     * @param contact name of contact
     * @return id of contact (-1 if none found)
     */
    public static int getContactID(String contact) throws SQLException {
        //SQL Query
        String sql = "SELECT * FROM CONTACTS WHERE Contact_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, contact);

        ResultSet rs = ps.executeQuery();

        //Did it find any results?
        if(rs.next())
        {
            return rs.getInt("Contact_ID");
        }

        return -1;
    }

    /** Queries the Database to retrieve the contact name by searching for a Contact ID
     *
     * <p>Selects the contact that matches the given ID and returns the contact name. If nothing is found,
     * returns null.</p>
     *
     * @param id Contact ID
     * @return Name of Contact, null if not found
     */
    public static String getContactNameByID(int id) throws SQLException {
        //SQL Query
        String sql = "SELECT * FROM CONTACTS WHERE Contact_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        //Did it find any results?
        if(rs.next())
        {
            return rs.getString("Contact_Name");
        }

        return null; //Nothing found. Return NULL.
    }

    /** Queries the Database to add a new appointment record
     *
     * <p>Take an appointment object and adds that to the Appointment Table as a new record.</p>
     *
     * @param appointment New Appointment
     * @return Number of rows affected
     */
    public static int createNewAppointment(Appointment appointment) throws SQLException {
        //SQL Query
        String sql = "INSERT INTO APPOINTMENTS (Title, Description, Location, Type, Start, End,"
                +" Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID)"
                +" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, appointment.getTitle()); //Title
        ps.setString(2, appointment.getDescription()); //Description
        ps.setString(3, appointment.getLocation()); //Location
        ps.setString(4, appointment.getType()); //Type
        ps.setTimestamp(5, Timestamp.valueOf(appointment.getStartTime())); //Start Time
        ps.setTimestamp(6, Timestamp.valueOf(appointment.getEndTime())); //End Time
        ps.setTimestamp(7, Timestamp.from(Instant.now())); //Create Date
        ps.setString(8, UserQuery.getUsername()); //Created By
        ps.setTimestamp(9, Timestamp.from(Instant.now())); //Update Date
        ps.setString(10, UserQuery.getUsername()); //Updated By
        ps.setInt(11, appointment.getCustomerID()); //CustomerID
        ps.setInt(12, appointment.getUserID()); //UserID
        ps.setInt(13, appointment.getContactID()); //ContactID

        return ps.executeUpdate();
    }

    /** Queries the Database to update an Existing appointment record
     *
     * <p>Takes an appointment object and updates the corresponding appointment in the Appointments Table.</p>
     *
     * @param appointment Appointment object
     * @return Number of rows affected
     */
    public static int updateExistingAppointment(Appointment appointment) throws SQLException {
        //SQL Query
        String sql = "UPDATE APPOINTMENTS \n"
                +"SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?,"
                +" Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? \n"
                +"WHERE Appointment_ID = ?";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, appointment.getTitle()); //Title
        ps.setString(2, appointment.getDescription()); //Description
        ps.setString(3, appointment.getLocation()); //Location
        ps.setString(4, appointment.getType()); //Type
        ps.setTimestamp(5, Timestamp.valueOf(appointment.getStartTime())); //Start Time
        ps.setTimestamp(6, Timestamp.valueOf(appointment.getEndTime())); //End Time
        ps.setTimestamp(7, Timestamp.from(Instant.now())); //Update Date
        ps.setString(8, UserQuery.getUsername()); //Updated By
        ps.setInt(9, appointment.getCustomerID()); //CustomerID
        ps.setInt(10, appointment.getUserID()); //UserID
        ps.setInt(11, appointment.getContactID()); //ContactID
        ps.setInt(12, appointment.getAppointmentID()); //AppointmentID

        return ps.executeUpdate();
    }

    /** Queries the Database to retrieve an appointment by ID
     *
     * <p>Searches the full list of appointments by ID. Returns the appointment containing that ID, otherwise
     *  returns null.</p>
     *
     * @param id AppointmentID
     * @return Appointment
     */
    public static Appointment getAppointmentByID(int id) throws SQLException {
        //SQL Query
        String sql = "SELECT * FROM APPOINTMENTS WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        //Create new Appointment object, return that object
        if (rs.next()) {
            return new Appointment(
                    rs.getInt("Appointment_ID"),
                    rs.getString("Title"),
                    rs.getString("Description"),
                    rs.getString("Location"),
                    rs.getString("Type"),
                    rs.getTimestamp("Start"),
                    rs.getTimestamp("End"),
                    rs.getInt("Customer_ID"),
                    rs.getInt("User_ID"),
                    rs.getInt("Contact_ID"),
                    getContactNameByID(rs.getInt("Contact_ID"))
            );
        }

        return null; //If it gets here, Query returned no results. Appointment will be set to null.
    }

    /** Queries the Database to retrieve all appointments involving a specific customer
     *
     * <p>Searches the full list of appointments for all that contain a specific customer's ID. Returns
     *  the list of appointments.</p>
     *
     * @param custID CustomerID
     * @return ObservableList of Appointments
     */
    public static ObservableList<Appointment> getAppointmentsOfCustomerID(int custID) throws SQLException {
        ObservableList<Appointment> appointments = FXCollections.observableList(new ArrayList<>());

        //SQL Query
        String sql = "SELECT * FROM APPOINTMENTS\n"
                + "WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, custID);
        ResultSet rs = ps.executeQuery();

        //Pull data from Query
        while(rs.next()) {
            //Create new Appointment object, add to list
            Appointment temp = new Appointment(
                    rs.getInt("Appointment_ID"),
                    rs.getString("Title"),
                    rs.getString("Description"),
                    rs.getString("Location"),
                    rs.getString("Type"),
                    rs.getTimestamp("Start"),
                    rs.getTimestamp("End"),
                    rs.getInt("Customer_ID"),
                    rs.getInt("User_ID"),
                    rs.getInt("Contact_ID"),
                    getContactNameByID(rs.getInt("Contact_ID"))
            );

            appointments.add(temp);
        }

        return appointments;
    }

    /** Queries the Database to retrieve all appointments involving a specific user
     *
     * <p>Searches the full list of appointments for all that contain a specific user's ID. Returns
     *  the list of appointments.</p>
     *
     * @param userID UserID
     * @return ObservableList of Appointments
     */
    public static ObservableList<Appointment> getAppointmentsOfUserID(int userID) throws SQLException {
        ObservableList<Appointment> appointments = FXCollections.observableList(new ArrayList<>());

        //SQL Query
        String sql = "SELECT * FROM APPOINTMENTS\n"
                + "WHERE User_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, userID);
        ResultSet rs = ps.executeQuery();

        //Pull data from Query
        while(rs.next()) {
            //Create new Appointment object, add to list
            Appointment temp = new Appointment(
                    rs.getInt("Appointment_ID"),
                    rs.getString("Title"),
                    rs.getString("Description"),
                    rs.getString("Location"),
                    rs.getString("Type"),
                    rs.getTimestamp("Start"),
                    rs.getTimestamp("End"),
                    rs.getInt("Customer_ID"),
                    rs.getInt("User_ID"),
                    rs.getInt("Contact_ID"),
                    getContactNameByID(rs.getInt("Contact_ID"))
            );

            appointments.add(temp);
        }

        return appointments;
    }

    /** Queries the Database to remove a specific appointment record
     *
     * <p>Searches the full list of appointments and removes the specified appointment record.</p>
     *
     * @param appointmentID AppointmentID
     * @return Number of Rows Affected
     */
    public static int deleteExistingAppointment(int appointmentID) throws SQLException {
        //SQL Query
        String sql = "DELETE FROM APPOINTMENTS WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, appointmentID);

        return ps.executeUpdate();
    }

    /** Queries the Database to retrieve the full Contacts List
     *
     * <p>Selects all contacts from the contact table and returns them as a ObservableList of
     * Strings.</p>
     *
     * @return ObservableList of Strings
     *
     */
    public static ObservableList<String> selectAllContacts() throws SQLException {
        ObservableList<String> contacts = FXCollections.observableList(new ArrayList<>());

        //SQL Query
        String sql = "SELECT * FROM CONTACTS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        //Pull data from Query
        while(rs.next()) {
            contacts.add(rs.getString("Contact_Name"));
        }

        return contacts;
    }

    /** Queries the Database to retrieve all Appointment Types
     *
     * <p>Selects all appointments and returns a list of all unique appointment types as a list of Strings.</p>
     *
     * @return ObservableList of Strings
     *
     */
    public static ObservableList<String> selectAllTypes() throws SQLException {
        ObservableList<String> types = FXCollections.observableList(new ArrayList<>());

        //SQL Query
        String sql = "SELECT type FROM APPOINTMENTS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        //Pull data from Query
        while(rs.next()) {

            if (!types.contains(rs.getString("type")))
                types.add(rs.getString("type"));

        }

        return types;
    }

}
