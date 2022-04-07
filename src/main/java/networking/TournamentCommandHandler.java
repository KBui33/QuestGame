package networking;

import component.card.Card;
import model.*;
import networking.server.Server;

import java.io.IOException;
import java.util.ArrayList;

public class TournamentCommandHandler extends CommandHandlerDecorator  {

    public TournamentCommandHandler(CommandHandler commandHandler) {
        super(commandHandler);
    }

    @Override
    public Command processGameCommand(Command command) throws IOException {
        TournamentCommand tournamentCommand = (TournamentCommand) command;
        Server server = Server.getInstance();
        InternalGameState gameState = server.getGameState();
        Tournament tournament = gameState.getCurrentTournament();
        Player player = null;

        CommandName commandName = tournamentCommand.getCommandName();
        int playerId = tournamentCommand.getPlayerId();
        if (playerId > 0) player = gameState.getPlayer(playerId);
        Card card = tournamentCommand.getCard();
        ArrayList<Card> cards = tournamentCommand.getCards();

        TournamentCommand returnCommand = new TournamentCommand();

       if (commandName.equals(TournamentCommandName.WILL_JOIN_TOURNAMENT)) {
           tournament.addPlayer(player); // Add participant to tournament

            System.out.println("== Command handler says: Player " + playerId + " agreed to participate in tournament");

            returnCommand.setCommandName(TournamentCommandName.JOINED_TOURNAMENT);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.FINDING_TOURNAMENT_PARTICIPANTS);

        } else if (commandName.equals(TournamentCommandName.WILL_NOT_JOIN_TOURNAMENT)) {
            System.out.println("== Command handler says: Player " + playerId + " did not agreed to participate in tournament");

            returnCommand.setCommandName(TournamentCommandName.DID_NOT_JOIN_TOURNAMENT);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.FINDING_TOURNAMENT_PARTICIPANTS);

        } else if (commandName.equals(TournamentCommandName.ACCEPT_TOURNAMENT_CARD)) {
            System.out.println("== Command handler says: Player " + playerId + " took tournament adventure card");

            tournament.getPlayer(playerId).addCard(card);

            returnCommand.setCommandName(TournamentCommandName.ACCEPTED_TOURNAMENT_CARD);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.RUNNING_TOURNAMENT);

        } else if (commandName.equals(TournamentCommandName.DISCARD_TOURNAMENT_CARD)) {
            System.out.println("== Command handler says: Player " + playerId + " discarded tournament adventure card");

            gameState.discardAdventureCard(card);

            returnCommand.setCommandName(TournamentCommandName.DISCARDED_TOURNAMENT_CARD);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.RUNNING_TOURNAMENT);

        } else if (commandName.equals(TournamentCommandName.TAKE_TOURNAMENT_TURN)) {
            System.out.println("== Command handler says: Player " + playerId + " took tournament turn");

           tournament.getPlayer(playerId).setCardsUsed(cards);
            boolean discardedCards = player.discardCards(cards);

            System.out.println("== Command handler says: Discarding tournament cards " + discardedCards);

            returnCommand.setCommandName(TournamentCommandName.TOOK_TOURNAMENT_TURN);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.RUNNING_TOURNAMENT);

        } else if (commandName.equals(TournamentCommandName.END_TOURNAMENT_TURN)) {
            System.out.println("== Command handler says: Player " + playerId + " ended tournament turn");

            returnCommand.setCommandName(TournamentCommandName.ENDED_TOURNAMENT_TURN);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.RUNNING_TOURNAMENT);

        } else if (commandName.equals(TournamentCommandName.END_TOURNAMENT)) {
            System.out.println("== Command handler says: Player " + playerId + " ended tournament");

            returnCommand.setCommandName(TournamentCommandName.ENDED_TOURNAMENT);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.RUNNING_TOURNAMENT);

        }

        server.incrementNumResponded(CommandType.TOURNAMENT, playerId);
        server.notifyClients(returnCommand);

        return returnCommand;
    }
}
