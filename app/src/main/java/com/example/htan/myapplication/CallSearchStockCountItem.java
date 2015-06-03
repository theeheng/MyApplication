package com.example.htan.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by htan on 22/01/2015.
 */
public class CallSearchStockCountItem extends AsyncTask<String,  String,  List<StockCountItem>> {
    private Context mContext;
    private Double quantityVoice;
    private Boolean updateVoice;

    public CallSearchStockCountItem(Context c) {
        this.mContext = c;
    }

    @Override
    protected List<StockCountItem> doInBackground(String... params) {

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
                if(params[0].equals(HomeActivity.SearchType.SearchByBarcode.toString())) {
                    List<StockCountItem> items = db.getStockCountItemByBarcode(params[1], params[2]);

                    return items;
                }
                else if (params[0].equals(HomeActivity.SearchType.SearchByName.toString()))
                {
                    List<StockCountItem> items = db.getStockCountItemByItemName(params[1]);

                    if(params[2] != null) {

                        try {
                            quantityVoice = Double.parseDouble(params[2]);
                        }catch (Exception ex) {

                        }
                    }

                    if(params[2] != null) {
                        try {
                            updateVoice = Boolean.parseBoolean(params[3]);
                        }catch (Exception ex) {

                        }
                    }

                    return items;
                }
                else if (params[0].equals(HomeActivity.SearchType.SearchBySiteItemId.toString()))
                {
                    List<StockCountItem> items = db.getStockCountItemBySiteItemId(params[1]);

                    return items;
                }

            } else {
                // error opening DB.
            }
        }
        catch(Exception ex) {

        }

        return null;
    }

    protected void onPostExecute(List<StockCountItem> result) {

        if(result != null && !result.isEmpty()) {
            Intent intent = new Intent(this.mContext, StockCountSwipeActivity.class);

            ArrayList<StockCountItem> resultArray = new ArrayList<StockCountItem>();
            resultArray.addAll(result);

            Bundle b = new Bundle();
            b.putSerializable(StockCountActivity.EXTRA_STOCKCOUNTITEM,resultArray);
            intent.putExtra(StockCountActivity.EXTRA_STOCKCOUNTITEM, b);

            if(quantityVoice != null) {
                intent.putExtra(StockCountActivity.EXTRA_STOCKCOUNTITEMQTY, quantityVoice);
            }

            if(updateVoice != null) {
                intent.putExtra(StockCountActivity.EXTRA_STOCKCOUNTITEMUPDATE, updateVoice);
            }

            this.mContext.startActivity(intent);

        }
        else
        {
            Toast.makeText(this.mContext, "FAILED To Find Item..........",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
