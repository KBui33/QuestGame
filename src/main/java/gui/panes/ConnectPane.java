package gui.panes;

import gui.main.ClientApplication;
import gui.scenes.LobbyScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import static gui.main.Const.BODY_FONT;
import static gui.main.Const.HEADER_FONT;

/**
 * @author James DiNovo
 *
 * ConnectPane contains the GUI for entering a server address
 *
 */
public class ConnectPane extends BorderPane {
    public ConnectPane() {

        VBox vBox = new VBox();

        Text header = new Text("Quest");
        header.setFont(HEADER_FONT);

        Label serverAddressLabel = new Label("Server Address");
        serverAddressLabel.setFont(BODY_FONT);

        TextField serverAddress = new TextField();
        serverAddress.setMaxWidth(200);
        serverAddress.setAlignment(Pos.CENTER);
        serverAddress.setText("127.0.0.1");

        Button connectButton = new Button("Connect");
        connectButton.setPrefSize(100, 25);
        connectButton.getStyleClass().add("success");
        connectButton.setOnAction(e -> {
            System.out.println("Connecting to " + serverAddress.getText().trim() + "...");
            ClientApplication.window.setScene(new LobbyScene());
        });


        vBox.getChildren().addAll(serverAddressLabel, serverAddress, connectButton);
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);

        this.setTop(header);
        setAlignment(header, Pos.CENTER);
        setMargin(header, new Insets(20));
        this.setCenter(vBox);

    }
}
