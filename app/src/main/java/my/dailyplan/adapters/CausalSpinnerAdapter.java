package my.dailyplan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import my.dailyplan.R;
import my.dailyplan.datahandlers.Causal;

public class CausalSpinnerAdapter extends ArrayAdapter<Causal> { //Adattatore per lo spinner delle causali

    private Context context;
    private List<Causal> causals;

    public CausalSpinnerAdapter(Context context, int resource, List<Causal> objects) { //costruttore dell'adattatore
        super(context, resource, objects);
        this.context = context;
        this.causals = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) { //metodo per l'ottenimento della singola riga
                                                                            //dello spinner

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.spinner_causal_row, null);
        }
        TextView idClient = (TextView) v.findViewById(R.id.causal_id);
        idClient.setText(causals.get(position).getId());

        TextView name = (TextView) v.findViewById(R.id.descr);
        name.setText(causals.get(position).getDescr());
        return v;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.spinner_causal_row, null);
        }
        TextView idClient = (TextView) v.findViewById(R.id.causal_id);
        idClient.setText(causals.get(position).getId());

        TextView name = (TextView) v.findViewById(R.id.descr);
        name.setText(causals.get(position).getDescr());
        return v;
    }

    @Override
    public Causal getItem(int position){  return causals.get(position);  }

    @Override
    public int getPosition(Causal c) {

        for(int i=0; i< causals.size();i++)
        {
            if(causals.get(i).getId().equals(c.getId()))
                return i;
        }
        return 0;
    }

}
