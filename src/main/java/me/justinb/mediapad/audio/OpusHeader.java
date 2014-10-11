package me.justinb.mediapad.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by Justin Baldwin on 10/9/2014.
 */
public class OpusHeader {
    private static Logger logger = Logger.getLogger(OpusHeader.class.getName());
    private static final String HEADER_ID = "OpusHead";
    private static final String HEADER_TAG = "OpusTags";
    private static final byte MAX_COMPATIBLE_VERSION = 15;

    private int channels;
    private int version;
    private int channelMapping;
    private int preSkip;
    private long inputSampleRate;
    private int outputGain;
    private int streamCount;
    private int streamCount_2ch;
    private String vendor = "";
    private HashMap<String, String> tags = new HashMap<>();

    private OpusHeader() {
    }

    public static OpusHeader parse(byte[] idHeader, byte[] commentHeader) {
        OpusHeader opusHeader = new OpusHeader();

        ByteBuffer buffer = ByteBuffer.wrap(idHeader);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Parse ID Header
        logger.info("Parsing ID header");
        for(char c : HEADER_ID.toCharArray()) {
            if(buffer.get() != c) {
                logger.severe("Not an opus header packet");
                return null;
            }
        }
        opusHeader.version = buffer.get();
        if(opusHeader.version > MAX_COMPATIBLE_VERSION) {
            logger.severe("Opus header reports unsupported version");
            return null;
        }
        opusHeader.channels = buffer.get();
        if(opusHeader.channels <= 0) {
            logger.severe("Opus header reports invalid number of channels.");
            return null;
        }
        opusHeader.preSkip = buffer.getShort();
        opusHeader.inputSampleRate = buffer.getInt();
        opusHeader.outputGain = buffer.getShort();
        opusHeader.channelMapping = buffer.get();
        if(opusHeader.channelMapping > OpusChannelMapping.SINGLE_STREAM) {
            if(opusHeader.channelMapping > OpusChannelMapping.VORBIS) { // Reserved
                logger.warning("Opus header uses reserved channel mapping.");
            }
            opusHeader.streamCount = buffer.get();
            if(opusHeader.streamCount <= 0) {
                logger.warning("Invalid stream count specified in header");
            }
            opusHeader.streamCount_2ch = buffer.get();
            if(opusHeader.streamCount_2ch > opusHeader.streamCount | opusHeader.streamCount_2ch + opusHeader.streamCount > 255) {
                logger.warning("Invalid channel mapping data");
            }
            // Todo implement channel mapping
        }

        // Parse comment header
        logger.info("Parsing comment header...");
        buffer = ByteBuffer.wrap(commentHeader);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for(char c : HEADER_TAG.toCharArray()) {
            if(buffer.get() != c) {
                logger.warning("Not an opus header packet");
            }
        }
        byte[] vendor = new byte[buffer.getInt()];
        buffer.get(vendor);
        opusHeader.vendor = new String(vendor);
        long tagCount = buffer.getInt();
        for(int i = 0; i < tagCount; i++) {
            byte[] tagKey = new byte[buffer.getInt()];
            buffer.get(tagKey);
            String[] tagKeyArray = new String(tagKey).split("=");
            opusHeader.tags.put(tagKeyArray[0], tagKeyArray[1]);
        }

        logger.info("Parsed ID Header and Comment Headers\n" + opusHeader.toString());

        return opusHeader;
    }

    public int getChannels() {
        return channels;
    }

    public int getVersion() {
        return version;
    }

    public int getPreSkip() {
        return preSkip;
    }

    public long getInputSampleRate() {
        return inputSampleRate;
    }

    public int getOutputGain() {
        return outputGain;
    }

    public String getVendor() {
        return vendor;
    }

    public String getTag(String tagName) {
        return tags.get(tagName);
    }

    public String[] getTags() {
        return tags.keySet().toArray(new String[tags.size()]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(250);
        sb.append("Encoded with ").append(vendor).append("\n");
        sb.append("User comments section follows...\n");
        for(String tag : tags.keySet()) {
            sb.append(tag).append("=").append(tags.get(tag)).append("\n");
        }
        sb.append("Opus stream: \n");
        sb.append("Pre-skip: ").append(preSkip).append("\n");
        sb.append("Playback gain: ").append(outputGain).append("\n");
        sb.append("Channels: ").append(channels).append("\n");
        sb.append("Original sample rate: ").append(inputSampleRate).append("\n");
        return sb.toString();
    }
}
