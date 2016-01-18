package my.dailyplan.connectionrelated;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import my.dailyplan.datahandlers.Causal;
import my.dailyplan.datahandlers.Client;
import my.dailyplan.datahandlers.Event;
import my.dailyplan.datahandlers.EventOperation;
import my.dailyplan.datahandlers.Proposal;
import my.dailyplan.datahandlers.ProposalCausal;

public class JSONParser {

    //Collegamento ai file php

    private String indexString = "http://" + AppConfig.ip + "/dailyplan/index.php"; //Costruzione URL remota del server

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private Context context;

    public JSONParser(Context context) {
        this.context = context;
    }

    public String getJSONString(Context context, String urlstring, String[] parName, String[] par, String tag) {

        /*DEMO VERSION*/
        String jsonString = "";  //inizializzazione del messaggio di ritorno a "error"

        /* DEMO
        String jsonString = "Error";  //inizializzazione del messaggio di ritorno a "error"
        com.squareup.okhttp.Response response;
        if (ConnectionStatus.getInstance(context).isOnline(context)) { //Se il dispositivo è connesso a una rete
            // Creazione HTTP client e HTTP post
            OkHttpClient client = new OkHttpClient();

            //questo utente e password viene usato per evitare che chiunque apre il file in php possa vedere le informazioni che restituisce
            String user = "dailyplan";
            String pass = "a6bb2dfaed81d348d91790b38bd5d7f7f04e48a3"; // sha1 della password

            FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
            formEncodingBuilder.add("tag", tag);
            formEncodingBuilder.add("admin", user);
            formEncodingBuilder.add("admin_pass", pass);

            if (par != null && parName != null)   //creazione di un mapping nome parametro - parametro all'interno della richiesta
                for (int i = 0; i < par.length; i++)
                    formEncodingBuilder.add(parName[i], par[i]);

            RequestBody formBody = formEncodingBuilder.build();

            Request request = new Request.Builder()   //creazione della richiesta
                    .url(urlstring)
                    .post(formBody)
                    .build();

            try {
                response = client.newCall(request).execute();  //Esecuzione della richiesta
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                jsonString = response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            // In caso di assenza di connessione
            Toast.makeText(context,
                    "Non sei connesso a internet!", Toast.LENGTH_LONG).show();

        }
        Log.e("JSON_STRING", jsonString);
        */

        return jsonString;
    }

