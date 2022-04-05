package component.deck;

import component.card.Card;
import component.card.EventCard;
import component.card.QuestCard;
import component.card.TournamentCard;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class StoryDeck extends Deck {
    @Override
    public void init() {
        try {

            Object ob = new JSONParser().parse(new FileReader(String.valueOf(StoryDeck.class.getResource("/json/story_deck_specs.json").getFile())));
            JSONObject jsonObject = (JSONObject) ob;
            JSONArray quests = (JSONArray) jsonObject.get("quests");
            JSONArray events = (JSONArray) jsonObject.get("testEvents");
            JSONArray tournaments = (JSONArray) jsonObject.get("tournaments");

            Iterator<JSONObject> it = quests.iterator();
//            while(it.hasNext()) {
//                JSONObject obj = (JSONObject) it.next();
//                String title = (String)  obj.get("title");
//                String image = (String) obj.get("image");
//                String foe = (String) obj.get("foe");
//                Long stages = (Long) obj.get("stages");
//                this.cards.add(new QuestCard(title, image, Math.toIntExact(stages), foe));
//            }

            it = events.iterator();
            while(it.hasNext()){
                JSONObject obj = it.next();
                String title = (String) obj.get("title");
                String image = (String)  obj.get("image");
                Long freq = (Long) obj.get("frequency");
                for(int i = 0 ; i < freq; i++ ){
                    this.cards.add(new EventCard(title, image));
                }
            }

//            it = tournaments.iterator();
//            while (it.hasNext()) {
//                JSONObject obj = it.next();
//                String title = (String) obj.get("title");
//                String image = (String) obj.get("image");
//                int shields = Math.toIntExact((Long) obj.get("shields"));
//                Long freq = (Long) obj.get("frequency");
//                for (int i = 0; i < freq; i++) {
//                    this.cards.add(new TournamentCard(title, image, shields));
//                }
//            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}
