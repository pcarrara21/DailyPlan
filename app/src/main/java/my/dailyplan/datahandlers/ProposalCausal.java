package my.dailyplan.datahandlers;

import java.io.Serializable;

public class ProposalCausal implements Serializable{ //Classe per la rappresentazione delle causali delle proposte

    private int id;
    private String descr;

    public ProposalCausal(int id, String descr) {
        this.id = id;
        this.descr = descr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
}
