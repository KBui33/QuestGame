package game.components.deck;

import game.components.card.FoeCard;
import game.components.card.QuestCard;
import game.components.card.WeaponCard;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class AdventureDeck extends Deck{
    @Override
    public void init() {
        try {
            Object ob = new JSONParser().parse(new FileReader(String.valueOf(StoryDeck.class.getResource("/json/adventure_deck_specs.json").getFile())));
            JSONObject jsonObject = (JSONObject) ob;
            JSONArray weapons = (JSONArray) jsonObject.get("weapons");
            JSONArray foes = (JSONArray) jsonObject.get("foes");
            // not needed *yet*
//            JSONArray tests = (JSONArray) jsonObject.get("tests");
//            JSONArray allies = (JSONArray) jsonObject.get("allies");
//            JSONArray amours = (JSONArray) jsonObject.get("amours");

            Iterator<JSONObject> it = weapons.iterator();
            while(it.hasNext()) {
                JSONObject obj = (JSONObject) it.next();
                Long freq = (Long)  obj.get("frequency");
                String title = (String)  obj.get("title");
                String image = (String)  obj.get("image");
                Long battlePoints = (Long) obj.get("battlePoints");
                for (int i = 0; i < freq; i++) {
                    this.cards.add(new WeaponCard(title, image, Math.toIntExact(battlePoints)));
                }
            }

            it = foes.iterator();
            while(it.hasNext()) {
                JSONObject obj = (JSONObject) it.next();
                Long freq = (Long)  obj.get("frequency");
                String title = (String)  obj.get("title");
                String image = (String)  obj.get("image");
                String extra = (String)  obj.get("extra");
                int[] bp = new int[2];
                bp[0] = Math.toIntExact((Long) obj.get("lowBp"));
                bp[1] = Math.toIntExact((Long) obj.get("highBp"));
                if (bp[1] == 0) {
                    int[] tmp = bp;
                    bp = new int[1];
                    bp[0] = tmp[0];
                }
                for (int i = 0; i < freq; i++) {
                    if (extra != null) {
                        this.cards.add(new FoeCard(title, image, bp, extra));
                    } else {
                        this.cards.add(new FoeCard(title, image, bp));

                    }
                }
            }
        } catch(ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AdventureDeck ad = new AdventureDeck();
        ad.init();
        System.out.println(ad.cards.size());
    }
}
