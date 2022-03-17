package model;

import component.card.Card;

import java.io.Serializable;

public abstract class Stage implements Serializable {
    private Card stageCard;

    public Stage(Card stageCard) {
        this.stageCard = stageCard;
    }
    public Card getStageCard() {
        return stageCard;
    }
}
