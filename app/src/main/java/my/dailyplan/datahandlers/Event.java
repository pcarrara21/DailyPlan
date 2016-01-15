package my.dailyplan.datahandlers;

import java.io.Serializable;

public class Event implements Serializable { //Classe per la rappresentazione degli eventi

    private int ID;
    private String USER_ID;
    private String date;
    private String time;
    private Client client;
    private boolean inOffice;
    private String note;
    private Causal causal;

    public Event(int ID,String USER_ID, String date, String time, Client client, boolean inOffice, String note,Causal causal) {
        this.ID = ID;
        this.USER_ID = USER_ID;
        this.date = date;
        this.time = time;
        this.client = client;
        this.inOffice = inOffice;
        this.note = note;
        this.causal = causal;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public boolean isInOffice() {
        return inOffice;
    }

    public void setInOffice(boolean inOffice) {
        this.inOffice = inOffice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Causal getCasual() {
        return causal;
    }

    public void setCasual(Causal casual) {
        this.causal = casual;
    }
}
