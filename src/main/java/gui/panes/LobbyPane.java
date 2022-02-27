package gui.panes;

import gui.main.ClientApplication;
import gui.scenes.ConnectScene;
import gui.scenes.GameScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import static gui.main.Const.BODY_FONT;
import static gui.main.Const.HEADER_FONT;

public class LobbyPane extends BorderPane {
    public LobbyPane() {
        Text header = new Text("Connected to Lobby");
        header.setFont(HEADER_FONT);
        setAlignment(header, Pos.CENTER);
        setMargin(header, new Insets(20));

        VBox infoBox = new VBox();
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setSpacing(10);

        Text serverText = new Text("Server Address: ");
        serverText.setFont(BODY_FONT);

        Text playersText = new Text("Players Connected: ");
        playersText.setFont(BODY_FONT);

        Text statusText = new Text("Waiting for players...");
        statusText.setFont(BODY_FONT);

        infoBox.getChildren().addAll(serverText, playersText, statusText);

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);
        setMargin(buttonBox, new Insets(20));

        Button readyButton = new Button("Ready");
        readyButton.setPrefSize(150, 25);
        readyButton.getStyleClass().add("success");
        readyButton.setOnAction(e -> {
            System.out.println("Ready clicked");

            ClientApplication.window.setScene(new GameScene());

            if (readyButton.getText().equals("Ready")) {
                readyButton.getStyleClass().remove("success");
                readyButton.getStyleClass().add("caution");
                readyButton.setText("Wait");
            } else {
                readyButton.getStyleClass().remove("caution");
                readyButton.getStyleClass().add("success");
                readyButton.setText("Ready");
            }
        });

        Button leaveButton = new Button("Disconnect");
        leaveButton.setPrefSize(150, 25);
        leaveButton.getStyleClass().add("warn");
        leaveButton.setOnAction(e -> {
            System.out.println("Disconnecting");
            ClientApplication.window.setScene(new ConnectScene());
        });

        buttonBox.getChildren().addAll(leaveButton, readyButton);

        this.setTop(header);
        this.setCenter(infoBox);
        this.setBottom(buttonBox);
    }
}
