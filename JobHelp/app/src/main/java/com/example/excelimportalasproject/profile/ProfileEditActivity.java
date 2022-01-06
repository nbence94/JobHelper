package com.example.excelimportalasproject.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.data.DataCoding;
import com.example.excelimportalasproject.data.DatabaseHelper;
import com.example.excelimportalasproject.data.SaveLocalDatas;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProfileEditActivity extends AppCompatActivity {

    private final String LOG_TITLE = "ProfileEditActivity";

    int user_id;
    String name_value, email_value, county_value, password_value, password_confirm_value;
    TextView county_field;
    EditText name_field, email_field, password_field, password_confirm_field;
    FloatingActionButton profile_edit_save_button;
    ImageView back_button;

    DatabaseHelper dh;
    DataCoding dc;
    SaveLocalDatas sld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        dh = new DatabaseHelper(this, this);
        dc = new DataCoding();
        sld = new SaveLocalDatas(this);

        name_field = findViewById(R.id.profile_fullname_edit_gui);
        email_field = findViewById(R.id.profile_email_edit_gui);
        county_field = findViewById(R.id.profile_county_edit_gui);
        password_field = findViewById(R.id.profile_password_edit_gui);
        password_confirm_field = findViewById(R.id.profile_password_confirm_gui);
        profile_edit_save_button = findViewById(R.id.profile_edit_save_gui);
        back_button = findViewById(R.id.profile_edit_step_back);

        name_field.setText(sld.loadUserName());
        email_field.setText(sld.loadUserEmail());
        county_field.setText(sld.loadUserCounty());

        profile_edit_save_button.setOnClickListener(v -> {
            password_value = password_field.getText().toString();
            password_confirm_value = password_confirm_field.getText().toString();
            name_value = name_field.getText().toString();
            email_value = email_field.getText().toString();
            county_value = county_field.getText().toString();

            boolean saveable = true;

            if(name_value.isEmpty()) {
                name_field.setError("Üresen hagyott mező!");
                name_field.requestFocus();
                Log.e(LOG_TITLE, "A név nem lett megadva");
                saveable = false;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email_value).matches()) {
                email_field.setError("Az e-mail cím nem megfelelő vagy nem lett megadva!");
                email_field.requestFocus();
                Log.e(LOG_TITLE, "Az e-mail nincs megadva vagy nem megfelelő a formátum.");
                saveable = false;
            }

            if(!password_value.isEmpty() && password_confirm_value.isEmpty()) {
                password_confirm_field.setError("Mindkét mező megadása szükséges!");
                password_confirm_field.requestFocus();
                Log.e(LOG_TITLE, "A jelszó nincs megadva.");
                saveable = false;
            }

            if(password_value.isEmpty() && !password_confirm_value.isEmpty()) {
                password_field.setError("Mindkét mező megadása szükséges!");
                password_field.requestFocus();
                Log.e(LOG_TITLE, "Jelszó megerősítés hiányzik.");
                saveable = false;
            }

            if(!password_value.isEmpty() && !password_confirm_value.isEmpty() && !password_value.equals(password_confirm_value)) {
                password_field.setError("A megadott jelszavak nem egyeznek!");
                password_field.requestFocus();
                password_confirm_field.setError("A megadott jelszavak nem egyeznek!");
                password_confirm_field.requestFocus();
                Log.e(LOG_TITLE, "Jelszavak nem egyeznek.");
                saveable = false;
            }

            if(!dh.checkEmail(dh.USERS, email_value, "WHERE ID != " + sld.loadUserID())) {
                email_field.setError("Ez az e-mail cím foglalt!");
                email_field.requestFocus();
                Log.e(LOG_TITLE, "Foglalt e-mail.");
                saveable = false;
            }

            if(saveable) {
                if(!password_value.isEmpty()) {
                    try {
                        password_value = dc.encrypt(password_value);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Hiba történt", Toast.LENGTH_SHORT).show();
                    }

                    if(!dh.editData("UPDATE " + dh.USERS + " SET Fullname ='" + name_value +
                            "', Email ='" + email_value +
                            "', Password ='" + password_value.trim() +
                            "' WHERE ID = " + sld.loadUserID() + ";")) return;
                } else {
                    if(!dh.editData("UPDATE " + dh.USERS + " SET Fullname ='" + name_value +
                            "', Email ='" + email_value +
                            "' WHERE ID = " + sld.loadUserID() + ";")) return;
                }

                sld.saveUserDatas(user_id, name_value, email_value, county_value, sld.loadUserRole(), sld.loadUserRoleID(), sld.loadUserStatus());
                finish();
                Intent profile_screen = new Intent(ProfileEditActivity.this, ProfileActivity.class);
                startActivity(profile_screen);
            }
        });

        back_button.setOnClickListener(v -> {
            finish();
            Intent profile_screen = new Intent(ProfileEditActivity.this, ProfileActivity.class);
            startActivity(profile_screen);
        });
    }
}