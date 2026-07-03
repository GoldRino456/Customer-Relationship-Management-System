package com.crmsystem;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;

/** Handles all Customer Queries
 *
 * <p>An abstract class used to retrieve and manage customer data and most other data involved in their
 *  creation, manipulation, and removal from the database (including countries and divisions).</p>
 *
 */
public abstract class CustomerQuery {

    /** Queries the Database to retrieve the full Customer List
     *
     * <p>Selects all customers from the customer table and returns them as a ObservableList of
     * Customer objects.</p>
     *
     *
     */
    public static ObservableList<Customer> selectAll() throws SQLException {
        ObservableList<Customer> customers = FXCollections.observableList(new ArrayList<Customer>());

        //SQL Query
        String sql = "SELECT * FROM CUSTOMERS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        //Pull data from Query
        while(rs.next()) {
            //Create new customer object for customer in table
            Customer temp = new Customer(
                    rs.getInt("Customer_ID"),
                    rs.getString("Customer_Name"),
                    rs.getString("Address"),
                    rs.getString("Postal_Code"),
                    rs.getString("Phone"),
                    rs.getInt("Division_ID"));

            customers.add(temp);
        }

        return customers;
    }

    /** Inserts a new Customer into the Customers Table
     *
     * <p>Pulls data from a customer object "c" and queries the database to insert it into the customers table. Returns
     * an int value representing the number of rows affected by the operation.</p>
     *
     */
    public static int createNewCustomer(Customer c) throws SQLException {
        //SQL Query
        String sql = "INSERT INTO CUSTOMERS (Customer_Name, Address, Postal_Code, Phone, Division_ID, Create_Date, Created_By, Last_Update, Last_Updated_By)"
                +" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, c.getName()); //Name
        ps.setString(2, c.getAddress()); //Address
        ps.setString(3, c.getPostalCode()); //Postal Code
        ps.setString(4, c.getPhoneNumber()); //Phone
        ps.setInt(5, c.getDivisionID()); //Division ID
        ps.setTimestamp(6, Timestamp.from(Instant.now())); // Created Date
        ps.setString(7, UserQuery.getUsername()); //Created By
        ps.setTimestamp(8, Timestamp.from(Instant.now())); //Update Date
        ps.setString(9, UserQuery.getUsername()); //Updated By

        return ps.executeUpdate();
    }

    /** Queries the Database to retrieve a Customer by ID
     *
     * <p>Searches the full list of Customers by ID. Returns a Customer object of the Customer containing that ID, otherwise
     *  returns null.</p>
     *
     * @param customerID customerID
     * @return Customer
     */
    public static Customer getCustomer(int customerID) throws SQLException {
        //SQL Query
        String sql = "SELECT * FROM CUSTOMERS WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerID);

        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            return new Customer(rs.getInt("Customer_ID"),
                    rs.getString("Customer_Name"),
                    rs.getString("Address"),
                    rs.getString("Postal_Code"),
                    rs.getString("Phone"),
                    rs.getInt("Division_ID"));
        }

