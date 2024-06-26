package gui.panes;

import gui.partials.AudioControlButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * @author James DiNovo
 *
 * ConnectPane contains the GUI for entering a server address
 *
 */
public class ConnectPane extends BorderPane {

    private VBox mainColumn;
    private Text header;
    private Label serverAddressLabel;
    private TextField serverAddress;
    private Button connectButton;
    private AudioControlButton audioControlButton;

    public TextField getServerAddress() {
        return serverAddress;
    }

    public Button getConnectButton() {
        return connectButton;
    }

    public ConnectPane() {

        VBox mainColumn = new VBox();

        header = new Text("Quest");
        header.getStyleClass().add("header-font");

        serverAddressLabel = new Label("Server Address");
        serverAddressLabel.getStyleClass().add("body-font");

        serverAddress = new TextField();
        serverAddress.setMaxWidth(200);
        serverAddress.setAlignment(Pos.CENTER);
        serverAddress.setText("127.0.0.1");

        connectButton = new Button("Connect");
        connectButton.getStyleClass().add("success");

        audioControlButton = new AudioControlButton();

        mainColumn.getChildren().addAll(serverAddressLabel, serverAddress, connectButton);
        mainColumn.setSpacing(10);
        mainColumn.setAlignment(Pos.CENTER);

        this.setTop(header);
        setAlignment(header, Pos.CENTER);
        setMargin(header, new Insets(20));
        this.setCenter(mainColumn);
        this.setBottom(audioControlButton);
        setAlignment(audioControlButton, Pos.BOTTOM_LEFT);
        setMargin(audioControlButton, new Insets(5));

    }
}
