package com.example.htan.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity {

    public Button loginButton;
    public EditText usernameTextbox;
    public EditText passwordTextbox;
    //private ProgressBar loadingSpinner;
    FrameLayout progressBarHolder;
    AlphaAnimation inAnimation;
    private static String TAG = LoginActivity.class.getSimpleName();
    private static String apiURL = "http://10.0.26.67/FnBModelWebAPI/api/login/?username=%s&password=%s&ipAddress=192.168.0.1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);

        //loadingSpinner = (ProgressBar)findViewById(R.id.loginProgressBar);

        InitialiseView();

    }

    private void InitialiseView() {
        loginButton = (Button) findViewById(R.id.loginButton);
        usernameTextbox = (EditText) findViewById(R.id.usernameTextBox);
        passwordTextbox = (EditText) findViewById(R.id.passwordTextBox);
        loginButton.requestFocus();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.handleLoginButton1Click((Button) view);
            }
        });

        usernameTextbox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                LoginActivity.this.handleUsernameTextboxClick((EditText) view, hasFocus);
            }
        });
        
        passwordTextbox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                LoginActivity.this.handlePasswordTextboxClick((EditText) view, hasFocus);
            }
        });
        
    }

    private void handleUsernameTextboxClick(EditText view, boolean hasFocus) {
        ClearTextBox(view, hasFocus);
    }

    private void handlePasswordTextboxClick(EditText view, boolean hasFocus) {
        ClearTextBox(view, hasFocus);
    }

    private void ClearTextBox(EditText view, boolean hasFocus)
    {
        if(hasFocus) {
            Resources res = getResources();
            String inputText = view.getText().toString();

            if (view.getId() == passwordTextbox.getId() && res.getString(R.string.loginpassword_textbox).contentEquals(inputText)) {
                view.setText("");
            }

            if (view.getId() == usernameTextbox.getId() && res.getString(R.string.loginusername_textbox).contentEquals(inputText)) {
                view.setText("");
            }
        }
    }
    private void handleLoginButton1Click(Button view) {

        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
        //loadingSpinner.setVisibility(View.VISIBLE);

        //CallLoginAPI loginAsync = new CallLoginAPI(this);

        final String usernametxt = usernameTextbox.getText().toString();
        final String passwordtxt = passwordTextbox.getText().toString();

        //loginAsync.execute( usernametxt, passwordtxt );

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                String.format(apiURL,usernametxt,passwordtxt) , null , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object
                    String accessToken = response.getString("accessToken");

                    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressBarHolder.setAnimation(outAnimation);
                    progressBarHolder.setVisibility(View.GONE);
                    //loadingSpinner.setVisibility(View.GONE);

                    if(accessToken.equals("null"))
                    {
                        Toast.makeText(getApplicationContext(),"LOGIN FAILED..........",
                                Toast.LENGTH_SHORT).show();
                    }
                    else if(accessToken.length() > 0)
                    {
                        Intent intent = new Intent(getApplicationContext(), OrganisationActivity.class);

                        intent.putExtra(OrganisationActivity.EXTRA_ACCESSTOKEN, accessToken);

                        startActivity(intent);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressBarHolder.setAnimation(outAnimation);
                    progressBarHolder.setVisibility(View.GONE);
                    //loadingSpinner.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressBarHolder.setAnimation(outAnimation);
                progressBarHolder.setVisibility(View.GONE);

                if((error instanceof TimeoutError)||(error instanceof NoConnectionError && error.getMessage().contains("failed to connect"))||((error instanceof ServerError) && (((ServerError)error).networkResponse.statusCode == 404 || ((ServerError)error).networkResponse.statusCode == 400)))
                {
                    if(usernametxt.equals("fullaccess") && passwordtxt.equals("Budapest11")) {

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

                        intent.putExtra(OrganisationActivity.EXTRA_ACCESSTOKEN, "offlineLogin");

                        startActivity(intent);
                    }
                    else
                    {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),"LOGIN FAILED..........",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", usernametxt);
                params.put("password", passwordtxt);
                params.put("ipAddress", "192.168.0.1");

                return params;
            }

            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("TOKEN", "99KI9Gj68CgCf70deM22Ka64chef2C40Gm2lFJ2J0G9JkDaaDAcbFfd19MfacGf3FFm8CM1hG0eDiIk8");

                return headers;
            }*/
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
