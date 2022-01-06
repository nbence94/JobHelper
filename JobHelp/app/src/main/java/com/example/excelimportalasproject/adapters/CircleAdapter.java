package com.example.excelimportalasproject.adapters;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.excelimportalasproject.alertdialog.MyAlertDialog;
import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.alertdialog.OnDialogChoice;
import com.example.excelimportalasproject.data.Circles;
import com.example.excelimportalasproject.data.DatabaseHelper;
import com.example.excelimportalasproject.job.JobActivity;
import com.example.excelimportalasproject.job.JobDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CircleAdapter extends RecyclerView.Adapter<CircleAdapter.ViewHolder> implements Filterable, OnDialogChoice {

    private final String LOG_TITLE = "CircleAdapter";

    DatabaseHelper dh;
    LayoutInflater inflater;
    Context context;
    Activity activity;
    MyAlertDialog mad;
    JobActivity ja;
    ArrayList<Circles> circles_data_list;
    private final ArrayList<Circles> search_data_list;
    ArrayList<Circles> result_data_list;

    //Dolgok
    int circle_id;
    int pos;

    public CircleAdapter(Context context, Activity activity, ArrayList<Circles> circles_list) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
        this.mad = new MyAlertDialog(context, activity);

        this.ja = (JobActivity) context;
        this.dh = new DatabaseHelper(context, activity);

        this.circles_data_list = circles_list;
        this.search_data_list = new ArrayList<>(circles_data_list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_circle_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.circle_file_name.setText(circles_data_list.get(position).getFile_name());

        //Dátumok megjelenítése
        String[] import_date_value = circles_data_list.get(position).getImport_date().split("\\.");
        holder.import_date.setText(import_date_value[0]);

        if(circles_data_list.get(position).getDone_date() != null) {
            String[] done_date_value = circles_data_list.get(position).getDone_date().split("\\.");
            holder.done_date.setText(done_date_value[0]);
        } else {
            holder.done_date.setText("");
        }

        //Státusz beállítása
        int circle_status = circles_data_list.get(position).getStatus();
        holder.circle_status.setChecked(circle_status != 0);
        holder.circle_status.setEnabled(circle_status == 0);

        //Munka megnyitása, szükséges adatok átadása
        holder.item.setOnClickListener(v -> {
            activity.finish();
            Intent intent = new Intent(activity, JobDetailsActivity.class);
            intent.putExtra("circle_id", circles_data_list.get(position).getId());
            intent.putExtra("circle_status", circles_data_list.get(position).getStatus());
            activity.startActivityForResult(intent, 1);
        });

        holder.circle_status.setOnClickListener(v -> {
            pos = position;
            circle_id = circles_data_list.get(position).getId();

            if(ja.checkStatuses("SELECT Status FROM " + dh.PLACES + " WHERE CircleID = " + circle_id)) {
                mad.AlertWarningDialog("Megerősítés", "Biztosan lezárod a munkát? Ha lezárod, már nem tudod módosítani!", "Rendben","Mégse", holder, position, 1,this);
            } else {
                mad.AlertWarningDialog("Megerősítés", "A munkához lezáratlan helyek tartoznak. Lezárásával minden hely lezárásra kerül! Biztos lezárod?", "Rendben","Mégse", holder, position, 2,this);
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(this::notifyDataSetChanged, 800);
    }

    private void setCircleStatus(int position, int circle_id, View item) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String aktualis_datumido = sdf.format(new Date());

        if(!dh.editData("UPDATE " + dh.CIRCLES + " SET Status = " + 1 + ", Finish_date='" + aktualis_datumido + "' WHERE ID = " + circle_id)) return;

        circles_data_list.get(position).setDone_date(aktualis_datumido);
        circles_data_list.get(position).setStatus(1);
        Log.e(LOG_TITLE, "A " + circle_id + " azonosítóval rendelkező kör lezárva.");
        item.setEnabled(false);
    }

    private void setPlacesStatus(int circle_id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String aktualis_datumido = sdf.format(new Date());
        if(!dh.editData("UPDATE " + dh.PLACES + " SET Status = " + 1 + ", done_date='" + aktualis_datumido + "' WHERE CircleID = " + circle_id)) return;
        Log.e(LOG_TITLE, "A " + circle_id + " azonosítójú helyek lezárva.");
    }


    @Override
    public int getItemCount() {
        return circles_data_list.size();
    }

    @Override
    public Filter getFilter() {
        return searching;
    }

    Filter searching = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence searchField) {
            result_data_list = new ArrayList<>();

            if(searchField.toString().isEmpty()) {
                result_data_list.addAll(search_data_list);
            } else {
                for(int i = 0; i < search_data_list.size(); i++) {
                    if(search_data_list.get(i).getFile_name().toLowerCase().contains(searchField.toString().toLowerCase())) {
                        result_data_list.add(search_data_list.get(i));
                    }
                }
            }
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            circles_data_list.clear();
            circles_data_list.addAll(result_data_list);
            notifyDataSetChanged();
        }
    };

    @Override
    public void OnPositiveClick(@NonNull ViewHolder holder, int position, int status) {
        switch (status) {
            case 1: setCircleStatus(position, circle_id, holder.circle_status); break;
            case 2: setCircleStatus(position, circle_id, holder.circle_status);
                setPlacesStatus(circle_id);
                break;
        }
    }

    @Override
    public void OnNegativeClick(@NonNull ViewHolder holder, int position, int status) {
        holder.circle_status.setChecked(false);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView circle_file_name, import_date, done_date;
        CheckBox circle_status;
        CardView item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circle_file_name = itemView.findViewById(R.id.circle_title_gui);
            import_date = itemView.findViewById(R.id.circle_import_date);
            done_date = itemView.findViewById(R.id.circle_done_date);
            circle_status = itemView.findViewById(R.id.circle_status_check_gui);
            item = itemView.findViewById(R.id.circle_item);

        }
    }
}
