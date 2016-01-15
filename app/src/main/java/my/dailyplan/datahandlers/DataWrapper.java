package my.dailyplan.datahandlers;

import java.io.Serializable;
import java.util.ArrayList;

public final class DataWrapper implements Serializable{ //Classe per l'incapsulamento delle informazioni
                                                        //relative a proposte, clienti e causali affinchè siano passate
                                                        //correttamente all'activity che le deve utilizzare
    private ArrayList<Client> client_array;
    private ArrayList<Causal> causal_array;
    private ArrayList<ProposalCausal> proposal_array;

    public DataWrapper(ArrayList<Client> client, ArrayList<Causal> causal){
        this.causal_array = causal;
        this.client_array = client;
    }

    public DataWrapper(ArrayList<ProposalCausal> proposal)
    {
        this.proposal_array = proposal;
    }

    public void setClient_array(ArrayList<Client> client_array) {
        this.client_array = client_array;
    }

    public void setCausal_array(ArrayList<Causal> causal_array) {
        this.causal_array = causal_array;
    }

    public ArrayList<ProposalCausal> getProposal_array() {
        return proposal_array;
    }

    public void setProposal_array(ArrayList<ProposalCausal> proposal_array) {
        this.proposal_array = proposal_array;
    }

    public ArrayList<Client> getClient_array() { return client_array; }

    public ArrayList<Causal> getCausal_array() { return causal_array; }
}
