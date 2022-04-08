package gui.partials.quest;

import component.card.Card;
import gui.partials.CardView;
import gui.partials.quest.StageSetupView;
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
    private Button nextStageButton, backButton;
    private StageSetupView stageSetupView;
    public static String STAGE_PROMPT = "Set up stage ";

    public CardView getChosenQuestCard() {
        return chosenQuestCard;
    }

    public StageSetupView getStageSetupView() {
        return stageSetupView;
    }

    public Text getPromptText() {
        return promptText;
    }

    public Button getNextStageButton() {
        return nextStageButton;
    }

    public Button getBackButton() {
        return backButton;
    }

    public StageSetupView setStageSetupView(Card card) {
        stageSetupView.setStageCard(card);
        this.setCenter(stageSetupView);
        return stageSetupView;
    }

    public void clearStage() {
        this.setCenter(null);
        getStageSetupView().getStageCard().clearCard();
        getStageSetupView().getWeaponsView().getListView().getItems().clear();
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

        backButton = new Button("Back");
        backButton.getStyleClass().add("warn");
        backButton.setVisible(false);

        stageSetupView = new StageSetupView();

        stageBox.getChildren().addAll(backButton, promptText, nextStageButton);
        topBox.getChildren().addAll(chosenQuestCard, stageBox);
        this.setTop(topBox);

    }



}
