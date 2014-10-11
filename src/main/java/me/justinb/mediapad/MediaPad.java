package me.justinb.mediapad;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import me.justinb.mediapad.audio.AudioManager;

public class MediaPad extends Application {
    static {
        System.out.println(System.getProperty("java.library.path"));
        System.loadLibrary("jnopus");
    }
    private Stage primaryStage;
    private BorderPane overviewPane;

    private static MediaPad instance;

    public MediaPad() {
        instance = this;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        overviewPane = FXMLLoader.load(getClass().getResource("controller/overview.fxml"));
        primaryStage.setTitle("MediaPad");
        primaryStage.setScene(new Scene(overviewPane, 800, 600));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> exit());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public BorderPane getOverview() {
        return overviewPane;
    }

    public void exit() {
        AudioManager.getInstance().stopAll();
        AudioManager.getInstance().getAudioExecutor().shutdown();
        System.exit(0);
    }

    public static MediaPad getInstance() {
        return instance;
    }
}
