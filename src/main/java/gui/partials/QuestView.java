package gui.partials;

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
    private StageCompleteView stageView;
    private StageCardSelectionView scsv;
    private Quest quest;

    public static final String STAGE_TEXT = "Stage: ";

    public void setStage(Stage stage) {
        StageCompleteView sv = new StageCompleteView(stage);
    }

    public StageCardSelectionView getStageCardSelectionView() {
        return scsv;
    }

    public void clearStage() {
        this.setCenter(null);
    }

    public QuestView() {

        headerText = new Text("Quest");
        headerText.getStyleClass().add("header-font");

        stageText = new Text();
        stageText.getStyleClass().add("body-font");

        // add current quest card to top
        questCard = new CardView();
        questCard.setSize(200);
        setAlignment(questCard, Pos.CENTER);


        infoBox = new VBox();
        infoBox.setSpacing(5);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.getChildren().addAll(headerText, questCard, stageText);

        this.setTop(infoBox);

        // show stages in the middle
        stageView = new StageCompleteView();
        scsv = new StageCardSelectionView();

    }

    public QuestView(Quest quest) {
        this();
        setQuest(quest);
    }

    public void mode(boolean pickCards) {
        if (pickCards) {
            this.setCenter(this.scsv);
        } else {
            this.setCenter(this.stageView);
        }
    }

    public void setQuest(Quest q) {
        this.quest = q;
        this.questCard.setCard(q.getQuestCard());
        this.setStage(q.getCurrentStage());
    }

    public CardView getQuestCard() {
        return questCard;
    }

    public Text getStageText() {
        return stageText;
    }

    public Text getHeaderText() {
        return headerText;
    }

    public StageCompleteView getStageView() {
        return stageView;
    }

    public StageCardSelectionView getScsv() {
        return scsv;
    }
}
