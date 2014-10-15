package me.justinb.mediapad.audio;

import java.io.File;

/**
 * Created by Justin Baldwin on 10/5/2014.
 */
public abstract class AudioPlayer {
    enum State {
        PLAYING, PAUSED, STOPPED
    }
    protected final File audioFile;
    protected final AudioManager audioManager;
    protected State state;

    public AudioPlayer(File audioFile) {
        this.audioFile = audioFile;
        audioManager = AudioManager.getInstance();
        audioManager.registerPlayer(this);
    }

    public File getAudioFile() {
        return audioFile;
    }

    public abstract boolean play();
    public abstract boolean pause();
    public abstract boolean stop();
}
