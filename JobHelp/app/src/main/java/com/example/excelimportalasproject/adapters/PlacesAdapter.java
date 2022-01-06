package com.example.excelimportalasproject.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.alertdialog.MyAlertDialog;
import com.example.excelimportalasproject.data.DatabaseHelper;
import com.example.excelimportalasproject.data.Places;
import com.example.excelimportalasproject.job.JobDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> implements Filterable {

    LayoutInflater inflater;
    ArrayList<Places> places_data_list;
    Context context;
    JobDetailsActivity jda;
    DatabaseHelper dh;
    int circle_status;
    MyAlertDialog mad;

    private final ArrayList<Places> search_data_list;
    ArrayList<Places> result_data_list;

    public PlacesAdapter(Context context, Activity activity, ArrayList<Places> data_list, int status) {
        dh = new DatabaseHelper(context, activity);
        this.mad = new MyAlertDialog(context, activity);

        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.jda = (JobDetailsActivity) context;
        this.circle_status = status;

        this.places_data_list = data_list;
        this.search_data_list = new ArrayList<>(places_data_list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_billboard_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Adatok megjelenítése
        holder.city_text.setText(places_data_list.get(position).getCity());
        String billboard_code = " - " + places_data_list.get(position).getBillboard_code() + " - ";
        holder.billboard_code_text.setText(billboard_code);
        holder.size_text.setText(places_data_list.get(position).getSize());
        holder.campaign_text.setText(places_data_list.get(position).getCampaign());
        holder.address_text.setText(places_data_list.get(position).getAddress());
        holder.done_date_text.setText(places_data_list.get(position).getDone_date());

        //Dátum megjelenítése
        if(places_data_list.get(position).getDone_date() != null) {
            String[] done_date_value = places_data_list.get(position).getDone_date().split("\\.");
            holder.done_date_text.setText(done_date_value[0]);
        } else {
            holder.done_date_text.setText("");
        }

        //Státusz megjelenítése
        int current_status = places_data_list.get(position).getStatus();
        holder.place_status.setChecked(current_status != 0);
        if(circle_status == 1) holder.place_status.setEnabled(false);

        //Minden adat megtekintése
        holder.item.setOnLongClickListener(v -> {

            int bill_code = places_data_list.get(position).getBillboard_code();
            String city = places_data_list.get(position).getCity();
            String size = places_data_list.get(position).getSize();
            String campaign = places_data_list.get(position).getCampaign();
            String address = places_data_list.get(position).getAddress();
            String date = places_data_list.get(position).getDone_date();

            if(date == null) date = "-";

            StringBuilder message = new StringBuilder();
            message.append("Város: ").append(city).append("\n")
                    .append("Cím: ").append(address).append("\n\n")
                    .append("Kampány: ").append(campaign).append("\n")
                    .append("Méret: ").append(size).append("\n")
                    .append("Kód: ").append(bill_code).append("\n\n");

            if(holder.place_status.isChecked()) message.append("Befejezve: ").append(date);

            //AlertDialogHelper.setMessage(context, "További adatok", message.toString());
            mad.InfoDialog("További adatok", message.toString(), "Rendben");
            return true;
        });

        //Státuszállítás
        holder.place_status.setOnClickListener(v -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String aktualis_datumido = sdf.format(new Date());

            int place_id = places_data_list.get(position).getId();
            if(holder.place_status.isChecked()) {
                if(!dh.editData("UPDATE " + dh.PLACES + " SET Status = " + 1 + ", done_date='" + aktualis_datumido + "' WHERE ID = " + place_id)) return;
                places_data_list.get(position).setDone_date(aktualis_datumido);
                places_data_list.get(position).setStatus(1);
            } else {
                if(!dh.editData("UPDATE " + dh.PLACES + " SET Status = " + 0 + ", done_date='" + aktualis_datumido + "' WHERE ID = " + place_id)) return;
                places_data_list.get(position).setStatus(0);
            }

            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                if(jda.vision_status) getFilter().filter(String.valueOf(0));
                notifyDataSetChanged();
            }, 800);


        });
    }

    @Override
    public int getItemCount() {
        return places_data_list.size();
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
                    if(String.valueOf(search_data_list.get(i).getStatus()).equals("0")) {
                        result_data_list.add(search_data_list.get(i));
                    }
                }
            }
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            places_data_list.clear();
            places_data_list.addAll(result_data_list);
            jda.setNumber(places_data_list.size());
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView billboard_code_text, size_text, campaign_text, address_text, done_date_text, city_text;
        CheckBox place_status;
        CardView item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            billboard_code_text = itemView.findViewById(R.id.billboard_code_gui);
            size_text = itemView.findViewById(R.id.billboard_size_gui);
            city_text = itemView.findViewById(R.id.billboard_city_gui);
            campaign_text = itemView.findViewById(R.id.billboard_campaign_name_gui);
            address_text = itemView.findViewById(R.id.billboard_address_gui);
            done_date_text = itemView.findViewById(R.id.billboard_done_date_gui);
            place_status = itemView.findViewById(R.id.billboard_status_check_gui);
            item = itemView.findViewById(R.id.billboard_item);

        }
    }
}
