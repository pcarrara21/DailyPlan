import android.app.FragmentManager;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import org.json.JSONException;
import java.util.ArrayList;
import my.dailyplan.R;
import my.dailyplan.activities.MainActivity;
import my.dailyplan.connectionrelated.JSONParser;
import my.dailyplan.datahandlers.Client;
import my.dailyplan.datahandlers.DataWrapper;
import my.dailyplan.fragments.ClientFragment;

public class ClientFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    ClientFragment fragment;
    ArrayList<Client> e = new ArrayList<>();

    public ClientFragmentTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();

        ArrayList<Client> clients = new ArrayList<>();
        ClientFragment f = new ClientFragment();

        try {
            JSONParser jsonParser = new JSONParser(getInstrumentation().getContext());
            clients = jsonParser.getClients();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Bundle b = new Bundle();
        b.putSerializable("DATA_WRAPPER", new DataWrapper(clients, null));
        f.setArguments(b);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, f).commit();
        fragment = f;
        e = clients;
    }

    @SmallTest
    public void test_clientfragment(){   //Each test must begin with "test" keyword, otherwise it's not picked up by JUnit 3
        assertNotNull(e.get(0));
    }

}
