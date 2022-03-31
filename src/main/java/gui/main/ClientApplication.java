package gui.main;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import gui.scenes.ConnectScene;

import java.util.Objects;

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

        Font.loadFont(String.valueOf(ConnectScene.class.getResource("/fonts/GentiumBasic-Regular.ttf")), 12);
        Font.loadFont(String.valueOf(ConnectScene.class.getResource("/fonts/GentiumBasic-Italic.ttf")), 12);
        Font.loadFont(String.valueOf(ConnectScene.class.getResource("/fonts/GentiumBasic-Bold.ttf")), 12);

        // launch connect scene for user to enter server address
        window.setScene(new ConnectScene());

        window.setTitle("Quest");
        window.setResizable(false);
        window.centerOnScreen();
        window.show();
    }
}
