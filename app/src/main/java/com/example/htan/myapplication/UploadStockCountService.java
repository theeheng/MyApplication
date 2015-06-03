package com.example.htan.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by htan on 10/02/2015.
 */
public class UploadStockCountService extends Service {


    HandlerThread mHandlerThread;
    Handler mHandler;
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

    private void doWork(Intent intent, int startId) {

        LogHelper.logThreadId("doWork");

        mNotificationHelper = new MyNotificationHelper();
        mNotificationHelper.displayNotification(getApplicationContext(), "Download Stock Count Item", "Downloading Stock Count Item", 20);

        CallGetAllStockCountItem getAllItemAsync = new CallGetAllStockCountItem(getApplicationContext(), mNotificationHelper, startId, this);
        getAllItemAsync.execute("upload", intent.getStringExtra(OrganisationActivity.EXTRA_ACCESSTOKEN), intent.getStringExtra(HomeActivity.EXTRA_SITEID));

    }

}
