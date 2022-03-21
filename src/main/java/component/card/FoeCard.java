package component.card;

import java.util.Arrays;

public class FoeCard extends Card{

    private String extra; // Special description for foe (See for_11.png)
    private int[] bp;

    public FoeCard(String title, String cardImg, int[] bp, String extra) {
        super(title, cardImg);
        this.bp =  bp;
        Arrays.sort(this.bp);// Always sorted
        this.extra = extra;
    }

    public FoeCard(String title, String cardImg, int[] bp) {
        super(title, cardImg);
        this.bp = bp;
        this.extra = "";
    }

    public String getExtra() {
        return extra;
    }

    public int[] getBp() {
        return bp;
    }
}
