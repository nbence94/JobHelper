package com.example.excelimportalasproject.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.data.SaveLocalDatas;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LoginSettingsActivity extends AppCompatActivity {

    ImageView back_button;
    EditText ip_field, port_field, name_field, password_field, database_field;
    FloatingActionButton save_button;

    SaveLocalDatas sld;

    String ip_value, name_value, password_value, database_value;
    int port_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_settings);

        sld = new SaveLocalDatas(this);

        back_button = findViewById(R.id.login_settings_step_back);
        ip_field = findViewById(R.id.login_settings_ip_gui);
        port_field = findViewById(R.id.login_settings_port_gui);
        name_field = findViewById(R.id.login_settings_username_gui);
        password_field = findViewById(R.id.login_settings_password_gui);
        database_field = findViewById(R.id.login_settings_database_gui);
        save_button = findViewById(R.id.login_settings_save_gui);

        if(!sld.loadIP().isEmpty()) {
            ip_value = sld.loadIP();
            ip_field.setText(ip_value);
        }
        if(sld.loadPort() != -1) {
            port_value = sld.loadPort();
            port_field.setText(String.valueOf(port_value));
        }
        if(!sld.loadUsername().isEmpty()) {
            name_value = sld.loadUsername();
            name_field.setText(name_value);
        }
        if(!sld.loadPassword().isEmpty()) {
            password_value = sld.loadPassword();
            //password_field.setText(password_value);
        }
        if(!sld.loadDB().isEmpty()) {
            database_value = sld.loadDB();
            database_field.setText(database_value);
        }

        back_button.setOnClickListener(v -> {
            finish();
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        });

        save_button.setOnClickListener(v -> {
            ip_value = ip_field.getText().toString();
            port_value = Integer.parseInt(port_field.getText().toString());
            name_value = name_field.getText().toString();
            password_value = password_field.getText().toString();
            database_value = database_field.getText().toString();

            sld.saveDatabaseValues(ip_value, port_value, database_value, name_value, password_value);
            Toast.makeText(this, "Adatok elmentve", Toast.LENGTH_SHORT).show();

        });

    }


}