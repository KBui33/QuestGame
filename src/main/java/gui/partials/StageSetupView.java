package gui.partials;

import component.card.Card;
import component.card.FoeCard;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;

/**
 * @author James DiNovo
 *
 * GUI for selecting cards to be used on a given stage
 */
public class StageSetupView extends BorderPane {
    private CardView stageCard;
    private DeckView weaponsView;

    public CardView getStageCard() {
        return stageCard;
    }

    public DeckView getWeaponsView() {
        return weaponsView;
    }

    public StageSetupView(Card card) {
        stageCard = new CardView(card);
        stageCard.setSize(200);
        stageCard.getButtonBox().setVisible(true);
        stageCard.getPlayButton().setVisible(false);
        stageCard.getDiscardButton().setText("Remove");
        setAlignment(stageCard, Pos.CENTER);
        this.setTop(stageCard);

        weaponsView = new DeckView();
        weaponsView.setSize(225);
        if (card instanceof FoeCard) {
            this.setCenter(weaponsView.getListView());
        } else {
            weaponsView.setVisible(false);
        }
    }
}
