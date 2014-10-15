package me.justinb.mediapad.elements;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import me.justinb.mediapad.audio.AudioPlayer;

/**
 * Created by Justin Baldwin on 10/13/2014.
 */
public class AudioControls extends BorderPane {
    private final AudioPlayer audioPlayer;
    private final Button playButton;
    private final Button pauseButton;
    private final Button stopButton;
    private final HBox hBox;

    public AudioControls(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        playButton = new Button("Play");
        pauseButton = new Button("Pause");
        stopButton = new Button("Stop");

        stopButton.setOnMouseClicked(event -> audioPlayer.stop());
        playButton.setOnMouseClicked(event -> audioPlayer.play());
        pauseButton.setOnMouseClicked(event -> audioPlayer.pause());

        hBox = new HBox(playButton, pauseButton, stopButton);
        setCenter(hBox);
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }



}
