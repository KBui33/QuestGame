package gui.partials.tournament;

import gui.partials.CardSelectionView;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

public class TournamentView extends BorderPane {
    public enum Mode {
        PICK_CARDS,
        SHOW_RESULTS
    }

    private Text headerText;
    private TournamentResultsView tournamentResultsView;
    private CardSelectionView cardSelectionView;

    public Text getHeaderText() {
        return headerText;
    }

    public TournamentResultsView getTournamentResultsView() {
        return tournamentResultsView;
    }

    public CardSelectionView getCardSelectionView() {
        return cardSelectionView;
    }

    public TournamentView() {

        headerText = new Text("Tournament");
        headerText.getStyleClass().add("header-font");
        setAlignment(headerText, Pos.CENTER);
        this.setTop(headerText);

        cardSelectionView = new CardSelectionView();

        tournamentResultsView = new TournamentResultsView();


    }

    public void clearTournament() {
        this.setCenter(null);
    }

    public void mode(TournamentView.Mode c) {
        switch (c) {
            case PICK_CARDS:
                this.setCenter(this.cardSelectionView);
                break;
            case SHOW_RESULTS:
                this.setCenter(this.tournamentResultsView);
                break;
            default:
                clearTournament();
        }
    }
}
