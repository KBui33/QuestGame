package gui.partials.quest;

import gui.partials.CardView;
import gui.partials.DeckView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.FoeStage;
import model.Stage;

/**
 * @author James DiNovo
 *
 * Display outcome of the stage. Display stage card, a table indicating who passed and who failed.
 */
public class StageCompleteView extends BorderPane {

    private Stage stage;
    private Text stageText;
    private CardView stageCard;
    private DeckView weaponsView;
    private ObservableList<CardView> weapons;
    private HBox stageBox;
    private VBox topBox;
    private Button continueButton;

    public StageCompleteView() {

        stageText = new Text("Stage Complete");
        stageText.getStyleClass().add("body-font");

        stageCard = new CardView();
        stageCard.setSize(150);
        stageCard.getButtonBox().setVisible(false);
        setAlignment(stageCard, Pos.CENTER);

        weaponsView = new DeckView();
        weaponsView.setHeight(225);
        weaponsView.setWidth(400);
        this.weapons = FXCollections.observableArrayList();

        continueButton = new Button();

        stageBox = new HBox();
        stageBox.setAlignment(Pos.CENTER);
        setAlignment(stageBox, Pos.CENTER);
        stageBox.getChildren().add(stageCard);

        topBox = new VBox();
        topBox.setAlignment(Pos.CENTER);
        setAlignment(stageBox, Pos.CENTER);
        topBox.getChildren().addAll(stageText, stageBox);

        this.setTop(topBox);

    }

//    public StageCompleteView(Stage s) {
//        this();
//        setStage(s);
//    }

    public void setStage(Stage s, boolean passed) {
        this.stage = s;
        this.stageCard.setCard(s.getStageCard());
        this.weapons.clear();
        this.stageBox.getChildren().remove(weaponsView.getListView());

        if (s instanceof FoeStage) {
            if (((FoeStage) s).getWeapons().size() > 0) {
                ((FoeStage) s).getWeapons().forEach(w -> {
                    weapons.add(new CardView(w));
                });
                this.stageBox.getChildren().add(weaponsView.getListView());
            }
        }

        if (passed) {
            stageText.setText("Stage Complete: Passed");
            continueButton.setText("Continue");
            continueButton.getStyleClass().add("success");
        } else {
            stageText.setText("Stage Complete: Defeated");
            continueButton.setText("Sit out");
            continueButton.getStyleClass().add("warn");
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

    public Button getContinueButton() {
        return continueButton;
    }

}
