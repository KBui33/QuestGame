package gui.partials.tournament;

import gui.partials.CardSelectionView;
import gui.partials.quest.QuestView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class TournamentView extends BorderPane {
    public enum Mode {
        PICK_CARDS,
        SHOW_RESULTS
    }

    private Text headerText;
    private TournamentResults tournamentResults;
    private CardSelectionView cardSelectionView;

    public Text getHeaderText() {
        return headerText;
    }

    public TournamentResults getTournamentResults() {
        return tournamentResults;
    }

    public CardSelectionView getCardSelectionView() {
        return cardSelectionView;
    }

    public TournamentView() {

        headerText = new Text("Tournament");
        headerText.getStyleClass().add("header-font");
        this.setTop(headerText);


    }

    public void clearTournament() {
        this.setCenter(null);
    }

    public void mode(QuestView.Mode c) {
        switch (c) {
            case PICK_CARDS:
                this.setCenter(this.cardSelectionView);
                break;
            case SHOW_RESULTS:
                this.setCenter(this.tournamentResults);
                break;
            default:
                clearTournament();
        }
    }
}
