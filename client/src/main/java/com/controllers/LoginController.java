package com.controllers;

import com.Main;
import com.chat.Listener;
import com.database.DBConnect;
import com.database.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    public TextField inputNick;
    public PasswordField inputPassword;
    public TextField inputHost;
    public TextField inputPort;
    public static Stage stageDivision;
    @FXML
    public AnchorPane anchorPane;
    public static Stage RegStage;

    private double xOffset;
    private double yOffset;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        anchorPane.setOnMousePressed(event -> {
            xOffset = RegController.LoginStage.getX() - event.getScreenX();
            yOffset = RegController.LoginStage.getY() - event.getScreenY();
            anchorPane.setCursor(Cursor.CLOSED_HAND);
        });

        anchorPane.setOnMouseDragged(event -> {
            RegController.LoginStage.setX(event.getScreenX() + xOffset);
            RegController.LoginStage.setY(event.getScreenY() + yOffset);

        });

        anchorPane.setOnMouseReleased(event -> anchorPane.setCursor(Cursor.DEFAULT));
    }

    public void OnRegisterClicked(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("views/register.fxml")));
            Stage stage = new Stage();
            stage.setTitle("Division - Register");
            stage.setScene(new Scene(root, 650, 650));
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            RegStage = stage;
            stage.show();
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSubmit() {
        DBConnect connect = new DBConnect();
        if (connect.connectionError) {
            return;
        }
        User user = connect.getQueryUser(inputNick.getText(), inputPassword.getText());
        connect.shutdown();
        if (user == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Login failed");
            alert.setHeaderText("Some fields completed incorrectly:");
            alert.setContentText("Wrong nickname or password");
            alert.show();
        } else {
            try {

                FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/division.fxml"));
                Parent root = loader.load();
                DivisionController controller = loader.<DivisionController>getController();
                Stage stage = new Stage();
                stage.setTitle("Division");
                stage.setScene(new Scene(root, 1040, 620));
                stage.setResizable(false);
                stage.initStyle(StageStyle.UNDECORATED);
                stageDivision = stage;
                stage.show();

                Listener listener = new Listener(this.inputHost.getText(), Integer.parseInt(this.inputPort.getText()), user.getNick(), user.getIcon(), controller);
                new Thread(listener).start();

                inputNick.getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeLoginPage(MouseEvent mouseEvent) {
        Platform.exit();
        System.exit(0);
    }
}