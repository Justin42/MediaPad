package me.justinb.mediapad.audio;

import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Created by Justin Baldwin on 10/10/2014.
 */
public class AudioManager implements LineListener {
    private static final Logger logger = Logger.getLogger(AudioManager.class.getName());
    public static AudioManager instance;
    private final ExecutorService audioExecutor;
    private HashSet<Line> linesOpen = new HashSet<>();
    private HashSet<AudioPlayer> audioPlayers = new HashSet<>();

    private AudioManager() {
        logger.info("Initialized audio manager.");
        audioExecutor = Executors.newSingleThreadExecutor();
    }

    public static AudioManager getInstance() {
        if(instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public ExecutorService getAudioExecutor() {
        return audioExecutor;
    }

    @Override
    public void update(LineEvent event) {
        if(event.getType() == LineEvent.Type.OPEN) {
            linesOpen.add(event.getLine());
            logger.info("Line opened.");
        }
        else if(event.getType() == LineEvent.Type.CLOSE) {
            linesOpen.remove(event.getLine());
            logger.info("Line closed.");
        }
    }

    public boolean hasOpen() {
        return !linesOpen.isEmpty();
    }

    public Line[] getOpenLines() {
        return linesOpen.toArray(new Line[linesOpen.size()]);
    }

    public void stopAll() {
        logger.info("Stopping all audio.");
        for(AudioPlayer player : audioPlayers) {
            player.stop();
        }
    }

    protected void registerPlayer(AudioPlayer player) {
        audioPlayers.add(player);
        logger.info("Registered audio player");
    }
}
