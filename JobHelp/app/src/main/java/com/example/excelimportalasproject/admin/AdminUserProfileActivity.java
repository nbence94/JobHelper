package com.example.excelimportalasproject.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.adapters.AdminUserCirclesAdapter;
import com.example.excelimportalasproject.data.Circles;
import com.example.excelimportalasproject.data.DatabaseHelper;
import com.example.excelimportalasproject.data.SaveLocalDatas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AdminUserProfileActivity extends AppCompatActivity {

    private final String LOG_TITLE = "AdminUserProfileActivity";
    DatabaseHelper dh;
    SaveLocalDatas sld;
    TextView name_textview, email_textview, county_textview, status_textview;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch status_switch;
    RecyclerView details_recyclerview;
    ImageView back_button, settings_button;
    int status, chosen_user_id, chosen_user_roleid, user_countyid;
    String user_name, user_email;
    String status_text;
    ArrayList<Circles> circles_data_list;
    AdminUserCirclesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_profile);

        //Osztály(ok) inicializálása
        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);

        //GUI elemek inicializálása
        name_textview = findViewById(R.id.admin_profile_name);
        email_textview = findViewById(R.id.admin_profile_email);
        county_textview = findViewById(R.id.admin_profile_county);
        status_switch = findViewById(R.id.admin_status_switch_gui);
        status_textview = findViewById(R.id.admin_status_title);
        details_recyclerview = findViewById(R.id.admin_user_circles_recycler_gui);
        back_button = findViewById(R.id.admin_user_details_step_back);
        settings_button = findViewById(R.id.user_settings_button);

        //Adatok átvétele a korábbi Activityből
        getDataFromIntent();

        //Felhasználó munkáinak betöltése
        circles_data_list = new ArrayList<>();
        getUserJobs("SELECT * FROM " + dh.CIRCLES + " WHERE UserID = " + chosen_user_id + " ORDER BY import_date DESC;");

        adapter = new AdminUserCirclesAdapter(this, this, circles_data_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        details_recyclerview.setLayoutManager(manager);
        details_recyclerview.setAdapter(adapter);

        back_button.setOnClickListener(v -> {
            finish();
            Intent admin_main_screen = new Intent(AdminUserProfileActivity.this, AdminMainActivity.class);
            startActivity(admin_main_screen);
        });

        settings_button.setOnClickListener(v -> {
            finish();
            Intent admin_useredit_screen = new Intent(AdminUserProfileActivity.this, AdminUserEditActivity.class);
            admin_useredit_screen.putExtra("user_id", chosen_user_id);
            admin_useredit_screen.putExtra("user_name", user_name);
            admin_useredit_screen.putExtra("user_email", user_email);
            admin_useredit_screen.putExtra("user_roleid", chosen_user_roleid);
            admin_useredit_screen.putExtra("user_county", user_countyid);
            startActivity(admin_useredit_screen);
        });

        status_switch.setOnClickListener(v -> {
            if(chosen_user_roleid == 1) {
                Toast.makeText(this, "Admin felhasználót nem lehet inaktiválni!", Toast.LENGTH_SHORT).show();
                status_switch.setChecked(true);
            }
            else if(chosen_user_id == sld.loadUserID()) {
                Toast.makeText(this, "Saját magad nem inaktiválhatod!", Toast.LENGTH_SHORT).show();
                status_switch.setChecked(true);
            } else {

                if (status_switch.isChecked()) {
                    status = 1;
                    status_text = "Állapot: aktív";
                } else {
                    status = 0;
                    status_text = "Állapot: inaktív";
                }
                if (!dh.editData("UPDATE " + dh.USERS + " SET Status = " + status + " WHERE ID = " + chosen_user_id + ";"))
                    return;
                status_textview.setText(status_text);
                Log.i(LOG_TITLE, "Státuszállítás");
            }
        });
    }

    private void getDataFromIntent() {
        if(getIntent().hasExtra("user_id")) {
            chosen_user_id = getIntent().getIntExtra("user_id", -1);
            getUserDatas(chosen_user_id);
        }
    }

    private void getUserDatas(int id) {
        Connection con = dh.connectionClass(this);

        try {
            if(con != null) {
                String select = "SELECT * FROM Users u, Counties c WHERE u.ID = " + id + " AND u.CountyID = c.ID;";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                while(rs.next()) {
                    user_name = rs.getString(dh.USER_NAME_INDEX);
                    user_email = rs.getString(dh.USER_EMAIL_INDEX);
                    user_countyid = Integer.parseInt(rs.getString(dh.USER_COUNTY_INDEX));

                    name_textview.setText(user_name);
                    email_textview.setText(user_email);
                    county_textview.setText(rs.getString(9));
                    chosen_user_roleid = Integer.parseInt(rs.getString(dh.USER_ROLE_INDEX));

                    status = Integer.parseInt(rs.getString(dh.USER_STATUS_INDEX));
                    if(status == 0) {
                        status_text = "Állapot: inaktív";
                        status_switch.setChecked(false);
                    } else {
                        status_text = "Állapot: aktív";
                        status_switch.setChecked(true);
                    }
                    status_textview.setText(status_text);
                }

                Log.i(LOG_TITLE, "Adatbázis lekérdezés sikeres");

            }
        } catch (SQLException throwables) {

            throwables.printStackTrace();
            Toast.makeText(this, "Adatbázis hiba", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TITLE, "Adatbázis hiba");

        }
    }

    private void getUserJobs(String select) {
        Connection con = dh.connectionClass(this);

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                int id, status, userid;
                String filename, import_date, done_date;

                while(rs.next()) {

                    id = Integer.parseInt(rs.getString(dh.CIRCLES_ID_INDEX));
                    status = Integer.parseInt(rs.getString(dh.CIRCLES_STATUS_INDEX));
                    userid = Integer.parseInt(rs.getString(dh.CIRCLES_USER_ID_INDEX));
                    filename = rs.getString(dh.CIRCLES_FILENAME_INDEX);
                    import_date = rs.getString(dh.CIRCLES_IMPORT_DATE_INDEX);
                    done_date = rs.getString(dh.CIRCLES_DONE_DATE_INDEX);

                    circles_data_list.add(new Circles(id, filename, import_date, done_date, status, userid));
                }
                Log.i(LOG_TITLE, "Adatbázis lekérdezés sikeres");

            }
        } catch (SQLException throwables) {

            throwables.printStackTrace();
            Toast.makeText(this, "Adatbázis hiba", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TITLE, "Adatbázis hiba");

        }
    }
}