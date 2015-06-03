package com.example.htan.myapplication;

import java.util.Locale;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;


public class StockCountSwipeActivity extends FragmentActivity {

    ViewPager mViewpager;
    StockCountPagerAdapter stockCountPagerAdapter;
    public Double quantityVoice;
    public Boolean updateVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_count_swipe);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        configureActionBar();

        if(bundle.get(StockCountActivity.EXTRA_STOCKCOUNTITEMQTY) != null)
        {
            quantityVoice = Double.parseDouble(bundle.get(StockCountActivity.EXTRA_STOCKCOUNTITEMQTY).toString());
        }

        if(bundle.get(StockCountActivity.EXTRA_STOCKCOUNTITEMUPDATE) != null)
        {
            updateVoice = Boolean.parseBoolean(bundle.get(StockCountActivity.EXTRA_STOCKCOUNTITEMUPDATE).toString());
        }


        if (bundle != null && bundle.getBundle(StockCountActivity.EXTRA_STOCKCOUNTITEM) != null) {
            bundle = bundle.getBundle(StockCountActivity.EXTRA_STOCKCOUNTITEM);
            ArrayList<StockCountItem> resultArray = (ArrayList<StockCountItem>) bundle.getSerializable(StockCountActivity.EXTRA_STOCKCOUNTITEM);

            stockCountPagerAdapter = new StockCountPagerAdapter(getSupportFragmentManager(), resultArray, quantityVoice, updateVoice);
            mViewpager = (ViewPager) findViewById(R.id.pager);
            mViewpager.setAdapter(stockCountPagerAdapter);
        }
    }

    private void configureActionBar() {
        ActionBar actionBar = getActionBar();
        Resources r = getResources();
        actionBar.setTitle(r.getString(R.string.stock_count).toString());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            //getMenuInflater().inflate(R.menu.stock_count_swipe, menu);
            return true;
        }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:

                handleBackAction();
                // Toast.makeText(this, "home pressed", Toast.LENGTH_LONG).show();
                    return true;
            default:
                return super.onMenuItemSelected(featureId,item);

        }
    }

    private void handleBackAction() {
        finish();
    }
}

