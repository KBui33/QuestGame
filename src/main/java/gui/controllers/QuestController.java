package gui.controllers;

import gui.partials.QuestView;
import gui.partials.StageView;
import model.Quest;

/**
 * @author James DiNovo
 *
 * Control QuestView and handle interactions
 */
public class QuestController {
    private QuestView questView;
    private StageView currentStage;
    private Quest quest;

    public QuestController(GameController parent, Quest quest) {
        questView = new QuestView(quest);

        // take in quest

        // show player current stage

        // let player choose weapon cards if its a foe to raise their battle points

        // cycle through other quest players

        // show results to player and then move to next quest player or after each player has chosen cards?
        

    }

    public StageView getCurrentStageView() {
        return currentStage;
    }

    public QuestView getQuestView() {
        return questView;
    }
}
