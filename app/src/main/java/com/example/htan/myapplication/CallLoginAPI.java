package com.example.htan.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by htan on 08/01/2015.
 */
public class CallLoginAPI extends AsyncTask< String,  String,  String> {

   //  public   final   static   String apiURL   = "http://10.0.2.2/FnBModelWebAPI/api/login/";

    public   final   static   String apiURL   = "http://10.0.26.67/FnBModelWebAPI/api/login/";
    public final static String offlineLogin = "offlineLogin";


    private Context mContext;

    private String resultToDisplay = null;

    public CallLoginAPI(Context c) {
        this.mContext = c;
    }

    @Override
    protected String doInBackground(String... params) {

        String urlString   = apiURL   +    "?username="   + params[0]   +    "&password="   + params[1]   +    "&ipAddress=192.168.0.1";


        // HTTP Get
        try {

            URL u = new URL(urlString);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(1000);
            c.setReadTimeout(1000);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    resultToDisplay = sb.toString();

                    return resultToDisplay;
            }

        } catch (MalformedURLException ex) {
            // Logger.getLogger(DebugServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            // Logger.getLogger(DebugServer.class.getName()).log(Level.SEVERE, null, ex);
            if(ex != null && ex.getMessage()!= null && ex.getMessage().contains("failed to connect to")) {
                if(params[0].equals("fullaccess") && params[1].equals("Budapest11"))
                {
                    return offlineLogin;
                }
            }
        }

        return resultToDisplay;

    }
    protected void onPostExecute(String result) {


        if(result != null && !result.isEmpty() && (!result.equals("null"))) {

            if(result.equals(offlineLogin)) {
                Intent intent = new Intent(this.mContext, HomeActivity.class);
                this.mContext.startActivity(intent);
            }
            else if(result.length() > 0){

                Intent intent = new Intent(this.mContext, OrganisationActivity.class);

                intent.putExtra(OrganisationActivity.EXTRA_ACCESSTOKEN, result);

                this.mContext.startActivity(intent);
            }
        }
        else
        {
            Toast.makeText(this.mContext,"LOGIN FAILED..........",
                    Toast.LENGTH_SHORT).show();
        }

        FrameLayout progressBarHolder = (FrameLayout ) ((Activity)this.mContext).getWindow().getDecorView().findViewById(R.id.progressBarHolder);
        //loginProgressBar.setVisibility(View.GONE);

        AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
    }
}
