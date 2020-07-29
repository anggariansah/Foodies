package com.solvedev.foodies.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {

    private static final String PREFERENCES_NAME = "foodiesPref";
    private static final String ID_KEY = "id_user";
    private static final String IS_USER_LOGIN = "UserLogin";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context c;

    public UserPreferences(Context context) {
        c = context;
        pref = c.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSessionUser(String id) {
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(ID_KEY, id);
        editor.apply();
    }

    public void deleteLoginSession() {
        editor.remove(ID_KEY);
        editor.remove(IS_USER_LOGIN);
        editor.apply();
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public String getUserId() {
        return pref.getString(ID_KEY, "");
    }


}
