package com.example.htan.myapplication;

import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by htan on 30/01/2015.
 */
public class CallUpdateStockCountItem extends AsyncTask<List<StockCountItem>,  String,  String> {

    private Context mContext;
    private MyNotificationHelper mNotificationHelper;
    private int startId;
    private Service downloadStockService;

    public CallUpdateStockCountItem(Context c, MyNotificationHelper notificationHelper, int serviceId, Service stockService) {
        this.mContext = c;
        this.mNotificationHelper = notificationHelper;
        this.startId = serviceId;
        this.downloadStockService = stockService;
    }

    @Override
    protected String doInBackground(List<StockCountItem>... params) {

        DB db = new DB(mContext);

        // copy assets DB to app DB.
        try {
            db.create();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            // get all locations
            if (db.open()) {

                db.insertStockCountItem(params[0]);

                return "successful";

            } else {
                // error opening DB.
            }
        } catch (Exception ex) {

        }
        return null;
    }

    protected void onPostExecute(String result) {

        if(result != null && !result.isEmpty()) {

            Toast.makeText(this.mContext, "Update Stock Count Item Successful..........",
                    Toast.LENGTH_SHORT).show();

            mNotificationHelper.removeNotification(mContext, "Download Stock Count Item", "Done");

            this.downloadStockService.stopSelfResult(this.startId);
        }
        else
        {
            Toast.makeText(this.mContext, "FAILED To Update Stock Count Item..........",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
