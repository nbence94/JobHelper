package com.example.excelimportalasproject.reading;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.excelimportalasproject.alertdialog.MyAlertDialog;
import com.example.excelimportalasproject.data.DatabaseHelper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class ReadExcel {

    Context context;
    DatabaseHelper dh;
    Workbook workbook;
    MyAlertDialog mad;

    public ReadExcel(Context context, Activity activity) {
        this.context = context;
        dh = new DatabaseHelper(context, activity);
        mad = new MyAlertDialog(context, activity);
    }

    public void readExcel(File file, int user_id) {
        SimpleDateFormat sdf;
        WorkbookSettings ws = new WorkbookSettings();
        ws.setGCDisabled(true);

        //Ha talált fájlt
        if (file != null) {
            try {

                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                workbook = Workbook.getWorkbook(file);
                Sheet sheet = workbook.getSheet(0);

                //Circle adatok
                String[] separated_file_name = file.getName().split("\\.");
                String file_name = separated_file_name[0];
                String import_date = sdf.format(new Date());

                //Circles feltöltése
                if (!dh.insertData("INSERT INTO " + dh.CIRCLES + " VALUES ('" + file_name + "', '" + import_date + "', null, 0, " + user_id + ");")) {
                    Toast.makeText(context, "Adatbázis hiba történt", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Kiolvasandó adatok deklarálása
                String city;
                String address;
                String size;
                String campaign;
                String billboard_code;
                int status = 0;
                int circle_id = getNewID("SELECT MAX(ID) FROM " + dh.CIRCLES + ";");

                for (int i = 1; i < sheet.getRows(); i++) {
                    Cell[] column_cell = sheet.getRow(i);

                    city = column_cell[1].getContents();
                    if (city.equals("") || !isNumeric(column_cell[3].getContents())) continue;
                    address = column_cell[4].getContents();
                    Log.e("Teszt", column_cell[4].getContents());
                    size = column_cell[6].getContents();
                    campaign = column_cell[7].getContents();
                    billboard_code = column_cell[3].getContents();
                    address = new String(address.getBytes("8859_1"), StandardCharsets.UTF_8);

                    if (!dh.insertData("INSERT INTO " + dh.PLACES + " VALUES ('" + city + "', '"
                            + address + "', '"
                            + size + "', '"
                            + campaign + "', "
                            + billboard_code + ", "
                            + status + ", "
                            + circle_id + ", null);")) {
                        Toast.makeText(context, "Sikertelen feltöltés", Toast.LENGTH_SHORT).show();
                        dh.deleteData(dh.PLACES, "CircleID", circle_id);
                        dh.deleteData(dh.CIRCLES, "ID", circle_id);
                        return;
                    }
                }

                //Toast.makeText(context, "Adatok feltöltve", Toast.LENGTH_SHORT).show();
                mad.AlertSuccessDialog("Sikeres feltöltés", "A " + file_name + " nevű fájl feltöltése sikeres!", "Rendben");
            } catch (IOException e) {
                e.printStackTrace();
                mad.AlertErrorDialog("Hiba", "Nem sikerült elérni a fájlt!", "Rendben");
            } catch (BiffException e) {
                e.printStackTrace();
                //Toast.makeText(context, "A formátum nem megfelelő", Toast.LENGTH_SHORT).show();
                mad.AlertErrorDialog("Hiba", "A fájl formátuma nem megfelelő!", "Rendben");
            }
        }
    }

    private static boolean isNumeric(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private int getNewID(String select) {
        Connection con = dh.connectionClass(context);

        try {
            if (con != null) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(select);

                while (rs.next()) {
                    return Integer.parseInt(rs.getString(1));
                }

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return -1;
    }
}
