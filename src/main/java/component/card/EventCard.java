package component.card;

public class EventCard extends Card{
    private String description;
    public EventCard(String title, String cardImg, String description) {
        super(title, cardImg);
        this.description = description;
    }

}
