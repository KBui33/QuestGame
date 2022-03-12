package gui.partials;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
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
    private StageView stageView;
    private Quest quest;

    public QuestView() {

        headerText = new Text("Quest");
        headerText.getStyleClass().add("header-font");

        stageText = new Text();
        stageText.getStyleClass().add("body-font");

        // add current quest card to top
        questCard = new CardView();
        questCard.setSize(200);
        setAlignment(questCard, Pos.CENTER);
        this.setTop(questCard);

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
