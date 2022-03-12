package gui.controllers;

import gui.partials.QuestView;
import model.Quest;

/**
 * @author James DiNovo
 *
 * Control QuestView and handle interactions
 */
public class QuestController {
    private QuestView questView;
    Quest quest;

    public QuestController(Quest quest) {
        questView = new QuestView(quest);

        

    }

    public QuestView getQuestView() {
        return questView;
    }
}
