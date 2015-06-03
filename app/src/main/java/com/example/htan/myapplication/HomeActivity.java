package com.example.htan.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HomeActivity extends Activity implements ListView.OnItemClickListener {

    public Button scanButton;
    public Button searchByNameButton;
    public Button searchByVoiceButton;
    public Button viewAllStockButton;
    public Button printButton;
    public String accessToken;
    public Spinner siteSpinner;
    private List<Site> resultSiteArray;
    public TextView stockPeriodText;
    public TextView stockPeriodStatusText;
    public StockPeriodHeader stockPeriodHeader;
    private final static String tag ="SearchActivity";
    private static String TAG = HomeActivity.class.getSimpleName();
    private static String stockPeriodHeaderApiURL = "http://10.0.26.67/FnBModelWebAPI/api/stockperiodheader/?accesstoken=%s&siteId=%s";
    public final static String EXTRA_SITEID ="com.example.webapitutorial.SITEID";

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    FrameLayout progressBarHolder;
    AlphaAnimation inAnimation;

    NavigationDrawerHelper mNavigationDrawerHelper;

    public enum SearchType
    {
        SearchByBarcode,
        SearchByName,
        SearchBySiteItemId
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNavigationDrawerHelper = new NavigationDrawerHelper();

        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);

        stockPeriodText = (TextView) findViewById(R.id.stockPeriodText);
        stockPeriodStatusText = (TextView) findViewById(R.id.stockPeriodStatusText);
        siteSpinner = (Spinner) findViewById(R.id.siteSpinner);

        scanButton = (Button) findViewById(R.id.ScanBarcodeBtn);
        searchByNameButton = (Button) findViewById(R.id.SearchByNameBtn);
        searchByVoiceButton = (Button) findViewById(R.id.SearchByVoiceButton);
        printButton = (Button) findViewById(R.id.PrintButton);

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.this.handlePrintButton1Click((Button) view);
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.this.handleScanBarcodeButton1Click((Button) view);
            }
        });

        searchByNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.this.handleSearchByNameButton1Click((Button) view);
            }
        });

        searchByVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.this.handleSearchByVoiceButton1Click((Button) view);
            }
        });

        viewAllStockButton = (Button) findViewById(R.id.ViewAllStockCountBtn);

        viewAllStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.this.handleViewAllStockButton1Click((Button) view);
            }
        });


        //Body of onCreate

// get and process search query here
        final Intent queryIntent = getIntent();

        this.accessToken   = queryIntent.getStringExtra(OrganisationActivity.EXTRA_ACCESSTOKEN);

        Bundle bundle   = queryIntent.getExtras();

        if(bundle != null && bundle.getBundle(OrganisationActivity.EXTRA_SITE) != null) {
            bundle = bundle.getBundle(OrganisationActivity.EXTRA_SITE);
            resultSiteArray = (List<Site>) bundle.getSerializable(OrganisationActivity.EXTRA_SITE);

            SiteAdapter siteAdapter = new SiteAdapter(getApplicationContext(), resultSiteArray);
            // apply the Adapter:
            siteSpinner.setAdapter(siteAdapter);
            // onClickListener:
            siteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                /**
                 * Called when a new item was selected (in the Spinner)
                 */
                public void onItemSelected(AdapterView<?> parent,
                                           View view, int pos, long id) {
                    Site s = (Site) parent.getItemAtPosition(pos);

                    inAnimation = new AlphaAnimation(0f, 1f);
                    inAnimation.setDuration(200);
                    progressBarHolder.setAnimation(inAnimation);
                    progressBarHolder.setVisibility(View.VISIBLE);
                    CallStockPeriodHeaderAPI(s.SiteId);
                }



                public void onNothingSelected(AdapterView parent) {
                    // Do nothing.
                }
            });

            siteSpinner.setVisibility(View.VISIBLE);

            mNavigationDrawerHelper.init(this, this);
            //mNavigationDrawerHelper.setSelection(courseLib);
        }
        else
        {
            siteSpinner.setVisibility(View.GONE);
        }

