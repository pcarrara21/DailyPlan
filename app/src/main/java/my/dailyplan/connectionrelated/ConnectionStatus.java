package my.dailyplan.connectionrelated;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectionStatus {

    private static ConnectionStatus instance = new ConnectionStatus();
    static Context context;
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    boolean connected = false;

    public static ConnectionStatus getInstance(Context ctx) {
        context = ctx;
        return instance;
    }

    // booleano che indica se lo stato di connessione e' online
    public boolean isOnline(Context con) {
        try {
            connectivityManager = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE); // Istanza di ConnectivityManager ottenuta tramite chiamata al servizio

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); // Info sulla rete attiva
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected(); // connected e' true se la rete attiva esiste, e' disponibile ed e' connessa
            return connected;


        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }
}



