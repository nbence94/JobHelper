package com.example.excelimportalasproject.job;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.adapters.PlacesAdapter;
import com.example.excelimportalasproject.data.DatabaseHelper;
import com.example.excelimportalasproject.data.Places;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class JobDetailsActivity extends AppCompatActivity {

    private final String LOG_TITLE = "JobDetailsActivity";

    RecyclerView recycler;
    ImageView back_button, visible_all;
    TextView number_of_elements_text;
    DatabaseHelper dh;
    ArrayList<Places> places_data_list;
    PlacesAdapter adapter;

    int circle_id, circle_status;
    public boolean vision_status = false;//0 - minden, 1 - csak aktívak

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        dh = new DatabaseHelper(this, this);

        recycler = findViewById(R.id.job_details_recycler_gui);
        back_button = findViewById(R.id.billboards_main_step_back);
        visible_all = findViewById(R.id.billboards_see_all_gui);
        number_of_elements_text = findViewById(R.id.all_billboards_text_gui);
        places_data_list = new ArrayList<>();

        getIntentData();
        getDatasFromDatabase("SELECT * FROM " + dh.PLACES + " WHERE CircleID = " + circle_id  + ";");

        setNumber(places_data_list.size());

        //Adapter
        //Meg kell jeleníteni a dolgokat
        adapter = new PlacesAdapter(this, this, places_data_list, circle_status);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);

        visible_all.setColorFilter(ContextCompat.getColor(this, android.R.color.black), PorterDuff.Mode.MULTIPLY);
        visible_all.setOnClickListener(v -> {
            if(!vision_status) {
                visible_all.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_green_light), PorterDuff.Mode.MULTIPLY);
                vision_status = true;
                adapter.getFilter().filter(String.valueOf(0));
            } else {
                visible_all.setColorFilter(ContextCompat.getColor(this, android.R.color.black), PorterDuff.Mode.MULTIPLY);
                vision_status = false;
                adapter.getFilter().filter("");
            }
        });

        //Vissza gomb
        back_button.setOnClickListener(v -> {
            finish();
            Intent main_screen = new Intent(JobDetailsActivity.this, JobActivity.class);
            startActivity(main_screen);
        });
    }

    private void getIntentData() {
        if(getIntent().hasExtra("circle_id")) {
            circle_id = getIntent().getIntExtra("circle_id", -1);
            circle_status = getIntent().getIntExtra("circle_status", -1);
        }
    }

    private void getDatasFromDatabase(String select) {
        Connection con = dh.connectionClass(this);

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                int id, billboard_code, status, circle_id;
                String city, address, size, campaign, done_date;


                while(rs.next()) {

                    id = Integer.parseInt(rs.getString(dh.PLACES_ID_INDEX));
                    city = rs.getString(dh.PLACES_CITY_INDEX);
                    address = rs.getString(dh.PLACES_ADDRESS_INDEX);
                    size = rs.getString(dh.PLACES_SIZE_INDEX);
                    campaign = rs.getString(dh.PLACES_CAMPAIGN_INDEX);
                    billboard_code = Integer.parseInt(rs.getString(dh.PLACES_BILLBOARD_CODE_INDEX));
                    status = Integer.parseInt(rs.getString(dh.PLACES_STATUS_INDEX));
                    circle_id = Integer.parseInt(rs.getString(dh.PLACES_CIRCLE_ID_INDEX));
                    done_date = rs.getString(dh.PLACES_DONE_DATE_INDEX);

                    places_data_list.add(new Places(id, city, address, size, campaign, billboard_code, status, circle_id, done_date));

                }

                Log.i(LOG_TITLE, "Adatok lekérdezve adatbázisból. (" +  select + ")");
            }

        } catch (SQLException throwables) {

            throwables.printStackTrace();
            Toast.makeText(this, "Adatbázis hiba", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TITLE, "Adatbázis hiba");

        }

    }

    public void setNumber(int number) {
        number_of_elements_text.setText(String.valueOf(number));
    }
}