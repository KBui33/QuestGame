package gui.other;

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
        a.setHeaderText(null);
        ((Stage) a.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
        ((Stage) a.getDialogPane().getScene().getWindow()).toFront();
        a.setOnCloseRequest(e);
        a.showAndWait();
    }
}
