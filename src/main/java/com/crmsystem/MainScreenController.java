package com.crmsystem;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import java.io.IOException;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/** Controller for main-screen.fxml
 *
 * <p>Handles all events and actions on the main screen, including populating tables, calculating reports,
 *  and appointment sorting.</p>
 *
 */
public class MainScreenController {

    /*######################################
     * Variables
      ######################################*/

    /** Used to determine if the Appointment Notification has already been displayed (should only display on login) */
    private static boolean appointmentNotificationDisplayed = false;

    /** List of Customers being displayed in Table */
    ObservableList<Customer> customerList;

    /** List of Appointments being displayed in Table */
    ObservableList<Appointment> appointmentList;

    /*######################################
     * FXML References
      ######################################*/

    /** Customer Table */
    @FXML
    private TableView<Customer> customerTable;

    /** Customer Table Column: Customer ID */
    @FXML
    private TableColumn<Customer, Integer> col_c_custid;

    /** Customer Table Column: Customer Name */
    @FXML
    private TableColumn<Customer, String> col_c_name;

    /** Customer Table Column: Customer Address */
    @FXML
    private TableColumn<Customer, String> col_c_address;

    /** Customer Table Column: Customer Postal Code */
    @FXML
    private TableColumn<Customer, String> col_c_postalcode;

    /** Customer Table Column: Customer Phone */
    @FXML
    private TableColumn<Customer, String> col_c_phone;

    /** Customer Table Column: Customer Division */
    @FXML
    private TableColumn<Customer, String> col_c_division;

    /**Customer Table Column: Customer Country */
    @FXML
    private TableColumn<Customer, String> col_c_country;

    //Appointment Table
    /** Appointment Table */
    @FXML
    private TableView<Appointment> appointmentTable;

    /** Appointment Table Column: Appointment ID */
    @FXML
    private TableColumn<Appointment, Integer> col_a_appid;

    /** Appointment Table Column: Appointment Title */
    @FXML
    private TableColumn<Appointment, String> col_a_title;

    /** Appointment Table Column: Appointment Description */
    @FXML
    private TableColumn<Appointment, String> col_a_desc;

    /** Appointment Table Column: Appointment Location */
    @FXML
    private TableColumn<Appointment, String> col_a_location;

    /** Appointment Table Column: Appointment Contact */
    @FXML
    private TableColumn<Appointment, String> col_a_contact;

    /** Appointment Table Column: Appointment Type */
    @FXML
    private TableColumn<Appointment, String> col_a_type;

    /** Appointment Table Column: Appointment Start Time */
    @FXML
    private TableColumn<Appointment, LocalDateTime> col_a_startdatetime;

    /** Appointment Table Column: Appointment End Time */
    @FXML
    private TableColumn<Appointment, LocalDateTime> col_a_enddatetime;

    /** Appointment Table Column: Customer ID */
    @FXML
    private TableColumn<Appointment, String> col_a_custid;

    /** Appointment Table Column: User ID */
    @FXML
    private TableColumn<Appointment, String> col_a_userid;

    //Reports Table
    /** Reports Table */
    @FXML
    private TableView<Appointment> reportsTable;

    /** Reports Table Column: Appointment ID */
    @FXML
    private TableColumn<Appointment, Integer> col_r_appid;

    /** Reports Table Column: Appointment Title */
    @FXML
    private TableColumn<Appointment, String> col_r_title;

    /** Reports Table Column: Appointment Description */
    @FXML
    private TableColumn<Appointment, String> col_r_desc;

    /** Reports Table Column: Appointment Location */
    @FXML
    private TableColumn<Appointment, String> col_r_location;

    /** Reports Table Column: Appointment Contact */
    @FXML
    private TableColumn<Appointment, String> col_r_contact;

    /** Reports Table Column: Appointment Type */
    @FXML
    private TableColumn<Appointment, String> col_r_type;

