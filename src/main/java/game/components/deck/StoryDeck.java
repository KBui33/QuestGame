package game.components.deck;

import game.components.card.QuestCard;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class StoryDeck extends Deck{
    @Override
    public void init() {
        try {

            Object ob = new JSONParser().parse(new FileReader(String.valueOf(StoryDeck.class.getResource("/json/story_deck_specs.json").getFile())));
            JSONObject jsonObject = (JSONObject) ob;
            JSONArray quests = (JSONArray) jsonObject.get("quests");
            JSONArray events = (JSONArray) jsonObject.get("events");
            JSONArray tournaments = (JSONArray) jsonObject.get("tournaments");

            Iterator<JSONObject> it = quests.iterator();
            while(it.hasNext()) {
                JSONObject obj = (JSONObject) it.next();
                String title = (String)  obj.get("title");
                String image = title.replace(' ', '_') + ".png";
                String foe = (String) obj.get("foes");
                Long stages = (Long) obj.get("stages");
                this.cards.add(new QuestCard(title, image, Math.toIntExact(stages), foe));
            }
        } catch(ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}