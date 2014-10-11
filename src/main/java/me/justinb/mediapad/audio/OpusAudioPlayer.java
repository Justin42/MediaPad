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

    public static List<String> getSupportedFormats() {
        return supportedFormats;
    }

    protected OpusAudioPlayer(File audioFile) throws IOException, UnsupportedFileException {
        super(audioFile);
        // Check for supported format
        boolean fileSupported = false;
        for(String format : getSupportedFormats()) {
            if(audioFile.getName().endsWith(format)) {
                fileSupported = true;
                break;
            }
        }
        if(!fileSupported) {
            throw new UnsupportedFileException(this.getClass().getName() + " does not support this format.");
        }

        // Read headers and initialize decoder
        oggFile = new FileStream(new RandomAccessFile(audioFile, "r"));
        for (LogicalOggStream stream : oggFile.getLogicalStreams()) {
            // TODO handle multiple logical streams?
            opusHeaders.add(OpusHeader.parse(stream.getNextOggPacket(), stream.getNextOggPacket()));
        }
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
        if(isPlaying | audioManager.hasOpen()) return false;
        audioManager.getAudioExecutor().execute(() -> {
            try(SourceDataLine speaker = AudioSystem.getSourceDataLine(audioFormat)){
                speaker.addLineListener(audioManager);
                speaker.open();
                speaker.start();
                logger.info("Starting audio playback");
                LogicalOggStream stream = (LogicalOggStream)oggFile.getLogicalStreams().toArray()[0];
                byte[] nextPacket = stream.getNextOggPacket();
                isPlaying = true;
                logger.info("Beginning decoding...");
                while(nextPacket != null && isPlaying) {
                    byte[] decodedData = decode(nextPacket);
                    if(decodedData != null) {
                        speaker.write(decodedData, 0, decodedData.length);
                    }
                    try { nextPacket = stream.getNextOggPacket();
                    } catch(EndOfOggStreamException eos) {
                        logger.info("Reached end of stream.");
                        break;
                    }
                }
                logger.info("Finished decoding, closing resources.");
                speaker.drain();
                speaker.stop();
                speaker.close();
                oggFile.close();
            } catch (Exception e) {
                e.printStackTrace();
                try {oggFile.close();}catch(Exception ignored){};
            }
        });
        return true;
    }

    public void destroy() {
        Opus.decoder_destroy(opusState);
    }
}
