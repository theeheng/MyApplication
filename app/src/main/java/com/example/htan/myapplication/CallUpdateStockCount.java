package com.example.htan.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by htan on 22/01/2015.
 */
public class CallUpdateStockCount extends AsyncTask<StockCount, String, String> {

    private Context mContext;

    public CallUpdateStockCount(Context c) {
        this.mContext = c;
    }

    @Override
    protected String doInBackground(StockCount... params) {

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

                if(params[0].operation == StockCount.DBOperation.Insert)
                    db.insertStockCount(params[0]);

                if(params[0].operation == StockCount.DBOperation.Update)
                    db.updateStockCount(params[0]);

                return "successfull";

            } else {
                // error opening DB.
            }
        }
        catch(Exception ex) {

        }

        return null;
    }

    protected void onPostExecute(String result) {

        if(result != null && !result.isEmpty()) {
            Toast.makeText(this.mContext, "Successfully updated Stock Count..........",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this.mContext, "FAILED To Update Stock Count..........",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

