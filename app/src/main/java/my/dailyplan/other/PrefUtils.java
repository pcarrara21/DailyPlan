package my.dailyplan.other;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {  //Classe per il salvataggio delle credenziali di login

    public static final String PREFS_LOGIN_USERNAME_KEY = "__USERNAME__" ;
    public static final String PREFS_LOGIN_PASSWORD_KEY = "__PASSWORD__" ;

    //Metodo per salvare i valori forniti secondo la coppia chiave-valore

    public static void saveToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }

    //Metodo per caricare i valori salvati secondo la coppia chiave-valore

     public static String getFromPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    //Metodo per la cancellazione delle preferenze relative all'app
    public static void deletePrefs(Context context)
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.commit();
    }
}

