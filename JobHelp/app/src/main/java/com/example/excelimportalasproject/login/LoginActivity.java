package com.example.excelimportalasproject.login;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.excelimportalasproject.MainScreenActivity;
import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.data.DataCoding;
import com.example.excelimportalasproject.data.DatabaseHelper;
import com.example.excelimportalasproject.data.SaveLocalDatas;
import com.example.excelimportalasproject.reading.RealPathUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class LoginActivity extends AppCompatActivity {

    private final String LOG_TITLE = "MainActivity";

    DataCoding dc;
    DatabaseHelper dh;
    SaveLocalDatas sld;

    Button login_button;
    EditText email_field, password_field;
    CheckBox stay_logged_in;
    ProgressBar progress_bar;
    ImageView settings_button;

    int user_id =-1, user_status=-1, role_id;
    String user_name, user_email, user_county, user_role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dc = new DataCoding();
        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);

        login_button = findViewById(R.id.login_login_gui);
        email_field = findViewById(R.id.login_email_gui);
        password_field = findViewById(R.id.login_password_gui);
        stay_logged_in = findViewById(R.id.login_stay_logged_gui);
        progress_bar = findViewById(R.id.login_progressbar);
        progress_bar.setVisibility(View.GONE);
        settings_button = findViewById(R.id.login_settings_gui);

        //TODO: Ez csak debug. Ha már nem kell, töröld ki
        //email_field.setText("admin@admin.com");
        //password_field.setText("admin");

        if(sld.checkStayLoggedStatus()) {
            Connection con = dh.connectionClass(this);
            if(con != null) {
                finish();
                Intent main = new Intent(LoginActivity.this, MainScreenActivity.class);
                startActivity(main);
                return;
            }
        }

        login_button.setOnClickListener(v -> {
            progress_bar.setVisibility(View.VISIBLE);

            String email_value = email_field.getText().toString();
            String password_value = password_field.getText().toString();
            boolean loginable = true;

            //Ellenőrzés
            if(!Patterns.EMAIL_ADDRESS.matcher(email_value).matches()) {
                email_field.setError("A mező üres vagy helytelen formátum");
                email_field.requestFocus();
                Log.e(LOG_TITLE, "Email hiba: A mező üres vagy helytelen a formátum");
                loginable = false;
            }

            if(password_value.isEmpty()) {
                password_field.setError("Üresen hagyott mező!");
                password_field.requestFocus();
                Log.e(LOG_TITLE, "Üresen hagyott jelszó");
                loginable = false;
            }

            //Adatbázis ellenőrzés
            loginable = getDatas(email_value, password_value);

            //Bejelentkezés
            if(loginable) {
                Log.i(LOG_TITLE, "Sikeres bejelentkezés");
                finish();
                Intent main = new Intent(LoginActivity.this, MainScreenActivity.class);
                startActivity(main);

                sld.saveStayLoggedStatus(stay_logged_in.isChecked());
            }
            progress_bar.setVisibility(View.GONE);
        });


        settings_button.setOnClickListener(v -> {
            Intent settings = new Intent(this, LoginSettingsActivity.class);
            startActivity(settings);
            finish();
        });
    }

    private boolean getDatas(String email, String password) {
        Connection con = dh.connectionClass(this);

        try {
            if(con != null) {
                password = dc.encrypt(password);
                String select = "SELECT * FROM " + dh.USERS + " u, " + dh.ROLES + " r, " + dh.COUNTIES + " c WHERE u.RoleID = r.ID AND u.Email = '" + email + "' AND u.Password='" + password.trim() + "' AND u.CountyID = c.ID;";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                while(rs.next()) {
                    user_role = rs.getString(9);
                    user_name = rs.getString(dh.USER_NAME_INDEX);
                    user_email = rs.getString(dh.USER_EMAIL_INDEX);
                    user_county = rs.getString(11);
                    user_id = Integer.parseInt(rs.getString(dh.USER_ID_INDEX));
                    role_id = Integer.parseInt(rs.getString(dh.USER_ROLE_INDEX));
                    user_status = Integer.parseInt(rs.getString(dh.USER_STATUS_INDEX));
                }

                sld.saveUserDatas(user_id, user_name, user_email, user_county, user_role, role_id, user_status);
                Log.i(LOG_TITLE, select);
            }
        } catch (SQLException throwables) {

            throwables.printStackTrace();
            Toast.makeText(this, "Adatbázis hiba", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TITLE, "Adatbázis hiba");

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(this, "Adatbázis hiba", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TITLE, "Kódolás hiba");

        }

        if(user_id == -1) {
            Toast.makeText(this, "Helytelen e-mail vagy jelszó!", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TITLE, "Nem jó ID vagy Status");
            return false;

        } else if(user_status == 0) {

            Toast.makeText(this, "Ez a fiók inaktív!", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TITLE, "A felhasználó inaktív");
            return false;
        }

        return true;
    }
}
