package gui.partials;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import model.FoeStage;
import model.Stage;

public class StageView extends BorderPane {

    private Stage stage;
    private CardView stageCard;
    private DeckView weaponsView;
    private ObservableList<CardView> weapons;

    public StageView() {
        stageCard = new CardView();
        stageCard.setSize(200);
        stageCard.getButtonBox().setVisible(true);
        stageCard.getPlayButton().setVisible(false);
        stageCard.getDiscardButton().setText("Remove");
        setAlignment(stageCard, Pos.CENTER);
        this.setTop(stageCard);

        weaponsView = new DeckView();
        weaponsView.setSize(225);
        this.weapons = FXCollections.observableArrayList();

    }

    public StageView(Stage s) {
        this();
        setStage(s);
    }

    public void setStage(Stage s) {
        this.stage = s;
        this.stageCard.setCard(s.getStageCard());
        this.weapons.clear();

        if (s instanceof FoeStage) {
            ((FoeStage) s).getWeapons().forEach(w -> {
                weapons.add(new CardView(w));
            });
        }
    }

    public void stageHidden(boolean hidden) {
        // hide card and info
        weaponsView.setVisible(hidden);
        stageCard.setVisible(hidden);
    }

    public void clearStage() {
        this.weapons.clear();

    }

}
