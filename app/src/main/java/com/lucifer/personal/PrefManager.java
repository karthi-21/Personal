package com.lucifer.personal;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class for Shared Preference
 */
public class PrefManager {

    Context context;

    PrefManager(Context context) {
        this.context = context;
    }

    public void saveLangDetails(String lang) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LangDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Lang", lang);
        editor.commit();
    }

    public String getLang() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LangDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Lang", "");
    }

    public boolean isUserLogedOut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LangDetails", Context.MODE_PRIVATE);
        boolean isLangEmpty = sharedPreferences.getString("Lang", "").isEmpty();
        return isLangEmpty;
    }
}