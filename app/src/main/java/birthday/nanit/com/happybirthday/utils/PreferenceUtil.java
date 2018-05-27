package birthday.nanit.com.happybirthday.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Project: Nanit
 * Created by Gil on 5/16/2018.
 */
public class PreferenceUtil {

    private static SharedPreferences.Editor getEditor(@NonNull Context context) {
        return getSharedPreferences(context).edit();
    }

    private static SharedPreferences getSharedPreferences(@NonNull Context context) {
        return context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static String getName(@NonNull Context context) {
        return getSharedPreferences(context).getString(Constants.PREF_NAME, null);
    }

    public static long getBirthday(@NonNull Context context) {
        return getSharedPreferences(context).getLong(Constants.PREF_BIRTH_DATE, 0L);
    }

    @Nullable
    public static Uri getImageUri(@NonNull Context context) {
        final String uriString = getSharedPreferences(context).getString(Constants.PREF_IMAGE_URI, null);
        return TextUtils.isEmpty(uriString) ? null : Uri.parse(uriString);
    }

    public static void saveImageUri(@NonNull Context context, String uriString) {
        final SharedPreferences.Editor editor = getEditor(context);
        editor.remove(Constants.PREF_IMAGE_URI).apply();
        editor.putString(Constants.PREF_IMAGE_URI, uriString).apply();
    }
}
