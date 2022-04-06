package gui.partials;

import gui.other.Audio;
import javafx.scene.control.Button;

public class AudioControlButton extends Button {

    public AudioControlButton() {
        if (Audio.isPlaying()) {
            this.setText("Mute");
        } else {
            this.setText("Unmute");
        }
        this.getStyleClass().add("caution");
        this.setMaxWidth(90);

        this.setOnAction(e -> {
            if (this.getText().equals("Mute")) {
                this.setText("Unmute");
                Audio.stop();
            } else {
                this.setText("Mute");
                Audio.theme();
            }
        });
    }
}
