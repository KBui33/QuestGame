package networking.server;

import component.card.Card;
import model.*;

import java.util.ArrayList;

public class QuestJoinRunner extends Runner {

    private Quest quest;
    private Server server;

    public QuestJoinRunner(Server server, Quest quest) {
        this.server = server;
        this.quest = quest;
    }

    @Override
    public void loop() {
        InternalGameState gameState = this.server.getGameState();
        gameState.setGameStatus(GameStatus.FINDING_QUEST_PARTICIPANTS);
        shouldRespond = 0;
        server.resetNumResponded(CommandType.QUEST);

        System.out.println("== Quest join runner says: Finding quest participants");

        for(Player player: gameState.getPlayers()) {
            int playerId = player.getPlayerId();

            if(playerId == quest.getSponsor().getPlayerId()) continue; // Do not prompt quest sponsor
            shouldRespond++;

            quest.setCurrentTurnPlayer(player);
            gameState.setGameStatus(GameStatus.PROMPTING_QUEST_PARTICIPANT);
            QuestCommand playerShouldJoinQuestCommand = new QuestCommand(QuestCommandName.SHOULD_JOIN_QUEST);
            playerShouldJoinQuestCommand.setQuest(quest);
            playerShouldJoinQuestCommand.setCard(gameState.getCurrentStoryCard());
            playerShouldJoinQuestCommand.setPlayer(player);
            playerShouldJoinQuestCommand.setPlayerId(playerId);

            server.notifyClientByPlayerId(playerId, playerShouldJoinQuestCommand);
            System.out.println("== Quest join runner says: should join quest command sent to player " + playerId);
        }

        waitForResponses();

        shouldStopRunner();

        int numParticipants = quest.getQuestPlayers().size();
        if(numParticipants > 0) {  // if any participants, start quest
            System.out.println("== Quest join runner says: starting quest with " + numParticipants + " participants");
            new Thread(new QuestRunner(server, quest)).start();
        } else { // ...otherwise continue running
            // Return cards to sponsor
            Player sponsor = quest.getSponsor();
            ArrayList<Card> questCardsUsed = quest.getAllQuestCards(false);
            sponsor.addCards(questCardsUsed);

            QuestCommand noPlayerJoinedQuestCommand = new QuestCommand(QuestCommandName.NO_PLAYER_JOINED_QUEST);
            noPlayerJoinedQuestCommand.setPlayerId(sponsor.getPlayerId());
            noPlayerJoinedQuestCommand.setPlayer(sponsor);
            noPlayerJoinedQuestCommand.setQuest(quest);
            noPlayerJoinedQuestCommand.setCards(questCardsUsed);
            server.notifyClientByPlayerId(sponsor.getPlayerId(), noPlayerJoinedQuestCommand);
            System.out.println("== Quest join runner says: No participants to join quest. Exiting " + questCardsUsed.size());
            gameState.setGameStatus(GameStatus.RUNNING);
        }
    }

    @Override
    protected void waitForResponses() {
        try {
            while (server.getNumResponded(CommandType.QUEST) < shouldRespond) Thread.sleep(1000);
            server.resetNumResponded(CommandType.QUEST);
            shouldRespond = 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
