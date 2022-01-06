package com.example.excelimportalasproject.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.excelimportalasproject.MainScreenActivity;
import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.adapters.AdminMainAdapter;
import com.example.excelimportalasproject.data.DatabaseHelper;
import com.example.excelimportalasproject.data.Users;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AdminMainActivity extends AppCompatActivity {

    private final String LOG_TITLE = "AdminMainActivity";

    DatabaseHelper dh;
    RecyclerView recycler;
    AdminMainAdapter adapter;
    ArrayList<Users> users_data_list;
    FloatingActionButton new_user_open_button;
    ImageView back_button;
    EditText search_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        dh = new DatabaseHelper(this, this);

        recycler = findViewById(R.id.admin_main_recycler_gui);
        users_data_list = new ArrayList<>();
        new_user_open_button = findViewById(R.id.admin_main_new_user_button_gui);
        back_button = findViewById(R.id.admin_main_step_back);
        search_bar = findViewById(R.id.admin_main_search_gui);

        getDatas("SELECT * FROM " + dh.USERS + ";");

        adapter = new AdminMainAdapter(this, this, users_data_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);

        new_user_open_button.setOnClickListener(v -> {
            finish();
            Intent add_user_screen = new Intent(AdminMainActivity.this, AdminNewUserActivity.class);
            startActivity(add_user_screen);
        });

        back_button.setOnClickListener(v -> {
            finish();
            Intent main_screen = new Intent(AdminMainActivity.this, MainScreenActivity.class);
            startActivity(main_screen);
        });

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            recreate();
        }
    }

    private void getDatas(String select) {
        Connection con = dh.connectionClass(this);

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                String name, email, password, county;
                int id, role_id, status;

                while(rs.next()) {

                    id = Integer.parseInt(rs.getString(dh.USER_ID_INDEX));
                    name = rs.getString(dh.USER_NAME_INDEX);
                    email = rs.getString(dh.USER_EMAIL_INDEX);
                    password = rs.getString(dh.USER_PASSWORD_INDEX);
                    county = rs.getString(dh.USER_COUNTY_INDEX);
                    role_id = Integer.parseInt(rs.getString(dh.USER_ROLE_INDEX));
                    status = Integer.parseInt(rs.getString(dh.USER_STATUS_INDEX));

                    users_data_list.add(new Users(id, name, email, password, county, role_id, status));
                }
            }

            Log.i(LOG_TITLE, "Adatbázis lekérdezés sikeres. (" + select + ")");


        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.e(LOG_TITLE, "Sikertelen adatbázis lekérdezés");
        }
    }
}