package com.example.htan.myapplication;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by htan on 09/02/2015.
 */
public class DownloadStockCountItemService extends Service {

    HandlerThread mHandlerThread;
    Handler mHandler;

    private List<StockCountItem> resultStockCountItemArray;
    private List<StockItemSize> resultStockItemSizeArray;
    private static String stockCountItemApiURL = "http://10.0.26.67/FnBModelWebAPI/api/stockcountitem/?accesstoken=%s&siteId=%s";
    private MyNotificationHelper mNotificationHelper;


    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogHelper.logThreadId("onCreate");

        mHandlerThread = new HandlerThread("MySimpleService");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Intent intent = (Intent) msg.obj;
                int startId = msg.arg1;
                doWork(intent, startId);
               // stopSelfResult(startId);

            }
        };
    }

    @Override
    public void onDestroy() {
        LogHelper.logThreadId("onDestroy");
        mHandlerThread.quit();
        mHandlerThread = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LogHelper.logThreadId("onStartCommand");

        Message msg = mHandler.obtainMessage();
        msg.obj = intent;
        msg.arg1 = startId;
        msg.sendToTarget();

        return 0;
    }

    private void doWork(Intent intent, final int startId) {

        LogHelper.logThreadId("doWork");

        mNotificationHelper = new MyNotificationHelper();
        mNotificationHelper.displayNotification(getApplicationContext(), "Download Stock Count Item", "Downloading Stock Count Item", 20);

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(
                String.format(stockCountItemApiURL,intent.getStringExtra(OrganisationActivity.EXTRA_ACCESSTOKEN), intent.getStringExtra(HomeActivity.EXTRA_SITEID)) , new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                try {

                    mNotificationHelper.displayNotification(getApplicationContext(), "Download Stock Count Item", "Processing Stock Count Item", 40);

                    // Parsing json object response
                    // response will be a json object

                    resultStockCountItemArray = new LinkedList<StockCountItem>();
                    resultStockItemSizeArray = new LinkedList<StockItemSize>();

                    for (int i = 0; i < response.length(); i++) {

                        JSONObject jsonUsrPro = (JSONObject) response.get(i);

                        StockCountItem s = new StockCountItem();
                        s.SiteItemId = Integer.parseInt(jsonUsrPro.getString("SiteItemID"));
                        s.CategoryId = Integer.parseInt(jsonUsrPro.getString("CategoryID"));
                        s.ItemName = jsonUsrPro.getString("ItemName");
                        s.CategoryName = jsonUsrPro.getString("CategoryName");
                        s.CategoryHierarchy = jsonUsrPro.getString("CategoryHierarchy");
                        s.SupplierId = Integer.parseInt(jsonUsrPro.getString("SupplierID"));
                        s.SiteId = Integer.parseInt(jsonUsrPro.getString("SiteID"));
                        s.StockItemId = Integer.parseInt(jsonUsrPro.getString("StockItemID"));
                        s.CostPrice = Double.parseDouble(jsonUsrPro.getString("CostPrice"));
                        s.Count = new LinkedList<StockCount>();

                        resultStockCountItemArray.add(s);

                        JSONArray stockItemSizes = (JSONArray) jsonUsrPro.getJSONArray("StockItemSizes");

                        for(int j=0; j < stockItemSizes.length() ; j++)
                        {
                            int stockItemSizeId = Integer.parseInt(stockItemSizes.getJSONObject(j).getString("StockItemSizeID"));
                            boolean exist = false;

                            for(StockItemSize stkSize : resultStockItemSizeArray)
                            {
                                if(stkSize.StockItemSizeId == stockItemSizeId)
                                {
                                    exist= true;
                                    break;
                                }
                            }

                            if(!exist) {
                                StockItemSize sis = new StockItemSize();
                                sis.StockItemSizeId = stockItemSizeId ;
                                sis.StockItemId = Integer.parseInt(stockItemSizes.getJSONObject(j).getString("StockItemID")) ;
                                sis.Size = Double.parseDouble(stockItemSizes.getJSONObject(j).getString("Size")) ;
                                sis.UnitOfMeasureCode = stockItemSizes.getJSONObject(j).getString("UnitOfMeasureCode");
                                // sis.UnitOfMeasureId = Integer.parseInt(stockItemSizes.getJSONObject(j).getString("Size"));
                                sis.ConversionRatio = Double.parseDouble(stockItemSizes.getJSONObject(j).getString("ConversionRatio"));
                                sis.CaseSizeDescription = stockItemSizes.getJSONObject(j).getString("CaseDescriptionID").equals("null") ? null : stockItemSizes.getJSONObject(j).getString("CaseDescriptionID") ;
                                sis.IsDefault = stockItemSizes.getJSONObject(j).getString("IsDefault").equals("true") ? true : false ;

                                String currentCount = stockItemSizes.getJSONObject(j).getString("StockCount");

                                if(!currentCount.equals("null"))
                                {
                                    StockCount cnt = new StockCount();
                                    cnt.StockItemSizeId = sis.StockItemSizeId;
                                    cnt.SiteItemId = s.SiteItemId;
                                    cnt.CurrentCount = Double.parseDouble(currentCount);
                                    cnt.Updated = false;
                                    s.Count.add(cnt);
                                }

                                resultStockItemSizeArray.add(sis);
                            }
                        }
                    }

                    resultStockCountItemArray.get(0).StockItemSizes = resultStockItemSizeArray;

                    CallResetStockCountItemDBForSite(resultStockCountItemArray, mNotificationHelper, startId);



                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    private void CallResetStockCountItemDBForSite(List<StockCountItem> stockCountItem, MyNotificationHelper mNotificationHelper, int startId) {

        mNotificationHelper.displayNotification(getApplicationContext(), "Download Stock Count Item", "Resetting Stock Count Item", 60);

        CallResetStockCountItemForSite resetDBStockCountItem = new CallResetStockCountItemForSite(getApplicationContext(), mNotificationHelper, startId, this);
        resetDBStockCountItem.execute(stockCountItem);
    }
}
