package gui.partials;

import game.components.card.Card;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * @author James DiNovo
 *
 * View for sponsors to set up quest
 */
public class QuestSetupView extends BorderPane {

    private CardView chosenQuestCard;
    private Text promptText;
    private VBox topBox;
    private HBox stageBox;
    private Button nextStageButton;
    public static String STAGE_PROMPT = "Choose a card for stage ";

    public CardView getChosenQuestCard() {
        return chosenQuestCard;
    }

    public Text getPromptText() {
        return promptText;
    }

    public Button getNextStageButton() {
        return nextStageButton;
    }

    public StageSetupView setStageSetupView(Card card) {
        StageSetupView ssv = new StageSetupView(card);
        this.setCenter(ssv);
        return ssv;
    }

    public void clearStage() {
        this.setCenter(null);
    }

    public QuestSetupView(Card card) {
        this.setMaxSize(500, 700);
        setMargin(this, new Insets(20));
        topBox = new VBox();
        topBox.setSpacing(5);
        topBox.setAlignment(Pos.CENTER);
        setAlignment(topBox, Pos.CENTER);

        stageBox = new HBox();
        stageBox.setSpacing(5);
        stageBox.setAlignment(Pos.CENTER);
        setAlignment(stageBox, Pos.CENTER);

        chosenQuestCard = new CardView(card);
        chosenQuestCard.setSize(150);
        chosenQuestCard.getPlayButton().setVisible(false);
        chosenQuestCard.getDiscardButton().setVisible(true);
        chosenQuestCard.getDiscardButton().setText("Decline");
        setAlignment(chosenQuestCard, Pos.CENTER);

        promptText = new Text();
        promptText.getStyleClass().add("body-font");

        nextStageButton = new Button("Next");
        nextStageButton.getStyleClass().add("success");
        nextStageButton.setVisible(false);


        stageBox.getChildren().addAll(promptText, nextStageButton);
        topBox.getChildren().addAll(chosenQuestCard, stageBox);
        this.setTop(topBox);

    }



}
