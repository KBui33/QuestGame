package model;

import game.components.card.Card;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class Stage implements Serializable {
    private Card stageCard;
    private Map<String, String> stageResults;

    public Stage(Card stageCard) {
        this.stageResults = new HashMap<>();
        this.stageCard = stageCard;
    }
    public Card getStageCard() {
        return stageCard;
    }

    public void setStageResults(Map<String, String> stageResults) {
        this.stageResults = stageResults;
    }

    public Map<String, String> getStageResults() {
        return stageResults;
    }
}
