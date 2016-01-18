package my.dailyplan.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.getbase.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import my.dailyplan.R;
import my.dailyplan.adapters.PropCausalSpinnerAdapter;
import my.dailyplan.connectionrelated.JSONParser;
import my.dailyplan.datahandlers.DataWrapper;
import my.dailyplan.datahandlers.EventOperation;
import my.dailyplan.datahandlers.Proposal;
import my.dailyplan.datahandlers.ProposalCausal;
import my.dailyplan.other.PrefUtils;

public class ProposalCreationActivity extends AppCompatActivity {

    private FloatingActionButton saveEvent;
    private FloatingActionButton deleteEvent;
    private Button datePickerButton;
    private Button timePickerButton;

    private Calendar calendar;
    private TextView dateView;
    private TextView timeView;
    private TextView customProp;

    private int year, month, day;
    private int hour, min;

    private Spinner pdescrSpinner;

    private ArrayList<ProposalCausal> proposalCausals;

    private TextView note;

    private Proposal currentProposal;
    private int currentID;
    private EventOperation eventOperation;

    private CheckBox checkCustumProp;

    private PropCausalSpinnerAdapter pDescrSpinnerAdapter;

    private int lastPropCausID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposal_creation);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Blocca l'orientamento dello schermo a "ritratto"

        pdescrSpinner = (Spinner)findViewById(R.id.pdescr_spinner);

        saveEvent = (FloatingActionButton) findViewById(R.id.saveEvent);
        saveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClick();
            }
        });

        deleteEvent = (FloatingActionButton) findViewById(R.id.deleteEvent);
        deleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteButtonClick();
            }
        });

        checkCustumProp = (CheckBox) findViewById(R.id.checkCustumProp);

        Typeface font = Typeface.createFromAsset( getAssets(), "fonts/fontawesome-webfont.ttf" ); // Setta il font da assets/fonts/fontawesome-webfont.ttf
        datePickerButton = (Button)findViewById( R.id.datePickerButton);
        datePickerButton.setTypeface(font);

        timePickerButton = (Button) findViewById(R.id.timePicker);
        timePickerButton.setTypeface(font);


        dateView = (TextView)findViewById(R.id.dateView);
        timeView = (TextView)findViewById(R.id.timeView);
        customProp = (TextView) findViewById(R.id.customProp);
        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        hour =calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        timeView.setText(hour + " : " + ((min < 10) ? ("0"+min) : min));

        note = (TextView) findViewById(R.id.note);

        showDate(year, month + 1, day);

        currentProposal = null;
        currentID = 0;

        // checkBox per causale personalizzata della proposta
        checkCustumProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCustumProp.isChecked()) {
                    pdescrSpinner.setEnabled(false);
                    customProp.setEnabled(true);
                }
                else
                {
                    pdescrSpinner.setEnabled(true);
                    customProp.setEnabled(false);
                }
            }
        });

    }

    @Override
    public void onStart()
    {
        super.onStart();
        Bundle b = getIntent().getExtras();
        DataWrapper d = (DataWrapper) b.getSerializable("DATA_WRAPPER");

        lastPropCausID = b.getInt("idPropCaus",0);

         pDescrSpinnerAdapter = null;
        // Riempimento dello spinner con le varie causali
        if(d!= null) proposalCausals = d.getProposal_array();

        if(proposalCausals != null  && !proposalCausals.isEmpty()) {
            pDescrSpinnerAdapter  = new PropCausalSpinnerAdapter(getBaseContext(), R.id.pdescr_spinner, proposalCausals);
            pdescrSpinner.setAdapter(pDescrSpinnerAdapter);
        }


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);   //Nascondi la tastiera all'avvio

        currentProposal = (Proposal) b.getSerializable("PROPOSAL");
        // Verifica se la proposta corrente non esiste quindi va inserita, altrimenti va modificata
        if(currentProposal == null) {
            eventOperation = EventOperation.INSERT;
            currentID = getIntent().getIntExtra("id", 0) + 1;
            deleteEvent.setEnabled(false);
        }
        else {
            eventOperation = EventOperation.MODIFY;
            currentID = currentProposal.getID();
            dateView.setText(currentProposal.getDate());
            timeView.setText(currentProposal.getTime());


            if(pDescrSpinnerAdapter != null) {
                int pdescrSpinnnerPosition = pDescrSpinnerAdapter.getPosition(currentProposal.getPropCausal());
                pdescrSpinner.setSelection(pdescrSpinnnerPosition);
            }
            note.setText(currentProposal.getNote());
        }

        getSupportActionBar().setTitle("Codice proposta " + (currentProposal != null ? currentProposal.getID() : currentID));
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        if(id == 998)
            return new TimePickerDialog(this,myTimeSetListener,hour,min,true);
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(arg1, arg2+1, arg3);
        }
    };

    private TimePickerDialog.OnTimeSetListener myTimeSetListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour = hourOfDay;
            min = minute;
            String minute_str = (min < 10) ? ("0"+min): min + "";
            timeView.setText(hour +" : " + minute_str);
        }
    };

    private void showDate(int year, int month, int day) {    // bisogna cambiare il formato della data per adattarsi a quello di mysql
        dateView.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
    }

    public void onSaveButtonClick(){

            /*DEMO*/
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();

        /*DEMO VERSION
        boolean createNewCausale = false;

        // se lo spinner è vuoto e non è stata inserita nessuna causale personalizzata, il salvataggio viene bloccato
        if(pDescrSpinnerAdapter == null && customProp.getText().toString().matches("")) {
            Toast.makeText(this, "Causale non inserita", Toast.LENGTH_SHORT).show();
            return;
        }else

        // se l'utente ha scelto di creare una nuova causale e non l'ha inserita, il salvataggio viene bloccato
        if(checkCustumProp.isChecked() &&  customProp.getText().toString().matches(""))
        {
            Toast.makeText(this, "Causale personalizzata non inserita", Toast.LENGTH_SHORT).show();
            return;
        }else

        // la prima volta che viene inserita una causale = spinner vuoto
        if(pdescrSpinner == null)
        {
            currentProposal = new Proposal(currentID,PrefUtils.getFromPrefs(this, "__USERNAME__", ""),
                    dateView.getText().toString(),timeView.getText().toString(), note.getText().toString(),
                    new ProposalCausal(1,customProp.getText().toString()),0);
        }else
        // nel caso di creazione di una nuova causale (vale sia in creazione che in modifica evento, nel caso l'utente volesse cambiare causale della proposta)
        if(checkCustumProp.isChecked())
        {
            currentProposal = new Proposal(currentID,PrefUtils.getFromPrefs(this, "__USERNAME__", ""),
                    dateView.getText().toString(),timeView.getText().toString(), note.getText().toString(),
                    new ProposalCausal(1,customProp.getText().toString()),0);
            createNewCausale = true;
        }
        // creazione evento normale con causale scelta dallo spinner
        else
        {
            currentProposal = new Proposal(currentID,PrefUtils.getFromPrefs(this, "__USERNAME__", ""),
                    dateView.getText().toString(),timeView.getText().toString(), note.getText().toString(),
                    (ProposalCausal)pdescrSpinner.getSelectedItem(),0);
        }

        EventTask eventInsertTask = new EventTask(getApplicationContext());
        eventInsertTask.execute(createNewCausale);
        */
    }

    public void onDeleteButtonClick(){
        //String USER_ID, String date, String time, String client, boolean inOffice, String note,String casual
        DeletionAlert();

    }

    private void DeletionAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attenzione")
                .setMessage("Vuoi davvero cancellare questa proposta?")
                .setIcon(R.drawable.ic_delete_event)
                .setPositiveButton("Sì", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        eventOperation = EventOperation.DELETE;
                        EventTask eventInsertTask = new EventTask(getApplicationContext());
                        eventInsertTask.execute();

                    }
                });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {


            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attenzione")
                .setMessage("Se esci i dati di questa proposta non verranno salvati. Sei sicuro?")
                .setIcon(R.drawable.ic_delete_event)
                .setPositiveButton("Sì", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i);
                        finish();

                    }
                });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {


            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }




    @SuppressWarnings("deprecation")
    public void showTimePickerDialog(View v) {  showDialog(998); }

    class EventTask extends AsyncTask<Boolean, Void, Void> {

        private boolean r;
        private Context context;
        public EventTask(Context context)
        {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Boolean... params) { //azioni eseguite in background


            JSONParser jsonParser = new JSONParser(getBaseContext());

            if(eventOperation == EventOperation.INSERT)
                r = jsonParser.ioProposal(currentProposal, eventOperation,params != null?params[0]:false,lastPropCausID);
            else if (eventOperation == EventOperation.MODIFY)
                r =  jsonParser.ioProposal(currentProposal, eventOperation,params != null?params[0]:false,lastPropCausID);
            else if (eventOperation == EventOperation.DELETE)
                r =  jsonParser.ioProposal(currentProposal, eventOperation,params != null?params[0]:false,lastPropCausID);


            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);   //Nascondi la tastiera
            mgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            String message = "";

            if(r){
                if(eventOperation == EventOperation.INSERT)
                    message = "Proposta creata con successo!";
                else if (eventOperation == EventOperation.MODIFY)
                    message = "Proposta aggiornata con successo!";
                else if (eventOperation == EventOperation.DELETE)
                    message = "Proposta cancellata con successo!";
            }else
            {
                message = "Errore!";
            }

            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();

            if(r)
            {
                Intent i = new Intent(context,MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

}
