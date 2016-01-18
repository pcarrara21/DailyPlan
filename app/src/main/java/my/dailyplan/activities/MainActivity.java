package my.dailyplan.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import my.dailyplan.R;
import my.dailyplan.connectionrelated.JSONParser;
import my.dailyplan.connectionrelated.SessionManager;
import my.dailyplan.datahandlers.Client;
import my.dailyplan.datahandlers.DataWrapper;
import my.dailyplan.datahandlers.Event;
import my.dailyplan.datahandlers.EventWrapper;
import my.dailyplan.datahandlers.Proposal;
import my.dailyplan.datahandlers.ProposalWrapper;
import my.dailyplan.fragments.ClientFragment;
import my.dailyplan.fragments.DataFragment;
import my.dailyplan.fragments.DayListFragment;
import my.dailyplan.fragments.ProposalFragment;
import my.dailyplan.fragments.YearPickerFragment;
import my.dailyplan.other.PrefUtils;

public class MainActivity extends AppCompatActivity implements ActionBar.OnNavigationListener{

    // action bar
    private ActionBar actionBar;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    private SessionManager session;

    private GregorianCalendar g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Parte su ActionBar
        actionBar = getSupportActionBar();
        // Nascondi il titolo della action bar
        actionBar.setDisplayShowTitleEnabled(true);
        g = new GregorianCalendar();
        actionBar.setTitle(getMonthName(g.get(GregorianCalendar.MONTH)) + " " + g.get(GregorianCalendar.YEAR));

