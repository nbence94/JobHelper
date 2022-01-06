package com.example.excelimportalasproject.job;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.excelimportalasproject.MainScreenActivity;
import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.adapters.CircleAdapter;
import com.example.excelimportalasproject.data.Circles;
import com.example.excelimportalasproject.data.DatabaseHelper;
import com.example.excelimportalasproject.data.SaveLocalDatas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class JobActivity extends AppCompatActivity {

    private final String LOG_TITLE = "JobActivity";
    CircleAdapter adapter;
    RecyclerView recycler;
    EditText search_bar;
    ImageView back_button;
    DatabaseHelper dh;
    SaveLocalDatas sld;
    ArrayList<Circles> circles_data_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);

        //Bekötjük a GUI elemeket
        recycler = findViewById(R.id.jobs_recycler_gui);
        search_bar = findViewById(R.id.jobs_search_gui);
        back_button = findViewById(R.id.jobs_main_step_back);
        circles_data_list = new ArrayList<>();

        //Adatbázisból le kell szedni a Circles táblából a dolgokat
        getDatasFromDatabase("SELECT * FROM "+ dh.CIRCLES + " WHERE UserID = " + sld.loadUserID() + " ORDER BY ID DESC;");

        //Adapter
        adapter = new CircleAdapter(this, this, circles_data_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);

        //Search bar működtetése
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

        //Vissza gomb
        back_button.setOnClickListener(v -> {
            finish();
            Intent main_screen = new Intent(JobActivity.this, MainScreenActivity.class);
            startActivity(main_screen);
        });

    }

    private void getDatasFromDatabase(String select) {
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

            }
        } catch (SQLException throwables) {

            throwables.printStackTrace();
            Toast.makeText(this, "Adatbázis hiba", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TITLE, "Adatbázis hiba");

        }
        Log.i(LOG_TITLE, "Adatok lekérdezve adatbázisból. (" + select + ")");
    }

    public boolean checkStatuses(String select) {
        Connection con = dh.connectionClass(this);

        try {
            if(con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                int status;
                while(rs.next()) {
                    status = Integer.parseInt(rs.getString(1));
                    if(status == 0) return false;
                }

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Toast.makeText(this, "Adatbázis hiba", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TITLE, "Adatbázis hiba");
        }
        return true;
    }


}