package me.justinb.mediapad.elements;

import javafx.scene.canvas.Canvas;
import me.justinb.mediapad.audio.Waveform;
import me.justinb.mediapad.util.Quartet;

/**
 * Created by Justin Baldwin on 10/17/2014.
 */
public class WaveformDisplay extends Canvas {
    public WaveformDisplay(double width, double height) {
        super(width, height);
    }

    public void displayWaveform(Waveform waveform) {
        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
        for(Quartet<Double> ls : waveform.getSegments()) {
            getGraphicsContext2D().strokeLine(ls.getValue1(), ls.getValue2(), ls.getValue3(), ls.getValue4());
            System.out.println(String.format("Line: %s %s %s %s", ls.getValue1(), ls.getValue2(), ls.getValue3(), ls.getValue4()));
        }
    }
}
