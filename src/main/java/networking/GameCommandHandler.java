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
        Command command = gameCommand.getCommand();
        GameCommand returnCommand = new GameCommand();
        InternalGameState internalGameState = server.getGameState();
        Player player = gameCommand.getPlayer();
        int playerId = gameCommand.getPlayerId();
        ExternalGameState externalGameState = server.getExternalGameState();
        boolean startGame = false;
        boolean shouldNotifyClients = true;

        Quest quest = gameCommand.getQuest();
        Event event = null;
        boolean startQuest = false;

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
                playerId = internalGameState.addPlayer(new Player());

                server.setClientPlayerId(gameCommand.getClientIndex(), playerId);

                returnCommand.setCommand(Command.IS_READY);
                returnCommand.setPlayerId(playerId);
                returnCommand.setReadyPlayers(internalGameState.getNumPlayers());

                // If all lobby players ready, start the game
                if (server.getNumClients() >= 2 && internalGameState.getNumPlayers() == server.getNumClients())
                    startGame = true;
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
                playerId = gameCommand.getPlayerId();
                Card card = gameCommand.getCard();
                System.out.println("== Command handler says: Player " + playerId + " is discarding a card");
                player.discardCard(card);
                internalGameState.discardAdventureCard(card);
                returnCommand.setCommand(Command.DISCARDED_CARD);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId));
                returnCommand.setPlayerId(playerId);
                break;
            }

            case WILL_SPONSOR_QUEST: {
                playerId = gameCommand.getPlayerId();

                quest = gameCommand.getQuest();
                quest.setSponsor(player);
                internalGameState.setCurrentQuest(quest);

                System.out.println("== Command handler says: Player " + playerId + " agreed to sponsor quest");

                returnCommand.setCommand(Command.FOUND_QUEST_SPONSOR);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId));
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.FINDING_QUEST_PARTICIPANTS);

                break;
            }

            case WILL_NOT_SPONSOR_QUEST: {
                playerId = gameCommand.getPlayerId();

                System.out.println("== Command handler says: Player " + playerId + " refused to sponsor quest");

                returnCommand.setCommand(Command.FIND_QUEST_SPONSOR);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId));
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.FINDING_QUEST_SPONSOR);

                break;
            }

            case WILL_JOIN_QUEST: {
                playerId = gameCommand.getPlayerId();

                quest = internalGameState.getCurrentQuest();
                quest.addQuestPlayer(player); // Add participant to quest

                System.out.println("== Command handler says: Player " + playerId + " agreed to participate in quest");

                returnCommand.setCommand(Command.JOINED_QUEST);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId));
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.FINDING_QUEST_PARTICIPANTS);

                break;
            }

            case WILL_NOT_JOIN_QUEST: {
                playerId = gameCommand.getPlayerId();

                System.out.println("== Command handler says: Player " + playerId + " did not agreed to participate in quest");

                returnCommand.setCommand(Command.DID_NOT_JOIN_QUEST);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId));
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.FINDING_QUEST_PARTICIPANTS);

                break;
            }

            case END_TURN: {
                playerId = gameCommand.getPlayerId();

                System.out.println("== Command handler says: Player took " + playerId);

                returnCommand.setCommand(Command.ENDED_TURN);
                returnCommand.setPlayerId(playerId);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId));

                internalGameState.setGameStatus(GameStatus.RUNNING); // Update game status

                break;
            }

            case ACCEPT_QUEST_STAGE_CARD: {
                playerId = gameCommand.getPlayerId();
                Card card = gameCommand.getCard();
                System.out.println("== Command handler says: Player " + playerId + " took stage card");

                quest = internalGameState.getCurrentQuest();
                quest.getQuestPlayerByPlayerId(playerId).addCard(card);

                returnCommand.setCommand(Command.ACCEPTED_QUEST_STAGE_CARD);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId));
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }

            case DISCARD_QUEST_STAGE_CARD: {
                playerId = gameCommand.getPlayerId();
                Card card = gameCommand.getCard();
                System.out.println("== Command handler says: Player " + playerId + " discarded stage card");

                internalGameState.discardAdventureCard(card);

                returnCommand.setCommand(Command.DISCARDED_QUEST_STAGE_CARD);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId));
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }

            case TAKE_QUEST_TURN: {
                playerId = gameCommand.getPlayerId();
                ArrayList<Card> stageCards = gameCommand.getCards();
                System.out.println("== Command handler says: Player " + playerId + " took stage turn");

                quest = internalGameState.getCurrentQuest();
                quest.getQuestPlayerByPlayerId(playerId).setPlayerQuestCardsUsed(stageCards);
                player = internalGameState.getPlayer(playerId);
                boolean discardedQuestCards = player.discardCards(stageCards);

                System.out.println("== Command handler says: Discarding quest cards " + discardedQuestCards);

                returnCommand.setCommand(Command.TOOK_QUEST_TURN);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId));
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }

            case END_QUEST_TURN: {
                playerId = gameCommand.getPlayerId();
                System.out.println("== Command handler says: Player " + playerId + " ended stage turn");

                player = internalGameState.getPlayer(playerId);

                returnCommand.setCommand(Command.ENDED_QUEST_TURN);
                returnCommand.setPlayer(internalGameState.getPlayer(playerId));
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }

            case ACCEPT_SPONSOR_QUEST_CARDS: {
                quest = internalGameState.getCurrentQuest();
                Player sponsor = quest.getSponsor();
                playerId = gameCommand.getPlayerId();
                ArrayList<Card> cards = gameCommand.getCards();
                System.out.println("== Command handler says: Player " + playerId + " accepted quest sponsor cards");

                sponsor.addCards(cards);

                returnCommand.setCommand(Command.ACCEPTED_SPONSOR_QUEST_CARDS);
                returnCommand.setPlayer(sponsor);
                returnCommand.setPlayerId(sponsor.getPlayerId());

                internalGameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }

            // TODO :: Remove. No longer needed
            case ACCEPT_QUEST_SHIELDS: {
                playerId = gameCommand.getPlayerId();
                ArrayList<Card> cards = gameCommand.getCards();
                System.out.println("== Command handler says: Player " + playerId + " accepted quest shields");

                returnCommand.setCommand(Command.ACCEPTED_QUEST_SHIELDS);
                returnCommand.setPlayer(player);
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }

            case END_QUEST: {
                playerId = gameCommand.getPlayerId();
                System.out.println("== Command handler says: Player " + playerId + " ended quest");

                returnCommand.setCommand(Command.ENDED_QUEST);
                returnCommand.setPlayer(player);
                returnCommand.setPlayerId(playerId);

                internalGameState.setGameStatus(GameStatus.RUNNING_QUEST);

                break;
            }
        }

        if (shouldNotifyClients) {
            server.incrementNumAccepted();
            server.notifyClients(returnCommand);
        };

        if (startGame) new Thread(new GameRunner(server, server.getGameState())).start();

        return returnCommand;
    }
}
