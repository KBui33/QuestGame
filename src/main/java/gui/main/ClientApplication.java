package gui.main;

import javafx.application.Application;
import javafx.stage.Stage;
import gui.scenes.ConnectScene;

/**
 * @author James DiNovo
 *
 * Client application launcher and scene manager
 *
 */
public class ClientApplication extends Application {

    public static Stage window;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        window = stage;

        // launch connect scene for user to enter server address
        window.setScene(new ConnectScene());

        window.setTitle("Quest");
        window.setResizable(false);
        window.centerOnScreen();
        window.show();
    }
}