    /** Reports Table Column: Appointment Start Time */
    @FXML
    private TableColumn<Appointment, LocalDateTime> col_r_startdatetime;

    /** Reports Table Column: Appointment End Time */
    @FXML
    private TableColumn<Appointment, LocalDateTime> col_r_enddatetime;

    /** Reports Table Column: Customer ID */
    @FXML
    private TableColumn<Appointment, String> col_r_custid;

    /** Reports: Contact Selection ComboBox */
    @FXML
    private ComboBox<String> cb_r_contactSelect;

    /** Reports: Month Selection ComboBox */
    @FXML
    private ComboBox<Month> cb_r_month;

    /** Reports: Type Selection ComboBox */
    @FXML
    private ComboBox<String> cb_r_type;

    /** Reports: Month and Type Report Total Text */
    @FXML
    private Text txt_r_custreport;

    /** Reports: Total Appointments Today Text */
    @FXML
    private Text txt_r_totalToday;

    //Clock
    /** Clock Text: Current Time (displayed on Appointment Tab) */
    @FXML
    private Text txt_a_currenttime;

    /** Clock Text: Current Time (displayed on Customer Tab) */
    @FXML
    private Text txt_c_currenttime;

    /** Used to update the Clock Text each frame and display current time */
    @FXML
    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long l) {
            txt_c_currenttime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")) + " " + ZoneId.systemDefault().getDisplayName(TextStyle.NARROW, Locale.ENGLISH));
            txt_a_currenttime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")) + " " + ZoneId.systemDefault().getDisplayName(TextStyle.NARROW, Locale.ENGLISH));
        }
    };

    /*######################################
     * FXML Actions & Events
      ######################################*/

    /** Runs when the page first loads
     *
     * <p>Called when the page initializes. Starts the timer, populates All tables and ComboBoxes. If this is
     *  the user's first visit to this screen (just logged in) then a notification is displayed showing any
     *  upcoming appointments or that no appointments are coming up.</p>
     *
     */
    @FXML
    private void initialize() {
        //Starts the clock to display on both Customer and Appointment Pages
        timer.start();
        getFullAppointmentList();
        populateCustomerTable();
        populateAppointmentTable();
        populateReportComboBoxes();
        getNumAppointmentsToday();

        if(!appointmentNotificationDisplayed)
        {
            checkUsersAppointments();
            appointmentNotificationDisplayed = true;
        }


    }

    /** Fills the Reports Table with Appointments for selected Contact (Lambda Expression Used)
     *
     * <p>Gets the full list of appointments, then removes any appointments from the list that do
     * not have the specified contact. The remaining list of appointments is displayed in the Reports Table.
     *  A Lambda expression was used to check each element in the appointment list and remove if not assigned to
     *  the selected contact. This was done for code readability and to avoid writing a query method that would only
     *  be used in this one situation.</p>
     *
     */
    @FXML
    private void onReportContactSelected() {
        ObservableList<Appointment> temp = null;

        try {
            temp = AppointmentQuery.selectAll();
        }
        catch (SQLException err)
        {
            System.out.println("FATAL ERROR: Appointment Table Query (Select All) Failed!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }

        temp.removeIf(appointment -> !appointment.getContactName().equals(cb_r_contactSelect.getValue()));

        //Add data from Vector to Table
        col_r_appid.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("appointmentID"));
        col_r_title.setCellValueFactory(new PropertyValueFactory<Appointment, String>("title"));
        col_r_desc.setCellValueFactory(new PropertyValueFactory<Appointment, String>("description"));
        col_r_location.setCellValueFactory(new PropertyValueFactory<Appointment, String>("location"));
        col_r_type.setCellValueFactory(new PropertyValueFactory<Appointment, String>("type"));
        col_r_contact.setCellValueFactory(new PropertyValueFactory<Appointment, String>("contactName"));
        col_r_startdatetime.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("startTime"));
        col_r_enddatetime.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("endTime"));
        col_r_custid.setCellValueFactory(new PropertyValueFactory<Appointment, String>("customerID"));

        reportsTable.setItems(temp);

    }

    /** Handles the Customer Create Button Clicked Event
     *
     * <p>Opens the customer info screen to allow user to add a new customer to the customer list.</p>
     */
    @FXML
    private void onCustomerCreateButtonClick() {
        //Ensures that Scene will be in "New Customer" mode
        CustomerInfoController.updatingExistingRecord(false, 0);

        loadCustomerInfoScreen();
    }

    /** Handles the Customer Update Button Clicked Event
     *
     * <p>Opens the customer info screen and populates data for the selected customer. Will call function
     *  to display error message if no customer is selected.</p>
     */
    @FXML
    private void onCustomerUpdateButtonClick() {

        if(customerTable.getSelectionModel().getSelectedItem() == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No Customer Selected!");
            alert.showAndWait();
            return;
        }

        //Ensures that Scene will be in "Update Customer" mode
        CustomerInfoController.updatingExistingRecord(true, customerTable.getSelectionModel().getSelectedItem().getCustomerID());
        loadCustomerInfoScreen();
    }

    /** Handles the Customer Delete Button Clicked Event
     *
     * <p>Deletes a customer record from the database then refreshes the Customer Table. Will call function
     *  to display error message if no customer is selected or if selected customer has any existing appointments. User
     *   is prompted to confirm deletion before the contact is removed.</p>
     */
    @FXML
    private void onCustomerDeleteButtonClick() {
        Customer customer = customerTable.getSelectionModel().getSelectedItem();

        if(customer == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No customer selected to delete!");
            alert.show();
        }
        else
        {
            //If any appointments are tied to the contact, return and don't delete
            if (checkCustomerHasAppointments(customer.getCustomerID()))
                return;

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to delete this customer?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {

                try {
                    System.out.println(CustomerQuery.deleteCustomer(customer.getCustomerID()) + " line(s) affected.");
                    Alert notif = new Alert(Alert.AlertType.INFORMATION, "Customer successfully removed!");
                    notif.showAndWait();
                } catch (SQLException err) {
                    System.out.println("ERROR: Customer Unable to be Deleted!");
                    System.out.println("Message: " + err.getMessage());
                    System.out.println("Cause: " + err.getCause());
                    System.exit(-1);
                }
            }

            populateCustomerTable();
        }
    }

    /** Handles the Create Button Clicked Event in the Appointments Tab
     *
     * <p>Opens the appointment info screen to allow user to add a new customer to the customer list.</p>
     */
    @FXML
    private void onAppointmentCreateButtonClick() {
        //Ensures that Scene will be in "New Appointment" mode
        AppointmentInfoController.updatingExistingRecord(false, 0);

        loadAppointmentInfoScreen();
    }

    /** Handles the Update Button Clicked Event in the Appointments Tab
     *
     * <p>Opens the appointment info screen and populates data for the selected appointment. Will call function
     *  to display error message if no appointment is selected.</p>
     */
    @FXML
    private void onAppointmentUpdateButtonClick() {

        if(appointmentTable.getSelectionModel().getSelectedItem() == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No Appointment Selected!");
            alert.showAndWait();
            return;
        }

        //Ensures that Scene will be in "Update Appointment" mode
        AppointmentInfoController.updatingExistingRecord(true , appointmentTable.getSelectionModel().getSelectedItem().getAppointmentID());

        loadAppointmentInfoScreen();
    }

    /** Handles the Delete Button Clicked Event in the Appointments Tab
     *
     * <p>Deletes an appointment record from the database then refreshes the appointment Table. Will call function
     *  to display error message if no appointment is selected. User is prompted to confirm deletion before the
     *  appointment is removed.</p>
     */
    @FXML
    private void onAppointmentDeleteButtonClick() {
        Appointment appointment = appointmentTable.getSelectionModel().getSelectedItem();

        if(appointment == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No appointment selected to delete!");
            alert.show();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you would like to delete this appointment?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK)
            {
                try {
                    System.out.println(AppointmentQuery.deleteExistingAppointment(appointment.getAppointmentID()) +
                            " line(s) affected.");
                    Alert notif = new Alert(Alert.AlertType.INFORMATION, "Appointment #" + appointment.getAppointmentID() +
                            " , Type -\" " + appointment.getType() + "\": Successfully removed!");
                    notif.showAndWait();
                }
                catch (SQLException err)
                {
                    System.out.println("ERROR: Appointment Unable to be Deleted!");
                    System.out.println("Message: " + err.getMessage());
                    System.out.println("Cause: " + err.getCause());
                    System.exit(-1);
                }
            }

            getFullAppointmentList();
            populateAppointmentTable();
        }
    }

    /** Handles the Week Sort Clicked Event
     *
     * <p>Retrieves the full list of appointments and removes all appointments that don't take place within
     *  the next seven days. A Lambda expression was used for the remove statement to replace an otherwise long
     *  for each loop. Makes the function much more readable.</p>
     */
    @FXML
    private void onAppointmentSortWeek() {
        getFullAppointmentList(); //Repopulate the full list.

        ObservableList<Appointment> temp = appointmentList;

        //Remove Appointment from List if not within next 7 days
        temp.removeIf(appointment -> appointment.getStartTime().getDayOfYear() < LocalDateTime.now().getDayOfYear() ||
                appointment.getStartTime().getDayOfYear() >= LocalDateTime.now().getDayOfYear() + 7);

        //Save Changes and Re-populate view
        appointmentList = temp;
        populateAppointmentTable();
    }

    /** Handles the Month Sort Clicked Event
     *
     * <p>Retrieves the full list of appointments and removes all appointments that don't take place within
     *  the same month (EX: All appointments in September). A Lambda expression was used for the remove statement
     *  to replace an otherwise long for each loop. Makes the function much more readable.</p>
     */
    @FXML
    private void onAppointmentSortMonth() {
        getFullAppointmentList(); //Repopulate the full list.

        ObservableList<Appointment> temp = appointmentList;

        //Remove Appointment from List if not within next Month
        temp.removeIf(appointment -> !appointment.getStartTime().getMonth().equals(LocalDateTime.now().getMonth()));

        //Save Changes and Re-populate view
        appointmentList = temp;
        populateAppointmentTable();
    }

    /** Handles the All Sort Clicked Event
     *
     * <p>Retrieves the full list of appointments and displays them on the table.</p>
     */
    @FXML
    private void onAppointmentSortAll() {
        getFullAppointmentList(); //Repopulate the full list.
        populateAppointmentTable();
    }

    /** Handles the Appointment Alert Event
     *
     * <p>Searches a list of appointments that are assigned to the logged in user. Upon finding the first
     * appointment (if any) that are occurring within the next 15 minutes, displays an alert for the user.
     * Otherwise displays an alert that no appointments are upcoming.</p>
     */
    @FXML
    private void checkUsersAppointments() {
        ObservableList<Appointment> temp = null;

        try {
            temp = AppointmentQuery.getAppointmentsOfUserID(UserQuery.getUserID(UserQuery.getUsername()));
        } catch (SQLException err) {
            System.out.println("ERROR: Unable to retrieve list of appointments for user.");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }

        for(Appointment appointment : temp)
        {
            if(appointment.getStartTime().isBefore(LocalDateTime.now().plusMinutes(15)) &&
                !appointment.getStartTime().isBefore(LocalDateTime.now()))
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Appointment starting soon!\n" +
                        "ID# " + appointment.getAppointmentID() + ": " + appointment.getTitle() + " at " +
                        appointment.getStartTime().toString());
                alert.show();
                return; //No need to stack alerts. After one is found, return.
            }
        }

        //If it gets to this point, no appointments were found within the next 15 minutes for this user
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "No upcoming appointments!");
        alert.show();

    }

    /** Handles the Month and Type Selection Report Event
     *
     * <p>When a month or type are selected from the Reports page, this function is called. Displays nothing unless
     *  both a month and type have been selected. If both are selected, changes the text on the form to list the
     *  number of appointments occurring that month of the specified type.</p>
     */
    @FXML
    private void onMonthOrTypeSelection() {

        ObservableList<Appointment> temp = null;

        //Both are required for result
        if(cb_r_month.getValue() == null || cb_r_type.getValue() == null)
            return;

        try {
            temp = AppointmentQuery.selectAll();
        } catch (SQLException err) {
            System.out.println("FATAL ERROR: Appointment Table Query (Select All) Failed!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }

        temp.removeIf(appointment -> !appointment.getStartTime().getMonth().equals(cb_r_month.getValue()) ||
                !appointment.getType().equals(cb_r_type.getValue()));

        txt_r_custreport.setText("Total Appointments: " + temp.size());
    }

    /*######################################
    * Helper Functions
      ######################################*/

    /** Changes the scene to display the Customer Info Screen
     *
     * <p>Calls changeScene() from class ScheduleApplication to load the customerInfo-screen.fxml. If
     * file is not found, method will exit the program.</p>
     */
    private void loadCustomerInfoScreen() {
        //Try to load the Customer Info Window
        try
        {
            ScheduleApplication app = new ScheduleApplication();
            app.changeScene("customerInfo-screen.fxml");
            timer.stop();
        }
        catch (IOException err)
        {
            System.out.println("FATAL ERROR: \"customerInfo-screen.fxml\" NOT FOUND!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }
    }

    /** Changes the scene to display the Appointment Info Screen
     *
     * <p>Calls changeScene() from class ScheduleApplication to load the appointmentInfo-screen.fxml. If
     * file is not found, method will exit the program.</p>
     */
    private void loadAppointmentInfoScreen() {
        //Try to load the Appointment Info Window
        try
        {
            ScheduleApplication app = new ScheduleApplication();
            app.changeScene("appointmentInfo-screen.fxml");
            timer.stop();
        }
        catch (IOException err)
        {
            System.out.println("FATAL ERROR: \"appointmentInfo-screen.fxml\" NOT FOUND!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }
    }

    /** Loads customer information into the CustomerTable
     *
     * <p>Retrieves the full customer list and displays the list in the customer table.</p>
     */
    private void populateCustomerTable() {

        getFullCustomerList();

        //Add data from List to Table
        col_c_custid.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("CustomerID"));
        col_c_name.setCellValueFactory(new PropertyValueFactory<Customer, String>("Name"));
        col_c_address.setCellValueFactory(new PropertyValueFactory<Customer, String>("Address"));
        col_c_postalcode.setCellValueFactory(new PropertyValueFactory<Customer, String>("PostalCode"));
        col_c_phone.setCellValueFactory(new PropertyValueFactory<Customer, String>("PhoneNumber"));
        col_c_division.setCellValueFactory(new PropertyValueFactory<Customer, String>("Division"));
        col_c_country.setCellValueFactory(new PropertyValueFactory<Customer, String>("Country"));
        customerTable.setItems(customerList);

    }

    /** Loads appointment information into the AppointmentsTable
     *
     * <p>Displays the current appointments list (appointmentList) in the appointmentTable. Does not retrieve the
     * full list of appointments.</p>
     */
    private void populateAppointmentTable() {

        //Add data from List to Table
        col_a_appid.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("appointmentID"));
        col_a_title.setCellValueFactory(new PropertyValueFactory<Appointment, String>("title"));
        col_a_desc.setCellValueFactory(new PropertyValueFactory<Appointment, String>("description"));
        col_a_location.setCellValueFactory(new PropertyValueFactory<Appointment, String>("location"));
        col_a_type.setCellValueFactory(new PropertyValueFactory<Appointment, String>("type"));
        col_a_contact.setCellValueFactory(new PropertyValueFactory<Appointment, String>("contactName"));
        col_a_startdatetime.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("startTime"));
        col_a_enddatetime.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("endTime"));
        col_a_custid.setCellValueFactory(new PropertyValueFactory<Appointment, String>("customerID"));
        col_a_userid.setCellValueFactory(new PropertyValueFactory<Appointment, String>("userID"));

        appointmentTable.setItems(appointmentList);

    }

    /** Loads values into the Report ComboBoxes
     *
     * <p>Retrieves a list of Months and all existing appointment types, then uses those to populate
     * the Month and Type ComboBoxes on the reports tab.</p>
     */
    private void populateReportComboBoxes() {
        //Months
        ObservableList<Month> months = FXCollections.observableList(new ArrayList<>());
        months.addAll(Arrays.asList(Month.values()));

        cb_r_month.setItems(months);

        //Types
        try {
            cb_r_type.setItems(AppointmentQuery.selectAllTypes());

            //Contacts
            cb_r_contactSelect.setItems(AppointmentQuery.selectAllContacts());
        }
        catch (SQLException err) {
            System.out.println("FATAL ERROR: Could not populate Report fields!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }
    }

    /** Checks if Customer has any Appointments in System
     *
     * <p>Used when a user attempts to delete a customer. If the database search returns ANY appointments tied
     * to this customer's ID, returns true. If customer has no appointments, returns false. Displays an error if
     * an appointment is detected.</p>
     *
     * @param id Customer ID
     * @return True if Customer has Appointments in the System, False otherwise.
     */
    private boolean checkCustomerHasAppointments(int id) {

        try {
            if(AppointmentQuery.getAppointmentsOfCustomerID(id).size() != 0)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Customer has existing appointments and cannot be deleted.\n" +
                        "All appointments for this customer must be cancelled before their record can be removed.");
                alert.showAndWait();
                return true;
            }
        } catch (SQLException err) {
            System.out.println("ERROR: Customer's appointment list could not be retrieved from query!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }
        return false;
    }

    /** Populates the number of appointments occurring today
     *
     * <p>Searches the list of appointments for any that are occurring today. Displays the total number of those
     * appointments on the Reports Tab.</p>
     */
    private void getNumAppointmentsToday() {
        ObservableList<Appointment> temp = null;

        try {
            temp = AppointmentQuery.selectAll();
        } catch (SQLException err) {
            System.out.println("FATAL ERROR: Appointment Table Query (Select All) Failed!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }

        temp.removeIf(appointment -> appointment.getStartTime().getDayOfYear() != LocalDateTime.now().getDayOfYear());

        txt_r_totalToday.setText("Total Appointments Today: " + temp.size());
    }

    /** Loads customer information into the customerList
     *
     * <p>Fetches all customer data from the database and sets it in the customerList.</p>
     */
    private void getFullCustomerList() {
        //Tries to Query Customer Table
        try
        {
            customerList = CustomerQuery.selectAll();
            System.out.println("Customer Table Query (Select All) Successful!");

        }
        catch (SQLException err)
        {
            System.out.println("FATAL ERROR: Customer Table Query (Select All) Failed!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }
    }

    /** Loads appointment information into the appointmentList
     *
     * <p>Fetches all appointment data from the database and sets it in the appointmentList.</p>
     */
    private void getFullAppointmentList() {

        //Tries to Query Customer Table
        try
        {
            appointmentList = AppointmentQuery.selectAll();
            System.out.println("Appointment Table Query (Select All) Successful!");

        }
        catch (SQLException err)
        {
            System.out.println("FATAL ERROR: Appointment Table Query (Select All) Failed!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }

    }

}
