import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

import my.dailyplan.R;
import my.dailyplan.activities.EventCreationActivity;
import my.dailyplan.activities.ProposalCreationActivity;
import my.dailyplan.connectionrelated.JSONParser;
import my.dailyplan.datahandlers.Causal;
import my.dailyplan.datahandlers.Client;
import my.dailyplan.datahandlers.DataWrapper;
import my.dailyplan.datahandlers.Event;
import my.dailyplan.datahandlers.Proposal;
import my.dailyplan.datahandlers.ProposalCausal;


public class ProposalCreationActivityTest extends ActivityInstrumentationTestCase2<ProposalCreationActivity> {

    ProposalCreationActivity activity;

    public ProposalCreationActivityTest() {
        super(ProposalCreationActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();

        ArrayList<ProposalCausal> proposalCausals = new ArrayList<>();
        int idPropCausal = 0;

        try {
            JSONParser jsonParser = new JSONParser(getInstrumentation().getContext());

                proposalCausals = jsonParser.getPropCausals();
                idPropCausal = jsonParser.getLastPropCausalID();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ContextThemeWrapper context = new ContextThemeWrapper(getInstrumentation().getContext(), R.style.Theme_AppCompat);
        Intent intent = new Intent(context,ProposalCreationActivity.class);

        Bundle b = new Bundle();
        b.putSerializable("DATA_WRAPPER", new DataWrapper(proposalCausals));

        b.putSerializable("PROPOSAL", new Proposal(5,"","","","",new ProposalCausal(5, ""),0));
        intent.putExtra("idPropCaus", idPropCausal);

        intent.putExtras(b);
        intent.putExtra("id", idPropCausal);

        setActivityIntent(intent);
        activity = getActivity();
    }


    public void testPreconditions(){
            com.getbase.floatingactionbutton.FloatingActionButton fl = (com.getbase.floatingactionbutton.FloatingActionButton) activity.findViewById(R.id.saveEvent);
            assertNotNull(fl);
            com.getbase.floatingactionbutton.FloatingActionButton fl2 = (com.getbase.floatingactionbutton.FloatingActionButton) activity.findViewById(R.id.deleteEvent);
            assertNotNull(fl2);
            Spinner pdescr = (Spinner) activity.findViewById(R.id.pdescr_spinner);
            assertNotNull(pdescr);
        EditText customprop = (EditText) activity.findViewById(R.id.customProp);
        assertNotNull(customprop);
            CheckBox codprop = (CheckBox) activity.findViewById(R.id.checkCustumProp);
            assertNotNull(codprop);
            Button setdate = (Button) activity.findViewById(R.id.datePickerButton);
            assertNotNull(setdate);
            Button time = (Button) activity.findViewById(R.id.timePicker);
            assertNotNull(time);
            TextView textView8 = (TextView) activity.findViewById(R.id.textView8);
            assertNotNull(textView8);
            TextView textView9 = (TextView) activity.findViewById(R.id.textView9);
            assertNotNull(textView9);
        EditText notetext = (EditText) activity.findViewById(R.id.note);
        assertNotNull(notetext);
            TextView note = (TextView) activity.findViewById(R.id.noteLabel);
            assertNotNull(note);
            TextView dateview = (TextView) activity.findViewById(R.id.dateView);
            assertNotNull(dateview);
            TextView timeview = (TextView) activity.findViewById(R.id.timeView);
            assertNotNull(timeview);
    }

    @SmallTest
    public void test_Textview8(){   //Each test must begin with "test" keyword, otherwise it's not picked up by JUnit 3
        TextView textView = (TextView) activity.findViewById(R.id.textView8);
        assertEquals("Data Proposta", textView.getText());
    }

    @SmallTest
    public void test_Textview9(){   //Each test must begin with "test" keyword, otherwise it's not picked up by JUnit 3
        TextView textView = (TextView) activity.findViewById(R.id.textView9);
        assertEquals("Codici proposte esistenti", textView.getText());
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

    @SmallTest
    public void test_check(){   //Each test must begin with "test" keyword, otherwise it's not picked up by JUnit 3
        CheckBox codprop = (CheckBox) activity.findViewById(R.id.checkCustumProp);
        assertEquals("Nuovo codice proposta", codprop.getText());
    }


}
