package com.example.excelimportalasproject.data;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

public class SaveLocalDatas {

    private final String LOG_TITLE = "SaveLocalDatas";
    Activity activity;

    public SaveLocalDatas(Activity activity) {
        this.activity = activity;
    }

    public void saveUserDatas(int id, String name, String email, String county, String role_name, int role_id, int status) {
        Log.i(LOG_TITLE, "Felhasználó adatai eltárolva az eszközön");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("user_id", id);
        edit.putString("user_name", name);
        edit.putString("user_email", email);
        edit.putString("user_county", county);
        edit.putString("user_role", role_name);
        edit.putInt("user_role_id", role_id);
        edit.putInt("user_status", status);
        edit.apply();
    }

    public int loadUserID() {
        Log.i(LOG_TITLE, "Felhasználó azonosítójának betöltése");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return sp.getInt("user_id", -1);
    }

    public String loadUserName() {
        Log.i(LOG_TITLE, "Felhasználó nevének betöltése");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return sp.getString("user_name", null);
    }

    public int loadUserRoleID() {
        Log.i(LOG_TITLE, "Felhasználó szerepkör azonosítójának betöltése");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return sp.getInt("user_role_id", -1);
    }

    public String loadUserRole() {
        Log.i(LOG_TITLE, "Felhasználó szerepkörének betöltése");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return sp.getString("user_role", null);
    }

    public String loadUserEmail(){
        Log.i(LOG_TITLE, "Felhasználó e-mail címének betöltése");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return sp.getString("user_email", null);
    }

    public String loadUserCounty(){
        Log.i(LOG_TITLE, "Felhasználó hatókörének betöltése");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return sp.getString("user_county", null);
    }

    public int loadUserStatus(){
        Log.i(LOG_TITLE, "Felhasználó állapotának betöltése");
        SharedPreferences sp = activity.getSharedPreferences("user_datas", MODE_PRIVATE);
        return sp.getInt("user_status", -1);
    }

    public boolean checkStayLoggedStatus() {
        Log.i(LOG_TITLE, "Automatikus bejelentkeztetés");
        SharedPreferences sp = activity.getSharedPreferences("open_check", MODE_PRIVATE);
        return sp.getBoolean("key", false);
    }

    public void saveStayLoggedStatus(boolean stay_logged_in) {
        Log.i(LOG_TITLE, "Automata bejelentkeztetés adatok elmentve");
        SharedPreferences sp = activity.getSharedPreferences("open_check", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("key", stay_logged_in);
        edit.apply();
    }

    //Database
    public void saveDatabaseValues(String ip, int port, String database, String username, String password) {
        Log.i(LOG_TITLE, "Aktuális felhasználó azonosítójának mentése");
        SharedPreferences sp = activity.getSharedPreferences("database_data", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("db_ip", ip);
        edit.putInt("db_port", port);
        edit.putString("db_name", database);
        edit.putString("db_username", username);
        edit.putString("db_password", password);
        edit.apply();
    }

    public String loadIP() {
        SharedPreferences sp = activity.getSharedPreferences("database_data", MODE_PRIVATE);
        return sp.getString("db_ip", "");
    }

    public int loadPort() {
        SharedPreferences sp = activity.getSharedPreferences("database_data", MODE_PRIVATE);
        return sp.getInt("db_port", -1);
    }

    public String loadDB() {
        SharedPreferences sp = activity.getSharedPreferences("database_data", MODE_PRIVATE);
        return sp.getString("db_name", "");
    }

    public String loadUsername() {
        SharedPreferences sp = activity.getSharedPreferences("database_data", MODE_PRIVATE);
        return sp.getString("db_username", "");
    }

    public String loadPassword() {
        SharedPreferences sp = activity.getSharedPreferences("database_data", MODE_PRIVATE);
        return sp.getString("db_password", "");
    }
}
