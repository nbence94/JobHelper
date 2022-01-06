package com.example.excelimportalasproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.data.Circles;

import java.util.ArrayList;

public class AdminUserCirclesAdapter extends RecyclerView.Adapter<AdminUserCirclesAdapter.ViewHolder>{

    LayoutInflater inflater;
    Context context;
    Activity activity;

    ArrayList<Circles> circles_data_list;

    public AdminUserCirclesAdapter(Context context, Activity activity, ArrayList<Circles> circles_list) {
        this.inflater = LayoutInflater.from(context);
        this.circles_data_list = circles_list;
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_circle_details_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.file_name.setText(circles_data_list.get(position).getFile_name());

        String[] import_date_value = circles_data_list.get(position).getImport_date().split("\\.");
        String import_date_text = "Fájl importálva: " + import_date_value[0];
        holder.import_date.setText(import_date_text);

        String done_date = "Befejezve: ";
        if(circles_data_list.get(position).getDone_date() != null) {
            String[] done_date_value = circles_data_list.get(position).getDone_date().split("\\.");
            done_date += done_date_value[0];
        } else {
            done_date += "-";
        }
        holder.done_date.setText(done_date);
    }

    @Override
    public int getItemCount() {
        return circles_data_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView file_name, import_date, done_date;
        CardView item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            file_name = itemView.findViewById(R.id.circle_title_gui);
            import_date = itemView.findViewById(R.id.circle_import_date);
            done_date = itemView.findViewById(R.id.circle_done_date);
            item = itemView.findViewById(R.id.circle_item);
        }
    }
}
