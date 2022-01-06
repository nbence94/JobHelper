package com.example.excelimportalasproject.alertdialog;

import static com.example.excelimportalasproject.R.color.warning_color;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.excelimportalasproject.R;
import com.example.excelimportalasproject.adapters.CircleAdapter;
import com.example.excelimportalasproject.adapters.SingleSelectAdapter;

import java.util.concurrent.atomic.AtomicBoolean;

public class MyAlertDialog {

    Context context;
    Activity activity;

    //All
    Button yes_button, no_button;
    TextView dialog_title, dialog_message;
    ImageView icon;

    //SelectAlertDialog
    SingleSelectAdapter single_adapter;
    RecyclerView recycler;
    public boolean[] selected_array;

    public MyAlertDialog(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public MyAlertDialog(Context context, Activity activity, int length) {
        this.context = context;
        this.activity = activity;
        this.selected_array = new boolean[length];
    }

    public void AlertSuccessDialog(String title, String message, String button_title) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_success_dialog, activity.findViewById(R.id.layoutDialogContainer));
        builder.setView(view);

        ((TextView) view.findViewById(R.id.textTitle)).setText(title);
        ((TextView) view.findViewById(R.id.textMessage)).setText(message);
        ((Button) view.findViewById(R.id.button)).setText(button_title);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.done_icon);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.button).setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    public void InfoDialog(String title, String message, String button_title) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_info_dialog, activity.findViewById(R.id.layoutDialogContainer));
        builder.setView(view);

        this.dialog_title = view.findViewById(R.id.textTitle);
        this.dialog_message = view.findViewById(R.id.textMessage);
        this.yes_button = view.findViewById(R.id.button);
        this.icon = view.findViewById(R.id.imageIcon);

        dialog_title.setText(title);
        dialog_message.setText(message);
        dialog_message.setTextSize(20);
        yes_button.setText(button_title);
        icon.setImageResource(R.drawable.info_icon);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.button).setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    public void AlertErrorDialog(String title, String message, String button_title) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_error_dialog, activity.findViewById(R.id.layoutDialogContainer));
        builder.setView(view);

        ((TextView) view.findViewById(R.id.textTitle)).setText(title);
        ((TextView) view.findViewById(R.id.textMessage)).setText(message);
        ((Button) view.findViewById(R.id.button)).setText(button_title);
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.error_icon);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.button).setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    public void AlertWarningDialog(String title, String message, String buttonYes, String buttonNo, @NonNull CircleAdapter.ViewHolder holder, int position, int status, final OnDialogChoice listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View my_view = LayoutInflater.from(context).inflate(R.layout.layout_warning_dialog, activity.findViewById(R.id.layoutDialogContainer));
        builder.setView(my_view);

        this.yes_button = my_view.findViewById(R.id.buttonYes);
        this.no_button = my_view.findViewById(R.id.buttonNo);
        this.icon = my_view.findViewById(R.id.imageIcon);
        this.dialog_title = my_view.findViewById(R.id.textTitle);
        this.dialog_message = my_view.findViewById(R.id.textMessage);

        icon.setImageResource(R.drawable.warning_icon);
        dialog_title.setText(title);
        dialog_message.setText(message);
        yes_button.setText(buttonYes);
        no_button.setText(buttonNo);

        final AlertDialog alertDialog = builder.create();

        yes_button.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            listener.OnPositiveClick(holder, position, status);
            alertDialog.dismiss();

        });

        no_button.findViewById(R.id.buttonNo).setOnClickListener(v -> {
            listener.OnNegativeClick(holder, position, status);
            alertDialog.dismiss();

        });


        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    public void singleSelectAlertDialog(String title, String[] elements, boolean[] selected_element, boolean[] tmp_selected_element, String buttonYes, String ButtonNo, @NonNull CircleAdapter.ViewHolder holder, int position, int status, final OnDialogChoice listener) {
        this.single_adapter = new SingleSelectAdapter(context, activity, elements, selected_element);
        this.selected_array = new boolean[selected_element.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View my_view;

        if(elements.length > 10) {
            my_view = LayoutInflater.from(context).inflate(R.layout.layout_multiselect_dialog_exp, activity.findViewById(R.id.layoutDialogContainer));
        } else {
            my_view = LayoutInflater.from(context).inflate(R.layout.layout_multiselect_dialog, activity.findViewById(R.id.layoutDialogContainer));
        }

        builder.setView(my_view);
        this.recycler = my_view.findViewById(R.id.elements);
        this.yes_button = my_view.findViewById(R.id.buttonYes);
        this.no_button = my_view.findViewById(R.id.buttonNo);
        this.icon = my_view.findViewById(R.id.imageIcon);
        this.dialog_title = my_view.findViewById(R.id.textTitle);

        //Megjelenő elemek
        dialog_title.setText(title);
        yes_button.setText(buttonYes);
        no_button.setText(ButtonNo);
        icon.setImageResource(R.drawable.select_icon);

        final AlertDialog alertDialog = builder.create();

        System.arraycopy(selected_element, 0, tmp_selected_element, 0, selected_element.length);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(single_adapter);

        my_view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            for(int i = 0; i < selected_array.length; i++) {
                selected_element[i] = single_adapter.selected_item[i];
            }
            listener.OnPositiveClick(holder, position, status);
        });

        my_view.findViewById(R.id.buttonNo).setOnClickListener(v -> {
            alertDialog.dismiss();
            listener.OnNegativeClick(holder, position, status);
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }
}
