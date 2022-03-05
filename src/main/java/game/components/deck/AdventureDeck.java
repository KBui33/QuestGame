package game.components.deck;

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
            //JSONArray events = (JSONArray) jsonObject.get("events");
            //JSONArray tournaments = (JSONArray) jsonObject.get("tournaments");

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
