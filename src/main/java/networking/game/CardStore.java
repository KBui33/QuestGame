package networking.game;

import networking.game.components.card.Card;

public abstract class CardStore {

    protected abstract Card createCard(String type);
}
