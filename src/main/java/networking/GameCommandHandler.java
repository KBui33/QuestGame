package networking;

import component.card.Card;
import model.*;
import networking.server.GameRunner;
import networking.server.Server;

import java.io.IOException;
import java.util.ArrayList;

public class GameCommandHandler {
    private Server server;

    public GameCommandHandler(Server server) {
        this.server = server;
    }

    public GameCommand processGameCommand(GameCommand gameCommand) throws IOException {
        InternalGameState gameState = server.getGameState();
        Quest quest = gameState.getCurrentQuest();
        Player player = null;

        Command command = gameCommand.getCommand();
        int playerId = gameCommand.getPlayerId();
        if(playerId > 0) player =  gameState.getPlayer(playerId);
        Card card = gameCommand.getCard();
        ArrayList<Card> cards = gameCommand.getCards();

        GameCommand returnCommand = new GameCommand();

        boolean startGame = false;
        boolean shouldNotifyClients = true;

        Event event = null;

        // Check if it's the player's turn TODO :: Add check for player turns
        /*
        if(internalGameState.getGameStatus().equals(GameStatus.RU))
        if (player != null && internalGameState.getCurrentTurnPlayer() != null && (
                (internalGameState.getCurrentQuest() != null && !internalGameState.getCurrentQuest().getCurrentTurnPlayer().equals(player)) ||
                        !internalGameState.getCurrentTurnPlayer().equals(player)
        )) {
            System.out.println("== Command handler says: " + player.getPlayerId() + " not your turn");
            returnCommand.setCommand(Command.NOT_PLAYER_TURN);
            returnCommand.setPlayer(player);
            returnCommand.setPlayerId(player.getPlayerId());
            server.notifyClientByPlayerId(player.getPlayerId(), returnCommand);

            return returnCommand;
        }
        */

        switch (command) {
            case READY: {
                System.out.println("== Command handler says:  Adding new player");
                player = gameState.addPlayer(new Player());

                server.setClientPlayerId(gameCommand.getClientIndex(), player.getPlayerId());

                returnCommand.setCommand(Command.IS_READY);
                returnCommand.setPlayer(player);
                returnCommand.setReadyPlayers(gameState.getNumPlayers());

                // If all lobby players ready, start the game
                if (server.getNumClients() >= 2 && gameState.getNumPlayers() == server.getNumClients())
                    startGame = true;
                break;
            }

            case GET_ATTACHED_PLAYER: {
                System.out.println("== Command handler says: Fetching player attached to client");
                returnCommand.setCommand(Command.RETURN_ATTACHED_PLAYER);
                returnCommand.setPlayer(player);
                returnCommand.setPlayerId(gameCommand.getPlayerId());
                break;
            }

            case GET_LOBBY_STATE: {
                System.out.println("== Command handler says: Fetching lobby state");
                returnCommand.setCommand(Command.RETURN_LOBBY_STATE);
                returnCommand.setReadyPlayers(gameState.getNumPlayers());
                returnCommand.setJoinedPlayers(server.getNumClients());
                break;
            }

            case DISCARD_CARD: {
                System.out.println("== Command handler says: Player " + playerId + " is discarding a card");
                player.discardCard(card);
                gameState.discardAdventureCard(card);
                returnCommand.setCommand(Command.DISCARDED_CARD);
                returnCommand.setPlayer(player);
                break;
            }

            case WILL_SPONSOR_QUEST: {
                quest = gameCommand.getQuest();
                quest.setSponsor(player);
                gameState.setCurrentQuest(quest);

                System.out.println("== Command handler says: Player " + playerId + " agreed to sponsor quest");

                returnCommand.setCommand(Command.FOUND_QUEST_SPONSOR);
                returnCommand.setPlayer(player);

                gameState.setGameStatus(GameStatus.FINDING_QUEST_PARTICIPANTS);

                break;
            }

            case WILL_NOT_SPONSOR_QUEST: {
                System.out.println("== Command handler says: Player " + playerId + " refused to sponsor quest");

                returnCommand.setCommand(Command.FIND_QUEST_SPONSOR);
                returnCommand.setPlayer(player);

                gameState.setGameStatus(GameStatus.FINDING_QUEST_SPONSOR);

                break;
            }

            case WILL_JOIN_QUEST: {
                quest.addQuestPlayer(player); // Add participant to quest

                System.out.println("== Command handler says: Player " + playerId + " agreed to participate in quest");

                returnCommand.setCommand(Command.JOINED_QUEST);
                returnCommand.setPlayer(player);

                gameState.setGameStatus(GameStatus.FINDING_QUEST_PARTICIPANTS);

                break;
            }

            case WILL_NOT_JOIN_QUEST: {
                System.out.println("== Command handler says: Player " + playerId + " did not agreed to participate in quest");

                returnCommand.setCommand(Command.DID_NOT_JOIN_QUEST);
                returnCommand.setPlayer(player);

                gameState.setGameStatus(GameStatus.FINDING_QUEST_PARTICIPANTS);

                break;
            }

            case END_TURN: {
                System.out.println("== Command handler says: Player took " + playerId);

                returnCommand.setCommand(Command.ENDED_TURN);
                returnCommand.setPlayer(player);

                gameState.setGameStatus(GameStatus.RUNNING); // Update game status

                break;
            }

            case ACCEPT_QUEST_STAGE_CARD: {
                System.out.println("== Command handler says: Player " + playerId + " took stage card");

                quest.getQuestPlayerByPlayerId(playerId).addCard(card);

                returnCommand.setCommand(Command.ACCEPTED_QUEST_STAGE_CARD);
                returnCommand.setPlayer(player);

                gameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }

            case DISCARD_QUEST_STAGE_CARD: {
                System.out.println("== Command handler says: Player " + playerId + " discarded stage card");

                gameState.discardAdventureCard(card);

                returnCommand.setCommand(Command.DISCARDED_QUEST_STAGE_CARD);
                returnCommand.setPlayer(player);

                gameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }

            case TAKE_QUEST_TURN: {
                System.out.println("== Command handler says: Player " + playerId + " took stage turn");

                quest.getQuestPlayerByPlayerId(playerId).setPlayerQuestCardsUsed(cards);
                boolean discardedQuestCards = player.discardCards(cards);

                System.out.println("== Command handler says: Discarding quest cards " + discardedQuestCards);

                returnCommand.setCommand(Command.TOOK_QUEST_TURN);
                returnCommand.setPlayer(player);

                gameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }

            case END_QUEST_TURN: {
                System.out.println("== Command handler says: Player " + playerId + " ended stage turn");

                returnCommand.setCommand(Command.ENDED_QUEST_TURN);
                returnCommand.setPlayer(player);

                gameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }

            case ACCEPT_SPONSOR_QUEST_CARDS: {
                Player sponsor = quest.getSponsor();
                System.out.println("== Command handler says: Player " + playerId + " accepted quest sponsor cards");

                sponsor.addCards(cards);

                returnCommand.setCommand(Command.ACCEPTED_SPONSOR_QUEST_CARDS);
                returnCommand.setPlayer(sponsor);

                gameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }

            // TODO :: Remove. No longer needed
            case ACCEPT_QUEST_SHIELDS: {
                System.out.println("== Command handler says: Player " + playerId + " accepted quest shields");

                returnCommand.setCommand(Command.ACCEPTED_QUEST_SHIELDS);
                returnCommand.setPlayer(player);

                gameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }

            case END_QUEST: {
                System.out.println("== Command handler says: Player " + playerId + " ended quest");

                returnCommand.setCommand(Command.ENDED_QUEST);
                returnCommand.setPlayer(player);

                gameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }

            case COMPLETE_GAME: {
                System.out.println("== Command handler says: Player " + playerId + " completed game");

                returnCommand.setCommand(Command.COMPLETED_GAME);
                returnCommand.setPlayer(player);
                returnCommand.setPlayerId(playerId);

                break;
            }

            case END_EVENT: {
                playerId = gameCommand.getPlayerId();
                System.out.println("== Command handler says: Player " + playerId +" ended event");

                returnCommand.setCommand(Command.ENDED_EVENT);
                returnCommand.setPlayer(player);
                returnCommand.setPlayerId(playerId);

                gameState.setGameStatus(GameStatus.ENDING_QUEST);
            }
        }

        if (shouldNotifyClients) {
            server.incrementNumAccepted();
            server.notifyClients(returnCommand);
        }
        ;

        if (startGame) new Thread(new GameRunner(server, server.getGameState())).start();

        return returnCommand;
    }
}
