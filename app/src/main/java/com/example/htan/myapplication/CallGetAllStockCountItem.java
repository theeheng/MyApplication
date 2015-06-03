package com.example.htan.myapplication;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by htan on 27/01/2015.
 */
public class CallGetAllStockCountItem extends AsyncTask<String,  String,  List<StockCountDisplay>> {

    private Context mContext;
    private String action;
    private MyNotificationHelper mNotificationHelper;
    private int startId;
    private Service uploadStockService;

    public CallGetAllStockCountItem(Context c, MyNotificationHelper notificationHelper, int serviceId, Service uploadService) {
        this.mContext = c;
        this.mNotificationHelper = notificationHelper;
        this.startId = serviceId;
        this.uploadStockService = uploadService;
    }
    private String accessToken;
    private int siteId;
    public int stockCounter;

    @Override
    protected List<StockCountDisplay> doInBackground(String... params) {

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
                List<StockCountDisplay> items;

                action = params[0];
                accessToken = params[1];
                siteId = Integer.parseInt(params[2]);

                if(action.equals("view"))
                    items = db.getStockCountDisplay(siteId);
                else
                    items = db.getStockCountUpload(siteId);

                return items;

            } else {
                // error opening DB.
            }
        } catch (Exception ex) {

        }
        return null;
    }

    protected void onPostExecute(final List<StockCountDisplay> result) {

        if(result != null && !result.isEmpty()) {

            if(action.equals("view"))
            {
                AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);

                FrameLayout progressBarHolder = (FrameLayout ) ((Activity)mContext).getWindow().getDecorView().findViewById(R.id.progressBarHolder);
                progressBarHolder.setAnimation(outAnimation);
                progressBarHolder.setVisibility(View.GONE);

                Intent intent = new Intent(this.mContext, ViewAllStockCountActivity.class);

                ArrayList<StockCountDisplay> resultArray = new ArrayList<StockCountDisplay>();
                resultArray.addAll(result);

                Bundle b = new Bundle();
                b.putSerializable(StockCountActivity.EXTRA_STOCKCOUNTITEM, resultArray);

                intent.putExtra(StockCountActivity.EXTRA_STOCKCOUNTITEM, b);

                this.mContext.startActivity(intent);
            }
            else {

               String stockCountUploadPOSTApiURL = "http://10.0.26.67/FnBModelWebAPI/api/stockcountitem/";
               final String TAG = "CallGetAllStockCountItem";

               mNotificationHelper.displayNotification(this.mContext, "Upload Stock Count", "Uploading Stock Count", 1);


                if(result != null && result.size() > 0)
                {
                    stockCounter = 1;

                    for(final StockCountDisplay scd : result)
                    {
                        JSONObject params = new JSONObject();

                        try {
                            params.put("accessToken", accessToken);
                            params.put("siteId", siteId );
                            params.put("siteItemId", scd.SiteItemId);
                            params.put("stockItemSizeId", scd.StockItemSizeId);
                            params.put("quantity",scd.CurrentCount);
                        }catch(JSONException ex)
                        {

                        }

                        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                                stockCountUploadPOSTApiURL , params , new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());

                                try {

                                    mNotificationHelper.displayNotification(mContext, "Upload Stock Count", "Uploaded Stock Count for siteitem : " + scd.SiteItemId.toString() , (stockCounter)*(100/result.size()));

                                    Toast.makeText(mContext,
                                            "Site Item Count : " + scd.SiteItemId + " successfully updated",
                                            Toast.LENGTH_SHORT).show();

                                    if(stockCounter == result.size()) {

                                        mNotificationHelper.removeNotification(mContext, "Upload Stock Count Complete", "Done");

                                        uploadStockService.stopSelfResult(startId);
                                    }

                                    stockCounter++;

                                } catch (Exception e) {
                                    e.printStackTrace();

                                    Toast.makeText(mContext,
                                            "Error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                                VolleyLog.d(TAG, "Error: " + error.getMessage());
                                Toast.makeText(mContext,
                                        error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(jsonObjReq);

                    }
                }
            }
        }
        else
        {
            Toast.makeText(this.mContext, "FAILED To Find Item..........",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

