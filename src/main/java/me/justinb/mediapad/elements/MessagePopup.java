package me.justinb.mediapad.elements;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by Justin Baldwin on 10/12/2014.
 */
public class MessagePopup {
    private static Label messageLabel = new Label();
    private static Stage window = new Stage(StageStyle.UTILITY);
    private static BorderPane borderPane = new BorderPane(messageLabel);
    private static Scene scene = new Scene(borderPane, 300, 100);
    private static Button okButton = new Button("Ok");
    private static AnchorPane buttonPane = new AnchorPane(okButton);
    static {
        window.setScene(scene);
        AnchorPane.setRightAnchor(okButton, 14d);
        borderPane.setBottom(buttonPane);

        okButton.setOnMouseClicked(event -> {
            window.hide();
        });
    }

    public static void display(PopupType type, String message) {
        window.setTitle(type.getTitle());
        messageLabel.setText(message);
        window.show();
    }
}
