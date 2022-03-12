package model;

import game.components.card.Card;

public abstract class Stage {
    private Card stageCard;

    public Stage(Card stageCard) {
        this.stageCard = stageCard;
    }

    public Card getStageCard() {
        return stageCard;
    }
}
