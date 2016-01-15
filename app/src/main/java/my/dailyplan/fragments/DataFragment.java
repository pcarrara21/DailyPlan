package my.dailyplan.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import my.dailyplan.activities.EventCreationActivity;
import my.dailyplan.activities.ProposalCreationActivity;
import my.dailyplan.adapters.DataExpandableListAdapter;
import my.dailyplan.R;
import my.dailyplan.connectionrelated.JSONParser;
import my.dailyplan.datahandlers.Causal;
import my.dailyplan.datahandlers.Client;
import my.dailyplan.datahandlers.DataWrapper;
import my.dailyplan.datahandlers.Event;
import my.dailyplan.datahandlers.EventWrapper;
import my.dailyplan.datahandlers.Proposal;
import my.dailyplan.datahandlers.ProposalCausal;

public class DataFragment extends Fragment {      //Frammento per la visualizzazione degli eventi in output per una qualsiasi query

    private ExpandableListAdapter listAdapter;
    private ExpandableListView explv;
    private int lastExpandedPosition = -1;
    private boolean isProposal;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // boolean utilizzata durante la creazione dell'evento per capire se si tratta di una proposta o un evento
        isProposal = false;

        EventWrapper eventWrapper = (EventWrapper) getArguments().getSerializable("EVENT_WRAPPER");

        ArrayList<String> data_header = new ArrayList<>();

        HashMap<String, List<String>> data_child = new HashMap<>();
        int n_ev = 0;
        for(Event event : eventWrapper.getEvents()) {
            ArrayList<String> labels = new ArrayList<>();
            data_header.add("Codice evento: "+event.getID());

            //TODO: add here data to be shown
            labels.add("Data:     " + event.getDate());
            labels.add("Causale:     " + event.getCasual().getDescr());
            labels.add("Cliente:     " + event.getClient().getName());
            labels.add("USER_ID:     " + event.getUSER_ID());
            labels.add("Ora:     " + event.getTime());
            labels.add("Ufficio:     " + event.isInOffice());
            labels.add("Note:     " + event.getNote());

            data_child.put(data_header.get(n_ev), labels);
            n_ev++;
        }
        // Adapter utilizzato per settare i vari campi per il dettaglio dell'evento
        listAdapter = new DataExpandableListAdapter(this.getActivity().getBaseContext(), data_header, data_child);
        explv = (ExpandableListView) this.getActivity().findViewById(R.id.day_list);
        explv.setAdapter(listAdapter);

        int count = listAdapter.getGroupCount();
        for (int position = 1; position <= count; position++)
            explv.expandGroup(position - 1);

        // evento click per creazione evento/proposta
        getActivity().findViewById(R.id.new_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEventType();

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    // classe evento asincrono per scaricare i dati dal database
    class DataDownloaderTask extends AsyncTask<Event, Void, DataWrapper> {
        private ArrayList<Causal> causals;
        private ArrayList<Client> clients;
        private ArrayList<ProposalCausal> proposalCausals;
        private Event event;
        private int id;
        private int idPropCausal;

        @Override
        protected DataWrapper doInBackground(Event... params) {
            try {
                JSONParser jsonParser = new JSONParser(getActivity().getBaseContext());
                // nel caso la scelta dell'utente si quella di creare un evento...
                if(!isProposal){
                    //scarica le informazioni dei clienti e delle causali
                    clients = jsonParser.getClients();
                    causals = jsonParser.getCausals();
                }
                else {
                    //altrimenti scarica le causali relative alle proposte e l'id dell'ultima causale
                    proposalCausals = jsonParser.getPropCausals();
                    idPropCausal = jsonParser.getLastPropCausalID();
                }

                event = null;
                // ottiene l'ultimo id dell'evento o proposta
                if(isProposal) id = jsonParser.getLastProposalID();
                else id = jsonParser.getLastEventID();


            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(isProposal) return new DataWrapper(proposalCausals);

            return new DataWrapper(clients,causals);
        }

        @Override
        protected void onPostExecute(DataWrapper d) {
            Intent intent = null;
            Bundle b = new Bundle();

            b.putSerializable("DATA_WRAPPER", d);
            // nel caso sia una proposta crea un intent per avviare l'activity della proposta...
            if(isProposal) {
                intent = new Intent(getActivity(),ProposalCreationActivity.class);
                b.putSerializable("PROPOSAL", event);
                intent.putExtra("idPropCaus",idPropCausal);
            }
            // o dell'evento...
            else {
                intent = new Intent(getActivity(),EventCreationActivity.class);
                b.putSerializable("EVENT", event);
            }

            intent.putExtras(b);
            intent.putExtra("id", id);
            startActivity(intent);
            getActivity().finish();
        }
    }

    // metodo che crea un alert dialog che mostra la scelta tra la creazione di un evento o una proposta
    public void selectEventType()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Evento o proposta?")
                .setMessage("Che tipo di evento vuoi creare?")
                .setIcon(R.drawable.ic_delete_event)
                        // nel caso positivo creo un evento...
                .setPositiveButton("Evento standard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isProposal = false;
                        DataDownloaderTask dataDownloader = new DataDownloaderTask();
                        dataDownloader.execute((Event) null);

                    }
                });
        //...altrimenti una proposta
        builder.setNegativeButton("Proposta", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                isProposal = true;
                DataDownloaderTask dataDownloader = new DataDownloaderTask();
                dataDownloader.execute((Proposal) null);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }





}
