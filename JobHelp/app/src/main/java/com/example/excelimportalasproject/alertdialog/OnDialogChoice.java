package com.example.excelimportalasproject.alertdialog;

import androidx.annotation.NonNull;

import com.example.excelimportalasproject.adapters.CircleAdapter;

public interface OnDialogChoice {
    public void OnPositiveClick(@NonNull CircleAdapter.ViewHolder holder, int position, int status);
    public void OnNegativeClick(@NonNull CircleAdapter.ViewHolder holder, int position, int status);
}
