import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

import my.dailyplan.R;
import my.dailyplan.activities.EventCreationActivity;
import my.dailyplan.connectionrelated.JSONParser;
import my.dailyplan.datahandlers.Causal;
import my.dailyplan.datahandlers.Client;
import my.dailyplan.datahandlers.DataWrapper;


public class EventCreationActivityTest extends ActivityInstrumentationTestCase2<EventCreationActivity> {

    EventCreationActivity activity;

    public EventCreationActivityTest() {
        super(EventCreationActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();

        ArrayList<Causal> causals = new ArrayList<>();
        ArrayList<Client> clients = new ArrayList<>();

        try {
            JSONParser jsonParser = new JSONParser(getInstrumentation().getContext());
            clients = jsonParser.getClients();
            causals = jsonParser.getCausals();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getContext(), R.style.Theme_AppCompat);
        Intent intent = new Intent(context,EventCreationActivity.class);

        Bundle b = new Bundle();
        b.putSerializable("DATA_WRAPPER", new DataWrapper(clients,causals));
        intent.putExtras(b);
        setActivityIntent(intent);
        activity = getActivity();
    }

    public void testPreconditions(){
        com.getbase.floatingactionbutton.FloatingActionButton fl = (com.getbase.floatingactionbutton.FloatingActionButton) activity.findViewById(R.id.saveEvent);
        assertNotNull(fl);
        com.getbase.floatingactionbutton.FloatingActionButton fl2 = (com.getbase.floatingactionbutton.FloatingActionButton) activity.findViewById(R.id.deleteEvent);
        assertNotNull(fl2);
        Spinner client = (Spinner) activity.findViewById(R.id.clients_spinner);
        assertNotNull(client);
        Spinner causal = (Spinner) activity.findViewById(R.id.causal_spinner);
        assertNotNull(causal);
        CheckBox office = (CheckBox) activity.findViewById(R.id.isOffice);
        assertNotNull(office);
        Button date = (Button) activity.findViewById(R.id.datePickerButton);
        assertNotNull(date);
        Button time = (Button) activity.findViewById(R.id.timePicker);
        assertNotNull(time);
        TextView textView8 = (TextView) activity.findViewById(R.id.textView8);
        assertNotNull(textView8);
        TextView textView9 = (TextView) activity.findViewById(R.id.textView9);
        assertNotNull(textView9);
        TextView textView10 = (TextView) activity.findViewById(R.id.textView10);
        assertNotNull(textView10);
        TextView note = (TextView) activity.findViewById(R.id.noteLabel);
        assertNotNull(note);
        TextView dateview = (TextView) activity.findViewById(R.id.dateView);
        assertNotNull(dateview);
        TextView timeview = (TextView) activity.findViewById(R.id.timeView);
        assertNotNull(timeview);
    }

    @SmallTest
    public void test_office(){   //Each test must begin with "test" keyword, otherwise it's not picked up by JUnit 3
        CheckBox office = (CheckBox) activity.findViewById(R.id.isOffice);
        assertEquals("Svolto in ufficio", office.getText());
    }

    @SmallTest
    public void test_Textview8(){   //Each test must begin with "test" keyword, otherwise it's not picked up by JUnit 3
        TextView textView = (TextView) activity.findViewById(R.id.textView8);
        assertEquals("Data evento", textView.getText());
    }

    @SmallTest
    public void test_Textview9(){   //Each test must begin with "test" keyword, otherwise it's not picked up by JUnit 3
        TextView textView = (TextView) activity.findViewById(R.id.textView9);
        assertEquals("Cliente", textView.getText());
    }

    @SmallTest
    public void test_Textview10(){   //Each test must begin with "test" keyword, otherwise it's not picked up by JUnit 3
        TextView textView = (TextView) activity.findViewById(R.id.textView10);
        assertEquals("Causale", textView.getText());
    }

    @SmallTest
    public void test_note(){   //Each test must begin with "test" keyword, otherwise it's not picked up by JUnit 3
        TextView textView = (TextView) activity.findViewById(R.id.noteLabel);
        assertEquals("Note", textView.getText());
    }

    @SmallTest
    public void test_date(){   //Each test must begin with "test" keyword, otherwise it's not picked up by JUnit 3
        TextView textView = (TextView) activity.findViewById(R.id.dateView);
        assertEquals(17, textView.getGravity());
    }

    @SmallTest
    public void test_time(){   //Each test must begin with "test" keyword, otherwise it's not picked up by JUnit 3
        TextView textView = (TextView) activity.findViewById(R.id.timeView);
        assertEquals(17, textView.getGravity());
    }



}
