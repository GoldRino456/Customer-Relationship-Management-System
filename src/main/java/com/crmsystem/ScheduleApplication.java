package com.crmsystem;

import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** Main file of the Scheduling application
 *
 * Used to launch the app and also handles scene changes.
 */
public class ScheduleApplication extends Application {

    /** JavaFX Stage */
    private static Stage stage;


    /**
     * Initializes the Stage to display an FXML document.
     *
     * @param stage the stage being used to display
     * @exception IOException required to handle FXMLLoader.load()
     */
    @Override
    public void start(Stage stage) throws IOException {
        ScheduleApplication.stage = stage;
        stage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(ScheduleApplication.class.getResource("login-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("RBSS - Real Business Scheduling Solutions");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Changes the scene currently on the stage.
     *
     * @param fxml String - fxml file name or path
     * @exception IOException required to handle FXMLLoader.load()
     */
    public void changeScene(String fxml) throws IOException
    {
        Parent pane = FXMLLoader.load(ScheduleApplication.class.getResource(fxml));
        stage.getScene().setRoot(pane);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    /** Main Method
     *
     * Opens and closes the database connection through JDBC, and launches JFX.
     *
     */
    public static void main(String[] args) {
        JDBC.openConnection();
        launch();
        JDBC.closeConnection();
    }
}