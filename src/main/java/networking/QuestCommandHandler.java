package networking;

import component.card.Card;
import model.*;
import networking.server.Server;

import java.io.IOException;
import java.util.ArrayList;

public class QuestCommandHandler extends CommandHandlerDecorator {

    public QuestCommandHandler(CommandHandler commandHandler) {
        super(commandHandler);
    }

    @Override
    public Command processGameCommand(Command command) throws IOException {
        QuestCommand questCommand = (QuestCommand) command;
        Server server = Server.getInstance();
        InternalGameState gameState = server.getGameState();
        Quest quest = gameState.getCurrentQuest();
        Player player = null;

        CommandName commandName = questCommand.getCommandName();
        int playerId = questCommand.getPlayerId();
        if (playerId > 0) player = gameState.getPlayer(playerId);
        Card card = questCommand.getCard();
        ArrayList<Card> cards = questCommand.getCards();

        QuestCommand returnCommand = new QuestCommand();

        if (commandName.equals(QuestCommandName.WILL_SPONSOR_QUEST)) {
            quest = questCommand.getQuest();
            quest.setSponsor(player);
            gameState.setCurrentQuest(quest);

            // Discard used cards from sponsor
            ArrayList<Card> questCardsUsed = quest.getAllQuestCards(false);
            System.out.println("== Discarding quest cards used: " + player.discardCards(questCardsUsed));

            System.out.println("== Command handler says: Player " + playerId + " agreed to sponsor quest");

            returnCommand.setCommandName(QuestCommandName.FOUND_QUEST_SPONSOR);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.FINDING_QUEST_PARTICIPANTS);

        } else if (commandName.equals(QuestCommandName.WILL_NOT_SPONSOR_QUEST)) {
            System.out.println("== Command handler says: Player " + playerId + " refused to sponsor quest");

            returnCommand.setCommandName(QuestCommandName.FIND_QUEST_SPONSOR);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.FINDING_QUEST_SPONSOR);

        } else if (commandName.equals(QuestCommandName.WILL_JOIN_QUEST)) {
            quest.addPlayer(player); // Add participant to quest

            System.out.println("== Command handler says: Player " + playerId + " agreed to participate in quest");

            returnCommand.setCommandName(QuestCommandName.JOINED_QUEST);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.FINDING_QUEST_PARTICIPANTS);

        } else if (commandName.equals(QuestCommandName.WILL_NOT_JOIN_QUEST)) {
            System.out.println("== Command handler says: Player " + playerId + " did not agreed to participate in quest");

            returnCommand.setCommandName(QuestCommandName.DID_NOT_JOIN_QUEST);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.FINDING_QUEST_PARTICIPANTS);

        } else if (commandName.equals(QuestCommandName.ACCEPT_QUEST_STAGE_CARD)) {
            System.out.println("== Command handler says: Player " + playerId + " took stage card");

            quest.getQuestPlayerByPlayerId(playerId).addCard(card);

            returnCommand.setCommandName(QuestCommandName.ACCEPTED_QUEST_STAGE_CARD);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.RUNNING_QUEST);

        } else if (commandName.equals(QuestCommandName.DISCARD_QUEST_STAGE_CARD)) {
            System.out.println("== Command handler says: Player " + playerId + " discarded stage card");

            gameState.discardAdventureCard(card);

            returnCommand.setCommandName(QuestCommandName.DISCARDED_QUEST_STAGE_CARD);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.RUNNING_QUEST);

        } else if (commandName.equals(QuestCommandName.TAKE_QUEST_TURN)) {
            System.out.println("== Command handler says: Player " + playerId + " took stage turn");

            quest.getQuestPlayerByPlayerId(playerId).setCardsUsed(cards);
            boolean discardedQuestCards = player.discardCards(cards);

            System.out.println("== Command handler says: Discarding quest cards " + discardedQuestCards);

            returnCommand.setCommandName(QuestCommandName.TOOK_QUEST_TURN);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.RUNNING_QUEST);

        } else if (commandName.equals(QuestCommandName.END_QUEST_TURN)) {
            System.out.println("== Command handler says: Player " + playerId + " ended stage turn");

            returnCommand.setCommandName(QuestCommandName.ENDED_QUEST_TURN);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.RUNNING_QUEST);

        } else if (commandName.equals(QuestCommandName.ACCEPT_SPONSOR_QUEST_CARDS)) {
            Player sponsor = quest.getSponsor();
            System.out.println("== Command handler says: Player " + playerId + " accepted quest sponsor cards");

            sponsor.addCards(cards);

            returnCommand.setCommandName(QuestCommandName.ACCEPTED_SPONSOR_QUEST_CARDS);
            returnCommand.setPlayer(sponsor);

            gameState.setGameStatus(GameStatus.RUNNING_QUEST);

        } else if (commandName.equals(QuestCommandName.END_QUEST)) {
            System.out.println("== Command handler says: Player " + playerId + " ended quest");

            returnCommand.setCommandName(QuestCommandName.ENDED_QUEST);
            returnCommand.setPlayer(player);

            gameState.setGameStatus(GameStatus.RUNNING_QUEST);

        }

        server.incrementNumResponded(CommandType.QUEST);
        server.notifyClients(returnCommand);

        return returnCommand;
    }
}