        return null; //If it gets here, Query returned no results. Customer will be set to null.
    }

    /** Queries the Database to retrieve a Customer's ID by Name
     *
     * <p>Searches the full list of Customers by Name. Returns the int ID of that customer, otherwise
     *  returns -1.</p>
     *
     * @param customer customer name
     * @return CustomerID
     */
    public static int getCustomerID(String customer) throws SQLException {
        //SQL Query
        String sql = "SELECT * FROM CUSTOMERS WHERE Customer_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customer);

        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            return rs.getInt("Customer_ID");
        }

        return -1; //If it gets here, Query returned no results. Customer will be set to null.
    }

    /** Queries the Database to update an Existing customer record
     *
     * <p>Takes a customer object and updates the corresponding customer in the Customer Table.</p>
     *
     * @param c Customer Object
     * @return Number of rows affected
     */
    public static int updateCustomer(Customer c) throws SQLException {
        //SQL Query
        String sql = "UPDATE customers \n" +
                "SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ?, Last_Update = ?, Last_Updated_By = ?\n" +
                "WHERE Customer_ID = ?";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, c.getName()); //Set Name
        ps.setString(2, c.getAddress()); //Set Address
        ps.setString(3, c.getPostalCode()); // Set Postal Code
        ps.setString(4, c.getPhoneNumber()); //Set Phone
        ps.setInt(5, c.getDivisionID()); //Set Division ID
        ps.setTimestamp(6, Timestamp.from(Instant.now())); //Update Date
        ps.setString(7, UserQuery.getUsername()); //Updated By

        System.out.println(c.getCustomerID());
        ps.setInt(8, c.getCustomerID()); //Set Phone

        return ps.executeUpdate();
    }

    /** Removes an existing record from the Customers Table
     *
     * <p>Removes the specified customer with matching CustomerID from the Customers Table. Returns
     * an int value representing the number of rows affected by the operation.</p>
     *
     */
    public static int deleteCustomer(int customerID) throws SQLException {
        //SQL Query
        String sql = "DELETE FROM CUSTOMERS WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerID);

        return ps.executeUpdate();
    }

    /** Retrieves full list of Countries (divisions included)
     *
     * <p>Queries the database to build an ObservableList of all countries. Countries are
     *  stored as Country objects and include Division names and IDs.</p>
     *
     */
    public static ObservableList<Country> getCountriesAndDivisions() throws SQLException {

        ObservableList<Country> countries = FXCollections.observableList(new ArrayList<Country>());

        //SQL Query
        String sql = "SELECT countries.Country, countries.Country_ID, first_level_divisions.Division, first_level_divisions.Division_ID \n" +
                "FROM countries\n" +
                "INNER JOIN first_level_divisions on countries.Country_ID = first_level_divisions.Country_ID";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        System.out.println("Query Successful.");

        //Pull data from Query
        Country temp = null;

        while(rs.next()) {


            //If temp is NOT null AND next row shares the same CountryID
            if(temp != null && rs.getInt("Country_ID") == temp.getCountryID())
            {
                //Add Division to current Country
                temp.addDivisionToList(rs.getString("Division"), rs.getInt("Division_ID"));
            }
            else //If temp IS null or next row is in Different Country
            {
                if(temp != null) //Don't add country if none exists
                {
                    countries.add(temp);
                }

                temp = new Country();
                temp.setCountry(rs.getString("Country"));
                temp.setCountryID(rs.getInt("Country_ID"));
                temp.addDivisionToList(rs.getString("Division"), rs.getInt("Division_ID"));
            }


        }

        countries.add(temp); //Adds the final country stored in "Temp" to the list

        //Return List of Countries
        System.out.println("Successfully Retrieved Countries / Divisions!");
        return countries;

    }

    /** Queries the Database to retrieve a customer's Country and Division name
     *
     * <p>Takes a divisionID from a customer and finds the corresponding Country. Joins first_level_divisions and
     * countries to find the matching Country.</p>
     *
     * @param divisionID first level division ID
     * @return ObservableList containing the Country's name followed by the Division name.
     */
    public static ObservableList<String> getCustomerCountry(int divisionID) throws SQLException {

        //Initialize List
        ObservableList<String> countryDivisionPair = FXCollections.observableList(new ArrayList<String>());

        //SQL Query
        String sql = "SELECT first_level_divisions.Division, countries.Country\n" +
                "FROM first_level_divisions\n" +
                "INNER JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID\n" +
                " WHERE Division_ID = ?";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, divisionID);

        ResultSet rs = ps.executeQuery();

        //Pull Data from Query Results
        if(rs.next())
        {
            countryDivisionPair.add(rs.getString("Country"));
            countryDivisionPair.add(rs.getString("Division"));

            return countryDivisionPair;
        }

        return null; //If it gets here, Query returned no results. Customer will be set to null.
    }
}
