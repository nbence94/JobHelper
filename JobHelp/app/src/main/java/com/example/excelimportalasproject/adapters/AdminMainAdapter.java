package com.example.excelimportalasproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.admin.AdminUserProfileActivity;
import com.example.excelimportalasproject.data.Users;

import java.util.ArrayList;

public class AdminMainAdapter extends RecyclerView.Adapter<AdminMainAdapter.ViewHolder> implements Filterable {

    LayoutInflater inflater;
    ArrayList<Users> users_data_list;
    private final ArrayList<Users> search_data_list;
    ArrayList<Users> result_data_list;
    Context context;
    Activity activity;

    public AdminMainAdapter(Context context, Activity activity, ArrayList<Users> users_data) {
        this.inflater = LayoutInflater.from(context);
        this.users_data_list = users_data;
        this.context = context;
        this.activity = activity;

        this.search_data_list = new ArrayList<>(users_data_list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_admin_userrecycler_gui, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.user_name.setText(users_data_list.get(position).getName());

        holder.item.setOnClickListener(v -> {
            activity.finish();
            Intent admin_user_details_screen = new Intent(context, AdminUserProfileActivity.class);
            admin_user_details_screen.putExtra("user_id", users_data_list.get(position).getId());
            activity.startActivityForResult(admin_user_details_screen, 1);
        });
    }

    @Override
    public int getItemCount() {
        return users_data_list.size();
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
                    if(search_data_list.get(i).getName().toLowerCase().contains(searchField.toString().toLowerCase())) {
                        result_data_list.add(search_data_list.get(i));
                    }
                }
            }
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            users_data_list.clear();
            users_data_list.addAll(result_data_list);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView user_name;
        CardView item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.custom_user_name);
            item = itemView.findViewById(R.id.admin_main_item_id);
        }
    }


}
