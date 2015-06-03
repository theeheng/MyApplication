package com.example.htan.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by htan on 30/01/2015.
 */
public class CallResetStockCountItemForSite extends AsyncTask<List<StockCountItem>,  String,  List<StockCountItem>> {

    private Context mContext;
    private MyNotificationHelper  mNotificationHelper;
    private int startId;
    private Service downloadStockService;

    public CallResetStockCountItemForSite(Context c, MyNotificationHelper notificationHelper, int serviceId, Service stockService) {
        this.mContext = c;
        this.mNotificationHelper = notificationHelper;
        this.startId = serviceId;
        this.downloadStockService = stockService;
    }

    @Override
    protected List<StockCountItem> doInBackground(List<StockCountItem>... params) {

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

                if(db.deleteStockCountItemBySite(params[0].get(0).SiteId))
                    return params[0];

            } else {
                // error opening DB.
            }
        } catch (Exception ex) {

        }
        return null;
    }

    protected void onPostExecute(List<StockCountItem> result) {

        if(result != null && result.size() > 0) {

            Toast.makeText(this.mContext, "Clear Stock Count Item..........",
                    Toast.LENGTH_SHORT).show();

            mNotificationHelper.displayNotification(mContext, "Download Stock Count Item", "Updating Stock Count Item", 80);


            CallUpdateStockCountItem updateStkCount = new CallUpdateStockCountItem(this.mContext, mNotificationHelper, this.startId, downloadStockService);
            updateStkCount.execute(result);

        }
        else
        {
            Toast.makeText(this.mContext, "FAILED To Clear Stock Count Item..........",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
