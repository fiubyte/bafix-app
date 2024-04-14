package com.fiubyte.bafix.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.fiubyte.bafix.R;

public final class SharedPreferencesManager {
    private static SharedPreferences sharedPreferences;

    public static void initialize(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    public static void savePreferences(String token, Context context) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(context.getString(R.string.login_token_key), token);
        editor.apply();
    }

    public static String getStoredToken(Context context) {
        String token = sharedPreferences.getString(context.getString(R.string.login_token_key), "");
        return token;
    }
}
