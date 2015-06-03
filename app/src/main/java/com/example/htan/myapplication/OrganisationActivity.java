package com.example.htan.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class OrganisationActivity extends Activity {

    public final static String EXTRA_ACCESSTOKEN ="com.example.webapitutorial.ACCESSTOKEN ";
    public final static String EXTRA_SITE ="com.example.webapitutorial.SITE ";
    private static String TAG = OrganisationActivity.class.getSimpleName();
    private static String organisationApiURL = "http://10.0.26.67/FnBModelWebAPI/api/organisation/?accessToken=%s";
    private static String profileApiURL = "http://10.0.26.67/FnBModelWebAPI/api/profile/?accessToken=%s&uniqueOrganisationId=%s";
    private static String profilePOSTApiURL = "http://10.0.26.67/FnBModelWebAPI/api/profile/";
    private static String siteApiURL = "http://10.0.26.67/FnBModelWebAPI/api/site/?accesstoken=%s";


    public Spinner organisationSpinner;
    public Spinner profileSpinner;
    public Button nextButton;
    private List<Organisation> resultOrganisationArray;
    private List<UserProfile> resultUserProfileArray;
    private List<Site> resultSiteArray;
    private String accessToken;

    FrameLayout progressBarHolder;
    AlphaAnimation inAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organisation);

        Intent intent   = getIntent();
        this.accessToken   = intent.getStringExtra(OrganisationActivity.EXTRA_ACCESSTOKEN);
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);

       organisationSpinner = (Spinner) findViewById(R.id.organisationSpinner);
        profileSpinner = (Spinner) findViewById(R.id.profileSpinner);
        nextButton = (Button) findViewById(R.id.organisationNextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrganisationActivity.this.handleNextButton1Click((Button) view);
            }
        });

        if(!this.accessToken.equals("offlineLogin"))
        {
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
            CallOrganisationAPI();
        }
    }

    private void CallOrganisationAPI() {
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(
                String.format(organisationApiURL,accessToken) , new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object

                    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressBarHolder.setAnimation(outAnimation);
                    progressBarHolder.setVisibility(View.GONE);

                    resultOrganisationArray = new LinkedList<Organisation>();

                    for (int i = 0; i < response.length(); i++) {

                        JSONObject jsonOrg = (JSONObject) response.get(i);

                        Organisation org = new Organisation();
                        org.OrganisationId = Integer.parseInt(jsonOrg.getString("OrganisationId"));
                        org.OrganisationCode = jsonOrg.getString("OrganisationCode");
                        org.OrganisationName = jsonOrg.getString("OrganisationName");
                        org.UniqueConfigId = jsonOrg.getString("UniqueConfigId");
                        org.UniqueOrganisationId = jsonOrg.getString("UniqueOrganisationId");

                        resultOrganisationArray.add(org);
                    }

                    //   uom.setText(uom.getText() + Integer.toString(result.StockItemSizes.get(0).Size) + result.StockItemSizes.get(0).UnitOfMeasureCode);
                    OrganisationAdapter organisationAdapter = new OrganisationAdapter(getApplicationContext(), resultOrganisationArray);
                    // apply the Adapter:
                    organisationSpinner.setAdapter(organisationAdapter);
                    // onClickListener:
                    organisationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        /**
                         * Called when a new item was selected (in the Spinner)
                         */
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int pos, long id) {
                            Organisation o = (Organisation) parent.getItemAtPosition(pos);

                            inAnimation = new AlphaAnimation(0f, 1f);
                            inAnimation.setDuration(200);
                            progressBarHolder.setAnimation(inAnimation);
                            progressBarHolder.setVisibility(View.VISIBLE);

                            CallProfileAPI(accessToken,o.UniqueOrganisationId);
                        }

                        public void onNothingSelected(AdapterView parent) {
                            // Do nothing.
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();

                    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressBarHolder.setAnimation(outAnimation);
                    progressBarHolder.setVisibility(View.GONE);

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

                VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
             }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void CallProfileAPI(String accessToken, String uniqueOrganisationId) {

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(
                String.format(profileApiURL,accessToken, uniqueOrganisationId) , new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                try {

                    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressBarHolder.setAnimation(outAnimation);
                    progressBarHolder.setVisibility(View.GONE);

                    // Parsing json object response
                    // response will be a json object

                    resultUserProfileArray = new LinkedList<UserProfile>();

                    for (int i = 0; i < response.length(); i++) {

                        JSONObject jsonUsrPro = (JSONObject) response.get(i);

                        UserProfile usrPro = new UserProfile();
                        usrPro.UserProfileID = Integer.parseInt(jsonUsrPro.getString("UserProfileID"));
                        usrPro.ProfileID = Integer.parseInt(jsonUsrPro.getString("ProfileID"));
                        usrPro.ConfigurationID = Integer.parseInt(jsonUsrPro.getString("ConfigurationID"));
                        usrPro.ConfigurationName = jsonUsrPro.getString("ConfigurationName");
                        usrPro.ProfileName = jsonUsrPro.getString("ProfileName");
                        usrPro.ProfileFullName = jsonUsrPro.getString("ProfileFullName");

                        resultUserProfileArray.add(usrPro);
                    }

                    //   uom.setText(uom.getText() + Integer.toString(result.StockItemSizes.get(0).Size) + result.StockItemSizes.get(0).UnitOfMeasureCode);
                    UserProfileAdapter userProfileAdapter = new UserProfileAdapter(getApplicationContext(), resultUserProfileArray);
                    // apply the Adapter:
                    profileSpinner.setAdapter(userProfileAdapter);
                    // onClickListener:
                    profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        /**
                         * Called when a new item was selected (in the Spinner)
                         */
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int pos, long id) {
                            UserProfile o = (UserProfile) parent.getItemAtPosition(pos);

                        }

                        public void onNothingSelected(AdapterView parent) {
                            // Do nothing.
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();

                    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressBarHolder.setAnimation(outAnimation);
                    progressBarHolder.setVisibility(View.GONE);

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

                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void handleNextButton1Click(Button view) {

        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);

        JSONObject  params = new JSONObject();

        try {
            params.put("accessToken", accessToken);
            params.put("userProfileId", Integer.toString(resultUserProfileArray.get(profileSpinner.getSelectedItemPosition()).UserProfileID));
            params.put("uniqueOrganisationId", resultOrganisationArray.get(organisationSpinner.getSelectedItemPosition()).UniqueOrganisationId);

        }catch(JSONException ex)
        {

        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                profilePOSTApiURL , params , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    CallSiteAPI();

                } catch (Exception e) {
                    e.printStackTrace();

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

                VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accessToken", accessToken);
                params.put("userProfileId", Integer.toString(resultUserProfileArray.get(profileSpinner.getSelectedItemPosition()).UserProfileID));
                params.put("uniqueOrganisationId", resultOrganisationArray.get(organisationSpinner.getSelectedItemPosition()).UniqueOrganisationId);

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

    private void CallSiteAPI() {

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(
                String.format(siteApiURL,accessToken) , new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                try {


                    // Parsing json object response
                    // response will be a json object

                    resultSiteArray = new LinkedList<Site>();

                    for (int i = 0; i < response.length(); i++) {

                        JSONObject jsonUsrPro = (JSONObject) response.get(i);

                        Site s = new Site();
                        s.SiteId = Integer.parseInt(jsonUsrPro.getString("NodeId"));
                        s.UnitSiteName = jsonUsrPro.getString("NodeName");
                        s.UnitName = jsonUsrPro.getString("UnitName");
                        resultSiteArray.add(s);
                    }

                    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressBarHolder.setAnimation(outAnimation);
                    progressBarHolder.setVisibility(View.GONE);
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

                    ArrayList<Site> resultArray = new ArrayList<Site>();
                    resultArray.addAll(resultSiteArray);

                    Bundle b = new Bundle();
                    b.putSerializable(OrganisationActivity.EXTRA_SITE,resultArray);

                    intent.putExtra(OrganisationActivity.EXTRA_SITE, b);
                    intent.putExtra(OrganisationActivity.EXTRA_ACCESSTOKEN, accessToken);
                    startActivity(intent);

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

                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_organisation, menu);
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
