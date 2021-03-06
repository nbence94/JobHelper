package com.example.excelimportalasproject.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.alertdialog.MyAlertDialog;
import java.util.Arrays;


public class SingleSelectAdapter extends RecyclerView.Adapter<SingleSelectAdapter.ViewHolder> {
    LayoutInflater inflater;
    Context context;
    Activity activity;
    String[] items;
    public boolean[] selected_item;
    MyAlertDialog mad;

    public SingleSelectAdapter(Context context, Activity activity, String[] items, boolean[] selected_item) {
        this.context = context;
        this.activity = activity;
        this.inflater = LayoutInflater.from(context);
        this.items = items;
        this.selected_item = selected_item;
        this.mad = new MyAlertDialog(context, activity, items.length);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_single_elements_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.radio_button.setText(items[position]);

        holder.radio_button.setChecked(selected_item[position]);

        holder.radio_button.setOnClickListener(v -> {
            Arrays.fill(selected_item, false);
            selected_item[position] = holder.radio_button.isChecked();
            for(int i = 0; i < selected_item.length; i++) holder.radio_button.setChecked(selected_item[position]);

            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                notifyDataSetChanged();
            }, 200);
        });
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RadioButton radio_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            radio_button = itemView.findViewById(R.id.item);
        }
    }

}
