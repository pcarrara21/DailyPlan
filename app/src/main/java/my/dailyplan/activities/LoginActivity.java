package my.dailyplan.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import my.dailyplan.R;
import my.dailyplan.connectionrelated.AppConfig;
import my.dailyplan.connectionrelated.AppController;
import my.dailyplan.connectionrelated.SessionManager;
import my.dailyplan.other.PrefUtils;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText username;
    private EditText password;
    private SessionManager session;
    private ProgressDialog pDialog;
    private CheckBox memo;

    private static final String USER_KEY = "__USERNAME__" ;
    private static final String PASSWORD_KEY = "__PASSWORD__";
    private static final String MEM_STATUS = "__MEM__";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Titolo dell' actionBar
        getSupportActionBar().setTitle("Daily Plan - Accedi");

        username = (EditText) findViewById(R.id.userText);
        password = (EditText) findViewById(R.id.passText);
        memo = (CheckBox) findViewById(R.id.memo);

        loginButton = (Button) findViewById(R.id.loginButton);

        //Dialog che mostra il progresso del login
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        username.setText(PrefUtils.getFromPrefs(this, USER_KEY, ""));
        password.setText(PrefUtils.getFromPrefs(this, PASSWORD_KEY, ""));
        memo.setChecked(Boolean.parseBoolean(PrefUtils.getFromPrefs(this, MEM_STATUS, "false")));

        //Session manager
        session = new SessionManager(getApplicationContext());
        session.setLogin(false);

        //Controlla se l'utente è loggato al sistema
        if (session.isLoggedIn()) {
            //Utente già loggato; inizio della mainactivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u = username.getText().toString();
                String p = password.getText().toString();

                //Controllo di dati vuoti all'interno dei moduli
                if (u.trim().length() > 0 && p.trim().length() > 0) {
                    // controllo del login utente
                    checkLogin(u, p);
                } else {
                    //Richiesta di immissione delle credenziali
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkLogin(final String username, final String password) {
        //Tag utilizzato per identificare la richiesta
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("APP", "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    //Controllo di errori nel json
                    if (!error) {
                        //Utente connesso con successo
                        //Creazione di una sessione di login
                        session.setLogin(true);
                        storeCredentials(username, password);
                        //Avvio della MainActivity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //errore di login con acquisizione stringa errore
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // errore JSON
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("APP", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Associazione dei parametri all'url di login
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("name", username);
                params.put("password", password);
                //Grazie a questi due parametri solo l'app ha accesso al database
                params.put("admin", "dailyplan");
                params.put("admin_pass", "a6bb2dfaed81d348d91790b38bd5d7f7f04e48a3");

                return params;
            }

        };
        // Aggiunta di una richiesta alla coda di richieste
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //Salvataggio di email e password
    public void storeCredentials(String email,String pass)
    {
        if(memo.isChecked())
        {
            PrefUtils.saveToPrefs(this,USER_KEY,email);
            PrefUtils.saveToPrefs(this,PASSWORD_KEY,pass);
            PrefUtils.saveToPrefs(this,MEM_STATUS,""+memo.isChecked());

        }
        else PrefUtils.deletePrefs(this);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
