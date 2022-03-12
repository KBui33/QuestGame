package gui.partials;

import javafx.scene.layout.AnchorPane;
import model.FoeStage;
import model.Stage;

import java.util.ArrayList;

public class StageView extends AnchorPane {

    Stage stage;
    CardView stageCard;
    ArrayList<CardView> weapons;

    public StageView() {
        stageCard = new CardView();
        stageCard.setSize(200);


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

    public void clearStage() {

    }

}
