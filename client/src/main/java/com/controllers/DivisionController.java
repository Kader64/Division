package com.controllers;

import com.chat.CellRenderer;
import com.chat.Listener;
import com.messages.Message;
import com.messages.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class DivisionController implements Initializable {

    @FXML
    public TextArea messageBox;
    @FXML
    public Label usernameLabel;
    @FXML
    public Label onlineCountLabel;
    @FXML
    public ListView userList;
    @FXML
    public Image userImg;
    public Circle userImageView;
    @FXML
    public VBox chatBox;
    @FXML
    BorderPane borderPane;
    @FXML
    ComboBox statusComboBox;

    private double xOffset;
    private double yOffset;
    private double maxHeight;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        statusComboBox.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            try {
                Listener.sendStatusUpdate(newValue.toUpperCase());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        borderPane.setOnMousePressed(event -> {
            xOffset = LoginController.stageDivision.getX() - event.getScreenX();
            yOffset = LoginController.stageDivision.getY() - event.getScreenY();
            borderPane.setCursor(Cursor.CLOSED_HAND);
        });

        borderPane.setOnMouseDragged(event -> {
            LoginController.stageDivision.setX(event.getScreenX() + xOffset);
            LoginController.stageDivision.setY(event.getScreenY() + yOffset);

        });

        borderPane.setOnMouseReleased(event -> borderPane.setCursor(Cursor.DEFAULT));
    }

    public void setOnlineLabel(String count) {
        Platform.runLater(() -> onlineCountLabel.setText(count));
    }

    public void setUsernameLabel(String username) {
        Platform.runLater(() -> usernameLabel.setText(username));
    }

    public void setImageLabel(String profile_src) throws IOException {
        userImg = new Image(profile_src);
        this.userImageView.setFill(new ImagePattern(userImg));
    }

    public void setUserList(Message msg) {
        Platform.runLater(() -> {
            ObservableList<User> users = FXCollections.observableList(msg.getUsers());
            userList.setItems(users);
            userList.setCellFactory(new CellRenderer());
            setOnlineLabel(String.valueOf(msg.getUsers().size()));
        });
    }

    @FXML
    public void closeApplication() {
        Platform.exit();
        System.exit(0);
    }

    public synchronized void addToChat(Message msg) {

        Platform.runLater(() -> {
            if (!chatBox.getChildren().isEmpty()) {
                maxHeight = 0;
                for (int i = 0; i < chatBox.getChildren().size(); i++) {
                    HBox box = (HBox) chatBox.getChildren().get(i);
                    maxHeight += box.getHeight();
                }
            }

            if (Objects.equals(msg.getName(), usernameLabel.getText())) {
                Text text = new Text(msg.getMsg());
                text.setFont(Font.font("Arial", 18));
                text.setFill(Color.WHITE);
                text.setWrappingWidth(700);
                text.setTextAlignment(TextAlignment.RIGHT);

                HBox hBox = new HBox();
                hBox.setPadding(new Insets(4, 8, 0, 0));
                hBox.setAlignment(Pos.TOP_RIGHT);
                hBox.setSpacing(8);
                hBox.setMargin(text, new Insets(11, 0, 0, 0));
                hBox.getStyleClass().add("chatBox-msg");

                Circle circle = new Circle(20);
                circle.setFill(new ImagePattern(userImg));

                hBox.getChildren().addAll(text, circle);
                chatBox.getChildren().add(hBox);
                HBox h = (HBox) chatBox.getChildren().get(chatBox.getChildren().size()-1);
                if(maxHeight + h.getHeight() >= chatBox.getHeight()-55){
                    chatBox.getChildren().remove(0);
                }

            } else {
                Text text = new Text(msg.getMsg());
                text.setFont(Font.font("Arial", 18));
                text.setFill(Color.WHITE);
                text.setWrappingWidth(700);
                text.setTextAlignment(TextAlignment.LEFT);

                HBox hBox = new HBox();
                hBox.setPadding(new Insets(4, 0, 0, 8));
                hBox.setSpacing(8);
                hBox.setMargin(text, new Insets(11, 0, 0, 0));
                hBox.getStyleClass().add("chatBox-msg");

                Circle circle = new Circle(20);
                circle.setFill(new ImagePattern(new Image(msg.getPicture())));
                hBox.getChildren().addAll(circle, text);
                chatBox.getChildren().add(hBox);
                HBox h = (HBox) chatBox.getChildren().get(chatBox.getChildren().size()-1);
                if(maxHeight + h.getHeight() >= chatBox.getHeight()-55){
                    chatBox.getChildren().remove(0);
                }
            }
        });
    }

    public void onMessegeBoxSubmit(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            sendButtonAction();
            keyEvent.consume();
        }
    }

    public void sendButtonAction() {
        if (!messageBox.getText().isEmpty()) {
            try {
                Listener.send(messageBox.getText());
                messageBox.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSendClicked() {
        sendButtonAction();
    }
}