package gui.other;

import gui.scenes.LobbyScene;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogEvent;
import javafx.stage.Stage;

/**
 * @author James DiNovo
 *
 * Alert pop up
 */
public class AlertBox {

    public static void alert(String message) {
        alert(message, AlertType.INFORMATION);
    }

    public static void alert(String message, AlertType at) {
        alert(message, at, null);
    }

    public static void alert(String message, AlertType at, EventHandler<DialogEvent> e) {
        Alert a = new Alert(at, message);
        // get rid of ugly header
        a.setHeaderText(null);
        // apply css
        a.getDialogPane().getScene().getStylesheets().add(String.valueOf(AlertBox.class.getResource("/styles/style.css")));
        // ensure pops up on top of scene
        ((Stage) a.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
        ((Stage) a.getDialogPane().getScene().getWindow()).toFront();
        // apply any given handler
        a.setOnCloseRequest(e);
        // freeze app until user closes
        a.showAndWait();
    }
}
