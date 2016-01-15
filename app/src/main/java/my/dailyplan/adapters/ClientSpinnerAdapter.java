package my.dailyplan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import my.dailyplan.R;
import my.dailyplan.datahandlers.Client;

public class ClientSpinnerAdapter extends ArrayAdapter<Client> {

    private Context context;
    private List<Client> clients; // Lista di clienti

    public ClientSpinnerAdapter(Context context, int resource, List<Client> objects) {
        super(context, resource, objects);
        this.context = context;
        this.clients = objects;
    }

    // Per ottenere la view dello spinner
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.spinner_client_row, null);
        }

        //TextView per l'ID del cliente
        TextView idClient = (TextView) v.findViewById(R.id.client_id);
        idClient.setText("" + clients.get(position).getId());

        //TextView per il nome del cliente
        TextView name = (TextView) v.findViewById(R.id.name);
        name.setText(clients.get(position).getName());

        //TextView per la citta'
        TextView city = (TextView) v.findViewById(R.id.city);
        city.setText(clients.get(position).getCity());
        return v;
    }


    // Per ottenere la view del menu a discesa
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.spinner_client_row, null);
        }
        //TextView dell'ID del cliente
        TextView idClient = (TextView) v.findViewById(R.id.client_id);
        idClient.setText("" + clients.get(position).getId());

        //TextView del nome del cliente
        TextView name = (TextView) v.findViewById(R.id.name);
        name.setText(clients.get(position).getName());

        //TextView della citta'
        TextView city = (TextView) v.findViewById(R.id.city);
        city.setText(clients.get(position).getCity());
        return v;
    }

    @Override
    public Client getItem(int position) { return clients.get(position);  }

    @Override
    public int getPosition(Client c) {

        for(int i=0; i< clients.size();i++)
        {
            if(clients.get(i).getId() == c.getId())
                return i;
        }
        return 0;
    }
}
