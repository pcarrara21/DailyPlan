import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import my.dailyplan.R;
import my.dailyplan.activities.LoginActivity;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    LoginActivity activity;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        activity = getActivity();
    }

    public void testPreconditions(){
        TextView textView = (TextView) activity.findViewById(R.id.textView);
        assertNotNull(textView);
        TextView textView2 = (TextView) activity.findViewById(R.id.textView2);
        assertNotNull(textView2);
        Button login = (Button) activity.findViewById(R.id.loginButton);
        assertNotNull(login);
        CheckBox memo = (CheckBox) activity.findViewById(R.id.memo);
        assertNotNull(memo);
    }


    @SmallTest
    public void test_Textview(){   //Each test must begin with "test" keyword, otherwise it's not picked up by JUnit 3
        TextView textView = (TextView) activity.findViewById(R.id.textView);
        assertEquals("Nome Utente", textView.getText());
    }

    @SmallTest
    public void test_editText(){
        EditText editText = (EditText) activity.findViewById(R.id.userText);
        assertNotNull(editText);
    }

    @SmallTest
    public void test_textView2(){
        TextView textView2 = (TextView) activity.findViewById(R.id.textView2);
        assertEquals("Password", textView2.getText());
    }

    @SmallTest
    public void test_passText(){
        EditText passText = (EditText) activity.findViewById(R.id.userText);
        assertNotNull(passText);
    }

    @SmallTest
    public void test_loginB(){
        Button login = (Button) activity.findViewById(R.id.loginButton);
        assertEquals("Accedi", login.getText());
    }

    @SmallTest
    public void test_memo(){
        CheckBox memo = (CheckBox) activity.findViewById(R.id.memo);
        assertEquals("Memorizza credenziali", memo.getText());
    }

}