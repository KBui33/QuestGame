package networking.server;

import model.*;

import java.io.IOException;

public class TournamentJoinRunner extends Runner {

    @Override
    protected void loop() {
        try {
            Server server = Server.getInstance();
            InternalGameState gameState = server.getGameState();
            Tournament tournament = gameState.getCurrentTournament();
            shouldRespond = 0;
            server.resetNumResponded(CommandType.TOURNAMENT);
            gameState.setGameStatus(GameStatus.FINDING_TOURNAMENT_PARTICIPANTS);

            System.out.println("== Tournament join runner says: Finding tournament participants");

            // Determine prompt order
            int[] promptOrder = computePromptOrder();

            // Iterate over clients to find tournament participants
            for(int playerId: promptOrder) {
                shouldRespond++;

                Player player = gameState.getPlayer(playerId);
                //tournament.setCurrentTurnPlayer(player);
                gameState.setGameStatus(GameStatus.PROMPTING_TOURNAMENT_PARTICIPANT);
                TournamentCommand playerShouldJoinTournamentCommand = new TournamentCommand(TournamentCommandName.SHOULD_JOIN_TOURNAMENT);
                playerShouldJoinTournamentCommand.setTournament(tournament);
                playerShouldJoinTournamentCommand.setCard(gameState.getCurrentStoryCard());
                playerShouldJoinTournamentCommand.setPlayerId(playerId);
                playerShouldJoinTournamentCommand.setPlayer(player);

                server.notifyClientByPlayerId(playerId, playerShouldJoinTournamentCommand);
                System.out.println("== Tournament join runner says: should join tournament command sent to player " + playerId);
            }

            waitForResponses();

            shouldStopRunner();

            int numParticipants = tournament.getPlayers().size();

            if(numParticipants > 0) {  // if any participants, start tournament
                System.out.println("== Tournament join runner says: starting tournament with " + numParticipants + " participants");
                new Thread(new TournamentRunner()).start();
            } else { // ...otherwise continue running
                TournamentCommand noPlayerJoinedTournamentCommand = new TournamentCommand(TournamentCommandName.NO_PLAYER_JOINED_TOURNAMENT);
                noPlayerJoinedTournamentCommand.setTournament(tournament);
                server.notifyClients(noPlayerJoinedTournamentCommand);
                System.out.println("== Tournament join runner says: No participants to join tournament. Exiting... ");
                gameState.setGameStatus(GameStatus.RUNNING);
                gameState.setCurrentTournament(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void waitForResponses() {
        try {
            Server server = Server.getInstance();
            while (server.getNumResponded(CommandType.TOURNAMENT) < shouldRespond) Thread.sleep(1000);
            server.resetNumResponded(CommandType.TOURNAMENT);
            shouldRespond = 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
