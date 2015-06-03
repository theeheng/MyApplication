package com.example.htan.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ViewAllStockCountActivity extends Activity {

    public GridView gridview;
    public TextView totalCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_stock_count);
        gridview = (GridView) findViewById(R.id.gridview);
        totalCount = (TextView) findViewById(R.id.totalCount);

        Intent intent   = getIntent();
        Bundle b   = intent.getExtras().getBundle(StockCountActivity.EXTRA_STOCKCOUNTITEM);

        ArrayList<StockCountDisplay> resultArray = (ArrayList<StockCountDisplay>) b.getSerializable(StockCountActivity.EXTRA_STOCKCOUNTITEM);

        List<Integer> countStockItem = new LinkedList<Integer>();

        for(StockCountDisplay s : resultArray)
        {
            if(!countStockItem.contains(s.SiteItemId))
            {
                countStockItem.add(s.SiteItemId);
            }
        }

        totalCount.setText(totalCount.getText().toString()+countStockItem.size());

        gridview.setAdapter(new DataAdapter(this,resultArray));

        configureActionBar();
    }

    private void handleBackAction() {
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_all_stock_count, menu);
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

    private void configureActionBar() {
        ActionBar actionBar = getActionBar();
        Resources r = getResources();
        actionBar.setTitle(r.getString(R.string.title_activity_view_all_stock_count).toString());
        actionBar.setDisplayHomeAsUpEnabled(true);
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
}
