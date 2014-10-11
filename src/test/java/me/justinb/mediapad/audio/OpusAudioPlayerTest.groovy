package me.justinb.mediapad.audio
/**
 * Created by Justin Baldwin on 10/6/2014.
 */
class OpusAudioPlayerTest extends GroovyTestCase {
    void testPlay() {
        OpusAudioPlayer opusAudioPlayer = new OpusAudioPlayer(new File(
                "C:\\Users\\Owner\\Desktop\\MediaPad\\Classes\\Math\\test.opus"
        ));
        opusAudioPlayer.play();
    }
}
