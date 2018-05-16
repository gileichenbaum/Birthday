package birthday.nanit.com.happybirthday.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Project: Nanit
 * Created by Gil on 5/16/2018.
 */
public class PreferenceUtil {

    public static void saveImageUri(Context context, String uriString) {
        if (context != null) {
            getEditor(context).putString(Constants.PREF_IMAGE_URI, uriString).apply();
        }
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }
}