    public ArrayList<Client> getClients() throws JSONException {

        String jsonString = getJSONString(context, indexString, null, null, "getClients");
        JSONArray jsonArray = new JSONArray(jsonString); // tutti gli array della stringa json
        ArrayList<Client> values = new ArrayList<Client>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Client clientRow = new Client(jsonObject.getInt("client_id"), jsonObject.getString("name"),
                    jsonObject.getString("city"), jsonObject.getString("tel"), jsonObject.getString("mail"));
            values.add(i, clientRow);
        }
        return values;
    }

    public ArrayList<Causal> getCausals() throws JSONException {

        String jsonString = getJSONString(context, indexString, null, null, "getCausals");
        JSONArray jsonArray = new JSONArray(jsonString); // tutti gli array della stringa json
        ArrayList<Causal> values = new ArrayList<Causal>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Causal causalRow = new Causal(jsonObject.getString("causal_id"), jsonObject.getString("descr"));
            values.add(i, causalRow);
        }
        return values;
    }


    public ArrayList<Proposal> getProposals()throws JSONException
    {
        String jsonString = getJSONString(context, indexString, null, null, "getProposals");
        JSONArray jsonArray = new JSONArray(jsonString); // tutti gli array della stringa json

        ArrayList<Proposal> values = new ArrayList<Proposal>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Proposal causalRow = new Proposal(jsonObject.getInt("proposal_id"),jsonObject.getString("user_id"),jsonObject.getString("date"),
                    jsonObject.getString("time"),jsonObject.getString("note"), new ProposalCausal(jsonObject.getInt("id"), jsonObject.getString("descr")), jsonObject.getInt("part_number"));
            values.add(i, causalRow);
        }
        return values;
    }

    public ArrayList<Proposal> getCalendarProposals(String initial_date, String final_date)throws JSONException
    {
        String jsonString = getJSONString(context, indexString, new String[]{"initial_date", "final_date"},
                new String[]{initial_date, final_date}, "getProposalsBetweenDates");

        JSONArray jsonArray = new JSONArray(jsonString); // tutti gli array della stringa json
        ArrayList<Proposal> values = new ArrayList<Proposal>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Proposal causalRow = new Proposal(jsonObject.getInt("proposal_id"),jsonObject.getString("user_id"),jsonObject.getString("date"),
                    jsonObject.getString("time"),jsonObject.getString("note"),new ProposalCausal(jsonObject.getInt("id"), jsonObject.getString("descr")),jsonObject.getInt("part_number"));
            values.add(i, causalRow);
        }
        return values;
    }


    public ArrayList<Event> getCalendarEvents(String initial_date, String final_date) throws JSONException {

        String jsonString = getJSONString(context, indexString, new String[]{"initial_date", "final_date"},
                new String[]{initial_date, final_date}, "getEvents");
        ArrayList<Event> values = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString); // tutti gli array della stringa json
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Event eventRow = new Event(jsonObject.getInt("ID"), jsonObject.getString("user_id"), jsonObject.getString("date"), jsonObject.getString("time")
                        , new Client(jsonObject.getInt("client_id"), jsonObject.getString("name"), jsonObject.getString("city"), jsonObject.getString("tel"), jsonObject.getString("mail")),
                        jsonObject.getString("office").equals("1"), jsonObject.getString("note"), new Causal(jsonObject.getString("causal_id"), jsonObject.getString("descr")));

                values.add(i, eventRow);
            }
        } catch (JSONException j) {
        }   //In caso di eccezione ritorna sempre un array vuoto ma mai nullo
        return values;
    }

    public ArrayList<Event> getSearchEvents(String title) throws JSONException {

        String jsonString = getJSONString(context, indexString, new String[]{"title"}, new String[]{title}, "getSearch");
        ArrayList<Event> values = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString); // tutti gli array della stringa json
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Event eventRow = new Event(jsonObject.getInt("ID"), jsonObject.getString("user_id"), jsonObject.getString("date"), jsonObject.getString("time")
                        , new Client(jsonObject.getInt("client_id"), jsonObject.getString("name"), jsonObject.getString("city"), jsonObject.getString("tel"), jsonObject.getString("mail")),
                        jsonObject.getString("office").equals("1"), jsonObject.getString("note"), new Causal(jsonObject.getString("causal_id"), jsonObject.getString("descr")));
                values.add(i, eventRow);
            }
        } catch (JSONException j) {
        }   //In caso di eccezione ritorna sempre un array vuoto ma mai nullo
        return values;
    }

    public boolean ioEvent(Event event, EventOperation eo) {
        String[] parName = {"id", "user_id", "date", "time", "client", "office", "causal", "note"};
        String[] par = {"" + event.getID(), "" + event.getUSER_ID(), event.getDate(), event.getTime(), "" + event.getClient().getId(), "" + event.isInOffice(), event.getCasual().getId(), event.getNote()};
        String jsonString = "false";

        switch (eo) {  //switch delle operazioni sull'evento
            case INSERT:
                jsonString = getJSONString(context, indexString, parName, par, "insert_event");
                break;
            case MODIFY:
                jsonString = getJSONString(context, indexString, parName, par, "update_event");
                break;
            case DELETE:
                jsonString = getJSONString(context, indexString, new String[]{"id"}, new String[]{"" + event.getID()}, "delete_event");
                break;

        }

        return Boolean.parseBoolean(jsonString);
    }


    public boolean ioProposal(Proposal proposal, EventOperation eo, boolean createNewCausal,int lastPropCausID) {
        String[] parName = {"id", "user_id","descr", "date", "time", "note"};
        String[] par = {"" + proposal.getID(), "" + proposal.getUSER_ID(),""+proposal.getPropCausal().getId(), proposal.getDate(), proposal.getTime(), proposal.getNote()};
        String jsonString = "false";

        switch (eo) {
            case INSERT:
                jsonString = getJSONString(context, indexString, parName, par, "insert_prop");
                if(createNewCausal) getJSONString(context,indexString,new String[]{"id","descr"},new String[]{""+(lastPropCausID+1),proposal.getPropCausal().getDescr()},"insert_propCausal");
                break;
            case MODIFY:
                jsonString = getJSONString(context, indexString, parName, par, "update_prop");
                if(createNewCausal) getJSONString(context,indexString,new String[]{"id","descr"},new String[]{""+(lastPropCausID+1),proposal.getPropCausal().getDescr()},"insert_propCausal");
                break;
            case DELETE:
                jsonString = getJSONString(context, indexString, new String[]{"id"}, new String[]{"" + proposal.getID()}, "delete_prop");
                break;

        }

        return Boolean.parseBoolean(jsonString);
    }

    public int getLastEventID() {
        String jsonString = getJSONString(context, indexString, null, null, "getLastEventID");
        int id;
        JSONArray jsonArray = null;
        JSONObject jsonObject = null;

        try {

            jsonArray = new JSONArray(jsonString);
            jsonObject = jsonArray.getJSONObject(0);
            id = jsonObject.getInt("ID");

        } catch (JSONException e) {
            e.printStackTrace();
            id = 0;
        }

        return id;

    }

    public int getLastProposalID()
    {
        String jsonString = getJSONString(context, indexString, null, null, "getLastProposalID");
        int id;
        JSONArray jsonArray = null;
        JSONObject jsonObject = null;

        try {

            jsonArray = new JSONArray(jsonString);

            jsonObject = jsonArray.getJSONObject(0);
            id = jsonObject.getInt("proposal_id");


        } catch (JSONException e) {
            e.printStackTrace();
            id = 0;
        }

        return id;
    }

    public int getLastPropCausalID()
    {
        String jsonString = getJSONString(context, indexString, null, null, "getLastPropCausalID");
        int id;
        JSONArray jsonArray = null;
        JSONObject jsonObject = null;

        try {

            jsonArray = new JSONArray(jsonString);
            jsonObject = jsonArray.getJSONObject(0);
            id = jsonObject.getInt("id");

        } catch (JSONException e) {
            e.printStackTrace();
            id = 0;
        }

        return id;
    }


    public  ArrayList<ProposalCausal> getPropCausals() throws JSONException
    {
        String jsonString = getJSONString(context, indexString, null, null, "getPropCausals");
        JSONArray jsonArray = new JSONArray(jsonString); // tutti gli array della stringa json
        ArrayList<ProposalCausal> values = new ArrayList<ProposalCausal>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ProposalCausal proposalCausal = new ProposalCausal(jsonObject.getInt("id"), jsonObject.getString("descr"));
            values.add(i, proposalCausal);
        }
        return values;
    }


    public ArrayList<Event> getMyEvents(String user_id) throws JSONException {

        String jsonString = getJSONString(context, indexString, new String[]{"user_id"}, new String[]{user_id}, "getMyEvents");
        ArrayList<Event> values = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString); // tutti gli array della stringa json
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Event eventRow = new Event(jsonObject.getInt("ID"), jsonObject.getString("user_id"), jsonObject.getString("date"), jsonObject.getString("time")
                        , new Client(jsonObject.getInt("client_id"), jsonObject.getString("name"), jsonObject.getString("city"), jsonObject.getString("tel"), jsonObject.getString("mail")),
                        jsonObject.getString("office").equals("1"), jsonObject.getString("note"), new Causal(jsonObject.getString("causal_id"), jsonObject.getString("descr")));

                values.add(i, eventRow);
            }
        } catch (JSONException j) {
        }   //In caso di eccezione ritorna sempre un array vuoto ma mai nullo
        return values;
    }

    public boolean setPartNumber(String user_id, int proposal_id)
    {
        String jsonString = getJSONString(context,indexString, new String[]{"user_id","proposal_id"},new String[]{user_id,""+proposal_id},"setPartNumber");

        return Boolean.parseBoolean(jsonString);
    }

    public ArrayList<String> getPropParticipation(String user_id)
    {
        String jsonString = getJSONString(context,indexString, new String[]{"user_id"}, new String[]{user_id},"getPropParticipation");
        ArrayList<String> propPart = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString); // tutti gli array della stringa json
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                propPart.add(i, jsonObject.getString("proposal_id"));
            }
        } catch (JSONException j) {
        }   //In caso di eccezione ritorna sempre un array vuoto ma mai nullo
        return propPart;

    }

    public boolean deleteParticipation(String user_id, int proposal_id)
    {
        String jsonString = getJSONString(context,indexString,new String[]{"user_id","proposal_id"}, new String[]{user_id,""+proposal_id},"deleteParticipation");
        return Boolean.parseBoolean(jsonString);
    }

}