//query action
        final String queryAction = queryIntent.getAction();
        if (Intent.ACTION_SEARCH.equals(queryAction))
        {
            CallSearchStockCountItem searchItemAsync = new CallSearchStockCountItem(this);
            String searchType = HomeActivity.SearchType.SearchByName.toString();
            searchItemAsync.execute(searchType, queryIntent.getStringExtra(SearchManager.QUERY), null, Boolean.toString(false));
            finish();
        }
        else if(Intent.ACTION_VIEW.equals(queryAction))
        {

           CallSearchStockCountItem searchItemAsync = new CallSearchStockCountItem(this);
            String searchType = SearchType.SearchBySiteItemId.toString();
            searchItemAsync.execute(searchType, queryIntent.getData().getLastPathSegment());
            finish();
        }
        else {
            Log.d(tag,"Create intent NOT from search");
        }
    }

    private void handlePrintButton1Click(Button view) {

        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/sample.pdf"));
        //String fileurl = "https://s01-ws-01.fhprelgb01.local/SetApp/Pages/Reporting/ReportingView.aspx?ReportCode=a38ffb7c-1e0d-4495-9800-5b0b70b11342&modelDSParam4=14607&RedirectToRefererOnCancel=true";

        //Uri uri = Uri.parse(fileurl);

        Intent printIntent = new Intent(this, PrintDialogActivity.class);
        printIntent.setDataAndType(uri , "application/pdf");
        printIntent.putExtra("title", "Android print demo");
        startActivity(printIntent);
    }

    private void handleSearchByVoiceButton1Click(Button view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                .getPackage().getName());

        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say: {Product Name} QUANTITY {Stock Count Value} UPDATE"
                .toString());

        // Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                Locale.getDefault());

        int noOfMatches = 10;
        // Specify how many results you want to receive. The results will be
        // sorted where the first result is the one with higher confidence.

        //intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    private void CallStockPeriodHeaderAPI(int siteId) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                String.format(stockPeriodHeaderApiURL,accessToken,Integer.toString(siteId)) , null , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressBarHolder.setAnimation(outAnimation);
                    progressBarHolder.setVisibility(View.GONE);

                    // Parsing json object response
                    // response will be a json object
                    stockPeriodHeader = new StockPeriodHeader();

                    stockPeriodHeader.StartDate = response.getString("StartDateShortString");
                    stockPeriodHeader.EndDate = response.getString("EndDateShortString");
                    stockPeriodHeader.StatusDisplayText = response.getString("StatusDisplayText");
                    stockPeriodHeader.StockPeriodHeaderID = Integer.parseInt(response.getString("StockPeriodHeaderID"));

                    Resources r = getResources();
                    stockPeriodText.setText(String.format(r.getString(R.string.stockperiodTxt_home_activity),stockPeriodHeader.StartDate,stockPeriodHeader.EndDate));
                    stockPeriodText.setVisibility(View.VISIBLE);

                    stockPeriodStatusText.setText(String.format(r.getString(R.string.stockperiodStatusTxt_home_activity),stockPeriodHeader.StatusDisplayText));
                    stockPeriodStatusText.setVisibility(View.VISIBLE);

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

    private void handleSearchByNameButton1Click(Button view) {
        super.onSearchRequested();
    }

    private void handleViewAllStockButton1Click(Button view) {

           inAnimation = new AlphaAnimation(0f, 1f);
                inAnimation.setDuration(200);
                progressBarHolder.setAnimation(inAnimation);
                progressBarHolder.setVisibility(View.VISIBLE);

        CallGetAllStockCountItem getAllItemAsync = new CallGetAllStockCountItem(this, null, 0, null);

        String sid = "0";

        if(siteSpinner.getVisibility() == View.VISIBLE)
            sid = Integer.toString(resultSiteArray.get(siteSpinner.getSelectedItemPosition()).SiteId);

        getAllItemAsync.execute("view", accessToken, sid);
    }

    private void handleScanBarcodeButton1Click(Button view) {

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();

        //CallSearchStockCountItem searchItemAsync = new CallSearchStockCountItem(this);

        //String barcodeContent = "21040971";
        //String barcodeFormat = "CODE_39";
        //String fullContent = "test full content";
        //searchItemAsync.execute(SearchType.SearchByBarcode.toString(), barcodeContent, barcodeFormat, fullContent);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if(requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanResult != null) {

                CallSearchStockCountItem searchItemAsync = new CallSearchStockCountItem(this);
                String searchType = SearchType.SearchByBarcode.toString();
                String barcodeContent = scanResult.getContents();
                String barcodeFormat = scanResult.getFormatName();
                String fullContent = scanResult.toString();
                searchItemAsync.execute(searchType, barcodeContent, barcodeFormat, fullContent);

            }
        }

        if (requestCode == this.VOICE_RECOGNITION_REQUEST_CODE)

            //If Voice recognition is successful then it returns RESULT_OK
            if(resultCode == RESULT_OK) {

                ArrayList<String> textMatchList = intent
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {

                    if(textMatchList.size() == 1) {
                        voiceProductSearch(textMatchList.get(0));
                    }else {
                        if (textMatchList.size() > 1) {
                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                    HomeActivity.this);
                            builderSingle.setIcon(R.drawable.ic_launcher);
                            builderSingle.setTitle("Select a correct suggestion:-");
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    HomeActivity.this,
                                    android.R.layout.select_dialog_singlechoice, textMatchList);

                            builderSingle.setNegativeButton("cancel",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builderSingle.setAdapter(arrayAdapter,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            voiceProductSearch(strName);
                                        }
                                    });
                            builderSingle.show();
                        }
                    }

                    // If first Match contains the 'search' word
                    // Then start web search.

                   // if (textMatchList.get(0).contains("search")) {

                        //String searchQuery = textMatchList.get(0).replace("search", " ");
                        //Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                        //search.putExtra(SearchManager.QUERY, searchQuery);
                        //startActivity(search);
                  //  } else {
                        // populate the Matches
                   //     mlvTextMatches
                   //             .setAdapter(new ArrayAdapter<String>(this,
                   //                     android.R.layout.simple_list_item_1,
                   //                     textMatchList));
                   // }

                         }
                //Result code for various error.
            }else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
                showToastMessage("Audio Error");
            }else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
                showToastMessage("Client Error");
            }else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
                showToastMessage("Network Error");
            }else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
                showToastMessage("No Match");
            }else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
                showToastMessage("Server Error");
            }

        //super.onActivityResult(requestCode, resultCode, intent);
    }

    public void voiceProductSearch(String voiceText)
    {
        CallSearchStockCountItem searchItemAsync = new CallSearchStockCountItem(this);
        String searchType = HomeActivity.SearchType.SearchByName.toString();

        String productName = voiceText.toLowerCase().replace(" qty "," quantity ").replace(" quantities "," quantity ").replace("updates","update");
        String quantityVoice = null;
        boolean updateVoice = false;
        showToastMessage(productName);


        if(productName.toLowerCase().contains(" quantity "))
        {
            if(productName.length() > 6 && productName.substring(productName.length()-6,productName.length()).toLowerCase().equals("update"))
            {
                updateVoice = true;
            }

            String[] voiceResult = productName.toLowerCase().replace("update","").split(" quantity ");

            productName = voiceResult[0];
            quantityVoice = voiceResult[1];
        }
        searchItemAsync.execute(searchType, productName, quantityVoice, Boolean.toString(updateVoice));

    }
    void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        mNavigationDrawerHelper.handleOnOptionsItemSelected(item);

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onNewIntent(Intent intent) {

        int i = 0;

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int optionLib, long l) {
        mNavigationDrawerHelper.handleSelect(optionLib, this, accessToken, resultSiteArray.get(siteSpinner.getSelectedItemPosition()).SiteId);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mNavigationDrawerHelper.syncState();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mNavigationDrawerHelper.handleOnPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mNavigationDrawerHelper.syncState();
        super.onConfigurationChanged(newConfig);
    }
}
