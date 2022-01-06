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

public class AdminUserEditActivity extends AppCompatActivity implements OnDialogChoice {

    private final String LOG_TITLE = "AdminUserEditActivity";

    DatabaseHelper dh;
    DataCoding dc;
    MyAlertDialog mad;

    EditText name_field, email_field, password_field, password_confirm_field;
    TextView role_field, county_field, title_text;
    FloatingActionButton save_button;
    ImageView back_button;

    int status, chosen_user_id, chosen_user_roleid, user_countyid;
    String user_name, user_email;

    ArrayList<Roles> roles_data_list;
    ArrayList<Counties> counties_data_list;

    String[] roles_array;
    int chosen_role = -1;
    int tmp_role;
    boolean[] choosen_role_boolean;
    boolean[] tmp_chosen_role_boolean;

    String[] counties_array;
    int chosen_county = -1;
    int tmp_county;
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
        title_text = findViewById(R.id.textView2);

        back_button.setOnClickListener(v -> {
            finish();
            Intent users = new Intent(this, AdminUserProfileActivity.class);
            users.putExtra("user_id", chosen_user_id);
            startActivity(users);
        });

        getDataFromIntent();

        //Listák Inicializálása
        roles_data_list = new ArrayList<>();
        counties_data_list = new ArrayList<>();

        dh.getRolesData(roles_data_list);
        dh.getCountiesData(counties_data_list);

        title_text.setText(R.string.admin_user_edit_title);
        name_field.setText(user_name);
        email_field.setText(user_email);
        chosen_role = chosen_user_roleid;
        role_field.setText(roles_data_list.get(chosen_user_roleid).getName());
        chosen_county = user_countyid;
        county_field.setText(counties_data_list.get(chosen_county).getName());

        //Tömbök meghatározása
        roles_array = new String[roles_data_list.size()];
        choosen_role_boolean = new boolean[roles_data_list.size()];
        tmp_chosen_role_boolean = new boolean[roles_data_list.size()];

        counties_array = new String[counties_data_list.size()];
        chosen_county_item = new boolean[counties_data_list.size()];
        tmp_chosen_county = new boolean[counties_data_list.size()];

        //AlertDialogban megjelenítendő elemek
        for(int i = 0; i < roles_data_list.size(); i++) {
            roles_array[i] = roles_data_list.get(i).getName();
            if(roles_data_list.get(i).getId() == chosen_user_roleid) choosen_role_boolean[i] = true;
        }

        for(int i = 0; i < counties_data_list.size(); i++) {
            counties_array[i] = counties_data_list.get(i).getName();
            if(counties_data_list.get(i).getId() == user_countyid) chosen_county_item[i] = true;
        }

        role_field.setOnClickListener(v -> {
            mad.singleSelectAlertDialog("Válassz szerepkört", roles_array, choosen_role_boolean, tmp_chosen_role_boolean, "Rendben", "Mégsem", null, 1, 1, this);
        });


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

            if(!dh.checkEmail(dh.USERS, email_value, "WHERE ID != " + chosen_user_id)) {
                email_field.setError("Ez az e-mail cím foglalt!");
                email_field.requestFocus();
                Log.e(LOG_TITLE, "Foglalt e-mail.");
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

            if(!dh.checkEmail(dh.USERS, email_value, "WHERE ID != " + chosen_user_id)) {
                email_field.setError("Ez az e-mail cím foglalt!");
                email_field.requestFocus();
                Log.e(LOG_TITLE, "Foglalt e-mail.");
                saveable = false;
            }

            if(saveable) {
                String update;
                    if(!password_value.isEmpty()) {
                        try {
                            password_value = dc.encrypt(password_value);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Hiba történt", Toast.LENGTH_SHORT).show();
                        }

                       update = "UPDATE " + dh.USERS + " SET Fullname='" + name_value +
                                "', Email='" + email_value + "', CountyID=" + chosen_county
                                + ", RoleID=" + chosen_role + ", Password='" + password_value
                                + "' WHERE ID = " + chosen_user_id + ";";
                    } else {
                        update = "UPDATE " + dh.USERS + " SET Fullname='" + name_value +
                                "', Email='" + email_value + "', CountyID=" + chosen_county
                                + ", RoleID=" + chosen_role
                                + " WHERE ID = " + chosen_user_id + ";";
                    }

                if(!dh.editData(update)) {
                    Log.e(LOG_TITLE, update);
                    return;
                }

                Log.i(LOG_TITLE, update);
                setResult(1);
                finish();
                Intent users = new Intent(this, AdminUserProfileActivity.class);
                users.putExtra("user_id", chosen_user_id);
                startActivity(users);
            }
        });


    }

    private void getDataFromIntent() {
        if(getIntent().hasExtra("user_id")) {
            chosen_user_id = getIntent().getIntExtra("user_id", -1);
            chosen_user_roleid = getIntent().getIntExtra("user_roleid", -1);
            user_name = getIntent().getStringExtra("user_name");
            user_email = getIntent().getStringExtra("user_email");
            user_countyid = getIntent().getIntExtra("user_county", -1);
            Log.i(LOG_TITLE, "Adatok átvéve. ( ID: " + chosen_user_id + ", RoleID: " + chosen_user_roleid + ", Name: "
                                                + user_name + ", E-mail: " + user_email + ", CountyID: " + user_countyid + " )");
        }
    }


    @Override
    public void OnPositiveClick(@NonNull CircleAdapter.ViewHolder holder, int position, int status) {
        switch(status) {
            case 1:
                for (int i = 0; i < choosen_role_boolean.length; i++) {
                    if (choosen_role_boolean[i]) {
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
        switch(status) {
            case 1:
                for (int i = 0; i < choosen_role_boolean.length; i++) {
                    choosen_role_boolean[i] = tmp_chosen_role_boolean[i];
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
