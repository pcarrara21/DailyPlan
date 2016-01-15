package my.dailyplan.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import android.view.View.OnClickListener;

import org.json.JSONException;
import my.dailyplan.activities.EventCreationActivity;
import my.dailyplan.activities.ProposalCreationActivity;
import my.dailyplan.adapters.DayExpandableListAdapter;
import my.dailyplan.R;
import my.dailyplan.connectionrelated.JSONParser;
import my.dailyplan.datahandlers.Causal;
import my.dailyplan.datahandlers.Client;
import my.dailyplan.datahandlers.DataWrapper;
import my.dailyplan.datahandlers.Event;
import my.dailyplan.datahandlers.EventWrapper;
import my.dailyplan.datahandlers.Proposal;
import my.dailyplan.datahandlers.ProposalCausal;

//Frammento per la visualizzazione del calendario
public class DayListFragment extends Fragment {                 

    private ExpandableListAdapter listAdapter;
    private ExpandableListView explv;
    private GregorianCalendar g = new GregorianCalendar();
    private boolean isProposal;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // boolean utilizzata durante la creazione dell'evento per capire se si tratta di una proposta o un evento
        isProposal = false;

        EventWrapper eventWrapper = (EventWrapper) getArguments().getSerializable("EVENT_WRAPPER");

        //campi che identifica l'header di ogni elemento della lista (il giorno del mese)
        ArrayList<String> data_header = new ArrayList<>();

        HashMap<String, List<Event>> data_child = new HashMap<>();
        List<Event> day_events = new ArrayList<>();

        //gestione dei giorni relativi al mese selezionato
        for(int min = g.getActualMinimum(g.DAY_OF_MONTH); min < g.getActualMaximum(g.DAY_OF_MONTH) +1; min++) {
            g.set(g.DAY_OF_MONTH, min);
            String giorno = (g.getDisplayName(g.DAY_OF_WEEK, Calendar.LONG, Locale.ITALIAN));
            data_header.add(min + "     " + Character.toUpperCase(giorno.charAt(0)) + giorno.substring(1));

            for(Event event : eventWrapper.getEvents()) {

               if(g.get(g.DAY_OF_MONTH) < 10){
                   String day = "0" + String.valueOf(g.get(g.DAY_OF_MONTH));  //uniformation to format gg

                       if (event.getDate().compareTo(g.get(g.YEAR) + "-" + (g.get(g.MONTH) + 1) + "-" + day) == 0){     //yyyy-mm-gg format, inherited from mysql date
                            day_events.add(event);
                       }
               }
               else if (event.getDate().compareTo(g.get(g.YEAR) + "-" + (g.get(g.MONTH) + 1) + "-" + g.get(g.DAY_OF_MONTH)) == 0)     //yyyy-mm-gg format, inherited from mysql date
                    day_events.add(event);
            }
            data_child.put(data_header.get(min - 1), day_events);
            day_events = new ArrayList<>();
        }

        //adapter che gestisce i campi sottostanti al giorno selezionato
        listAdapter = new DayExpandableListAdapter(this.getActivity().getBaseContext(), data_header, data_child);
        explv = (ExpandableListView) this.getActivity().findViewById(R.id.day_list);
        explv.setAdapter(listAdapter);

        int count = listAdapter.getGroupCount();
        for (int position = 1; position <= count; position++)
            explv.expandGroup(position - 1);

        // quando l'utente clicca sull'evento viene mostrato un nuovo fragment (DataFragment) con il dettaglio dell'evento
        explv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                DataFragment fragment = new DataFragment();
                ArrayList<Event> events = new ArrayList<>();

                Event e = null;


                e = (Event) listAdapter.getChild(groupPosition, childPosition);


                events.add(e);

                Bundle b = new Bundle();
                b.putSerializable("EVENT_WRAPPER", new EventWrapper(events));
                fragment.setArguments(b);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                return false;
            }
        });

        //
        explv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    int childPosition = ExpandableListView.getPackedPositionChild(id);
                    Event e = null;

                    if(listAdapter.getChild(groupPosition, childPosition) instanceof  Proposal) {
                        e = (Proposal) listAdapter.getChild(groupPosition, childPosition);
                        isProposal = true;
                    }
                    else {
                        e = (Event) listAdapter.getChild(groupPosition, childPosition);
                        isProposal = false;
                    }

                    DataDownloaderTask dataDownloaderTask = new DataDownloaderTask();
                    dataDownloaderTask.execute(e);

                    return true;
                }

                return false;
            }
        });

       getActivity().findViewById(R.id.new_event).setOnClickListener(new OnClickListener() {
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

    public void setCalendarMonth(int month){ g.set(g.MONTH, month); }

    public void setCalendarYear(int year){
        g.set(g.YEAR, year);
    }

    public String getInitialDate(){
        Log.e("DATA_INIZIALE",g.get(g.YEAR)+ "-" + (g.get(g.MONTH)+1) + "-" + g.getActualMinimum(g.DAY_OF_MONTH) );
        return  g.get(g.YEAR)+ "-" + (g.get(g.MONTH)+1) + "-" + g.getActualMinimum(g.DAY_OF_MONTH);
    }

    public String getFinalDate(){
        return g.get(g.YEAR)+ "-" + (g.get(g.MONTH)+1) + "-" + g.getActualMaximum(g.DAY_OF_MONTH);
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
                    //scaricare le informazioni dei clienti e delle causali
                    clients = jsonParser.getClients();
                    causals = jsonParser.getCausals();
                }
                else {
                    //altrimenti scarica le causali relative alle proposte e l'id dell'ultima causale
                    proposalCausals = jsonParser.getPropCausals();
                    idPropCausal = jsonParser.getLastPropCausalID();
                }
                // se siamo nella situazione di modifica...
                if (params[0] != null) {
                    // salvo l'evento e l'id preso dai parametri params
                        event = params[0];
                        id = params[0].getID();
                }
                // ottiene l'ultimo id dell'evento o proposta
                else
                {
                    event = null;
                    if(isProposal) id = jsonParser.getLastProposalID();
                    else id = jsonParser.getLastEventID();
                }




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
                //nell'intent vengono passati anche l'oggetto event e id proposta
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
