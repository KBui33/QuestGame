package model;

import java.io.Serializable;

public class QuestCommand extends GameCommand implements Serializable {
    private Quest quest;

    public QuestCommand() {
        super();
        commandType = CommandType.QUEST;
    }

    public QuestCommand(QuestCommandName commandName) {
        this();
        this.commandName = commandName;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    @Override
    public String toString() {
        String cmd = super.toString();
        if(quest != null) cmd += "Quest: " + quest.getTitle() + ", ";
        return cmd;
    }
}
