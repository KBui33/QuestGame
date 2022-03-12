package gui.other;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class AlertBox {

    public static void alert(String message) {
        alert(message, AlertType.INFORMATION);
    }

    public static void alert(String message, AlertType at) {
        Alert a = new Alert(at, message);
        a.setHeaderText(null);
        ((Stage) a.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
        ((Stage) a.getDialogPane().getScene().getWindow()).toFront();
        a.showAndWait();
    }
}
