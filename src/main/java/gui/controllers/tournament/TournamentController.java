package gui.controllers.tournament;

import component.card.Card;
import component.card.WeaponCard;
import gui.controllers.AbstractFightController;
import gui.controllers.GameController;
import gui.other.AlertBox;
import gui.partials.CardView;
import gui.partials.tournament.TournamentPlayerCardsView;
import gui.partials.tournament.TournamentView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.Tournament;
import utils.Callback;
import utils.CallbackEmpty;

import java.util.ArrayList;
import java.util.HashSet;

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

        ObservableList<CardView> weapons = parent.getMyHandList().filtered(c -> c.getCard() instanceof WeaponCard);
        ObservableList<CardView> addedWeapons = FXCollections.observableArrayList();

        for (CardView w : weapons) {
            w.getPlayButton().setVisible(true);
            w.getPlayButton().setText("Add Weapon");
            w.getDiscardButton().setVisible(false);
            w.getPlayButton().setOnAction(e1 -> {
                if (canAddWeapon(w.getCard())) {
                    // once foe is chosen remove it from hand
                    parent.getMyHandList().remove(w);
                    addedWeapons.add(w);
                    parent.hideDecks();
                    // add it to stage
                    CardView weap = addWeapon((WeaponCard) w.getCard());
                    weap.getDiscardButton().setOnAction(e2 -> {
                        removeWeapon(weap);
                        parent.getMyHandList().add(w);
                        addedWeapons.remove(w);
                        parent.showHand();
                    });
                } else {
                    AlertBox.alert(w.getCard().getTitle() + " has already been added to your selection.", Alert.AlertType.WARNING);
                }
            });
        }
        parent.getView().getHud().getMyHand().setListViewItems(weapons);
        parent.showHand();

        tournamentView.getCardSelectionView().getWeaponsView().setListViewItems(weaponCards);

        tournamentView.getCardSelectionView().getDoneButton().setOnAction(e -> {
            ArrayList<Card> wl = new ArrayList<>();
            for (CardView cv : weaponCards) {
                wl.add((WeaponCard) cv.getCard());
            }

            addedWeapons.clear();
            weaponNames.clear();
            weaponCards.clear();

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

        tournament.getTournamentPlayers().forEach(p -> {
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

    private void updateTournament(Tournament t) {
        this.tournament = t;
        tournamentView.getHeaderText().setText(tournament.getTournamentCard().getTitle());
    }
}
