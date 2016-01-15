package my.dailyplan.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;

import my.dailyplan.R;
import my.dailyplan.connectionrelated.JSONParser;
import my.dailyplan.datahandlers.Event;
import my.dailyplan.other.PrefUtils;

public class ProposalExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader; // header titles
    private HashMap<String, List<String>> listDataChild;  // child data in format of header title, child title
    private ArrayList<String> propPart;
    // array il numero di partecipanti
    private ArrayList<Integer> nOfAttendes;
    // array di boolean per gestire se l'utente sta partecipando o meno alle proposte
    private ArrayList<Boolean> inParticipation;


    public ProposalExpandableListAdapter(Context context, List<String> listDataHeader,
                                         HashMap<String, List<String>> listChildData, ArrayList<String> propPart, ArrayList<Integer> nOfAttendes) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        this.propPart = propPart;
        this.nOfAttendes = nOfAttendes;
        inParticipation = new ArrayList<>();
        // setta a false tutto l'array che gestisce le partecipazioni alle proposte
        for (int i=0; i< nOfAttendes.size(); i++)
            inParticipation.add(false);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_child_element, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);


        txtListChild.setText(childText);



        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.proposal_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);

        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText("Codice proposta " + headerTitle);

        final TextView attendes = (TextView) convertView.findViewById(R.id.Attendes);
        attendes.setTypeface(null, Typeface.BOLD);
        // setta il numero di partecipanti...
        attendes.setText(""+nOfAttendes.get(groupPosition));

        // bottone partecipa a evento-proposta
        final ImageButton partButton = (ImageButton)convertView.findViewById(R.id.partButton);


        final int propID = Integer.parseInt((String)getGroup(groupPosition));

        //scorre l'arraylist per controllare a quali proposte l'utente ha deciso di partecipare
        for(int i=0; i< propPart.size();i++)
            // se trova una corrispondenza
            if(propID == Integer.parseInt(propPart.get(i))) {
                //icona stella piena = partecipa
                partButton.setBackgroundResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                //booleano a true
                inParticipation.set(groupPosition,true);
                break;
            }
            else {
                //icona stella vuota = non partecipa
                partButton.setBackgroundResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                //booleano a false
                inParticipation.set(groupPosition,false);
            }
        //click sul pulsante per partecipare o non partecipare...
        partButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ProposalTask dataDownloader = new ProposalTask();
                // nel caso stia già partecipando si procede a cancellare dalla tabella participation_table la riga della proposta
                if(inParticipation.get(groupPosition)) {
                    dataDownloader.execute(propID, 1);
                    partButton.setBackgroundResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                    //  e viene rimosso anche dall'array in locale
                    propPart.remove("" + propID);
                    nOfAttendes.set(groupPosition, nOfAttendes.get(groupPosition) - 1);
                    attendes.setText("" + nOfAttendes.get(groupPosition));
                    inParticipation.set(groupPosition, false);
                }
                // al contrario viene inserito id proposta nella tabella participation_table
                else {

                    dataDownloader.execute(propID, 0);
                    partButton.setBackgroundResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                    propPart.add("" + propID);
                    nOfAttendes.set(groupPosition, nOfAttendes.get(groupPosition) + 1);
                    attendes.setText("" + nOfAttendes.get(groupPosition));
                    inParticipation.set(groupPosition, true);
                }

            }
        });


        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class ProposalTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {

                JSONParser jsonParser = new JSONParser(context);
                // cancella o inserisci id proposta a cui l'utente vuole partecipare
                if(params[1] == 0)
                  jsonParser.setPartNumber(PrefUtils.getFromPrefs(context, "__USERNAME__", ""),params[0]);
                else
                  jsonParser.deleteParticipation(PrefUtils.getFromPrefs(context, "__USERNAME__", ""),params[0]);
                return null;
        }

        @Override
        protected void onPostExecute(Void d) {

        }
    }

}
