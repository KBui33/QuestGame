package component.card;

public class RankCard extends Card{

    private Rank rank;

    public RankCard(Rank rank) {
        super("", "");
        this.rank = rank;
        setRankDetails(rank);
    }

    private void setRankDetails(Rank rank) {
        switch (rank) {
            case SQUIRE -> {
                this.setTitle("Squire");
                this.setCardImg("/ranks/quest_rank_1.png");
            }
            case KNIGHT -> {
                this.setTitle("Knight");
                this.setCardImg("/ranks/quest_rank_2.png");
            }
            case CHAMPION_KNIGHT -> {
                this.setTitle("Champion Knight");
                this.setCardImg("/ranks/quest_rank_3.png");
            }
        }
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
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
                return 10;
            }
            case CHAMPION_KNIGHT -> {
                return 20;
            }
            default -> {
                return 0;
            }
        }
    }

    public static Rank getNextRank(Rank currentRank) {
        switch (currentRank) {
            case SQUIRE: {
                return Rank.KNIGHT;
            }
            case KNIGHT: {
                return Rank.CHAMPION_KNIGHT;
            }
            case CHAMPION_KNIGHT: {
                return Rank.ROUND_TABLE_KNIGHT;
            }
            default: {
                return currentRank;
            }
        }
    }

}
