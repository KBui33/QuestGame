package gui.partials;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;

public class QuestSetupView extends BorderPane {

    CardView chosenStageCard;
    ArrayList<CardView> chosenWeaponCards;

    public QuestSetupView() {
        chosenStageCard = new CardView();
        chosenStageCard.setSize(200);
        setAlignment(chosenStageCard, Pos.CENTER);
        this.setTop(chosenStageCard);

        
    }



}
