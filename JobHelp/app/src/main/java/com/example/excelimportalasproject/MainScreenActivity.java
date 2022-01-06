package com.example.excelimportalasproject;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.excelimportalasproject.admin.AdminMainActivity;
import com.example.excelimportalasproject.alertdialog.MyAlertDialog;
import com.example.excelimportalasproject.data.DatabaseHelper;
import com.example.excelimportalasproject.data.SaveLocalDatas;
import com.example.excelimportalasproject.job.JobActivity;
import com.example.excelimportalasproject.login.LoginActivity;
import com.example.excelimportalasproject.profile.ProfileActivity;
import com.example.excelimportalasproject.reading.ReadExcel;
import com.example.excelimportalasproject.reading.RealPathUtil;

import java.io.File;

public class MainScreenActivity extends AppCompatActivity {

    DatabaseHelper dh;
    SaveLocalDatas sld;
    MyAlertDialog mad;

    CardView import_button, profil_button, works_button, admin_button, logout_button;

    ActivityResultLauncher<Intent> file_browser_listener;
    ReadExcel ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        dh = new DatabaseHelper(this, this);
        sld = new SaveLocalDatas(this);
        ref = new ReadExcel(this, this);
        mad = new MyAlertDialog(this, this);

        import_button = findViewById(R.id.import_card);
        profil_button = findViewById(R.id.profil_button);
        works_button = findViewById(R.id.works_button);
        admin_button = findViewById(R.id.admin_button);
        logout_button = findViewById(R.id.logout_button);

        //Login utáni ID
        if(sld.loadUserRoleID() != 1) {
            admin_button.setVisibility(View.GONE);
        }

        import_button.setOnClickListener(v -> {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Intent file_browsing_intent = new Intent(Intent.ACTION_GET_CONTENT);
                    file_browsing_intent.setType("*/*");
                    file_browser_listener.launch(file_browsing_intent);
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            } else {
                if (ContextCompat.checkSelfPermission(MainScreenActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainScreenActivity.this, new String[]{READ_EXTERNAL_STORAGE}, 1);
                } else {
                    Intent file_browsing_intent = new Intent(Intent.ACTION_GET_CONTENT);
                    file_browsing_intent.setType("*/*");
                    file_browser_listener.launch(file_browsing_intent);
                }
            }
        });


        file_browser_listener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent data = result.getData();
                if (result.getResultCode() == Activity.RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    Context context = MainScreenActivity.this;
                    try {
                        String path = RealPathUtil.getRealPath(context, uri);
                        File file = new File(path);
                        ref.readExcel(file, sld.loadUserID());

                    } catch (Exception e) {
                        mad.AlertErrorDialog("Hiba", e.toString(), "Rendben");
                    }
                }
            }
        );

        profil_button.setOnClickListener(v -> {
            Intent profile_screen = new Intent(MainScreenActivity.this, ProfileActivity.class);
            startActivity(profile_screen);
        });

        works_button.setOnClickListener(v -> {
            Intent works_screen = new Intent(MainScreenActivity.this, JobActivity.class);
            startActivity(works_screen);
        });

        admin_button.setOnClickListener(v -> {
            finish();
            Intent admin_screen = new Intent(MainScreenActivity.this, AdminMainActivity.class);
            startActivity(admin_screen);
        });

        logout_button.setOnClickListener(v -> {
            finish();
            sld.saveStayLoggedStatus(false);
            Intent logout_screen = new Intent(MainScreenActivity.this, LoginActivity.class);
            startActivity(logout_screen);
        });
    }



    boolean checkPermission() {
        if(SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int read_check = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
            int write_check = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
            return read_check == PackageManager.PERMISSION_GRANTED && write_check == PackageManager.PERMISSION_GRANTED;
        }
    }
}