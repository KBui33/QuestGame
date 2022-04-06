package gui.panes;

import gui.partials.AudioControlButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * @author James DiNovo
 *
 * LobbyPane contains the gui for players waiting to start a game
 */
public class LobbyPane extends BorderPane {
    private Text header, serverText, playersText, statusText;
    private VBox infoBox;
    private HBox buttonBox;
    private Button readyButton, leaveButton;
    private AudioControlButton audioControlButton;

    public Text getHeader() {
        return header;
    }

    public Text getServerText() {
        return serverText;
    }

    public Text getPlayersText() {
        return playersText;
    }

    public Text getStatusText() {
        return statusText;
    }

    public Button getReadyButton() {
        return readyButton;
    }

    public Button getLeaveButton() {
        return leaveButton;
    }

    public LobbyPane() {
        header = new Text("Connected to Lobby");
        header.getStyleClass().add("header-font");
        setAlignment(header, Pos.CENTER);
        setMargin(header, new Insets(20));

        infoBox = new VBox();
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setSpacing(10);

        serverText = new Text("Server Address: ");
        serverText.getStyleClass().add("body-font");

        playersText = new Text("Players Connected: ");
        playersText.getStyleClass().add("body-font");

        statusText = new Text("Waiting for players...");
        statusText.getStyleClass().add("body-font");

        infoBox.getChildren().addAll(serverText, playersText, statusText);

        buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);
        setMargin(buttonBox, new Insets(20));

        readyButton = new Button("Ready");
        readyButton.setPrefSize(150, 25);
        readyButton.getStyleClass().add("success");

        leaveButton = new Button("Disconnect");
        leaveButton.setPrefSize(150, 25);
        leaveButton.getStyleClass().add("warn");

        audioControlButton = new AudioControlButton();
        setAlignment(audioControlButton, Pos.CENTER_LEFT);
        setMargin(audioControlButton, new Insets(5));

        buttonBox.getChildren().addAll(leaveButton, readyButton);

        BorderPane bp = new BorderPane();
        bp.setLeft(audioControlButton);
        bp.setCenter(buttonBox);

        this.setTop(header);
        this.setCenter(infoBox);
        this.setBottom(bp);
    }
}
