import android.app.FragmentManager;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import org.json.JSONException;
import java.util.ArrayList;
import my.dailyplan.R;
import my.dailyplan.activities.MainActivity;
import my.dailyplan.connectionrelated.JSONParser;
import my.dailyplan.datahandlers.Proposal;
import my.dailyplan.datahandlers.ProposalWrapper;
import my.dailyplan.fragments.ProposalFragment;

public class ProposalFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {

    ProposalFragment fragment;
    ArrayList<Proposal> e = new ArrayList<>();

    public ProposalFragmentTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();

        ArrayList<Proposal> proposals = new ArrayList<>();
        ProposalFragment f = new ProposalFragment();

        try {
            JSONParser jsonParser = new JSONParser(getInstrumentation().getContext());
            proposals = jsonParser.getProposals();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Bundle b = new Bundle();
        b.putSerializable("PROPOSAL_WRAPPER", new ProposalWrapper(proposals));
        f.setArguments(b);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, f).commit();
        fragment = f;
        e = proposals;
    }

    @SmallTest
    public void test_proposalfragment(){   //Each test must begin with "test" keyword, otherwise it's not picked up by JUnit 3
        assertNotNull(e.get(0));
    }

}
