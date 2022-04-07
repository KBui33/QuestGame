package gui.controllers.tournament;

import component.card.Card;
import gui.controllers.AbstractFightController;
import gui.controllers.GameController;
import gui.partials.tournament.TournamentPlayerCardsView;
import gui.partials.tournament.TournamentView;
import javafx.collections.FXCollections;
import model.Tournament;
import utils.Callback;
import utils.CallbackEmpty;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author James DiNovo
 *
 * Tournament gui controller for handling display of information to user and input from user
 */
public class TournamentController extends AbstractFightController {

    Tournament tournament;
    TournamentView tournamentView;

    public TournamentController(Tournament tournament, GameController parent) {
        this.parent = parent;
        this.tournamentView = new TournamentView();
        updateTournament(tournament);

        this.weaponNames = new HashSet<String>();
        this.weaponCards = FXCollections.observableArrayList();
    }

    public void pickCards(Tournament tournament, Callback<ArrayList<Card>> callback) {
        showGui();
        updateTournament(tournament);

        this.tournamentView.mode(TournamentView.Mode.PICK_CARDS);

        pickWeapons(tournamentView.getCardSelectionView(), wl -> {
            cleanUpGui();
            tournamentView.clearTournament();
            callback.call(wl);
        });
    }

    public void tournamentComplete(Tournament tournament, CallbackEmpty callback) {
        showGui();
        updateTournament(tournament);
        this.tournamentView.mode(TournamentView.Mode.SHOW_RESULTS);

        this.tournamentView.getTournamentResultsView().getPlayersBox().getChildren().clear();

        tournament.getCurrentPlayers().forEach(p -> {
            this.tournamentView.getTournamentResultsView().getPlayersBox().getChildren().add(new TournamentPlayerCardsView(p));
        });

        this.tournamentView.getTournamentResultsView().getContinueButton().setOnAction(e -> {
            cleanUpGui();
            callback.call();
        });

    }

    private void showGui() {
        parent.getView().getMainPane().clear();
        parent.getView().getMainPane().add(this.tournamentView);
    }

    public void updateTournament(Tournament t) {
        this.tournament = t;
        tournamentView.getHeaderText().setText(tournament.getTournamentCard().getTitle());
    }
}
