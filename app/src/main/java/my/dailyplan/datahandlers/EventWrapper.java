package my.dailyplan.datahandlers;

import java.io.Serializable;
import java.util.ArrayList;

public class EventWrapper implements Serializable{ //Classe per l'incapsulamento degli eventi in strutture dati
    private ArrayList<Event> events;

    public EventWrapper(ArrayList<Event> events) {
        this.events = events;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
}
