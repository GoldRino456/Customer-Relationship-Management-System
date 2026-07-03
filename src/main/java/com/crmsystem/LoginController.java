package com.crmsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.*;
import java.util.Locale;

/** Controller for login-screen.fxml
 *
 * <p>Handles all events and actions on the login screen, including displaying errors, validating login,
 *  and displaying text based on the user's Locale (currently only English and French implemented).</p>
 *
 */
public class LoginController {

    /*######################################
    * Variables
      ######################################*/
    /** Gets User's TimeZone at startup */
    static ZoneId userTimeZone = ZoneId.systemDefault();

    /** Gets User's Locale at startup */
    static Locale userLocale = Locale.getDefault();
    //static Locale userLocale = Locale.FRANCE;

    /*######################################
    * FXML References
      ######################################*/
    //Buttons
    /** Form Button to Attempt Login */
    @FXML
    private Button btn_login;

    //Text & Text Fields
    /** Form Field to collect Username */
    @FXML
    private TextField tf_userid;

    /** Form Field to collect Password */
    @FXML
    private TextField tf_password;

    /** Hidden at run time. Originally used for testing. */
    @FXML
    private Text txt_err;

    /** RBSS Platform Name, displayed above the login fields */
    @FXML
    private Text txt_headline;

    /** Sub-title under Headline */
    @FXML
    private Text txt_title;

    //Labels
    /** Displays the user's timezone */
    @FXML
    private Label lbl_zoneid;



    /*######################################
    * FXML Actions & Events
      ######################################*/
    /** Initializes the Login Form
     *
     * <p>Run when login screen page first loads. Calls all functions needed to initialize the
     * login screen including localizeText().</p>
     */
    @FXML
    private void initialize() {

        localizeText();

    }

    /** Handles the Login Button Clicked Event
     *
     * <p>If credentials entered are valid and found in list of users, attempts to
     * load the main-screen.fxml page.</p>
     */
    @FXML
    protected void onLoginButtonClick() {

        //Check No Empty Fields
        if(checkLoginFieldsForValue())
        {

            try{
                if(UserQuery.findUsername(tf_userid.getText()) && UserQuery.findPassword(tf_password.getText()))
                {
                    try {
                        recordLoginAttempt(true);
                    } catch (IOException err) {
                        System.out.println("ERROR: Could not write to log file.");
                        System.out.println("Message: " + err.getMessage());
                        System.out.println("Cause: " + err.getCause());
                    }
                        loadMainWindow();
                }
                else
                {
                    try {
                        recordLoginAttempt(false);
                    } catch (IOException err) {
                        System.out.println("ERROR: Could not write to log file.");
                        System.out.println("Message: " + err.getMessage());
                        System.out.println("Cause: " + err.getCause());
                    }
                    displayInvalidCredentialsError();
                }
            }
            catch (SQLException err) {

                System.out.println("ERROR: Unable to Check Username / Password with Database.");
                System.out.println("Message: " + err.getMessage());
                System.out.println("Cause: " + err.getCause());
                System.exit(-1);
            }
        }
        else
        {
            try {
                recordLoginAttempt(false);
            } catch (IOException err) {
                System.out.println("ERROR: Could not write to log file.");
                System.out.println("Message: " + err.getMessage());
                System.out.println("Cause: " + err.getCause());
            }
        }
    }



    /*######################################
    * Helper Functions
      ######################################*/
    /** Detects and Displays User's Timezone and Text in Locale Language
     *
     * <p>Called when the Login Form Initializes. Calls displayTimeZone() and the corresponding function
     * to translate the form's text into the language of user's Locale.</p>
     *
     */
    private void localizeText() {

        displayTimeZone();

        //Form by default will display in English
        if(!userLocale.getLanguage().equals("en"))
        {
            switch (userLocale.getLanguage())
            {
                case "fr":
                    displayLogin_FR();
                default:
                    //Language Not Implemented
            }

        }


    }

    /** Displays Login Form in French Language
     *
     * <p>All text on the Login Form will be displayed in the French Language. Should only be called by the localizeText()
     * function after it detects a user with System settings set to FR.</p>
     *
     */
    private void displayLogin_FR() {
        txt_title.setText("DVSPD");
        txt_headline.setText("De vraies solutions de planification d'entreprise");
        btn_login.setText("Connexion");
        tf_userid.setPromptText("Identifiant d'utilisateur");
        tf_password.setPromptText("Mot de passe");
    }

