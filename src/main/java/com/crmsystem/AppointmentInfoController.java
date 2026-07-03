package com.crmsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/** Controller for appointmentInfo-screen.fxml
 *
 * <p>Handles all events and actions on the Appointment Info screen, including populating data, preparing
 * appointment object to be updated/inserted into the table, and verifying data in form fields.</p>
 *
 */
public class AppointmentInfoController {

    /*######################################
     * Variables
      ######################################*/

    /** Sets the Mode of the Form (New or Update) */
    private static boolean isExistingAppointment;

    /** ID of the Selected Appointment */
    private static int selectedID;

    /** Business Hours Start of Day */
    private final String BUSINESS_HOURS_START = "08:00"; //This value will be read as EST and converted to Local Time

    /** Business Hours End of Day */
    private final String BUSINESS_HOURS_END = "22:00"; //This value will be read as EST and converted to Local Time

    /** Business Hours Time Zone */
    private final String BUSINESS_HOURS_ZONENAME = "US/Eastern";



    /** Form Appointment Headline Text (Changes to "new" or "update" appointment based on mode) */
    @FXML
    private Text txt_appointmentheadline;

    /** Form Appointment ID Text (Only used to make text visible/invisible) */
    @FXML
    private Text txt_appid;

    /** Form Appointment ID TextField */
    @FXML
    private TextField tf_appid;

    /** Form Appointment Title TextField */
    @FXML
    private TextField tf_title;

    /** Form Appointment Location TextField */
    @FXML
    private TextField tf_desc;

    /** Form Appointment Location TextField */
    @FXML
    private TextField tf_location;

    /** Form Appointment Type TextField */
    @FXML
    private TextField tf_type;

    /** Form Date Picker */
    @FXML
    private DatePicker dp_date;

    /** Form Start Time TextField */
    @FXML
    private TextField tf_starttime;

    /** Form End Time TextField */
    @FXML
    private TextField tf_endtime;

    /** Form Customer ComboBox */
    @FXML
    private ComboBox<String> cb_customer;

    /** Form User ComboBox */
    @FXML
    private ComboBox<String> cb_user;

    /** Form Contact ComboBox */
    @FXML
    private ComboBox<String> cb_contact;


    /*######################################
     * FXML Actions & Events
      ######################################*/

    /** Runs when the page first loads
     *
     * <p>Called when the page initializes. Sets update or new appointment mode based on
     * isExistingAppointment boolean. Also calls populateAppointmentData() when updating. ComboBoxes
     * are initialized here regardless of mode.</p>
     *
     */
    @FXML
    private void initialize() {

        if (isExistingAppointment)
        {
            txt_appointmentheadline.setText("Update Appointment");
            tf_appid.setText(Integer.toString(selectedID));

            try {
                populateAppointmentData();
            }
            catch (SQLException err) {
                System.out.println("FATAL ERROR: Appointment Unable to be loaded to page!");
                System.out.println("Message: " + err.getMessage());
                System.out.println("Cause: " + err.getCause());System.exit(-1);

            }
        }
        else
        {
            txt_appid.setVisible(false);
            tf_appid.setVisible(false);
        }

        setUpComboBoxes();

    }

    /** Handles the Cancel Button Clicked Event
     *
     * <p>Does not save any information on the page. Loads Main Screen.</p>
     */
    @FXML
    private void onCancelButtonClick() {

        //Try to load the Main Window
        loadMainScreen();
    }

    /** Handles the Save Button Clicked Event
     *
     * <p>Saves all information to database as a new record or updates existing record if isExistingAppointment is true.
     * All fields must have valid data and cannot be left blank in order to save. Transitions to main screen once
     * database has been updated.</p>
     */
    @FXML
    private void onSaveButtonClick() {

        if(validateFields())
        {
            insertAppointmentToTable();
            loadMainScreen();
        }
        else
        {
            System.out.println("Invalid Fields!");
        }
    }

    /*######################################
     * Helper Functions
      ######################################*/

    /** Changes the state of isExistingAppointment to ensure scene opens in either "new" or "update" mode
     *
     * <p>Should only be called BEFORE changing to the Appointment Info scene. Used by other class functions
     * to determine if data should be populated into the form and if text should read "new" or "update" appointment.
     * True, ID# = Editing existing appointment, False, 0 = Adding new appointment</p>
     *
     * @param bool Appointment Mode (new or update)
     * @param id Appointment ID
     */
    public static void updatingExistingRecord(boolean bool, int id) {
        isExistingAppointment = bool;
        selectedID = id;
    }

