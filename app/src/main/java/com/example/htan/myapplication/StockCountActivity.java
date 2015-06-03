package com.example.htan.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class StockCountActivity extends Activity {

    public final static String   EXTRA_STOCKCOUNTITEM ="com.example.webapitutorial.STOCKCOUNTITEM";
    public final static String   EXTRA_STOCKCOUNTITEMQTY ="com.example.webapitutorial.STOCKCOUNTITEMQTY";
    public final static String   EXTRA_STOCKCOUNTITEMUPDATE ="com.example.webapitutorial.STOCKCOUNTITEMUPDATE";

    public TextView productName;
    public TextView categoryHierarchy;
    public Spinner uom;
    public TextView costPrice;
   public  StockCountItem result;
    public EditText quantityText;
    public Button backBtn;
   public Button submitBtn;
  public TextView totalValue;
public Double quantityVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_count);

        Intent intent   = getIntent();
        Bundle bundle   = intent.getExtras();

        String qtyVoice = intent.getStringExtra(StockCountActivity.EXTRA_STOCKCOUNTITEMQTY);

        if(qtyVoice != null)
        {
            quantityVoice = Double.parseDouble(qtyVoice);
        }

        if(bundle != null && bundle.getBundle(StockCountActivity.EXTRA_STOCKCOUNTITEM) != null) {
            bundle = bundle.getBundle(StockCountActivity.EXTRA_STOCKCOUNTITEM);
            ArrayList<StockCountItem> resultArray = (ArrayList<StockCountItem>) bundle.getSerializable(StockCountActivity.EXTRA_STOCKCOUNTITEM);


            if (resultArray != null) {
                result = resultArray.get(0);

                productName = (TextView) findViewById(R.id.productName);
                categoryHierarchy = (TextView) findViewById(R.id.categoryHierarchy);
                uom = (Spinner) findViewById(R.id.uomSpinner);
                costPrice = (TextView) findViewById(R.id.costprice);
                quantityText = (EditText) findViewById(R.id.quantityText);
                totalValue = (TextView) findViewById(R.id.totalvalue);
                backBtn = (Button) findViewById(R.id.backButton);

                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StockCountActivity.this.handleBackButton1Click((Button) view);
                    }
                });

                submitBtn = (Button) findViewById(R.id.submitButton);

                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        StockCountActivity.this.handleSubmitButton1Click((Button) view);
                    }
                });
                productName.setText(productName.getText() + result.ItemName);
                categoryHierarchy.setText(categoryHierarchy.getText() + result.CategoryHierarchy);

             //   uom.setText(uom.getText() + Integer.toString(result.StockItemSizes.get(0).Size) + result.StockItemSizes.get(0).UnitOfMeasureCode);
                UomAdapter uomAdapter = new UomAdapter(this, result.StockItemSizes);
                // apply the Adapter:
                uom.setAdapter(uomAdapter);
                // onClickListener:
                uom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    /**
                     * Called when a new item was selected (in the Spinner)
                     */
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int pos, long id) {
                        StockItemSize g = (StockItemSize) parent.getItemAtPosition(pos);

                        if(result.Count != null) {
                            for (StockCount c : result.Count) {
                                if (c.StockItemSizeId == g.StockItemSizeId) {

                                    if(c.CurrentCount != null)
                                        quantityText.setText(Double.toString(c.CurrentCount));
                                    else
                                        quantityText.setText("");
                                    break;
                                }
                            }
                        }

                        //Toast.makeText(getApplicationContext(),Integer.toString(g.StockItemSizeId), Toast.LENGTH_LONG).show();
                    }

                    public void onNothingSelected(AdapterView parent) {
                        // Do nothing.
                    }
                });

                int uomDefaultPosition = 0;

                for(StockItemSize size: result.StockItemSizes)
                {
                    if(size.IsDefault)
                    {
                        uom.setSelection(uomDefaultPosition);
                        break;
                    }
                    else
                        uomDefaultPosition++;

                }

                costPrice.setText(costPrice.getText() + Double.toString(result.CostPrice));

                quantityText.addTextChangedListener(
                        new TextWatcher() {
                            public void afterTextChanged(Editable s) {

                                double quantity = 0;

                                if ((!quantityText.getText().toString().equals("")) && (!quantityText.getText().toString().equals("."))) {
                                    quantity = Double.parseDouble(quantityText.getText().toString());
                                }

                                Resources res = getResources();
                                DecimalFormat df = new DecimalFormat("####0.00");

                                totalValue.setText(res.getString(R.string.totalvalue_activity_stock_count) + df.format(result.CostPrice * quantity));
                            }

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        }
                );

                if (result.Count != null && result.Count.size() > 0) {

                    int stockItemSizeId = result.StockItemSizes.get(uom.getSelectedItemPosition()).StockItemSizeId;

                    for(StockCount c : result.Count)
                    {
                        if(c.StockItemSizeId == stockItemSizeId)
                        {
                            if(c.CurrentCount != null)
                                quantityText.setText(Double.toString(c.CurrentCount));
                            else
                                quantityText.setText("");

                            break;
                        }
                    }
                }

                if(quantityVoice != null)
                {
                    quantityText.setText(qtyVoice.toString());
                }
            }
        }
    }

    private void handleBackButton1Click(Button view) {
        this.finish();
    }

    private void handleSubmitButton1Click(Button view) {

        if(result != null)
        {
            CallUpdateStockCount searchItemAsync = new CallUpdateStockCount(this);

            StockCount param = new StockCount();

            param.SiteItemId = result.SiteItemId;
            param.StockItemSizeId = result.StockItemSizes.get(uom.getSelectedItemPosition()).StockItemSizeId;


            if(quantityText.getText() != null && (!quantityText.getText().toString().equals("")))
                    param.CurrentCount = Double.parseDouble(quantityText.getText().toString());
            else
                    param.CurrentCount = null;

            if(result.Count != null && result.Count.size() > 0 && MatchStockItemSize(param.StockItemSizeId))
            {
                param.operation = StockCount.DBOperation.Update;

                for(StockCount c : result.Count)
                {
                    if(c.StockItemSizeId == param.StockItemSizeId)
                    {
                        param.PreviousCount = c.CurrentCount;
                    }
                }
            }
            else
            {
                param.operation = StockCount.DBOperation.Insert;
                param.PreviousCount = param.CurrentCount;
            }

            param.Updated = true;

            searchItemAsync.execute(param);
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please enter a quantity!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                             quantityText.requestFocus();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    private boolean MatchStockItemSize(int stockItemSizeId) {

        for(StockCount c : result.Count)
        {
            if(c.StockItemSizeId == stockItemSizeId)
            {
                return true;
            }
        }

        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stock_count, menu);
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