    /** Checks UserID and Password Fields for content.
     *
     * <p>Returns true if a valid String does exist in both UserID and Password fields and that
     *string is not blank. Otherwise returns false.</p>
     *
     * @return boolean - UserID and Password Text Fields contain a non-blank value
     */
    private boolean checkLoginFieldsForValue() {

        try {
            if(tf_userid.getText().isEmpty() || tf_userid.getText().isBlank())
            {
                displayBlankUsernameError();
                return false;
            }
            else if (tf_password.getText().isEmpty() || tf_password.getText().isBlank())
            {
                displayBlankPasswordError();
                return false;
            }

        }
        catch (Error err) {

            txt_err.setText("ERROR: Invalid Input");
            System.out.println("ERROR: " + err.getMessage());
            return false;
        }

        return true;
    }

    /** Displays Blank Username Error in User's Local Language
     *
     * <p>Displays an error message when the username entered is either blank or empty. Error message displayed
     * is based on the Locale of the user's machine.</p>
     *
     */
    private void displayBlankUsernameError() {
        switch (userLocale.getLanguage()) {

            case "en":
                Alert alert_en = new Alert(Alert.AlertType.ERROR, "Username field cannot be empty!");
                alert_en.show();
                break;

            case "fr":
                Alert alert_fr = new Alert(Alert.AlertType.ERROR, "le champ du nom d'utilisateur ne peut pas être vide!");
                alert_fr.setTitle("ERREUR");
                alert_fr.setHeaderText("ERREUR");
                alert_fr.show();
                break;

            default:
                //Language Not Implemented.

        }

    }

    /** Displays Blank Password Error in User's Local Language
     *
     * <p>Displays an error message when the password entered is either blank or empty. Error message displayed
     * is based on the Locale of the user's machine.</p>
     *
     */
    private void displayBlankPasswordError() {
        switch (userLocale.getLanguage()) {

            case "en":
                Alert alert_en = new Alert(Alert.AlertType.ERROR, "Password field cannot be empty!");
                alert_en.show();
                break;

            case "fr":
                Alert alert_fr = new Alert(Alert.AlertType.ERROR, "le champ du mot de passe ne peut pas être vide!");
                alert_fr.setTitle("ERREUR");
                alert_fr.setHeaderText("ERREUR");
                alert_fr.show();
                break;

            default:
                //Language Not Implemented.

        }

    }

    /** Displays Invalid Credentials Error in User's Local Language
     *
     * <p>Displays an error message when the username/password combo isn't found in the database. Error message displayed
     * is based on the Locale of the user's machine.</p>
     *
     */
    private void displayInvalidCredentialsError() {
        switch (userLocale.getLanguage()) {

            case "en":
                Alert alert_en = new Alert(Alert.AlertType.ERROR, "Invalid Username or Password!");
                alert_en.show();
                break;

            case "fr":
                Alert alert_fr = new Alert(Alert.AlertType.ERROR, "Identifiant ou mot de passe invalide!");
                alert_fr.setTitle("ERREUR");
                alert_fr.setHeaderText("ERREUR");
                alert_fr.show();
                break;

            default:
                //Language Not Implemented.

        }
    }

    /** Displays TimeZone Message in User's Local Language
     *
     * <p>Displays an error message when the password entered is either blank or empty. Error message displayed
     * is based on the Locale of the user's machine.</p>
     *
     */
    private void displayTimeZone() {

        switch (userLocale.getLanguage()) {

            case "en":
                lbl_zoneid.setText("Current Timezone: " + userTimeZone.getId());
                break;

            case "fr":
                lbl_zoneid.setText("Fuseau horaire actuel: " + userTimeZone.getId());
                break;

            default:
                //Language Not Implemented.
        }

    }

    /** Changes the scene to display the Main Screen
     *
     * <p>Calls changeScene() from class ScheduleApplication to load the main-screen.fxml. If
     * file is not found, method will exit the program. Only called after a successful login.</p>
     */
    private void loadMainWindow() {
        //Try to load the Main Window
        try {
            ScheduleApplication app = new ScheduleApplication();
            app.changeScene("main-screen.fxml");
        } catch (IOException err) {
            System.out.println("FATAL ERROR: \"main-screen.fxml\" NOT FOUND!");
            System.out.println("Message: " + err.getMessage());
            System.out.println("Cause: " + err.getCause());
            System.exit(-1);
        }
    }

    /** Records a Login attempt to an external TXT file
     *
     * <p>Creates a file login_activity.txt if none already exists. Records all attempts at logging in
     * by appending a string to the log file. Boolean b is used to determine success or failure.</p>
     */
    private void recordLoginAttempt(boolean b) throws IOException {
        FileWriter file = new FileWriter("login_activity.txt", true);
        String result;

        if(b)
            result = "successful";
        else
            result = "failed";

        String str = tf_userid.getText() + " attempted Login. Result: " + result + ". Attempted at: " +
                LocalDateTime.now() + " " + ZoneId.systemDefault() + "." + System.lineSeparator();
        file.append(str);
        file.close();
    }
}
