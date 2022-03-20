package component.card;

public class RankCard extends Card{

    private Rank rank;

    public RankCard(String title, String cardImg, Rank rank) {
        super(title, cardImg);
        this.rank = rank;
    }

    /**
     * Compute the number of shields based on rank
     * @return an integer representing the number of shields for the given rank card
     */
    public int getShields() {
        switch (rank) {
            case SQUIRE -> {
                return 5;
            }
            case KNIGHT -> {
                return 7;
            }
            case CHAMPION_KNIGHT -> {
                return 10;
            }
            default -> {
                return 0;
            }
        }
    }

    public Rank getRank() {
        return rank;
    }
}