        //Parte sul Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mPlanetTitles = getResources().getStringArray(R.array.drawer_menu_array);    // Stringhe assegnate in string.xml
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getBaseContext(),R.layout.drawer_list_item);
        adapter.addAll(mPlanetTitles);
        mDrawerList.setAdapter(adapter);

        mTitle = mDrawerTitle = getTitle();
        // Abilita l'app icon di ActionBar a comportarsi come pulsante per aprire/chiudere il drawer
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // session manager
        session = new SessionManager(getApplicationContext());

                                  /*  if (!session.isLoggedIn()) {
                                        session.setLogin(false);

                                        // Launching the login activity
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } */

        // ActionBarDrawerToggle lega assieme le opportune interazioni
        // tra il drawer e l'app icon dell'actionbar
       mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                actionBar.setTitle(mTitle);
                invalidateOptionsMenu(); // crea chiamata ad onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // crea chiamata ad onPrepareOptionsMenu()
            }
         };
            mDrawerLayout.setDrawerListener(mDrawerToggle);

         if (savedInstanceState == null) {      //Settaggio del fragment di default all'apertura
                Log.e("FRAGMENT", "initial fragment ");
                createCalendarFragment(true, 0, 0);
            }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        // Associa configurazione "searchable" con la SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        return super.onCreateOptionsMenu(menu);
    }

    /* Chiamata ogni volta che chiamiamo invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Se il drawer è aperto, nascondi gli action item relativi alla content view
    //    boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
    //    menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // La action bar home/up action deve aprire o chiudere il drawer
         // ActionBarDrawerToggle si occuperà di questo.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Gestisci gli action button
        switch(item.getItemId()) {
            case R.id.action_websearch:
                // crea intent per realizzare una ricerca su web
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, actionBar.getTitle());
                // cattura l'evento in cui non c'è attività per gestire l'intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_search:
                Log.e("IIIIIIIII" ,"è una wuery");
                return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {   //Metodo per il click su uno spinner nell'actionbar
       return false;
    }

    /* ClickListener per ListView nel navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // aggiorna il content principale sostituendo i fragment
            switch(position) {
                default:
                    break;
                case 0:
                    getSupportActionBar().show();
                    Log.e("FRAGMENT", "calendario premuto ");
                    createCalendarFragment(true, 0, 0);
                    break;
                case 1:
                    createDataFragment("MYEVENTS");
                    break;
                case 2:
                    createProposalFragment();
                    break;
                case 3:
                    createClientFragment();
                    break;
            }

            // aggiorna l'item selezionato e titolo, poi chiudi il drawer
            mDrawerList.setItemChecked(position, true);
            setTitle(mPlanetTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        actionBar.setTitle(mTitle);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);
            final DataFragment fragment = new DataFragment();

            class EventDownloaderTask extends AsyncTask<Void, Void, ArrayList<Event>> {
                private ArrayList<Event> events = new ArrayList<>();

                @Override
                protected ArrayList<Event> doInBackground(Void... params) {
                    try {
                        JSONParser jsonParser = new JSONParser(getBaseContext());
                        events = jsonParser.getSearchEvents(query);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return events;
                }

                @Override
                protected void onPostExecute(ArrayList<Event> e) {

                    Bundle b = new Bundle();
                    b.putSerializable("EVENT_WRAPPER", new EventWrapper(events));
                    fragment.setArguments(b);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                }
            }
            EventDownloaderTask eventDownloader = new EventDownloaderTask();
            eventDownloader.execute();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sincronizza lo stato di toggle dopo che onRestoreInstanceState è avvenuto.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Passa qualsiasi cambio alla configurazione al toggle del drawer
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void createCalendarFragment(boolean default_month, int int_month, int int_year ){

        final DayListFragment fragment = new DayListFragment();
        Log.e("DEFAULT MONTH", Boolean.toString(default_month) + "  " + int_month);

        if(!default_month){
            fragment.setCalendarMonth(int_month);
            fragment.setCalendarYear(int_year);
        }

        class EventDownloaderTask extends AsyncTask<Void, Void, ArrayList<Event>> {
            private ArrayList<Event> events = new ArrayList<>();

            @Override
            protected ArrayList<Event> doInBackground(Void... params) {
                try {
                    JSONParser jsonParser = new JSONParser(getBaseContext());
                    events = jsonParser.getCalendarEvents(fragment.getInitialDate(),fragment.getFinalDate());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return events;
            }

            @Override
            protected void onPostExecute(ArrayList<Event> e) {

                Bundle b = new Bundle();
                b.putSerializable("EVENT_WRAPPER", new EventWrapper(events));
                fragment.setArguments(b);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        }
        EventDownloaderTask eventDownloader = new EventDownloaderTask();
        eventDownloader.execute();
    }

    public void createDataFragment(final String s){

        final DataFragment fragment = new DataFragment();

        class EventDownloaderTask extends AsyncTask<Void, Void, ArrayList<Event>> {
            private ArrayList<Event> events = new ArrayList<>();

            @Override
            protected ArrayList<Event> doInBackground(Void... params) {
                try {
                    JSONParser jsonParser = new JSONParser(getBaseContext());
                    if(s.equals("MYEVENTS"))
                        events = jsonParser.getMyEvents(PrefUtils.getFromPrefs(getBaseContext(), "__USERNAME__", ""));
                    //TODO:
                  //  else
                   //     events = jsonParser.getProposedEvents();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return events;
            }

            @Override
            protected void onPostExecute(ArrayList<Event> e) {

                Bundle b = new Bundle();
                b.putSerializable("EVENT_WRAPPER", new EventWrapper(events));
                fragment.setArguments(b);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        }
        EventDownloaderTask eventDownloader = new EventDownloaderTask();
        eventDownloader.execute();
    }



    public void createProposalFragment(){

        final ProposalFragment fragment = new ProposalFragment();

        class EventDownloaderTask extends AsyncTask<Void, Void, ArrayList<Proposal>> {
            private ArrayList<Proposal> proposals = new ArrayList<>();

            @Override
            protected ArrayList<Proposal> doInBackground(Void... params) {
                try {
                    JSONParser jsonParser = new JSONParser(getBaseContext());

                    proposals = jsonParser.getProposals();
                    //TODO:
                    //  else
                    //     events = jsonParser.getProposedEvents();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return proposals;
            }

            @Override
            protected void onPostExecute(ArrayList<Proposal> e) {

                Bundle b = new Bundle();
                b.putSerializable("PROPOSAL_WRAPPER", new ProposalWrapper(proposals));
                fragment.setArguments(b);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        }

        EventDownloaderTask eventDownloader = new EventDownloaderTask();
        eventDownloader.execute();
    }

    public void createClientFragment(){

        final ClientFragment fragment = new ClientFragment();

        class EventDownloaderTask extends AsyncTask<Void, Void, ArrayList<Client>> {
            private ArrayList<Client> clients = new ArrayList<>();

            @Override
            protected ArrayList<Client> doInBackground(Void... params) {
                try {
                    JSONParser jsonParser = new JSONParser(getBaseContext());
                    clients = jsonParser.getClients();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return clients;
            }

            @Override
            protected void onPostExecute(ArrayList<Client> e) {

                Bundle b = new Bundle();
                b.putSerializable("DATA_WRAPPER", new DataWrapper(clients,null));
                fragment.setArguments(b);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }
        }
        EventDownloaderTask eventDownloader = new EventDownloaderTask();
        eventDownloader.execute();
    }

    public void showDialogYear(MenuItem item){
        YearPickerFragment pd = new YearPickerFragment();
        pd.show(getFragmentManager(), "YearPickerFragment");
        pd.setListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                createCalendarFragment(false, monthOfYear - 1, year); // -1 per adeguarsi con daylistfragment (il calendario sottrae -1)
                actionBar.setTitle(getMonthName(monthOfYear - 1) + " " + year);

            }
        });
    }



    public static String getMonthName(int month){
        String[] monthNames = {"Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"};
        return monthNames[month];
    }



    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attenzione")
                .setMessage("Sei sicuro di voler uscire?")
                .setIcon(R.drawable.abc_dialog_material_background_light)
                .setPositiveButton("Sì", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent i = new Intent(getApplicationContext(),LoginActivity.class);
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

}