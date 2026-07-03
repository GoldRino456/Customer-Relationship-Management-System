package com.crmsystem;

import javafx.collections.ObservableList;

import java.sql.SQLException;

/** Stores data of Customer elements in the Database
 *
 * <p>Stores all customer data including ID, name, address, and contact information.</p>
 *
 */
public class Customer {

    /*######################################
    * Variables
      ######################################*/

    /** Customer ID */
    private int customerID;

    /** Customer Name */
    private String name;

    /** Customer Address */
    private String address;

    /** Customer Postal Code */
    private String postalCode;

    /** Customer Phone Number */
    private String phoneNumber;

    /** Customer First-Level Division ID */
    private int divisionID;

    /** Customer First-Level Division */
    private String division;

    /** Customer Country */
    private String country;

    /*######################################
    * Class Functions
      ######################################*/

    /** Constructor including ID
     *
     * <p>Used to construct existing customer objects that have already been added into the Database
     *(assigned an ID number).</p>
     *
     * @param customerID customer's ID
     * @param name customer's name
     * @param address customer's home address
     * @param postalCode customer's postal code
     * @param phoneNumber customer's phone number
     * @param divisionID division of customer's address
     */
    public Customer(int customerID, String name, String address, String postalCode, String phoneNumber, int divisionID) {
        this.customerID = customerID;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.divisionID = divisionID;

        getCountryAndDivisionName();
    }

    /** Constructor without ID
     *
     * <p>Used to construct new customers, typically before they are inserted into the database.</p>
     *
     * @param name customer's name
     * @param address customer's home address
     * @param postalCode customer's postal code
     * @param phoneNumber customer's phone number
     * @param divisionID division of customer's address
     */
    public Customer(String name, String address, String postalCode, String phoneNumber, int divisionID) {
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.divisionID = divisionID;

        getCountryAndDivisionName();
    }


    /*######################################
    * Setter / Getter Functions
      ######################################*/

    /** Returns the Customer ID
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
     * <p>Sets the ID of the customer to the new value.</p>
     *
     * @param customerID new customer ID
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /** Returns the Customer Name
     *
     * <p>Returns name value.</p>
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /** Set the value for Customer Name
     *
     * <p>Sets the name of the customer to the new value.</p>
     *
     * @param name new customer name
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Returns the Customer Address
     *
     * <p>Returns address value.</p>
     *
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /** Set the value for Customer Address
     *
     * <p>Sets the address of the customer to the new value.</p>
     *
     * @param address new customer Address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /** Returns the Customer's Postal code
     *
     * <p>Returns postalCode value.</p>
     *
     * @return postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /** Set the value for Customer Postal Code
     *
     * <p>Sets the postal code of the customer to the new value.</p>
     *
     * @param postalCode new customer postal code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /** Returns the Customer's Phone Number
     *
     * <p>Returns phoneNumber value.</p>
     *
     * @return phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /** Set the value for Customer Phone number
     *
     * <p>Sets the phone number of the customer to the new value.</p>
     *
     * @param phoneNumber new customer number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /** Returns the Customer's Division
     *
     * <p>Returns divisionID value.</p>
     *
     * @return divisionID
     */
    public int getDivisionID() {
        return divisionID;
    }

    /** Set the value for Customer division
     *
     * <p>Sets the division of the customer to the new value.</p>
     *
     * @param divisionID new customer division
     */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    /** Populates the Division Name and Country Name
     *
     * <p>Uses the division ID to call CustomerQuery to retrieve the Country and Division. Stores them in their
     * respetive variables.</p>
     *
     */
    private void getCountryAndDivisionName() {
        ObservableList<String> temp = null;

        try {
            //Country should be first element, Division name second
            temp = CustomerQuery.getCustomerCountry(divisionID);
        }
        catch (SQLException err)
        {
            System.out.println("FATAL ERROR: Countries could not be retrieved!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }

        if (temp != null) {
            country = temp.get(0);
            division = temp.get(1);
        }


    }

    /** Returns the Customer's Division Name
     *
     * <p>Returns division value.</p>
     *
     * @return division
     */
    public String getDivision() {
        return division;
    }

    /** Returns the Customer's Country
     *
     * <p>Returns country value.</p>
     *
     * @return county name
     */
    public String getCountry() {
        return country;
    }
}