    /** Changes the current screen to the Main Screen
     *
     * <p>Should only be used after a valid save or cancel event. Calls changeScene() from class
     * ScheduleApplication to load the main-screen.fxml. If file is not found, method will exit the program.</p>
     */
    private void loadMainScreen() {
        try
        {
            ScheduleApplication app = new ScheduleApplication();
            app.changeScene("main-screen.fxml");
        }
        catch (IOException err)
        {
            System.out.println("FATAL ERROR: \"main-screen.fxml\" NOT FOUND!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }
    }

    /** Fills fields with existing appointment's data
     *
     * <p>Only called when updating an appointment. Populates the fields with all data from the
     * existing appointment selected by the user.</p>
     *
     * @throws SQLException from AppointmentQuery.getAppointmentByID()
     */
    private void populateAppointmentData() throws SQLException {

        Appointment appointment = AppointmentQuery.getAppointmentByID(selectedID);

            //Fill in Fields
            if(appointment != null) {
                tf_title.setText(appointment.getTitle());
                tf_desc.setText(appointment.getDescription());
                tf_location.setText(appointment.getLocation());
                tf_type.setText(appointment.getType());
                tf_starttime.setText(appointment.getStartTime().toLocalTime().toString());
                tf_endtime.setText(appointment.getEndTime().toLocalTime().toString());
                cb_user.setValue(UserQuery.findUsernameByID(appointment.getUserID()));
                cb_customer.setValue(CustomerQuery.getCustomer(appointment.getCustomerID()).getName());
                cb_contact.setValue(AppointmentQuery.getContactNameByID(appointment.getContactID()));
                dp_date.setValue(appointment.getStartTime().toLocalDate());
            }
            else {
                System.out.println("Appointment null. Cannot populate fields on form.");
                System.exit(-1);
            }

    }

    /** Validates Value in all Fields
     *
     * <p>Checks all fields on form for non-blank values, then calls the various methods
     *  to ensure Time is valid. Displays an error on blank or null fields.</p>
     *
     * @return boolean value: True if all fields valid, False Otherwise
     */
    private boolean validateFields() {

        //Check for Null fields
        if(tf_title.getText().isBlank() ||
                tf_desc.getText().isBlank() ||
                tf_location.getText().isBlank() ||
                tf_type.getText().isBlank() ||
                dp_date.getValue() == null ||
                cb_customer.getValue() == null ||
                cb_user.getValue() == null ||
                cb_contact.getValue() == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill out all fields!");
            alert.show();
            return false;
        }
        //Check Time is in correct format and time in valid range
        else if(!validateTimeFields() ||
                !validateTimeRange() ||
                !validateTimeWithinBusinessHours() ||
                !validateCustomerIsFree())
        {
            //ValidateTimeFields and ValidateTimeRange will display their own Error Messages
            System.out.println("Failed Validation.");
            return false;
        }

        return true;
    }

    /** Validates Time is in Correct Format
     *
     * <p>Ensures the strings in tf_starttime and tf_endtime are in "HH:mm" format. Displays
     * an error if that is not the case.</p>
     *
     * @return boolean value: True if Time is in correct format, False Otherwise
     */
    private boolean validateTimeFields() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

        try {
            LocalTime.parse(tf_starttime.getText(), dtf);
            LocalTime.parse(tf_endtime.getText(), dtf);
        }
        catch (DateTimeParseException err) {
            System.out.println("Date time fields could not be parsed.");

            Alert alert = new Alert(Alert.AlertType.ERROR, "Start and End Times must be in HH:MM format!\n" +
                    "Ex: \"09:00\" or \"14:30\"");
            alert.showAndWait();

            return false;
        }

        return true;
    }

    /** Populates the ComboBoxes with Data (Lambda Expression Used)
     *
     * <p>Queries the database for the list of customers, users, and contacts to display them in their
     * respective ComboBoxes. CustomerQuery.selectAll() returns a list of customer objects, so a Lambda
     * expression is used to get the name value of each customer and then add each name into the tempList to be displayed.
     * A Lambda expression was used primarily to improve readability in the method and remove the clutter of a for loop.</p>
     */
    private void setUpComboBoxes() {
        ObservableList<Customer> tempList = FXCollections.observableList(new ArrayList<>());
        ObservableList<String> toDisplay = FXCollections.observableList(new ArrayList<>());

        //Try to get Data from Database
        try {
            tempList = CustomerQuery.selectAll(); //SelectAll returns Customer object, will need to loop through it
            cb_user.setItems(UserQuery.selectAll());
            cb_contact.setItems(AppointmentQuery.selectAllContacts());
        } catch (SQLException err) {
            System.out.println("ERROR: ComboBoxes Could Not Be Populated.");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
        }

        //Add all Customer names to list and display
        tempList.forEach(customer -> toDisplay.add(customer.getName()));
        cb_customer.setItems(toDisplay);
    }

    /** Converts a String to Timestamp
     *
     * <p>Returns a timestamp value for easier creation of appointment objects. Time
     * must be in HH:mm format and a date must be previously selected in the dp_date DatePicker.</p>
     *
     * @param t A String representing time in "HH:MM" format
     * @return Timestamp object of the date selected in dp_date at Time t
     */
    private Timestamp fieldsToTimestamp(String t) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

        LocalDateTime ldt = LocalDateTime.of(dp_date.getValue(), LocalTime.parse(t, dtf));
        return Timestamp.valueOf(ldt);
    }

