package me.justinb.mediapad.elements;

import me.justinb.mediapad.audio.AudioPlayer;
import me.justinb.mediapad.audio.AudioPlayerFactory;
import me.justinb.mediapad.exception.UnsupportedFileException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Justin Baldwin on 10/11/2014.
 */
public class AudioTab extends FileTab {
    private static final List<String> supportedFormats = Arrays.asList("opus");

    public AudioPlayer audioPlayer;
    public AudioControls audioControls;

    public static List<String> getSupportedFormats() {
        return supportedFormats;
    }

    public AudioTab(String name, File file) throws IOException, UnsupportedFileException {
        super(name, file);
        audioPlayer = AudioPlayerFactory.createAudioPlayer(file);
        audioControls = new AudioControls(audioPlayer);
        setContent(audioControls);
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public AudioControls getAudioControls() { return audioControls; }
}
