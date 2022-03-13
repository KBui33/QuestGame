package gui.partials;

import game.components.card.Card;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Quest;
import model.Stage;

/**
 * @author James DiNovo
 *
 * Displays the current quest to the user with its given stages
 */
public class QuestView extends BorderPane {

    private CardView questCard;
    private Text stageText, headerText;
    private VBox infoBox;
    private StageView stageView;
    private Quest quest;

    public static final String STAGE_TEXT = "Stage: ";

    public StageView setStageView(Stage stage) {
        StageView sv = new StageView(stage);
        this.setCenter(sv);
        return sv;
    }

    public void clearStage() {
        this.setCenter(null);
    }

    public QuestView() {

        headerText = new Text("Quest");
        headerText.getStyleClass().add("header-font");
        setAlignment(headerText, Pos.CENTER);

        stageText = new Text();
        stageText.getStyleClass().add("body-font");
        setAlignment(stageText, Pos.CENTER);

        // add current quest card to top
        questCard = new CardView();
        questCard.setSize(200);
        setAlignment(questCard, Pos.CENTER);

        infoBox = new VBox();
        infoBox.setSpacing(5);
        infoBox.getChildren().addAll(headerText, questCard, stageText);

        // show stages in the middle
        stageView = new StageView();

    }

    public QuestView(Quest quest) {
        this();
        setQuest(quest);
    }

    public void setQuest(Quest q) {
        this.quest = q;
        this.questCard.setCard(q.getQuestCard());
        this.setStage(q.getCurrentStage());
    }

    public void setStage(Stage s) {

    }
}
