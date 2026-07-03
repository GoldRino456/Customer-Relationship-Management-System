package com.crmsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** Stores data of Country elements in the Database
 *
 * <p>Stores all country data including ID, name, and divisions. Also performs operations to retrieve
 * division names and ID for database manipulation.</p>
 *
 */
public class Country {

    /*######################################
    * Variables
      ######################################*/

    /** Country ID */
    private int countryID;

    /** Country Name */
    private String country;

    /** Map of Country's First-Level Divisions (Division Name, DivisionID) */
    private Map<String, Integer> divisions = new HashMap<String, Integer>();


    /** Returns the Country ID
     *
     * <p>Returns countryID value.</p>
     *
     * @return countryID
     */
    public int getCountryID() {
        return countryID;
    }

    /** Returns the country name
     *
     * <p>Returns country's value.</p>
     *
     * @return country
     */
    public String getCountry() {
        return country;
    }

    /** Gets the division ID of a specified division
     *
     * <p>Returns division ID as an int based on the key value input to the map.</p>
     *
     * @param key division name
     * @return divisionID
     */
    public int getDivisionID(String key) {
        return divisions.get(key);
    }

    /** Sets the value of countryID to the new value
     *
     * <p>Changes countryID to the value of the input parameter.</p>
     *
     * @param countryID new countryID
     */
    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    /** Sets the country name to a new value
     *
     * <p>Changes country to the value specified in the input parameter.</p>
     *
     * @param country new country name
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /** Adds a division to the country's division list
     *
     * <p>Adds a new division / id pair to the division map. Since division ID is
     * auto incremented in the database, no ID should ever be repeated here.</p>
     *
     * @param division name of division
     * @param divisionID id of division
     */
    public void addDivisionToList(String division, int divisionID) {

        //DivisionID is auto incremented by Database, so no repeated value should ever reach this.
        divisions.put(division, divisionID);

    }

    /** Returns the Set of all division names
     *
     * <p>Returns a Set of all division names (keys).</p>
     *
     * @return Set of Strings, Division names
     */
    public Set<String> getDivisionName() {
        return divisions.keySet();
    }
}
