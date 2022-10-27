package com.example.onlinemcqexam;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingProgressBarDialog {
    private Activity activity;
    private AlertDialog alertDialog;

    public LoadingProgressBarDialog(Activity activity) {
        this.activity = activity;
    }

    void startProgressBarLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.circular_progressbar_dialog, null));
        builder.setCancelable(true);

        alertDialog = builder.create();
        alertDialog.show();
    }

    void dismissProgressBarDialog() {
        alertDialog.dismiss();
    }
}
