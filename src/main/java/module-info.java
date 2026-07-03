module com.crmsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.crmsystem to javafx.fxml;
    exports com.crmsystem;
}