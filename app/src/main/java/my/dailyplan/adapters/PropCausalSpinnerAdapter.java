package my.dailyplan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import my.dailyplan.R;
import my.dailyplan.datahandlers.ProposalCausal;

public class PropCausalSpinnerAdapter extends ArrayAdapter<ProposalCausal> {

    private Context context;
    private List<ProposalCausal> proposals;

    public PropCausalSpinnerAdapter(Context context, int resource, List<ProposalCausal> objects) {
        super(context, resource, objects);
        this.context = context;
        this.proposals = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) { //Metodo per l'ottenimento della vista in base alla posizione

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.spinner_probcausal_row, null);
        }
        TextView id = (TextView) v.findViewById(R.id.propCausalsID);
        id.setText("" + proposals.get(position).getId());

        TextView descr = (TextView) v.findViewById(R.id.descr);
        descr.setText(proposals.get(position).getDescr());
        return v;
    }


    public View getDropDownView(int position, View convertView, ViewGroup parent) { //metodo per l'ottenimento delle viste dello spinner

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.spinner_probcausal_row, null);
        }
        TextView id = (TextView) v.findViewById(R.id.propCausalsID);
        id.setText("" + proposals.get(position).getId());

        TextView descr = (TextView) v.findViewById(R.id.descr);
        descr.setText(proposals.get(position).getDescr());

        return v;
    }

    @Override
    public ProposalCausal getItem(int position) { return proposals.get(position);  }

    @Override
    public int getPosition(ProposalCausal p) {

        for(int i=0; i< proposals.size();i++)
        {
            if(proposals.get(i).getId() == p.getId())
                return i;
        }
        return 0;
    }
}
