package com.example.excelimportalasproject.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.adapters.CircleAdapter;
import com.example.excelimportalasproject.alertdialog.MyAlertDialog;
import com.example.excelimportalasproject.alertdialog.OnDialogChoice;
import com.example.excelimportalasproject.data.Counties;
import com.example.excelimportalasproject.data.DataCoding;
import com.example.excelimportalasproject.data.DatabaseHelper;
import com.example.excelimportalasproject.data.Roles;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AdminNewUserActivity extends AppCompatActivity implements OnDialogChoice {

    private final String LOG_TITLE = "AdminNewUserActivity";

    DatabaseHelper dh;
    DataCoding dc;
    MyAlertDialog mad;

    EditText name_field, email_field, password_field, password_confirm_field;
    TextView role_field, county_field;
    FloatingActionButton save_button;
    ImageView back_button;

    ArrayList<Roles> roles_data_list;
    ArrayList<Counties> counties_data_list;

    String[] roles_array;
    int chosen_role = -1;
    boolean[] chosen_role_item;
    boolean[] tmp_chosen_item;

    String[] counties_array;
    int chosen_county = -1;
    boolean[] chosen_county_item;
    boolean[] tmp_chosen_county;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_user);

        dh = new DatabaseHelper(this, this);
        dc = new DataCoding();
        mad = new MyAlertDialog(this, this);

        name_field = findViewById(R.id.admin_user_fullname_gui);
        email_field = findViewById(R.id.admin_user_email_gui);
        password_field = findViewById(R.id.admin_user_password_gui);
        password_confirm_field = findViewById(R.id.admin_user_password_confirm_gui);
        role_field = findViewById(R.id.admin_user_department_gui);
        save_button = findViewById(R.id.admin_new_user_save_gui);
        back_button = findViewById(R.id.admin_new_user_step_back);
        county_field = findViewById(R.id.admin_user_county_dropdown_gui);

        roles_data_list = new ArrayList<>();
        dh.getRolesData(roles_data_list);
        roles_array = new String[roles_data_list.size()];
        chosen_role_item = new boolean[roles_data_list.size()];
        tmp_chosen_item = new boolean[roles_data_list.size()];

        for(int i = 0; i < roles_data_list.size(); i++) {
            roles_array[i] = roles_data_list.get(i).getName();
        }

        role_field.setOnClickListener(v -> {

            mad.singleSelectAlertDialog("Válassz szerepkört", roles_array, chosen_role_item, tmp_chosen_item, "Rendben", "Mégsem", null, 1, 1, this);
        });

        counties_data_list = new ArrayList<>();
        dh.getCountiesData(counties_data_list);
        counties_array = new String[counties_data_list.size()];
        chosen_county_item = new boolean[counties_data_list.size()];
        tmp_chosen_county = new boolean[counties_data_list.size()];

        for(int i = 0; i < counties_data_list.size(); i++) {
            counties_array[i] = counties_data_list.get(i).getName();
        }

        county_field.setOnClickListener(v -> {
            mad.singleSelectAlertDialog("Válassz egy megyét", counties_array, chosen_county_item, tmp_chosen_county, "Rendben", "Mégsem", null, 1, 2, this);
        });

        save_button.setOnClickListener(v -> {
            boolean saveable = true;

            String name_value, email_value, password_value, password_confirm_value, county_value;
            name_value = name_field.getText().toString();
            email_value = email_field.getText().toString();
            password_value = password_field.getText().toString();
            password_confirm_value = password_confirm_field.getText().toString();

            if(name_value.isEmpty()) {
                name_field.setError("Üresen hagyott mező!");
                name_field.requestFocus();
                Log.e(LOG_TITLE, "Név hiba.");
                saveable = false;
            }

            if(chosen_role < 0) {
                role_field.setError("Válassz szerepkört!");
                role_field.requestFocus();
                Log.e(LOG_TITLE, "Szerepkör hiba");
                saveable = false;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email_value).matches()) {
                email_field.setError("Az e-mail cím nem megfelelő vagy nem lett megadva!");
                email_field.requestFocus();
                Log.e(LOG_TITLE, "E-mail cím hiba");
                saveable = false;
            }

            if(!dh.checkEmail(dh.USERS, email_value, "")) {
                email_field.setError("Ez az e-mail cím foglalt!");
                email_field.requestFocus();
                Log.e(LOG_TITLE, "Foglalt e-mail.");
                saveable = false;
            }

            if(password_value.isEmpty() || password_confirm_value.isEmpty()) {
                password_confirm_field.setError("Mindkét mező megadása szükséges!");
                password_confirm_field.requestFocus();
                Log.e(LOG_TITLE, "A jelszó nincs megadva");
                saveable = false;
            }

            if(!password_value.equals(password_confirm_value)) {
                password_field.setError("A megadott jelszavak nem egyeznek!");
                password_field.requestFocus();
                password_confirm_field.setError("A megadott jelszavak nem egyeznek!");
                password_confirm_field.requestFocus();
                Log.e(LOG_TITLE, "Jelszavak nem egyeznek");
                saveable = false;
            }

            if(saveable) {

                //Jelszó kódolás
                try {
                    password_value = dc.encrypt(password_value);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Hiba történt", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TITLE, "Sikertelen jelszó titkosítás.");
                    return;
                }

                //Adatok beillesztése adatbázisba
                if(!dh.insertData("INSERT INTO " + dh.USERS + " VALUES ('" + name_value +
                                                                    "', '" + email_value +
                                                                    "', '" + password_value.trim() +
                                                                    "','" + chosen_county +
                                                                    "', " + chosen_role + " , 1);")) {
                    Toast.makeText(this, "Felvitel sikertelen", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Lezárás, becsukás, véglegesítés
                setResult(1);
                finish();
                Intent users = new Intent(this, AdminMainActivity.class);
                startActivity(users);
            }
        });

        back_button.setOnClickListener(v -> {
            finish();
            Intent admin_main = new Intent(AdminNewUserActivity.this, AdminMainActivity.class);
            startActivity(admin_main);
        });

    }

    @Override
    public void OnPositiveClick(@NonNull CircleAdapter.ViewHolder holder, int position, int status) {
        switch (status) {
            case 1:
                for (int i = 0; i < chosen_role_item.length; i++) {
                    if (chosen_role_item[i]) {
                        chosen_role = roles_data_list.get(i).getId();
                        role_field.setText(roles_array[i]);
                        Log.i(LOG_TITLE, "A választott szerepkör ID: " + chosen_role + " - Name: " + roles_data_list.get(i).getName());
                        break;
                    }
                }
            break;
            case 2:
                for (int i = 0; i < chosen_county_item.length; i++) {
                    if (chosen_county_item[i]) {
                        chosen_county = counties_data_list.get(i).getId();
                        county_field.setText(counties_array[i]);
                        Log.i(LOG_TITLE, "A választott megye ID: " + chosen_county + " - Name: " + counties_data_list.get(i).getName());
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public void OnNegativeClick(@NonNull CircleAdapter.ViewHolder holder, int position, int status) {
        switch (status) {
            case 1:
                for (int i = 0; i < chosen_role_item.length; i++) {
                    chosen_role_item[i] = tmp_chosen_item[i];
                }
                break;

            case 2:

                for (int i = 0; i < chosen_county_item.length; i++) {
                    chosen_county_item[i] = tmp_chosen_county[i];
                }
                break;

        }
    }
}