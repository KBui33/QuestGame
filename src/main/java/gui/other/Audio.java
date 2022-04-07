package gui.other;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


/**
 * @author James DiNovo
 *
 * Manages mediaplayer. Plays game audio through entire game.
 */
public class Audio {

    private static MediaPlayer mediaPlayer;

    public static void theme() {

        Media media = new Media(String.valueOf(Audio.class.getResource("/audio/CelticAmbiance.mp3")));
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.1);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.play();

    }

    public static void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }

    public static boolean isPlaying() {
        return mediaPlayer != null;
    }
}
