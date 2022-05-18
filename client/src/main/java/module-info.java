module com.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com to javafx.fxml;
    exports com;
    exports com.database;
    opens com.database to javafx.fxml;
    exports com.controllers;
    opens com.chat to javafx.fxml;
    exports com.messages;
    opens com.messages to javafx.fxml;
    exports com.chat;
    opens com.controllers to javafx.fxml;
}