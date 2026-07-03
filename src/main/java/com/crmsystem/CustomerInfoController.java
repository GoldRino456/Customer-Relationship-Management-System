package com.crmsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/** Controller for customerInfo-screen.fxml
 *
 * <p>Handles all events and actions on the Customer Info screen, including populating data, preparing
 * customer object to be updated/inserted into the table, and verifying data in form fields.</p>
 *
 */
public class CustomerInfoController {

    /*######################################
     * Variables
      ######################################*/

    /** Sets the Mode of the Form (New or Update) */
    private static boolean isExistingCustomer;

    /** ID of the Selected Customer */
    private static int selectedID;

    /** Full List of Countries */
    private ObservableList<Country> countryList;

    /** Currently Selected Country on Form */
    private Country selectedCountry;

    /** List of Divisions from the selectedCountry */
    private ObservableList<String> divisionList;

    /*######################################
     * FXML References
      ######################################*/

    /** Form Customer Headline Text (Changes to "new" or "update" customer based on mode) */
    @FXML
    private Text txt_customerheadline;

    /** Form Customer ID Text (Only used to make text visible/invisible) */
    @FXML
    private Text txt_custid;

    /** Form Customer ID TextField */
    @FXML
    private TextField tf_custid;

    /** Form Customer Name TextField */
    @FXML
    private TextField tf_name;

    /** Form Customer Phone Number TextField */
    @FXML
    private TextField tf_phone;

    /** Form Customer Address TextField */
    @FXML
    private TextField tf_address;

    /** Form Customer Postal Code TextField */
    @FXML
    private TextField tf_postalcode;

    /** Form Customer Division ComboBox */
    @FXML
    private ComboBox<String> cb_division;

    /** Form Customer Country ComboBox */
    @FXML
    private ComboBox<String> cb_country;



    /*######################################
     * FXML Actions & Events
      ######################################*/

