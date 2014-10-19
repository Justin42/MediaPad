package me.justinb.mediapad.audio;

import me.justinb.mediapad.util.Quartet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by Justin Baldwin on 10/15/2014.
 */
public class Waveform {
    private static Logger logger = Logger.getLogger(Waveform.class.getName());
    private double x = 0;
    private double lastY = 0;
    private ByteBuffer convertBuffer = ByteBuffer.allocate(1024);
    private ArrayList<Quartet<Double>> lineSegments = new ArrayList<>();
    private final double width;
    private final double height;
    private final double pixelsPerSample;
    private final double maxSamplesPerFrame;

    public Waveform(double width, double height, double pixelsPerSample, double maxSamplesPerFrame) {
        this.width = width;
        this.height = height;
        this.pixelsPerSample = pixelsPerSample;
        this.maxSamplesPerFrame = maxSamplesPerFrame;
    }

    public void addFrame(byte[] frame) {
        if(frame.length < 2) return;
        int max = Short.MAX_VALUE;
        int min = Short.MIN_VALUE;
        convertBuffer.clear();
        convertBuffer.put(frame);
        convertBuffer.flip();
        double frameCount = 0;
        while(convertBuffer.remaining() >= Short.BYTES && x < width && frameCount < maxSamplesPerFrame) {
            double value = convertBuffer.getShort();
            double y = Math.abs((height / max) * value - height);
            lineSegments.add(new Quartet<>(x, lastY, x, y));
            lastY = y;
            x += pixelsPerSample;
            frameCount++;
        }
    }

    public double getWidth() {
        return width;
    }

    public double getheight() {
        return height;
    }

    public double getPixelsPerSample() {
        return pixelsPerSample;
    }

    public ArrayList<Quartet<Double>> getSegments() {
        return lineSegments;
    }
}
