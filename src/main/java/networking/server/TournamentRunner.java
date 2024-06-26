package networking.server;

import model.*;

import java.io.IOException;
import java.util.ArrayList;

public class TournamentRunner extends Runner {
    @Override
    protected void loop() {
        try {
            Server server = Server.getInstance();
            InternalGameState gameState = server.getGameState();
            Tournament tournament = gameState.getCurrentTournament();

            initShouldRespondIds(tournament);

            gameState.setGameStatus(GameStatus.RUNNING_TOURNAMENT);
            boolean shouldStartTournament = tournament.startTournament();

            shouldRespond = 0;
            server.resetNumResponded(CommandType.TOURNAMENT);

            if (!shouldStartTournament) { // Only one player joined tournament
                // Send end tournament command to player
                takeEndTournamentTurns(server, gameState, tournament);

                waitForResponses();

                // Distribute shields to single player
                distributeTournamentShields(server, gameState, tournament);
            } else {

                server.notifyClients(new TournamentCommand(TournamentCommandName.TOURNAMENT_STARTED));


                // Deal adventure cards to participants
                System.out.println("== Tournament runner says: Dealing an adventure card to each participant");
                for (TournamentPlayer tournamentPlayer : tournament.getCurrentPlayers()) {
                    tournament.setCurrentTurnPlayer(tournamentPlayer);
                    int playerId = tournamentPlayer.getPlayerId();
                    shouldRespond++;

                    System.out.println("== Tournament runner says: Sending tournament adventure card to player " + playerId);
                    gameState.setGameStatus(GameStatus.TAKING_TOURNAMENT_CARD);

                    TournamentCommand tournamentCardCommand = new TournamentCommand(TournamentCommandName.PLAYER_TAKE_TOURNAMENT_CARD);
                    tournamentCardCommand.setTournament(tournament);
                    tournamentCardCommand.setCard(gameState.drawAdventureCard());
                    server.notifyClientByPlayerId(playerId, tournamentCardCommand);
                }

                waitForResponses();

                // Player take turns (Choose weapons, etc...)
                takeTurns(server, gameState, tournament);

                waitForResponses();

                // Find tournament winners and losers
                ArrayList<TournamentPlayer> losers = tournament.computeWinners();
                ArrayList<TournamentPlayer> winners = tournament.getCurrentPlayers();
                System.out.println("== Tournament:\twinners -> " + winners.size() + "\tlosers -> " + losers.size());

                // Send end tournament command to all participants
                takeEndTournamentTurns(server, gameState, tournament);

                waitForResponses();

                // Distribute shields to winners
                tournament.distributeShields();
                distributeTournamentShields(server, gameState, tournament);

                server.notifyClients(new TournamentCommand(TournamentCommandName.TOURNAMENT_COMPLETED));


                System.out.println("== Discarding cards used");
                discardCardsUsed(gameState, tournament);
            }
            gameState.setGameStatus(GameStatus.RUNNING);

            shouldStopRunner();
            System.out.println("== Tournament runner says: Tournament completed");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void takeTurns(Server server, InternalGameState gameState, Tournament tournament) {
        for (TournamentPlayer tournamentPlayer : tournament.getCurrentPlayers()) {
            tournament.setCurrentTurnPlayer(tournamentPlayer);
            int playerId = tournamentPlayer.getPlayerId();
            shouldRespond++;

            System.out.println("== Tournament runner says: Sending take tournament turn command to player " + playerId);
            gameState.setGameStatus(GameStatus.TAKING_TOURNAMENT_TURN);

            TournamentCommand tournamentTurnCommand = new TournamentCommand(TournamentCommandName.PLAYER_TOURNAMENT_TURN);
            tournamentTurnCommand.setPlayerId(playerId);
            tournamentTurnCommand.setPlayer(tournamentPlayer.getPlayer());
            tournamentTurnCommand.setTournament(tournament);
            server.notifyClientByPlayerId(playerId, tournamentTurnCommand);
        }
    }

    private void notifyLosers(Server server, InternalGameState gameState, Tournament tournament, ArrayList<TournamentPlayer> losers) {
        for (TournamentPlayer loser : losers) {
            tournament.setCurrentTurnPlayer(loser);
            int playerId = loser.getPlayerId();
            shouldRespond++;

            System.out.println("== Tournament runner says: Sending end tournament turn to loser " + playerId);
            gameState.setGameStatus(GameStatus.ENDING_TOURNAMENT_TURN);

            TournamentCommand tournamentLostCommand = new TournamentCommand(TournamentCommandName.TOURNAMENT_LOST);
            tournamentLostCommand.setPlayerId(playerId);
            tournamentLostCommand.setPlayer(loser.getPlayer());
            tournamentLostCommand.setTournament(tournament);
            server.notifyClientByPlayerId(playerId, tournamentLostCommand);
        }
    }

    private void notifyWinners(Server server, InternalGameState gameState, Tournament tournament, ArrayList<TournamentPlayer> winners) {
        for (TournamentPlayer winner : winners) {
            tournament.setCurrentTurnPlayer(winner);
            int playerId = winner.getPlayerId();
            shouldRespond++;

            System.out.println("== Tournament runner says: Sending end tournament turn to winner " + playerId);
            gameState.setGameStatus(GameStatus.ENDING_TOURNAMENT_TURN);

            TournamentCommand tournamentWonCommand = new TournamentCommand(TournamentCommandName.TOURNAMENT_WON);
            tournamentWonCommand.setPlayerId(playerId);
            tournamentWonCommand.setPlayer(winner.getPlayer());
            tournamentWonCommand.setTournament(tournament);
            server.notifyClientByPlayerId(playerId, tournamentWonCommand);
        }
    }

    private void takeEndTournamentTurns(Server server, InternalGameState gameState, Tournament tournament) {
        for (TournamentPlayer tournamentPlayer : tournament.getPlayers()) {
            tournament.setCurrentTurnPlayer(tournamentPlayer);
            int playerId = tournamentPlayer.getPlayerId();
            shouldRespond++;

            System.out.println("== Tournament runner says: Sending end tournament to player " + playerId);
            gameState.setGameStatus(GameStatus.ENDING_TOURNAMENT);

            TournamentCommand endTournamentCommand = new TournamentCommand(TournamentCommandName.PLAYER_END_TOURNAMENT);
            endTournamentCommand.setPlayerId(playerId);
            endTournamentCommand.setPlayer(tournamentPlayer.getPlayer());
            endTournamentCommand.setTournament(tournament);
            server.notifyClientByPlayerId(playerId, endTournamentCommand);
        }
    }

    private void distributeTournamentShields(Server server, InternalGameState gameState, Tournament tournament) {
        for (TournamentPlayer tournamentPlayer : tournament.getCurrentPlayers()) {
            tournament.setCurrentTurnPlayer(tournamentPlayer);
            int playerId = tournamentPlayer.getPlayerId();

            System.out.println("== Tournament runner says: Sending tournament shields to player " + playerId);
            gameState.setGameStatus(GameStatus.DISTRIBUTING_TOURNAMENT_SHIELDS);

            TournamentCommand takeTournamentShieldsCommand = new TournamentCommand(TournamentCommandName.PLAYER_TAKE_TOURNAMENT_SHIELDS);
            takeTournamentShieldsCommand.setPlayerId(playerId);
            takeTournamentShieldsCommand.setPlayer(tournamentPlayer.getPlayer());
            server.notifyClientByPlayerId(playerId, takeTournamentShieldsCommand);
        }
    }

    private void discardCardsUsed(InternalGameState gameState, Tournament tournament) {
        gameState.discardAdventureCards(tournament.getCardsUsed());
    }

    @Override
    protected void initShouldRespondIds(Object o) {
        Tournament tournament = (Tournament) o;
        shouldRespondIds.clear();
        for (TournamentPlayer player : tournament.getPlayers()) {
            shouldRespondIds.add(player.getPlayerId());
        }
    }

    @Override
    protected void waitForResponses() {
        try {
            Server server = Server.getInstance();
            while (!server.getHaveResponded(CommandType.TOURNAMENT).equals(shouldRespondIds)) Thread.sleep(1000);
            server.resetNumResponded(CommandType.TOURNAMENT);
            shouldRespond = 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
