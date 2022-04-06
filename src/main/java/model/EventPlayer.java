package model;

import java.io.Serializable;

public class EventPlayer extends PlayerDecorator implements Serializable {
//    private Player player;

    public EventPlayer(Player player) {
        super(player);
    }

    @Override
    public int calculateBattlePoints() {
        return 0;
    }
}
