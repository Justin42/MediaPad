package me.justinb.mediapad.audio;

import de.jarnbjo.ogg.EndOfOggStreamException;
import de.jarnbjo.ogg.FileStream;
import de.jarnbjo.ogg.LogicalOggStream;
import me.justinb.mediapad.exception.UnsupportedFileException;
import org.jitsi.impl.neomedia.codec.audio.opus.Opus;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Justin Baldwin on 10/5/2014.
 */
public class OpusAudioPlayer extends AudioPlayer {
    private static final List<String> supportedFormats = Arrays.asList("opus");
    private static final Logger logger = Logger.getLogger(OpusAudioPlayer.class.getName());
    private static final int BUFFER_SIZE = 1024 * 1024;
    private static final int INPUT_BITRATE = 48000;
    private static final int OUTPUT_BITRATE = 48000;
    private static final int SAMPLE_SIZE = 16;

    private FileStream oggFile;
    private long opusState;
    private AudioFormat audioFormat;
    private final ArrayList<OpusHeader> opusHeaders = new ArrayList<>();
    private final ByteBuffer decodeBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private SourceDataLine speaker;

    public static List<String> getSupportedFormats() {
        return supportedFormats;
    }

    protected OpusAudioPlayer(File audioFile) throws IOException, UnsupportedFileException {
        super(audioFile);
        // Check for supported format
        if(!getSupportedFormats().stream().anyMatch(audioFile.getName()::endsWith)) {
            throw new UnsupportedFileException(this.getClass().getName() + " does not support this format.");
        }

        // Read headers and initialize decoder
        oggFile = new FileStream(new RandomAccessFile(audioFile, "r"));
        opusHeaders.addAll(readHeaders(oggFile));
        opusState = Opus.decoder_create(INPUT_BITRATE, opusHeaders.get(0).getChannels());
        audioFormat = new AudioFormat(OUTPUT_BITRATE, SAMPLE_SIZE, opusHeaders.get(0).getChannels(), true, false);
    }

    private byte[] decode(byte[] packetData) {
        int frameSize = Opus.decoder_get_nb_samples(opusState, packetData, 0, packetData.length);
        int decodedSamples = Opus.decode(opusState, packetData, 0, packetData.length, decodeBuffer.array(), 0, frameSize, 0);
        if(decodedSamples < 0) {
            logger.severe(String.format("Decode error %d", decodedSamples));
            decodeBuffer.clear();
            return null;
        }
        decodeBuffer.position(decodedSamples * 2 * opusHeaders.get(0).getChannels()); // 2 bytes per sample per channel
        decodeBuffer.flip();
        byte[] decodedData = new byte[decodeBuffer.remaining()];
        decodeBuffer.get(decodedData);
        decodeBuffer.flip();
        return decodedData;
    }

    @Override
    public boolean play() {
        if(state == State.PLAYING) return false;
        audioManager.getAudioExecutor().execute(() -> {
            try {
                if(speaker == null) speaker = AudioSystem.getSourceDataLine(audioFormat);
                if(!oggFile.isOpen()) oggFile = new FileStream(new RandomAccessFile(audioFile, "r"));
                if(state == State.STOPPED) readHeaders(oggFile);

                speaker.addLineListener(audioManager);
                speaker.open();
                speaker.start();
                logger.info("Starting audio playback");
                LogicalOggStream stream = (LogicalOggStream)oggFile.getLogicalStreams().toArray()[0];
                byte[] nextPacket = stream.getNextOggPacket();
                state = State.PLAYING;
                logger.info("Beginning decoding...");
                while(nextPacket != null && state == State.PLAYING) {
                    byte[] decodedData = decode(nextPacket);
                    if(decodedData != null) {
                        speaker.write(decodedData, 0, decodedData.length);
                    }
                    try { nextPacket = stream.getNextOggPacket();
                    } catch(EndOfOggStreamException eos) {
                        logger.info("Reached end of stream.");
                        state = State.STOPPED;
                        break;
                    }
                }
                if(state == State.STOPPED) {
                    logger.info("Finished decoding or playback stopped, closing resources.");
                    stop();
                }
                if(state == State.PAUSED) {
                    logger.info("Audio paused.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {oggFile.close(); speaker.close();}catch(Exception ignored){};
            }
        });
        return true;
    }

    public ArrayList<OpusHeader> readHeaders(FileStream fileStream) throws IOException {
        ArrayList<OpusHeader> headers = new ArrayList<>();
        for (LogicalOggStream stream : fileStream.getLogicalStreams()) {
            // TODO handle multiple logical streams?
            opusHeaders.add(OpusHeader.parse(stream.getNextOggPacket(), stream.getNextOggPacket()));
        }
        return headers;
    }

    @Override
    public boolean pause() {
        if(state == State.STOPPED) return false;
        else {
            state = State.PAUSED;
            return true;
        }
    }

    @Override
    public boolean stop() {
        state = State.STOPPED;
        if(speaker != null) {
            speaker.stop();
            speaker.close();
        }
        try {
            oggFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void destroy() {
        Opus.decoder_destroy(opusState);
    }
}
