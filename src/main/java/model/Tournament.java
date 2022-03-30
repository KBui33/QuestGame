package model;

import component.card.TournamentCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Tournament {
    private TournamentCard tournamentCard;
    private ArrayList<TournamentPlayer> currentTournamentPlayers;
    private ArrayList<TournamentPlayer> tournamentPlayers;
    private ArrayList<TournamentPlayer> tiedPlayers;

    public Tournament(TournamentCard tournamentCard) {
        this.tournamentCard = tournamentCard;
        this.tournamentPlayers = new ArrayList<>();
        this.tiedPlayers = new ArrayList<>();
    }

    public boolean addTournamentPlayer(Player player){ return this.tournamentPlayers.add(new TournamentPlayer(player));}

    public void startTournament(){
        this.currentTournamentPlayers = new ArrayList<>(tournamentPlayers);
        checkPlayers();
    }

    public TournamentCard getTournamentCard() {
        return tournamentCard;
    }

    public ArrayList<TournamentPlayer> getCurrentTournamentPlayers() {
        return currentTournamentPlayers;
    }

    public ArrayList<TournamentPlayer> getTournamentPlayers() {
        return tournamentPlayers;
    }


    /**
     * Checks if there is only one player in the tournament
     * */
    public void checkPlayers(){
        if(currentTournamentPlayers.size() == 1) {
            currentTournamentPlayers.get(0)
                    .incrementShields(1 + tournamentCard.getShields());
        }
    }

    /**
     * Find the winner(s) of tournament (This does not work with tied players)
     * Returns -> players who lost tournament
     * */
    public ArrayList<TournamentPlayer> computeWinners(){

        ArrayList<TournamentPlayer> losers = new ArrayList<>();

        // Player with the highest battle points
        TournamentPlayer highestPlayer =
                currentTournamentPlayers.stream()
                                .max(Comparator.comparing(TournamentPlayer::calculateBattlePoints)).get();

        System.out.println("== Current highest battle point player: " + highestPlayer);

        // Find if anybody else has same battle points as highest player
        for(int i = 1; i < currentTournamentPlayers.size(); i++){
            TournamentPlayer currentPlayer = currentTournamentPlayers.get(i);

            if(currentPlayer.calculateBattlePoints() != highestPlayer.calculateBattlePoints()){
                System.out.println("== Player " + currentPlayer.getPlayerId() + " has same battle points as " + highestPlayer.getPlayerId());
                currentTournamentPlayers.remove(currentPlayer);
                losers.add(currentPlayer);
            }
        }

        return losers;
    }

    /**
     * Distribute shields accordingly to tournament winners
     * */
    public void distributeShields(){
        for(TournamentPlayer player: currentTournamentPlayers){
            player.incrementShields(tournamentPlayers.size() + tournamentCard.getShields());
        }
    }


    public ArrayList<TournamentPlayer> computeTiedPlayers(){
        return null;
    }

}
