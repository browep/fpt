package com.github.browep.fpt.dao;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import com.github.browep.fpt.C;
import com.github.browep.fpt.util.Log;


/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: 2/2/11
 * Time: 9:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreferencesService {

    SharedPreferences sharedPreferences;

    public PreferencesService(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public PreferencesService(Context context){
        this.sharedPreferences = context.getSharedPreferences(C.PREFS_FILE_NAME,0);
    }



    protected void removePreference(String prefName) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(prefName);
        editor.commit();
    }

    public Long getLongPreference(String prefName) {

        return sharedPreferences.getLong(prefName, 0);
    }

    public Boolean getBooleanPreference(String prefName) {

        return sharedPreferences.getBoolean(prefName, false);
    }

    public int getIntPreference(String prefName) {

        return sharedPreferences.getInt(prefName, -1);
    }

    public void setIntPreference(String prefName, int value) {
        Log.i("DAO: set INT pref " + prefName + "->" + String.valueOf(value));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(prefName, value);
        editor.commit();
    }

    public void setBooleanPreference(String prefName, boolean value) {
        Log.i("DAO: set BOOLEAN pref " + prefName + "->" + String.valueOf(value));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(prefName, value);
        editor.commit();
    }

  public void setStringPreference(String prefName, String value){
    Log.i("DAO: set STRING pref " + prefName + "->" + value);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(prefName, value);
    editor.commit();

  }

  public String getStringPreference(String prefName, String value){
    return  sharedPreferences.getString(prefName,null);
  }
}