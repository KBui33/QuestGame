package model;

import java.io.Serializable;

public class QuestStageState extends Quest implements Serializable {
    private Quest quest;

    public QuestStageState(Quest quest) {
        this.quest = quest;
    }

    public Stage getCurrentStage() {return this.quest.getCurrentStage(); }


}
