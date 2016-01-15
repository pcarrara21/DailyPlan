package my.dailyplan.datahandlers;

import java.io.Serializable;
import java.util.ArrayList;

public class ProposalWrapper implements Serializable{ //Classe per l'incapsulamento delle proposte

    ArrayList<Proposal> proposals;

    public ProposalWrapper(ArrayList<Proposal> proposals) {
      this.proposals = proposals;
    }

    public ArrayList<Proposal> getProposals()
    {
        return proposals;
    }


}
