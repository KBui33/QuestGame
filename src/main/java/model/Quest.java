package model;

import java.util.ArrayList;

public class Quest {

    private ArrayList<Stage> stages;
    private ArrayList<QuestPlayer> questPlayers;

    public Quest() {
        this.stages = new ArrayList<>();
        this.questPlayers = new ArrayList<>();
    }

    public void setupQuest(){}

    public void startQuest(){}
}