    /** Validates User's Starting and End time range
     *
     * <p>Checks to ensure Start time truly does come before the user specified end time value. If valid range,
     * true is returned. If invalid, false is returned instead.</p>
     *
     * @return boolean value: True if Time is a Valid Range, False Otherwise
     */
    private boolean validateTimeRange() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

        //Check if Start value is before end value
        LocalTime start = LocalTime.parse(tf_starttime.getText(), dtf);
        LocalTime end = LocalTime.parse(tf_endtime.getText(), dtf);

        if(start.isBefore(end))
        {
            return true;
        }
        else
        {
            System.out.println("Start time is not before End time. Invalid appointment range.");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment Starting time MUST be before End time.");
            alert.showAndWait();
            return false;
        }
    }

    /** Validates Time Range is within Business Hours
     *
     * <p>Creates a temporary LocalDateTime object for the user's appointment start and end times. Converts the
     * Business hours to a ZonedDateTime. Compares the two values, returns true if time falls within Business Hours.</p>
     *
     * @return boolean value: True if Time is within Business Hours, False Otherwise
     */
    private boolean validateTimeWithinBusinessHours() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

        //Create LocalDateTime values (using date selected on Form)
        LocalDateTime ldt_start = LocalDateTime.of(dp_date.getValue(), LocalTime.parse(tf_starttime.getText(), dtf));
        LocalDateTime ldt_end = LocalDateTime.of(dp_date.getValue(), LocalTime.parse(tf_endtime.getText(), dtf));

        //Create ZonedDateTime values for Business Hours
        ZonedDateTime zdt_business_start = LocalDateTime.of(dp_date.getValue(), LocalTime.parse(BUSINESS_HOURS_START, dtf)).atZone(ZoneId.of(BUSINESS_HOURS_ZONENAME));
        ZonedDateTime zdt_business_end = LocalDateTime.of(dp_date.getValue(), LocalTime.parse(BUSINESS_HOURS_END, dtf)).atZone(ZoneId.of(BUSINESS_HOURS_ZONENAME));

        //Convert Business Hours to Local Time Zone
        zdt_business_start = zdt_business_start.withZoneSameInstant(ZoneId.systemDefault());
        zdt_business_end = zdt_business_end.withZoneSameInstant(ZoneId.systemDefault());


        //Compare Business Hours to Appointment Time
        if(ldt_start.isBefore(zdt_business_start.toLocalDateTime()) || //Is start time before business hours?
                ldt_start.isAfter(zdt_business_end.toLocalDateTime()) || //Is start time after business hours?
                ldt_end.isBefore(zdt_business_start.toLocalDateTime()) || //Is end time before business hours?
                ldt_end.isAfter(zdt_business_end.toLocalDateTime())) //Is end time after business hours?
        {
            //Displays an error message showing valid time range for business hours in user's timezone
            Alert alert = new Alert(Alert.AlertType.ERROR, "Appointment time must be within operating hours.\n" +
                    zdt_business_start.toLocalDateTime().toLocalTime().toString() + " - "
                    + zdt_business_end.toLocalDateTime().toLocalTime().toString() + " " + ZoneId.systemDefault() + "\n" +
                    " (" + BUSINESS_HOURS_START + " - " + BUSINESS_HOURS_END + " " + BUSINESS_HOURS_ZONENAME + ")");
            alert.showAndWait();
            return false;
        }

        return true;
    }

    /** Validates Customer Availability
     *
     * <p>Searches the full list of appointments for all appointments involving the selected customer.
     * If any appointment time</p>
     *
     * @return boolean value: True if Customer is Free, False Otherwise
     */
    private boolean validateCustomerIsFree() {
        int tempID = -1;
        ObservableList<Appointment> tempAppointments = null;

        //Create LocalDateTime values (using date selected on Form)
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime ldt_start = LocalDateTime.of(dp_date.getValue(), LocalTime.parse(tf_starttime.getText(), dtf));
        LocalDateTime ldt_end = LocalDateTime.of(dp_date.getValue(), LocalTime.parse(tf_endtime.getText(), dtf));

        //Retrieve Customer's appointments
        try {
            tempID = CustomerQuery.getCustomerID(cb_customer.getValue());
            tempAppointments = AppointmentQuery.getAppointmentsOfCustomerID(tempID);

        }
        catch (SQLException err) {
            System.out.println("ERROR: Could not completely query to validate customer's availability!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }

        //Check EVERY appointment found above, if it conflicts display alert.
        for (Appointment appointment: tempAppointments)
        {
            //Valid Appointments can only exist where New Appointment Start Time > Existing End Time OR New Appointment End Time < Existing Start Time
            if(ldt_start.isAfter(appointment.getEndTime()) || ldt_end.isBefore(appointment.getStartTime()))
            {
                continue; //This appointment doesn't conflict, go to next step
            }

            //If the appointment does conflict, are we updating an existing appointment and is this that appointment?
            if(isExistingAppointment && appointment.getAppointmentID() == selectedID)
            {
                continue; //This appointment shouldn't conflict with itself
            }

            Alert alert = new Alert(Alert.AlertType.ERROR, "Customer is not available between " + appointment.getStartTime() + " - " + appointment.getEndTime() + ".\n" +
                    "Please choose a different time or date.");
            alert.showAndWait();
            return false; //Conflicting appointment found
        }

        return true; //If we got here, no conflicting appointments

    }

    /** Determines if an appointment is being updated or inserted
     *
     * <p>Calls either updateExistingAppointment() or insertNewAppointment() depending on if isExistingAppointment
     * is true or false respectively. This is only called when an appointment has been successfully validated.</p>
     *
     */
    private void insertAppointmentToTable() {
        if(isExistingAppointment)
        {
            updateExistingAppointment();
        }
        else
        {
            insertNewAppointment();
        }
    }

    /** Updates an existing appointment
     *
     * <p>Creates a new appointment object, then calls AppointmentQuery to update the appointment
     * in the Database./p>
     *
     */
    private void updateExistingAppointment() {
        Appointment appointment = new Appointment(
                selectedID,
                tf_title.getText(),
                tf_desc.getText(),
                tf_location.getText(),
                tf_type.getText(),
                fieldsToTimestamp(tf_starttime.getText()),
                fieldsToTimestamp(tf_endtime.getText()),
                getCustomerID(cb_customer.getValue()),
                getUserID(cb_user.getValue()),
                getContactID(cb_contact.getValue()),
                cb_contact.getValue()
        );

        try
        {
            System.out.println(AppointmentQuery.updateExistingAppointment(appointment) + " lines affected.");
        }
        catch (SQLException err)
        {
            System.out.println("Failed to add Appointment to Database.");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }
    }

    /** Inserts a new appointment to the table
     *
     * <p>Creates a new appointment object, then calls AppointmentQuery to insert the appointment
     * as a new record in the Database.</p>
     *
     */
    private void insertNewAppointment() {
        Appointment appointment = new Appointment(
                tf_title.getText(),
                tf_desc.getText(),
                tf_location.getText(),
                tf_type.getText(),
                fieldsToTimestamp(tf_starttime.getText()),
                fieldsToTimestamp(tf_endtime.getText()),
                getCustomerID(cb_customer.getValue()),
                getUserID(cb_user.getValue()),
                getContactID(cb_contact.getValue()),
                cb_contact.getValue()
        );

        try
        {
            AppointmentQuery.createNewAppointment(appointment);
        }
        catch (SQLException err)
        {
            System.out.println("ERROR: Unable to add new appointment. Error in Query.");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
        }
    }

    /** Returns the Contact ID of a given Contact
     *
     * <p>Calls AppointmentQuery to retrieve the ID of a contact by specified Contact name.
     * If no contact found by that name, -1 is returned instead.</p>
     *
     * @param value Contact name
     * @return contactID as an int
     *
     */
    private int getContactID(String value) {
        int id = -1;

        try {
            id = AppointmentQuery.getContactID(value);
        }
        catch (SQLException err) {
            System.out.println("ERROR: Get Contact ID Query Failed!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
        }

        return id;
    }

    /** Returns the User ID of a given User
     *
     * <p>Calls UserQuery to retrieve the ID of a user by specified user name.
     * If no contact found by that name, -1 is returned instead.</p>
     *
     * @param value user name
     * @return userID as an int
     *
     */
    private int getUserID(String value) {
        int id = -1;

        try {
            id = UserQuery.getUserID(value);
        }
        catch (SQLException err) {
            System.out.println("ERROR: Get User ID Query Failed!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
        }

        return id;
    }

    /** Returns the Customer ID of a given Customer
     *
     * <p>Calls CustomerQuery to retrieve the ID of a customer by specified Customer name.
     * If no customer found by that name, -1 is returned instead.</p>
     *
     * @param value Customer name
     * @return customerID as an int
     *
     */
    private int getCustomerID(String value) {
        int id = -1;

        try{
            id = CustomerQuery.getCustomerID(value);
        }
        catch (SQLException err) {
            System.out.println("ERROR: Get Customer ID Query Failed!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
        }

        return id;
    }
}
