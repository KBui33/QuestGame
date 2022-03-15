package networking;

import game.components.card.Card;
import model.*;
import networking.server.GameRunner;
import networking.server.QuestRunner;
import networking.server.Server;

import java.io.IOException;
import java.util.ArrayList;

public class GameCommandHandler {
    private Server server;
    public GameCommandHandler(Server server) {
        this.server = server;
    }

    public GameCommand processGameCommand(GameCommand gameCommand) throws IOException {
        Command command =  gameCommand.getCommand();
        GameCommand returnCommand = new GameCommand();
        InternalGameState internalGameState = server.getGameState();
        Player player = gameCommand.getPlayer();
        ExternalGameState externalGameState = server.getExternalGameState();
        boolean startGame = false;

        Quest quest = null;
        boolean startQuest = false;

        switch (command) {
            case READY: {
                System.out.println("== Command handler says:  Adding new player");
                int playerId = internalGameState.addPlayer(new Player());
                returnCommand.setCommand(Command.IS_READY);
                returnCommand.setPlayerId(playerId);
                returnCommand.setReadyPlayers(internalGameState.getNumPlayers());

                // If all lobby players ready, start the game
                if(server.getNumClients() >= 2 && internalGameState.getNumPlayers() == server.getNumClients()) startGame = true;
                break;
            }

            case GET_ATTACHED_PLAYER: {
                System.out.println("== Command handler says: Fetching player attached to client");
                returnCommand.setCommand(Command.RETURN_ATTACHED_PLAYER);
                returnCommand.setPlayer(internalGameState.getPlayer(gameCommand.getPlayerId()));
                returnCommand.setPlayerId(gameCommand.getPlayerId());
                break;
            }

            case GET_LOBBY_STATE: {
                System.out.println("== Command handler says: Fetching lobby state");
                returnCommand.setCommand(Command.RETURN_LOBBY_STATE);
                returnCommand.setReadyPlayers(internalGameState.getNumPlayers());
                returnCommand.setJoinedPlayers(server.getNumClients());
                break;
            }

            case DISCARD_CARD: {
                int playerId = gameCommand.getPlayerId();
                Card card = gameCommand.getCard();
                System.out.println("== Command handler says: Player " + playerId + " is discarding a card");
                player.discardCard(card);
                internalGameState.discardAdventureCard(card);
                returnCommand.setCommand(Command.DISCARDED_CARD);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId - 1));
                returnCommand.setPlayerId(playerId);
                break;
            }

            case WILL_SPONSOR_QUEST: {
                int playerId = gameCommand.getPlayerId();

                quest = gameCommand.getQuest();
                quest.setSponsor(player);
                internalGameState.setCurrentQuest(quest);

                System.out.println("== Command handler says: Player " + playerId + " agreed to sponsor quest");

                returnCommand.setCommand(Command.FOUND_QUEST_SPONSOR);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId - 1));
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.FINDING_QUEST_PARTICIPANTS);

                break;
            }

            case WILL_NOT_SPONSOR_QUEST: {
                int playerId = gameCommand.getPlayerId();

                System.out.println("== Command handler says: Player " + playerId + " refused to sponsor quest");

                returnCommand.setCommand(Command.FIND_QUEST_SPONSOR);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId - 1));
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.FINDING_QUEST_SPONSOR);

                break;
            }

            case WILL_JOIN_QUEST: {
                int playerId = gameCommand.getPlayerId();

                quest = gameCommand.getQuest();
                quest.addQuestPlayer(playerId - 1, player); // Add participant to quest

                System.out.println("== Command handler says: Player " + playerId + " agreed to participate in quest");

                returnCommand.setCommand(Command.JOINED_QUEST);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId - 1));
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.FINDING_QUEST_PARTICIPANTS);

                break;
            }

            case WILL_NOT_JOIN_QUEST: {
                int playerId = gameCommand.getPlayerId();

                System.out.println("== Command handler says: Player " + playerId + " did not agreed to participate in quest");

                returnCommand.setCommand(Command.DID_NOT_JOIN_QUEST);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId - 1));
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.FINDING_QUEST_PARTICIPANTS);

                break;
            }

            case END_TURN: {
                int playerId = gameCommand.getPlayerId();

                System.out.println("== Command handler says: Player took " + playerId);

                returnCommand.setCommand(Command.ENDED_TURN);
                returnCommand.setPlayerId(playerId);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId - 1));

                internalGameState.setGameStatus(GameStatus.RUNNING); // Update game status

                break;
            }

            case TAKE_QUEST_TURN: {
                int playerId = gameCommand.getPlayerId();
                ArrayList<Card> stageCards = gameCommand.getCards();
                System.out.println("== Command handler says: Player " + playerId + " took stage turn");

                quest = internalGameState.getCurrentQuest();
                quest.getQuestPlayer(playerId - 1).setPlayerQuestCardUsed(stageCards);
                player = internalGameState.getPlayer(playerId - 1);
                boolean discardedQuestCards = player.discardCards(stageCards);

                System.out.println("== Command handler says: Discarding quest cards " + discardedQuestCards);

                returnCommand.setCommand(Command.TOOK_QUEST_TURN);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId - 1));
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }
        }

        server.notifyClients(returnCommand);

        if(startGame)  new Thread(new GameRunner(server, server.getGameState())).start();
        //if(startQuest)  new Thread(new QuestRunner(server, quest)).start();

        return returnCommand;
    }
}
