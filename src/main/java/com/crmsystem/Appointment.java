package com.crmsystem;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/** Stores data of Appointment elements in the Database
 *
 * <p>Stores all appointment data including ID, title, meeting details, and involved users, contacts and customers.</p>
 *
 */
public class Appointment {

    /** Appointment ID */
    private final int appointmentID;

    /** Appointment Title */
    private String title;

    /** Appointment Description */
    private String description;

    /** Appointment Location */
    private String location;

    /** Appointment Type */
    private String type;

    /** Appointment Start Time & Date */
    private LocalDateTime startTime;

    /** Appointment End Time & Date */
    private LocalDateTime endTime;

    /** ID of Customer Attending Appointment */
    private int customerID;

    /** ID of User assigned to Appointment */
    private int userID;

    /** ID of Contact assigned to Appointment */
    private int contactID;

    /** Name of Contact assigned to Appointment */
    private String contactName;


    /** Constructor containing ID
     *
     * <p>Used to construct existing appointment objects that have already been added into the Database
     * (assigned an ID number).</p>
     *
     * @param appointmentID ID of the appointment
     * @param title The appointment's title
     * @param description The appointment's description
     * @param location The appointment's meeting location
     * @param type The type of appointment scheduled
     * @param startTime The start date and time of the appointment. Converted to LocalDateTime upon creation.
     * @param endTime The end date and time of the appointment. Converted to LocalDateTime upon creation.
     * @param customerID ID of the customer attending the appointment.
     * @param userID ID of the user assigned to the appointment.
     * @param contactID ID of the contact assigned to the appointment.
     * @param contactName Name of the contact assigned to the appointment.
     */
    public Appointment(int appointmentID, String title, String description, String location,
                       String type, Timestamp startTime, Timestamp endTime, int customerID,
                       int userID, int contactID, String contactName)
    {

        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startTime = startTime.toLocalDateTime();
        this.endTime = endTime.toLocalDateTime();
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
        this.contactName = contactName;

    }

    /** Constructor without ID
     *
     * <p>Used to construct new appointments, typically before they are inserted into the database.</p>
     *
     * @param title The appointment's title
     * @param description The appointment's description
     * @param location The appointment's meeting location
     * @param type The type of appointment scheduled
     * @param startTime The start date and time of the appointment. Converted to LocalDateTime upon creation.
     * @param endTime The end date and time of the appointment. Converted to LocalDateTime upon creation.
     * @param customerID ID of the customer attending the appointment.
     * @param userID ID of the user assigned to the appointment.
     * @param contactID ID of the contact assigned to the appointment.
     * @param contactName Name of the contact assigned to the appointment.
     */
    public Appointment(String title, String description, String location,
                       String type, Timestamp startTime, Timestamp endTime, int customerID,
                       int userID, int contactID, String contactName)
    {

        appointmentID = -1;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startTime = startTime.toLocalDateTime();
        this.endTime = endTime.toLocalDateTime();
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
        this.contactName = contactName;

    }

    /** Returns the Appointment ID
     *
     * <p>Returns appointmentID value.</p>
     *
     * @return appointmentID
     */
    public int getAppointmentID() {
        return appointmentID;
    }

    /** Returns the Appointment Title
     *
     * <p>Returns title value.</p>
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /** Set the value for Appointment Title
     *
     * <p>Sets the title of the appointment to the new value.</p>
     *
     * @param title Title of appointment
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /** Returns the Appointment Description
     *
     * <p>Returns description value.</p>
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /** Set the value for Appointment description
     *
     * <p>Sets the description of the appointment to the new value.</p>
     *
     * @param description Description of appointment
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Returns the Appointment Location
     *
     * <p>Returns location value.</p>
     *
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /** Set the value for Appointment location
     *
     * <p>Sets the location of the appointment to the new value.</p>
     *
     * @param location Location of appointment
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /** Returns the Appointment Type
     *
     * <p>Returns type value.</p>
     *
     * @return type
     */
    public String getType() {
        return type;
    }

    /** Set the value for Appointment type
     *
     * <p>Sets the type of the appointment to the new value.</p>
     *
     * @param type Type of appointment
     */
    public void setType(String type) {
        this.type = type;
    }

    /** Returns the Appointment Start Date and Time
     *
     * <p>Returns startTime value.</p>
     *
     * @return startTime
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /** Set the value for Appointment Start Time
     *
     * <p>Sets the start date and time of the appointment to the new value.</p>
     *
     * @param startTime Start date and time of appointment
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /** Returns the Appointment End Date and Time
     *
     * <p>Returns endTime value.</p>
     *
     * @return endTime
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /** Set the value for Appointment End Time
     *
     * <p>Sets the end date and time of the appointment to the new value.</p>
     *
     * @param endTime End date and time of appointment
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /** Returns the CustomerID of attending Customer
     *
     * <p>Returns customerID value.</p>
     *
     * @return customerID
     */
    public int getCustomerID() {
        return customerID;
    }

    /** Set the value for Customer ID
     *
     * <p>Sets the ID for the customer attending the appointment.</p>
     *
     * @param customerID ID of Customer
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /** Returns the UserID of attending User
     *
     * <p>Returns userID value.</p>
     *
     * @return userID
     */
    public int getUserID() {
        return userID;
    }

    /** Set the value for User ID
     *
     * <p>Sets the ID for the user attending the appointment.</p>
     *
     * @param userID ID of User
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /** Returns the ContactID of attending Contact
     *
     * <p>Returns contactID value.</p>
     *
     * @return contactID
     */
    public int getContactID() {
        return contactID;
    }

    /** Set the value for Contact ID
     *
     * <p>Sets the ID for the contact attending the appointment.</p>
     *
     * @param contactID ID of Contact
     */
    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    /** Returns the Name of attending Contact
     *
     * <p>Returns customerName value.</p>
     *
     * @return contactName
     */
    public String getContactName() {
        return contactName;
    }

    /** Set the value for Contact Name
     *
     * <p>Sets the Name for the Contact attending the appointment.</p>
     *
     * @param contactName Name of Contact
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
