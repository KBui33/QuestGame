package component.card;

public class TournamentCard extends Card{
    private int shields;

    public TournamentCard(String title, String cardImg, int shields) {
        super(title, cardImg);
        this.shields = shields;
    }

    public int getShields() {
        return shields;
    }
}
