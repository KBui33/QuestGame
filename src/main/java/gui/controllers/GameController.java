package gui.controllers;

import gui.panes.GamePane;

/**
 * @author James DiNovo
 *
 * Controller for manipulating GamePane view
 *
 */
public class GameController {
    public GameController (GamePane view) {
        setView(view);
    }

    public void setView(GamePane view) {
        // a lot of this is just for laying out gui will be removed later
        view.getCurrentStateText().setText("Your turn!");
        view.getShieldsView().setShields(7);

        for (int i = 1; i <= 11; i++) {
            view.getMyHand().addCard("/specials/quest_ally_" + i + ".png");
        }
        for (int i = 1; i <= 11; i++) {
            view.getDiscardedCards().addCard("/foes/quest_foe_" + i + ".png");
        }

        view.getShowHandButton().setOnAction(e -> {
            System.out.println("showing hand");
            if (view.getBottom() != null && view.getBottom().equals(view.getMyHand().node())) {
                view.setBottom(null);
                view.getShowHandButton().getStyleClass().remove("caution");
            } else {
                view.setBottom(view.getMyHand().node());
                view.getShowHandButton().getStyleClass().add("caution");
                view.getShowDiscardedButton().getStyleClass().remove("caution");
            }
        });

        view.getShowDiscardedButton().setOnAction(e -> {
            System.out.println("showing discarded");
            if (view.getBottom() != null && view.getBottom().equals(view.getDiscardedCards().node())) {
                view.setBottom(null);
                view.getShowDiscardedButton().getStyleClass().remove("caution");
            } else {
                view.setBottom(view.getDiscardedCards().node());
                view.getShowDiscardedButton().getStyleClass().add("caution");
                view.getShowHandButton().getStyleClass().remove("caution");
            }
        });


    }
}
