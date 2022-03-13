package game.components.card;


public class QuestCard extends Card{

    private Integer stages;
    private String foe;

    public QuestCard(String title, String cardImg, Integer stages, String foe) {
        super(title, cardImg);
        this.stages = stages;
        this.foe = foe;
    }

    public Integer getStages() {
        return stages;
    }

    public String getFoe() {
        return foe;
    }
}
