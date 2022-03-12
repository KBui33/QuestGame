package gui.partials;

import javafx.scene.layout.BorderPane;

import java.util.ArrayList;

public class StageSetupView extends BorderPane {
    private CardView stageCard;
    private ArrayList<CardView> weapons;

    public StageSetupView() {
        stageCard = new CardView();
    }
}
