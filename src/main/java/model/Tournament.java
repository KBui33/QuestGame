package model;

import component.card.Card;
import component.card.TournamentCard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Tournament implements Serializable {
    private TournamentCard tournamentCard;
    private ArrayList<TournamentPlayer> currentPlayers;
    private ArrayList<TournamentPlayer> players;
    private ArrayList<TournamentPlayer> tiedPlayers;
    private TournamentPlayer currentTurnPlayer;

    public Tournament() {
        this.players = new ArrayList<>();
        this.tiedPlayers = new ArrayList<>();
    }

    public Tournament(TournamentCard tournamentCard) {
        this();
        this.tournamentCard = tournamentCard;
    }

    public boolean addPlayer(Player player){ return this.players.add(new TournamentPlayer(player));}

    public TournamentPlayer getCurrentTurnPlayer() {
        return currentTurnPlayer;
    }

    public void setCurrentTurnPlayer(TournamentPlayer currentTurnPlayer) {
        this.currentTurnPlayer = currentTurnPlayer;
    }

    public boolean startTournament(){
        this.currentPlayers = new ArrayList<>(players);
        return checkPlayers();
    }

    public void setTournamentCard(TournamentCard tournamentCard) {
        this.tournamentCard = tournamentCard;
    }

    public TournamentCard getTournamentCard() {
        return tournamentCard;
    }

    public ArrayList<TournamentPlayer> getCurrentPlayers() {
        return currentPlayers;
    }

    public ArrayList<TournamentPlayer> getPlayers() {
        return players;
    }

    public TournamentPlayer getPlayer(int playerId) {
        for (TournamentPlayer player : players) {
            if (player.getPlayerId() == playerId) return player;
        }

        return null;
    }

    public String getTitle() {
        return tournamentCard.getTitle();
    }


    /**
     * Checks if there is only one player in the tournament
     * */
    public boolean checkPlayers(){
        if(currentPlayers.size() == 1) {
            currentPlayers.get(0).incrementShields(1 + tournamentCard.getShields());
            return false;
        }

        return true;
    }

    /**
     * Find the winner(s) of tournament (This does not work with tied players)
     * Returns -> players who lost tournament
     * */
    public ArrayList<TournamentPlayer> computeWinners(){
        ArrayList<TournamentPlayer> losers = new ArrayList<>();

        // Player with the highest battle points
        TournamentPlayer highestPlayer =
                currentPlayers.stream()
                                .max(Comparator.comparing(TournamentPlayer::calculateBattlePoints)).get();

        System.out.println("== Current highest battle point player: " + highestPlayer.getPlayerId() + " with battle points: " + highestPlayer.calculateBattlePoints());

        // Find if anybody else has same battle points as highest player
        for(TournamentPlayer currentPlayer: currentPlayers) {
            if(currentPlayer.calculateBattlePoints() < highestPlayer.calculateBattlePoints()){
                losers.add(currentPlayer);
            } else {
                System.out.println("== Player " + currentPlayer.getPlayerId() + " has same battle points as " + highestPlayer.getPlayerId());
            }
        }

        for (TournamentPlayer loser : losers) {
            currentPlayers.remove(loser);
        }

        return losers;
    }

    /**
     * Distribute shields accordingly to tournament winners
     * */
    public void distributeShields(){
        for(TournamentPlayer player: currentPlayers){
            player.incrementShields(players.size() + tournamentCard.getShields());
        }
    }

    /**
     * Get all the cards used in a tournament
     * @return
     */
    public ArrayList<Card> getCardsUsed() {
        ArrayList<Card> cardsUsed = new ArrayList<>();
        for (TournamentPlayer player: players) {
            cardsUsed.addAll(player.getCardsUsed());
        }

        return cardsUsed;
    }


    public ArrayList<TournamentPlayer> computeTiedPlayers(){
        return null;
    }

}