    /** Runs when the page first loads
     *
     * <p>Called when the page initializes. Sets update or new customer mode based on
     * isExistingCustomer boolean. Also calls populateCustomerData() when updating. Country ComboBox
     * is initialized here regardless of mode, but Divisions are only initialized if updating.</p>
     *
     */
    @FXML
    private void initialize() {

        initCountryComboBox();

        if (isExistingCustomer)
        {
            txt_customerheadline.setText("Update Customer");
            try
            {
                populateCustomerData(selectedID);
            }
            catch (SQLException err) {
                System.out.println("ERROR: Selected Customer NOT FOUND!");
                System.out.println("Message: " + err.getMessage());
                System.out.println("Cause: " + err.getCause());
            }
        }
        else
        {
            //CustomerID should be set by the Database automatically, so will only be visible upon an update
            txt_custid.setVisible(false);
            tf_custid.setVisible(false);
            cb_division.setDisable(true);
        }



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
     * <p>Saves all information to database as a new record or updates existing record if isExistingCustomer is true.
     * All fields must have valid data and cannot be left blank in order to save. Transitions to main screen once
     * database has been updated.</p>
     */
    @FXML
    private void onSaveButtonClick() {

        if(validateFields())
        {
            insertCustomerToTable();
            loadMainScreen();
        }
        else
        {
            System.out.println("Invalid Fields!");
        }
    }

    /** Handles the Country Selected Event
     *
     * <p>When a Country is selected by a user, enables the Division ComboBox and populates
     * division data into the Division ComboBox.</p>
     */
    @FXML
    private void onCountrySelection() {
        cb_division.setDisable(false);
        int idx;

        for (Country country : countryList)
        {
            if(country.getCountry().equals(cb_country.getValue()))
            {
                selectedCountry = country;
                getCountryDivisions(selectedCountry);
                cb_division.setItems(divisionList);
                break;
            }
        }
    }


    /*######################################
     * Helper Functions
      ######################################*/

    /** Updates the isExistingCustomer variable
     *
     * <p>Called by other classes just before transitioning to the Customer Info screen to ensure class is in
     * the correct mode: "new" or "update".</p>
     *
     * @param bool used to select "new" or "update" customer mode
     * @param id Customer ID (if updating)
     */
    public static void updatingExistingRecord(boolean bool, int id) {
        isExistingCustomer = bool;
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

    /** Fills all form fields with existing Customer's data
     *
     * <p>Called at initialization when isExistingCustomer is TRUE. Pulls the selected customer's information
     * and fills all forms on the screen with that data.</p>
     *
     * @param selectedID selected Customer's ID
     */
    private void populateCustomerData(int selectedID) throws SQLException {

        //Get Customer Data
        Customer temp = CustomerQuery.getCustomer(selectedID);

            //Assign and Display
        if (temp != null) {
            ObservableList<String> countryTemp = CustomerQuery.getCustomerCountry(temp.getDivisionID());

            //Load Data into Fields
            tf_custid.setText(Integer.toString(selectedID));
            tf_name.setText(temp.getName());
            tf_address.setText(temp.getAddress());
            tf_postalcode.setText(temp.getPostalCode());
            tf_phone.setText(temp.getPhoneNumber());

            if (countryTemp != null)
            {
                cb_country.setValue(countryTemp.get(0)); //First Element in list will always be Country
                onCountrySelection();
                getCountryDivisions(selectedCountry);
                cb_division.setValue(countryTemp.get(1)); //Second Element in list will always be Division
            }

        }

    }

    /** Loads the CountryList and Fills Countries in Country ComboBox
     *
     * <p>Initializes the countryList by querying the database, then populates the Country ComboBox
     *  using that data. Should only be called when the page initializes.</p>
     */
    private void initCountryComboBox() {

        //Get CountryList
        try {
            countryList = CustomerQuery.getCountriesAndDivisions();
        }
        catch(SQLException err) {
            System.out.println("ERROR: Countries Could Not Be Queried!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
        }

        //Add Country Names to CB
        ObservableList<String> countriesToDisplay = FXCollections.observableList(new ArrayList<String>());

        for (Country country : countryList) {
            countriesToDisplay.add(country.getCountry());
        }

        cb_country.setItems(countriesToDisplay);
    }

    /** Loads the divisionList with all divisions for selected country
     *
     * <p>Converts the set of all divisions from the given country to an observable list of strings
     * and assigns the result to the divisionList.
     * Sorts the list of divisions for easier readability.</p>
     *
     * @param country The country to get divisions from
     */
    private void getCountryDivisions(Country country) {
        divisionList = FXCollections.observableList(new ArrayList<String>());

        Set<String> temp = country.getDivisionName();

        divisionList.addAll(temp);
        Collections.sort(divisionList);
    }

    /** Determines if a customer is being updated or inserted
     *
     * <p>Calls either updateExistingCustomer() or insertNewCustomer() depending on if isExistingCustomer
     * is true or false respectively. This is only called when a customer has been successfully validated.</p>
     *
     */
    private void insertCustomerToTable() {

        if(isExistingCustomer)
        {
            updateExistingCustomer();
        }
        else
        {
            insertNewCustomer();
        }
    }

    /** Inserts a new customer to the table
     *
     * <p>Creates a new customer object, then calls CustomerQuery to insert the customer
     * as a new record in the Database.</p>
     *
     */
    private void insertNewCustomer() {
        Customer customer = new Customer(
                tf_name.getText(),
                tf_address.getText(),
                tf_postalcode.getText(),
                tf_phone.getText(),
                selectedCountry.getDivisionID(cb_division.getValue()));

        try
        {
            System.out.println(CustomerQuery.createNewCustomer(customer) + " lines affected.");
        }
        catch (SQLException err)
        {
            System.out.println("Failed to add customer to Database.");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }
    }

    /** Updates an existing customer
     *
     * <p>Creates a new customer object, then calls CustomerQuery to update the customer
     * in the Database./p>
     *
     */
    private void updateExistingCustomer() {
        Customer customer = new Customer(
                selectedID,
                tf_name.getText(),
                tf_address.getText(),
                tf_postalcode.getText(),
                tf_phone.getText(),
                selectedCountry.getDivisionID(cb_division.getValue()));

        try
        {
            System.out.println(CustomerQuery.updateCustomer(customer) + " lines affected.");
        }
        catch (SQLException err)
        {
            System.out.println("Failed to add customer to Database.");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }
    }

    /** Validates that all form fields have a non-blank value
     *
     * <p>Checks all form fields for value (selection in the case of ComboBoxes). If any value is missing,
     * user is prompted to fill out all fields on the page.</p>
     *
     * @return True if all fields are filled, false otherwise
     */
    private boolean validateFields() {

        if(tf_name.getText().isBlank() ||
                tf_address.getText().isBlank() ||
                tf_phone.getText().isBlank() ||
                tf_postalcode.getText().isBlank() ||
                cb_country.getValue() == null ||
                cb_division.getValue() == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill out all fields!");
            alert.show();
            return false;
        }

        return true;
    }

}
