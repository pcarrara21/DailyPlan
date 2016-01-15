import android.app.FragmentManager;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import org.json.JSONException;
import java.util.ArrayList;
import my.dailyplan.R;
import my.dailyplan.activities.MainActivity;
import my.dailyplan.connectionrelated.JSONParser;
import my.dailyplan.datahandlers.Event;
import my.dailyplan.datahandlers.EventWrapper;
import my.dailyplan.fragments.DayListFragment;


public class DayListFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    DayListFragment fragment;
    ArrayList<Event> e = new ArrayList<>();

    public DayListFragmentTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();

        ArrayList<Event> events = new ArrayList<>();
        DayListFragment f = new DayListFragment();

            try {
                JSONParser jsonParser = new JSONParser(getInstrumentation().getContext());
                events = jsonParser.getCalendarEvents(f.getInitialDate(),f.getFinalDate());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Bundle b = new Bundle();
            b.putSerializable("EVENT_WRAPPER", new EventWrapper(events));
            f.setArguments(b);
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, f).commit();
        fragment = f;
        e = events;
    }

    @SmallTest
    public void test_daylistfragment(){   //Each test must begin with "test" keyword, otherwise it's not picked up by JUnit 3
        assertNotNull(e.get(0));
    }

}
