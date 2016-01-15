package my.dailyplan.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import my.dailyplan.R;
import my.dailyplan.datahandlers.Client;

public class ClientListAdapter extends ArrayAdapter<Client>{

    private Activity context;
    private List<Client> clients;
    private final Integer[] imgid;

    public ClientListAdapter(Activity context, List<Client> clients, Integer[] imgid) {
        super(context, R.layout.try_row, clients);
        this.context=context;
        this.clients=clients;
        this.imgid=imgid;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.try_row, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        ImageView imageView1 = (ImageView) rowView.findViewById(R.id.icon1);

        txtTitle.setText(clients.get(position).getName());
        imageView.setImageResource(imgid[0]);   //Inizializzazione della prima imageview con la prima immagine dell'array
                                                //passato al costruttore


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //metodo per l'avvio della chiamata sul clic dell'icona telefono
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + clients.get(position).getTel()));
                getContext().startActivity(intent);
            }
        });

        imageView1.setImageResource(imgid[1]); //Inizializzazione della seconda imageview con la seconda immagine dell'array
                                               //passato al costruttore

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //metodo per l'avvio del client di posta sul clic dell'icona mail
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{clients.get(position).getMail()});
                i.putExtra(Intent.EXTRA_SUBJECT, "-Oggetto-");
                i.putExtra(Intent.EXTRA_TEXT   , "-Scrivi qui il messaggio-");
                try {
                    getContext().startActivity(Intent.createChooser(i, "Invia mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "Non ci sono client mail installati.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rowView;
    }
}
