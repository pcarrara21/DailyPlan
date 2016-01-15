package my.dailyplan.connectionrelated;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue; // Coda delle request

    private static AppController mInstance; // Istanza di AppController

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    // Ottieni l'istanza di AppController
    public static synchronized AppController getInstance() {
        return mInstance;
    }

    // Ottieni la coda delle request
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mInstance.getApplicationContext());
        }

        return mRequestQueue;
    }

    // Aggiungi alla coda delle request
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}