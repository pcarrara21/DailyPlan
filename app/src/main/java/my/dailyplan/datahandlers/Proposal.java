package my.dailyplan.datahandlers;

import java.io.Serializable;

public class Proposal extends Event implements Serializable{ //Classe per la rappresentazione delle proposte: estende evento

    private ProposalCausal descProp;
    private int numbOfAttendes;

    public Proposal(int ID, String USER_ID, String date, String time, String note,ProposalCausal descrProp, int numbOfAttendes) {
        super(ID, USER_ID, date, time, null, false, note, null);
        this.descProp = descrProp;
        this.numbOfAttendes = numbOfAttendes;

    }

    public ProposalCausal getPropCausal() {
        return descProp;
    }

    public void setPropCausal(ProposalCausal descProp) {
        this.descProp = descProp;
    }

    public int getNumbOfAttendes() {
        return numbOfAttendes;
    }

    public void setNumbOfAttendes(int numbOfAttendes) {
        this.numbOfAttendes = numbOfAttendes;
    }
}
