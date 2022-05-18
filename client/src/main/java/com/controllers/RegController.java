package com.controllers;

import com.Main;
import com.database.DBConnect;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


public class RegController implements Initializable {


    public TextField nick;
    public PasswordField password;
    public PasswordField confirm_password;
    public TextField email;
    @FXML
    public Circle profile_img;
    public String profile_img_src;
    @FXML
    public AnchorPane anchorPane;
    public static Stage LoginStage;;

    private double xOffset;
    private double yOffset;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        profile_img_src = "C:\\Users\\WOJTEK\\IdeaProjects\\Division\\client\\src\\main\\resources\\com\\images\\profile.png";
        Image image = new Image(profile_img_src);
        profile_img.setFill(new ImagePattern(image));

        anchorPane.setOnMousePressed(event -> {
            xOffset = LoginController.RegStage.getX() - event.getScreenX();
            yOffset = LoginController.RegStage.getY() - event.getScreenY();
            anchorPane.setCursor(Cursor.CLOSED_HAND);
        });

        anchorPane.setOnMouseDragged(event -> {
            LoginController.RegStage.setX(event.getScreenX() + xOffset);
            LoginController.RegStage.setY(event.getScreenY() + yOffset);

        });

        anchorPane.setOnMouseReleased(event -> anchorPane.setCursor(Cursor.DEFAULT));
    }

    public void onLoginButtonClicked(MouseEvent event) {
        openLoginPage();
    }

    public void openLoginPage() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("views/login.fxml")));
            Stage stage = new Stage();
            stage.setTitle("Division - Login");
            stage.setScene(new Scene(root, 500, 500));
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setResizable(false);
            RegController.LoginStage = stage;
            stage.show();
            nick.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSubmit() {
        DBConnect connect = new DBConnect();
        if (connect.connectionError) {
            return;
        }
        String warningMsg = "";
        if (nick.getText().length() < 4) {
            warningMsg += "Nickname must contain at least 4 characters.\n";
        }
        if (password.getText().length() < 6) {
            warningMsg += "Password must contain at least 6 characters.\n";
        }
        if (!Objects.equals(password.getText(), confirm_password.getText())) {
            warningMsg += "Passwords must be the same.\n";
        }
        if (!Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
                .matcher(email.getText()).matches()) {
            warningMsg += "Wrong email syntax.\n";
        }
        if (connect.isEmailInDatabase(email.getText())) {
            warningMsg += "Email is already taken.\n";
        }
        if (!warningMsg.equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Registration failed");
            alert.setHeaderText("Some fields completed incorrectly:");
            alert.setContentText(warningMsg);
            alert.show();
        } else {
            connect.insertNewUser(nick.getText(), email.getText(), password.getText(), profile_img_src);
            connect.shutdown();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful!");
            alert.setHeaderText("Your account has been created");
            alert.setContentText("Confirm to go to the login page");
            alert.showAndWait();
            openLoginPage();
        }
    }


    public void onUploadClicked(MouseEvent mouseEvent) {
        try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select profile image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
            File selectedFile = fileChooser.showOpenDialog(nick.getScene().getWindow());
            if (selectedFile != null) {
                int index = selectedFile.getName().lastIndexOf('.');
                if(index > 0) {
                    String extension = selectedFile.getName().substring(index + 1);
                    if(extension.equals("png") || extension.equals("jpg") || extension.equals("gif")){
                        profile_img_src = selectedFile.getAbsolutePath();
                        Image image = new Image(profile_img_src);
                        profile_img.setFill(new ImagePattern(image));
                    }
                    else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Loading failed");
                        alert.setHeaderText("Wrong file extension");
                        alert.setContentText("Allowed extensions: png, jpg, gif");
                        alert.showAndWait();
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void closeRegPage(MouseEvent mouseEvent) {
        Platform.exit();
        System.exit(0);
    }
}