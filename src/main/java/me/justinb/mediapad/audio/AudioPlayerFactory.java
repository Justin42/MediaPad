package me.justinb.mediapad.audio;

import me.justinb.mediapad.exception.UnsupportedFileException;

import java.io.File;
import java.io.IOException;

/**
 * Created by Justin Baldwin on 10/9/2014.
 */
public class AudioPlayerFactory {
    private AudioPlayerFactory() {
    }

    public static AudioPlayer createAudioPlayer(File audioFile) throws IOException, UnsupportedFileException {
        for(String format : OpusAudioPlayer.getSupportedFormats()) {
            if(audioFile.getName().endsWith(format)) {
                return new OpusAudioPlayer(audioFile);
            }
        }
        throw new UnsupportedFileException(audioFile.getName() + " unsupported audio format.");
    }
}
