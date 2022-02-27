package gui.panes;

import gui.main.ClientApplication;
import gui.partials.CardView;
import gui.scenes.LobbyScene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class GamePane extends BorderPane {

    public GamePane() {

        CardView allyCards = new CardView();
        CardView foeCards = new CardView();


        for (int i = 1; i <= 11; i++) {
            allyCards.addCard("/specials/quest_ally_" + i + ".png");
        }
        for (int i = 1; i <= 11; i++) {
            foeCards.addCard("/foes/quest_foe_" + i + ".png");
        }

        this.setBottom(foeCards.node());

        Button toggleButton = new Button("Switch Deck");
        toggleButton.setPrefSize(150, 25);
        toggleButton.getStyleClass().add("caution");
        toggleButton.setOnAction(e -> {
            System.out.println("Switching deck");
            if (this.getBottom() != null && this.getBottom().equals(allyCards.node())) {
                this.setBottom(foeCards.node());
            } else {
                this.setBottom(allyCards.node());
            }
        });

        this.setCenter(toggleButton);
    }
}
