package com.example.excelimportalasproject.data;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseHelper {

    final private String LOG_TITLE = "DatabaseHelper";

    final public String USERS = "Users";
    final public String ROLES = "Roles";
    final public String CIRCLES = "Circles";
    final public String PLACES = "Places";
    final public String COUNTIES = "Counties";

    final public int USER_ID_INDEX = 1;
    final public int USER_NAME_INDEX = 2;
    final public int USER_EMAIL_INDEX = 3;
    final public int USER_PASSWORD_INDEX = 4;
    final public int USER_COUNTY_INDEX = 5;
    final public int USER_ROLE_INDEX = 6;
    final public int USER_STATUS_INDEX = 7;

    final public int CIRCLES_ID_INDEX = 1;
    final public int CIRCLES_FILENAME_INDEX = 2;
    final public int CIRCLES_IMPORT_DATE_INDEX = 3;
    final public int CIRCLES_DONE_DATE_INDEX = 4;
    final public int CIRCLES_STATUS_INDEX = 5;
    final public int CIRCLES_USER_ID_INDEX = 6;

    final public int PLACES_ID_INDEX = 1;
    final public int PLACES_CITY_INDEX = 2;
    final public int PLACES_ADDRESS_INDEX = 3;
    final public int PLACES_SIZE_INDEX = 4;
    final public int PLACES_CAMPAIGN_INDEX = 5;
    final public int PLACES_BILLBOARD_CODE_INDEX = 6;
    final public int PLACES_STATUS_INDEX = 7;
    final public int PLACES_CIRCLE_ID_INDEX = 8;
    final public int PLACES_DONE_DATE_INDEX = 9;

    final public int NAUTRAL_ID_INDEX = 1;
    final public int NAUTRAL_NAME_INDEX = 2;

    Context context;
    Activity activity;

    SaveLocalDatas sld;

    public DatabaseHelper(Context context) {
        this.context = context;
    }

    public DatabaseHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

        this.sld = new SaveLocalDatas(activity);
    }


    public void getCountiesData(ArrayList<Counties> list) {
        Connection con = connectionClass(context);
        String select = "SELECT * FROM " + COUNTIES + ";";

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);
                while(rs.next()) {
                    list.add(new Counties(Integer.parseInt(rs.getString(NAUTRAL_ID_INDEX)), rs.getString(NAUTRAL_NAME_INDEX)));
                }
            }
            Log.i(LOG_TITLE, "Sikeres adatbázis lekérdezés (" + select + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Sikertelen adatbázis lekérdezés");
        }
    }

    public void getRolesData(ArrayList<Roles> list) {
        Connection con = connectionClass(context);
        String select = "SELECT * FROM " + ROLES + ";";

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);
                while(rs.next()) {
                    list.add(new Roles(Integer.parseInt(rs.getString(NAUTRAL_ID_INDEX)), rs.getString(NAUTRAL_NAME_INDEX)));
                }
            }
            Log.i(LOG_TITLE, "Sikeres adatbázis lekérdezés (" + select + ")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Sikertelen adatbázis lekérdezés");
        }
    }


    public boolean checkEmail(String table, String email, String condition) {
        Connection con = connectionClass(context);
        String select = "SELECT Email FROM " + table + " " + condition + ";";

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                while(rs.next()) {

                    if(rs.getString(1).equals(email)) {
                        return false;
                    }
                }
            }
            Log.i(LOG_TITLE, "Adatbáizs lekérdezés (" + select +")");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Sikertelen adatbázis lekérdezés (" + select +")");
        }

        return true;
    }

    public boolean editData(String update_sql) {
        Connection con = this.connectionClass(context);

        try {
            if(con != null) {
                Statement st = con.createStatement();
                st.executeUpdate(update_sql);
                Log.i(LOG_TITLE, update_sql);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Hiba az adatok módosítása során");
            return false;
        }
        return true;
    }

    public boolean deleteData(String table, String condition_column, int condition_value) {
        Connection con = this.connectionClass(context);

        try {
            if(con != null) {
                Statement st = con.createStatement();
                st.executeUpdate("DELETE FROM " + table + " WHERE " + condition_column + " = " + condition_value + ";");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Hiba a törlés során");
            return false;
        }

        return true;
    }

    public boolean insertData(String insert_sql) {
        Connection con = this.connectionClass(context);

        try {
            if(con != null) {
                Log.i(LOG_TITLE, insert_sql);
                Statement st = con.createStatement();
                st.executeUpdate(insert_sql);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Hiba az adatok felvitele során");
            return false;
        }

        return true;
    }

    @SuppressLint("NewApi")
    public Connection connectionClass(Context context) {
        Connection con = null;
        this.sld = new SaveLocalDatas(activity);
        
        //IP: 192.168.0.17 - PORT: 1433 - TesztUser / Jelszo12AA - Workdb

        String ip = sld.loadIP();
        int port = sld.loadPort();
        String database = sld.loadDB();
        String username = sld.loadUsername();
        String password = sld.loadPassword();

        if(ip.equals("") || port == -1 || database.equals("") || username.equals("") || password.equals("")) {
            Toast.makeText(context, "Állítsd be az adatbázis", Toast.LENGTH_SHORT).show();
            return null;
        }

        StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tp);

        String conURL = "jdbc:jtds:sqlserver://" + ip + ":"
                + port + ";databasename="+  database + ";User=" +
                username + ";password=" + password +";";

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            con = DriverManager.getConnection(conURL);

        } catch (Exception e) {
            Log.e(LOG_TITLE, e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
        Log.i(LOG_TITLE, "Sikeres adatbázis kapcsolat. (" + ip + ", " + port + ", " + database + ")");
        return con;
    }
}
