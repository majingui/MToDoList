package com.example.myapplication;


import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;

public class PreferencesHelper {
    private static PreferencesHelper sPreferenceHelper;

    public synchronized static PreferencesHelper getInstance() {
        if (sPreferenceHelper == null) {
            sPreferenceHelper = new PreferencesHelper();
        }
        return sPreferenceHelper;
    }

    public Preferences getPreference(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context.getApplicationContext());//获取上下文
        String fileName = "note";
        return databaseHelper.getPreferences(fileName);
    }
    public Preferences getPreferenceTag(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context.getApplicationContext());//获取上下文
        String fileName = "tag";
        return databaseHelper.getPreferences(fileName);
    }
    public Preferences getPreferenceSort(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context.getApplicationContext());//获取上下文
        String fileName = "sort";
        return databaseHelper.getPreferences(fileName);
    }

}
