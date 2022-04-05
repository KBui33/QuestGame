package gui.partials.quest;

import gui.partials.CardSelectionView;
import gui.partials.CardView;
import gui.partials.CardsReceivedView;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Stage;


/**
 * @author James DiNovo
 *
 * Displays the current quest to the user with its given stages
 */
public class QuestView extends BorderPane {

    public enum Mode {
        PICK_CARDS,
        SHOW_RESULTS,
        COMPLETE,
        SPONSOR_CARDS
    }

    private CardView questCard;
    private Text stageText, headerText;
    private VBox infoBox;
    private StageCompleteView stageCompletedView;
    private CardSelectionView scsv;
    private QuestCompleteView questCompleteView;
    private gui.partials.CardsReceivedView cardsReceivedView;

    public static final String STAGE_TEXT = "Stage: ";

    public void setStageCompleted(Stage s, boolean passed) {
        this.stageCompletedView.setStage(s, passed);
        mode(Mode.SHOW_RESULTS);
    }

    public CardSelectionView getStageCardSelectionView() {
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
        stageCompletedView = new StageCompleteView();
        scsv = new CardSelectionView();
        questCompleteView = new QuestCompleteView();
        cardsReceivedView = new CardsReceivedView();

    }


    public void mode(Mode c) {
        switch (c) {
            case PICK_CARDS:
                this.setCenter(this.scsv);
                break;
            case SHOW_RESULTS:
                this.setCenter(this.stageCompletedView);
                break;
            case COMPLETE:
                this.setCenter(this.questCompleteView);
                this.stageText.setText("Quest Complete");
                break;
            case SPONSOR_CARDS:
                this.setCenter(this.cardsReceivedView);
                this.stageText.setText("Sponsored Quest has ended");
                break;
            default:
                clearStage();
        }
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

    public StageCompleteView getStageCompletedView() {
        return stageCompletedView;
    }

    public CardSelectionView getScsv() {
        return scsv;
    }

    public QuestCompleteView getQuestCompleteView() {
        return questCompleteView;
    }

    public CardsReceivedView getCardsReceivedView() {
        return cardsReceivedView;
    }
}
