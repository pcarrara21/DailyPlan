package my.dailyplan.adapters;

import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import my.dailyplan.R;
import my.dailyplan.datahandlers.Event;

public class DayExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader; // Titoli degli header
    private HashMap<String, List<Event>> listDataChild;  // Hashmap tra header e lista di eventi in un header

    public DayExpandableListAdapter(Context context, List<String> listDataHeader,
                                    HashMap<String, List<Event>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
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

        final Event event = (Event) getChild(groupPosition, childPosition); //Acquisizione dell'evento in base alla posizione
        //Creazione della stringa da visualizzare nella riga evento del calendario
        final String childText = event.getUSER_ID() + "\n" + event.getTime() + "\n" + event.getClient().getName() + "\n" + event.getCasual().getDescr();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_child_element, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        switch(event.getUSER_ID()){ //Modifica del colore evento a seconda dell'utente associato
            case "paolo":
                txtListChild.setBackgroundColor(0xFF7AFF40);
                break;
            case "federico":
                txtListChild.setBackgroundColor(0xFFFFC550);
                break;
            case "renzo":
                txtListChild.setBackgroundColor(0xFF88FCF0);
                break;
        }
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
    public View getGroupView(int groupPosition, boolean isExpanded,  //Metodo per l'ottenimento della vista relativa agli header in base
                                                                     //alla posizione
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}