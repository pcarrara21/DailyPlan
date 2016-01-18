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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import my.dailyplan.adapters.CausalSpinnerAdapter;
import my.dailyplan.adapters.ClientSpinnerAdapter;
import my.dailyplan.connectionrelated.JSONParser;
import my.dailyplan.datahandlers.Causal;
import my.dailyplan.datahandlers.Client;
import my.dailyplan.datahandlers.DataWrapper;
import my.dailyplan.datahandlers.Event;
import my.dailyplan.datahandlers.EventOperation;
import my.dailyplan.other.PrefUtils;

public class EventCreationActivity extends AppCompatActivity {

    private FloatingActionButton saveEvent;
    private FloatingActionButton deleteEvent;
    private Button datePickerButton;
    private Button timePickerButton;

    private Calendar calendar;
    private TextView dateView;
    private TextView timeView;
    private int year, month, day;
    private int hour, min;
    private Spinner spinnerClients;
    private Spinner spinnerCausals;

    private ArrayList<Causal> causals;
    private ArrayList<Client> clients;
    private CheckBox inOffice;
    private TextView note;

    private Event currentEvent;
    private int currentID;
    private EventOperation eventOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Vincola la EventCreationActivity
        //ad essere visualizzata in verticale per non cusare problemi durante la rotazione schermo

        //Spinners per clienti e causali
        spinnerClients = (Spinner)findViewById(R.id.clients_spinner);
        spinnerCausals = (Spinner)findViewById(R.id.causal_spinner);

        //Bottoni
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


        Typeface font = Typeface.createFromAsset( getAssets(), "fonts/fontawesome-webfont.ttf" );
        datePickerButton = (Button)findViewById( R.id.datePickerButton);
        datePickerButton.setTypeface(font);

        timePickerButton = (Button) findViewById(R.id.timePicker);
        timePickerButton.setTypeface(font);

        //Viste dell'Activity
        dateView = (TextView)findViewById(R.id.dateView);
        timeView = (TextView)findViewById(R.id.timeView);
        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        
        hour =calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        timeView.setText(hour + " : " + ((min < 10) ? ("0"+min) : min));

        inOffice = (CheckBox) findViewById(R.id.isOffice);
        note = (TextView) findViewById(R.id.note);

        showDate(year, month + 1, day);

        currentEvent = null;
        currentID = 0;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Bundle b = getIntent().getExtras();
        DataWrapper d = (DataWrapper) b.getSerializable("DATA_WRAPPER"); //Bundle contenente dati da visualizzare
                                                                         //negli spinners dell'activity

        clients = d.getClient_array();
        causals = d.getCausal_array();

        /*DEMO*/
                ClientSpinnerAdapter clientSpinnerAdapter = new ClientSpinnerAdapter(getBaseContext(),R.id.clients_spinner,new ArrayList<Client>());
                spinnerClients.setAdapter(clientSpinnerAdapter);

                CausalSpinnerAdapter causalSpinnerAdapter = new CausalSpinnerAdapter(getBaseContext(), R.id.causal_spinner,new ArrayList<Causal>());
                spinnerCausals.setAdapter(causalSpinnerAdapter);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);   //Nasconde la tasetiera all'inizio dell'activity

        currentEvent = (Event) b.getSerializable("EVENT");

        if(currentEvent == null) {   //Se l'avvio dell'activity non è la modifica di un evento già creato
            eventOperation = EventOperation.INSERT;
            currentID = getIntent().getIntExtra("id", 0) + 1;
            deleteEvent.setEnabled(false);
        }
        else {
            eventOperation = EventOperation.MODIFY;  //Se l'avvio dell'activity è la modifica di un evento già creato
            currentID = currentEvent.getID();
            dateView.setText(currentEvent.getDate());
            timeView.setText(currentEvent.getTime());

            int causalSpinnerPosition = causalSpinnerAdapter.getPosition(currentEvent.getCasual());
            spinnerCausals.setSelection(causalSpinnerPosition);

            int clientSpinnerPosition = clientSpinnerAdapter.getPosition(currentEvent.getClient());
            spinnerClients.setSelection(clientSpinnerPosition);

            inOffice.setChecked(currentEvent.isInOffice());
            note.setText(currentEvent.getNote());
        }        

        //Modifica del titolo dell'Activity
        getSupportActionBar().setTitle("Codice evento " + (currentEvent != null ? currentEvent.getID() : currentID));
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) { //Valori di partenza del dialog, 999 per la scelta della data e 998 per la scelta del tempo
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
            String minute_str = (min < 10) ? ("0" + min): min + "";
            timeView.setText(hour +" : " + minute_str);
        }
    };

    private void showDate(int year, int month, int day) {    //need to change date formato to adapd mysql one
        dateView.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
    }

    public void onSaveButtonClick(){
        /*DEMO VERSION
        currentEvent = new Event(currentID,PrefUtils.getFromPrefs(this, "__USERNAME__", ""),dateView.getText().toString(),timeView.getText().toString(),(Client)spinnerClients.getSelectedItem(),inOffice.isChecked(),note.getText().toString(),(Causal)spinnerCausals.getSelectedItem());

        EventTask eventInsertTask = new EventTask(getApplicationContext());
        eventInsertTask.execute();
        */

        /*DEMO*/
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
      }

    public void onDeleteButtonClick(){
        DeletionAlert();
    }

    private void DeletionAlert(){   //Avviso prima della cancellazione di un evento
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attenzione")
                .setMessage("Vuoi davvero cancellare questo evento?")
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
    public void onBackPressed()   //Avviso sull'uscita dall'activity con dati inseriti
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attenzione")
                .setMessage("Se esci i dati di questo evento non verranno salvati. Sei sicuro?")
                .setIcon(R.drawable.fab_background)
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

    class EventTask extends AsyncTask<Void, Void, Void> {   //Classe per la creazione di operazioni di inserimento, modifica e cancellazione
                                                            //evento
        private boolean r;
        private Context context;
        public EventTask(Context context)
        {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {

            JSONParser jsonParser = new JSONParser(getBaseContext());
            if(eventOperation == EventOperation.INSERT)
                r = jsonParser.ioEvent(currentEvent, eventOperation);
            else if (eventOperation == EventOperation.MODIFY)
                r =  jsonParser.ioEvent(currentEvent, eventOperation);
            else if (eventOperation == EventOperation.DELETE)
                r =  jsonParser.ioEvent(currentEvent, eventOperation);

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {              //Metodo eseguito dopo l'aggiornamento del database con le operazioni richieste
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            String message = "";

            if(r){
                if(eventOperation == EventOperation.INSERT)
                    message = "Evento creato con successo!";
                else if (eventOperation == EventOperation.MODIFY)
                    message = "Evento aggiornato con successo!";
                else if (eventOperation == EventOperation.DELETE)
                    message = "Evento cancellato con successo!";
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
