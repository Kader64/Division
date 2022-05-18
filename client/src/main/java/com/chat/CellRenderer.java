package com.chat;

import com.Main;
import com.messages.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class CellRenderer implements Callback<ListView<User>,ListCell<User>>{
    @Override
    public ListCell<User> call(ListView<User> p) {

        ListCell<User> cell = new ListCell<User>(){

            @Override
            protected void updateItem(User user, boolean bln) {
                super.updateItem(user, bln);
                setText(null);
                setBackground(new Background(new BackgroundFill(Color.web("#36393f"), new CornerRadii(0), Insets.EMPTY)));
                if (user != null) {
                    HBox hBox = new HBox();

                    Text name = new Text("  "+user.getName());
                    name.setFont(Font.font ("Calibri", 18));
                    name.setFill(Color.WHITE);


                    ImageView statusImageView = new ImageView();
                    Image statusImage = new Image(Main.class.getResource("images/" + user.getStatus().toLowerCase() + ".png").toString(), 16, 16,true,true);
                    statusImageView.setImage(statusImage);

                    Image image = new Image(user.getPicture(),50,50,true,true);
                    Circle circle = new Circle(20);
                    DropShadow ds = new DropShadow();
                    ds.setOffsetX(0.5);
                    ds.setOffsetY(0.5);
                    ds.setHeight(10.0);
                    ds.setWidth(10.0);
                    ds.setColor(Color.BLACK);
                    circle.setEffect(ds);
                    circle.setFill(new ImagePattern(image));

                    hBox.getChildren().addAll(statusImageView, circle, name);
                    hBox.setAlignment(Pos.CENTER_LEFT);

                    setGraphic(hBox);
                }
            }
        };
        return cell;
    }
}