package com.example.excelimportalasproject.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.excelimportalasproject.MainScreenActivity;
import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.data.SaveLocalDatas;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProfileActivity extends AppCompatActivity {

    SaveLocalDatas sld;

    TextView name_textview, email_textview, county_textview, role_textview;
    FloatingActionButton profile_edit_button;
    ImageView back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sld = new SaveLocalDatas(this);

        name_textview = findViewById(R.id.profile_name_gui);
        email_textview = findViewById(R.id.profile_email_gui);
        county_textview = findViewById(R.id.profile_county_gui);
        role_textview = findViewById(R.id.profile_department_gui);
        profile_edit_button = findViewById(R.id.profile_edit_button_gui);
        back_button = findViewById(R.id.profile_step_back);

        name_textview.setText(sld.loadUserName());
        role_textview.setText(sld.loadUserRole());
        email_textview.setText(sld.loadUserEmail());
        county_textview.setText(sld.loadUserCounty());

        profile_edit_button.setOnClickListener(v -> {
            Intent profile_edit = new Intent(ProfileActivity.this, ProfileEditActivity.class);
            startActivity(profile_edit);
            finish();
        });

        back_button.setOnClickListener(v -> {
            Intent main_screen = new Intent(ProfileActivity.this, MainScreenActivity.class);
            startActivity(main_screen);
            finish();
        });
    }

}