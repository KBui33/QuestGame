package networking.server;

import component.card.*;
import model.*;

import java.util.ArrayList;

public class QuestRunner extends Runner {
    private Server server;
    private InternalGameState gameState;
    private Quest quest;

    public QuestRunner(Server server, Quest quest) {
        this.server = server;
        this.gameState = server.getGameState();
        this.quest = quest;
    }


    @Override
    public void loop() {
        //gameState.setCurrentQuest(this.quest);
        gameState.setGameStatus(GameStatus.RUNNING_QUEST);
        quest.startQuest();
        server.notifyClients(new QuestCommand(QuestCommandName.QUEST_STARTED));
        System.out.println("== Quest runner says: initializing quest");

        shouldRespond = 0;
        server.resetNumResponded(CommandType.QUEST);

        // Setup already done in quest setup controller
        int stageIndex = 1;
        for (Stage stage : quest.getStages()) {
            System.out.println("== Quest runner says: Stage " + stageIndex + " started");

            // Deal adventure cards to participants
            System.out.println("== Quest runner says: Dealing an adventure card to each participant");
            for (QuestPlayer questPlayer : quest.getCurrentQuestPlayers()) {
                quest.setCurrentTurnPlayer(questPlayer.getPlayer());
                int playerId = questPlayer.getPlayerId();
                shouldRespond++;

                System.out.println("== Game runner says: Sending quest stage card to player " + playerId);
                gameState.setGameStatus(GameStatus.TAKING_QUEST_STAGE_CARD);

                QuestCommand questStageCardCommand = new QuestCommand(QuestCommandName.PLAYER_TAKE_STAGE_CARD);
                questStageCardCommand.setQuest(quest);
                questStageCardCommand.setCard(gameState.drawAdventureCard());
                server.notifyClientByPlayerId(playerId, questStageCardCommand);

            }

            waitForResponses();

            for (QuestPlayer questPlayer : quest.getCurrentQuestPlayers()) {
                quest.setCurrentTurnPlayer(questPlayer.getPlayer());
                int playerId = questPlayer.getPlayerId();
                shouldRespond++;

                System.out.println("== Game runner says: Sending take quest turn command to player " + playerId);
                gameState.setGameStatus(GameStatus.TAKING_QUEST_TURN);

                QuestCommand questStageCommand = new QuestCommand(QuestCommandName.PLAYER_QUEST_TURN);
                questStageCommand.setQuest(quest);
                questStageCommand.setCard(stage.getStageCard());
                server.notifyClientByPlayerId(playerId, questStageCommand);
            }

            waitForResponses();

            // Find stage winners and losers
            ArrayList<QuestPlayer> stageLosers = quest.computeStageWinners(stage);
            ArrayList<QuestPlayer> stageWinners = quest.getCurrentQuestPlayers();
            System.out.println("== Stage:\tin game -> " + quest.getCurrentQuestPlayers().size() + "\tlosers -> " + stageLosers.size());

            // Send notification to quest losers
            for (QuestPlayer stageLoser : stageLosers) {
                quest.setCurrentTurnPlayer(stageLoser.getPlayer());
                int playerId = stageLoser.getPlayerId();
                shouldRespond++;

                System.out.println("== Game runner says: Sending end quest turn to loser " + playerId);
                gameState.setGameStatus(GameStatus.ENDING_QUEST_TURN);

                QuestCommand questStageLostCommand = new QuestCommand(QuestCommandName.QUEST_STAGE_LOST);
                questStageLostCommand.setPlayerId(playerId);
                questStageLostCommand.setPlayer(stageLoser.getPlayer());
                questStageLostCommand.setQuest(quest);
                server.notifyClientByPlayerId(playerId, questStageLostCommand);
            }

            waitForResponses();

            // Send notification to quest winners
            for (QuestPlayer stageWinner : stageWinners) {
                quest.setCurrentTurnPlayer(stageWinner.getPlayer());
                int playerId = stageWinner.getPlayerId();
                shouldRespond++;

                System.out.println("== Game runner says: Sending end quest turn to winner " + playerId);
                gameState.setGameStatus(GameStatus.ENDING_QUEST_TURN);

                QuestCommand questStageWonCommand = new QuestCommand(QuestCommandName.QUEST_STAGE_WON);
                questStageWonCommand.setPlayerId(playerId);
                questStageWonCommand.setPlayer(stageWinner.getPlayer());
                questStageWonCommand.setQuest(quest);
                server.notifyClientByPlayerId(playerId, questStageWonCommand);
            }

            waitForResponses();

            // If no players are left, end quest
            if (stageWinners.size() <= 0) break;

            // Reset cards of winners
            for (QuestPlayer stageWinner : stageWinners) stageWinner.resetQuestCardsUsed();

            System.out.println("== Quest runner says: Stage " + stageIndex++ + " completed");
            quest.incrementStage(); // Increment stage
        }

        shouldStopRunner();
        System.out.println("== Quest runner says: Quest completed");

        // Give card(s) to the sponsor
        {
            System.out.println("== Quest runner says: Distributing cards to sponsor");

            int sponsorCardsNum = quest.distributeToSponsor();
            ArrayList<Card> sponsorQuestCards = new ArrayList<>();
            for (int i = 0; i < sponsorCardsNum; i++) sponsorQuestCards.add(gameState.drawAdventureCard());

            Player questSponsor = quest.getSponsor();
            quest.setCurrentTurnPlayer(questSponsor);
            int playerId = questSponsor.getPlayerId();
            shouldRespond = 1;
            System.out.println("== Game runner says: Sending quest cards to sponsor " + playerId);
            gameState.setGameStatus(GameStatus.TAKING_QUEST_SPONSOR_CARDS);

            QuestCommand takeQuestSponsorCardsCommand = new QuestCommand(QuestCommandName.PLAYER_TAKE_SPONSOR_QUEST_CARDS);
            takeQuestSponsorCardsCommand.setPlayerId(playerId);
            takeQuestSponsorCardsCommand.setPlayer(questSponsor);
            takeQuestSponsorCardsCommand.setQuest(quest);
            takeQuestSponsorCardsCommand.setCards(sponsorQuestCards);
            server.notifyClientByPlayerId(playerId, takeQuestSponsorCardsCommand);
        }

        // Distribute shields to winners
        quest.distributeShieldsToWinners();

        // Send end quest command to all participants
        for (QuestPlayer questPlayer : quest.getQuestPlayers()) {
            quest.setCurrentTurnPlayer(questPlayer.getPlayer());
            Player player = questPlayer.getPlayer();
            int playerId = player.getPlayerId();
            shouldRespond++;

            System.out.println("== Game runner says: Sending end quest to player " + playerId);
            gameState.setGameStatus(GameStatus.ENDING_QUEST);

            QuestCommand endQuestCommand = new QuestCommand(QuestCommandName.PLAYER_END_QUEST);
            endQuestCommand.setPlayerId(playerId);
            endQuestCommand.setPlayer(player);
            endQuestCommand.setQuest(quest);
            server.notifyClientByPlayerId(playerId, endQuestCommand);
        }

        waitForResponses();

        // Discard quest and stage cards
        System.out.println("== Game runner says: Discarding all stage cards");
        discardQuestStageCards();

        server.notifyClients(new QuestCommand(QuestCommandName.QUEST_COMPLETED));

        gameState.setGameStatus(GameStatus.RUNNING);

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

    private void discardQuestStageCards() {
        for (Stage stage : quest.getStages()) {
            gameState.discardAdventureCard(stage.getStageCard());
            if (stage instanceof FoeStage) {
                for (WeaponCard weaponCard : ((FoeStage) stage).getWeapons()) {
                    gameState.discardAdventureCard(weaponCard);
                }
            }
        }
    }
}
