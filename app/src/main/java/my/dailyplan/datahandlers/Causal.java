package my.dailyplan.datahandlers;

import java.io.Serializable;

public final class Causal implements Serializable{  //Classe per la rappresentazione delle causali

    private String id;
    private String descr;

    public Causal(String id, String descr) {
        this.id = id;
        this.descr = descr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
}
