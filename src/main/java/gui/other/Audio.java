package gui.other;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class Audio {
    private static final URL theme = Audio.class.getResource("/audio/CelticAmbiance.mp3");

    private static MediaPlayer mediaPlayer;

    public static void theme() {

        assert theme != null;
        Media media = new Media(theme.toString());
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
