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
        server.notifyClients(new GameCommand(Command.QUEST_STARTED));
        System.out.println("== Quest runner says: initializing quest");


        // Setup already done in quest setup controller
        try {
            int stageIndex = 1;
            for (Stage stage : quest.getStages()) {
                System.out.println("== Quest runner says: Stage " + stageIndex + " started");

                // Deal adventure cards to participants
                System.out.println("== Quest runner says: Dealing an adventure card to each participant");
                for (QuestPlayer questPlayer : quest.getCurrentQuestPlayers()) {
                    int playerId = questPlayer.getPlayerId();
                    System.out.println("== Game runner says: Sending quest stage card to player " + playerId);
                    gameState.setGameStatus(GameStatus.TAKING_QUEST_STAGE_CARD);

                    GameCommand questStageCardCommand = new GameCommand(Command.PLAYER_TAKE_STAGE_CARD);
                    questStageCardCommand.setQuest(quest);
                    questStageCardCommand.setCard(gameState.drawAdventureCard());
                    server.notifyClientByPlayerId(playerId, questStageCardCommand);

                    // Wait for player to take card
                    while (!gameState.getGameStatus().equals(GameStatus.RUNNING_QUEST)) {
                        Thread.sleep(1000);
                    }
                }

                for (QuestPlayer questPlayer : quest.getCurrentQuestPlayers()) {
                    int playerId = questPlayer.getPlayerId();
                    System.out.println("== Game runner says: Sending take quest turn command to player " + playerId);
                    gameState.setGameStatus(GameStatus.TAKING_QUEST_TURN);

                    GameCommand questStageCommand = new GameCommand(Command.PLAYER_QUEST_TURN);
                    questStageCommand.setQuest(quest);
                    questStageCommand.setCard(stage.getStageCard());
                    server.notifyClientByPlayerId(playerId, questStageCommand);

                    // Wait for player to take turn
                    while (!gameState.getGameStatus().equals(GameStatus.RUNNING_QUEST)) {
                        Thread.sleep(1000);
                    }
                }

                // Find stage winners and losers
                ArrayList<QuestPlayer> stageLosers = quest.computeStageWinners(stage);
                ArrayList<QuestPlayer> stageWinners = quest.getCurrentQuestPlayers();
                System.out.println("== Stage:\tin game -> " + quest.getCurrentQuestPlayers().size() + "\tlosers -> " + stageLosers.size());

                // Send notification to quest losers
                for (QuestPlayer stageLoser : stageLosers) {
                    int playerId = stageLoser.getPlayerId();
                    System.out.println("== Game runner says: Sending end quest turn to loser " + playerId);
                    gameState.setGameStatus(GameStatus.ENDING_QUEST_TURN);

                    GameCommand questStageLostCommand = new GameCommand(Command.QUEST_STAGE_LOST);
                    questStageLostCommand.setPlayerId(playerId);
                    questStageLostCommand.setPlayer(stageLoser.getPlayer());
                    questStageLostCommand.setQuest(quest);
                    server.notifyClientByPlayerId(playerId, questStageLostCommand);

                    // Wait for player to end turn
                    while (!gameState.getGameStatus().equals(GameStatus.RUNNING_QUEST)) {
                        Thread.sleep(1000);
                    }
                }

                // Send notification to quest winners
                for (QuestPlayer stageWinner : stageWinners) {
                    int playerId = stageWinner.getPlayerId();
                    System.out.println("== Game runner says: Sending end quest turn to winner " + playerId);
                    gameState.setGameStatus(GameStatus.ENDING_QUEST_TURN);

                    GameCommand questStageWonCommand = new GameCommand(Command.QUEST_STAGE_WON);
                    questStageWonCommand.setPlayerId(playerId);
                    questStageWonCommand.setPlayer(stageWinner.getPlayer());
                    questStageWonCommand.setQuest(quest);
                    server.notifyClientByPlayerId(playerId, questStageWonCommand);

                    // Wait for player to end turn
                    while (!gameState.getGameStatus().equals(GameStatus.RUNNING_QUEST)) {
                        Thread.sleep(1000);
                    }
                }

                // If no players are left, end quest
                if (stageWinners.size() <= 0) {
                    break;
                }

                // Reset cards of winners
                for (QuestPlayer stageWinner : stageWinners) {
                    stageWinner.resetQuestCardsUsed();
                }

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
                int playerId = questSponsor.getPlayerId();
                System.out.println("== Game runner says: Sending quest cards to sponsor " + playerId);
                gameState.setGameStatus(GameStatus.TAKING_QUEST_SPONSOR_CARDS);

                GameCommand takeQuestSponsorCardsCommand = new GameCommand(Command.PLAYER_TAKE_SPONSOR_QUEST_CARDS);
                takeQuestSponsorCardsCommand.setPlayerId(playerId);
                takeQuestSponsorCardsCommand.setPlayer(questSponsor);
                takeQuestSponsorCardsCommand.setQuest(quest);
                takeQuestSponsorCardsCommand.setCards(sponsorQuestCards);
                server.notifyClientByPlayerId(playerId, takeQuestSponsorCardsCommand);

                // Wait for sponsor to accept cards
                while (!gameState.getGameStatus().equals(GameStatus.RUNNING_QUEST)) {
                    Thread.sleep(1000);
                }
            }

            // Send end quest command to all participants
            for (QuestPlayer questPlayer : quest.getQuestPlayers()) {
                Player player = questPlayer.getPlayer();
                int playerId = player.getPlayerId();
                System.out.println("== Game runner says: Sending end quest to player " + playerId);
                gameState.setGameStatus(GameStatus.ENDING_QUEST);

                GameCommand endQuestCommand = new GameCommand(Command.PLAYER_END_QUEST);
                endQuestCommand.setPlayerId(playerId);
                endQuestCommand.setPlayer(player);
                endQuestCommand.setQuest(quest);
                server.notifyClientByPlayerId(playerId, endQuestCommand);

                // Wait for player to end quest
                while (!gameState.getGameStatus().equals(GameStatus.RUNNING_QUEST)) {
                    Thread.sleep(1000);
                }
            }

            // Discard quest and stage cards
            System.out.println("== Game runner says: Discarding all stage cards");
            discardQuestStageCards();

            server.notifyClients(new GameCommand(Command.QUEST_COMPLETED));

            gameState.setGameStatus(GameStatus.RUNNING);

        } catch (InterruptedException e) {
            e.printStackTrace();
            shouldStopRunner();
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
