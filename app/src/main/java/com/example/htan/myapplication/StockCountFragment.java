package com.example.htan.myapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StockCountFragment extends Fragment {

    public final static String   EXTRA_STOCKCOUNTITEM ="com.example.webapitutorial.STOCKCOUNTITEM";
    private final static int COURSE_ACTION_NOT_SET = -1;

    public TextView productName;
    public TextView categoryHierarchy;
    public Spinner uom;
    public TextView costPrice;
    public EditText quantityText;

    public TextView totalValue;
    public StockCountItem sci;

    public Double quantityVoice;
    public boolean updateVoice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView =  inflater.inflate(R.layout.fragment_stock_count, container, false);

        Bundle arguments = getArguments();

        updateVoice = Boolean.parseBoolean(arguments.getString(StockCountActivity.EXTRA_STOCKCOUNTITEMUPDATE));

        if(arguments.get(StockCountActivity.EXTRA_STOCKCOUNTITEMQTY) != null)
        {
            quantityVoice = Double.parseDouble(arguments.getString(StockCountActivity.EXTRA_STOCKCOUNTITEMQTY));
        }

        if(arguments != null  && arguments.get(StockCountFragment.EXTRA_STOCKCOUNTITEM) != null)
        {
            sci = (StockCountItem) arguments.get(StockCountFragment.EXTRA_STOCKCOUNTITEM);;

            productName = (TextView) mView.findViewById(R.id.productName);
            categoryHierarchy = (TextView) mView.findViewById(R.id.categoryHierarchy);
            uom = (Spinner) mView.findViewById(R.id.uomSpinner);
            costPrice = (TextView) mView.findViewById(R.id.costprice);
            quantityText = (EditText) mView.findViewById(R.id.quantityText);
            totalValue = (TextView) mView.findViewById(R.id.totalvalue);

            productName.setText(productName.getText() + sci.ItemName);
            categoryHierarchy.setText(categoryHierarchy.getText() + sci.CategoryHierarchy);

            //   uom.setText(uom.getText() + Integer.toString(result.StockItemSizes.get(0).Size) + result.StockItemSizes.get(0).UnitOfMeasureCode);
            UomAdapter uomAdapter = new UomAdapter(this.getActivity(), sci.StockItemSizes);
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

                    if(sci.Count != null && quantityVoice == null) {
                        for (StockCount c : sci.Count) {
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

            for(StockItemSize size: sci.StockItemSizes)
            {
                if(size.IsDefault)
                {
                    uom.setSelection(uomDefaultPosition);
                    break;
                }
                else
                    uomDefaultPosition++;

            }

            costPrice.setText(costPrice.getText() + Double.toString(sci.CostPrice));

            quantityText.addTextChangedListener(
                    new TextWatcher() {
                        public void afterTextChanged(Editable s) {

                            double quantity = 0;

                            if ((!quantityText.getText().toString().equals("")) && (!quantityText.getText().toString().equals("."))) {
                                quantity = Double.parseDouble(quantityText.getText().toString());
                            }

                            Resources res = getResources();
                            DecimalFormat df = new DecimalFormat("####0.00");

                            totalValue.setText(res.getString(R.string.totalvalue_activity_stock_count) + df.format(sci.CostPrice * quantity));
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                    }
            );

            if (sci.Count != null && sci.Count.size() > 0) {

                int stockItemSizeId = sci.StockItemSizes.get(uom.getSelectedItemPosition()).StockItemSizeId;

                for(StockCount c : sci.Count)
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
                quantityText.setText(quantityVoice.toString());

                if(updateVoice == true)
                    handleSavAction();
            }
        }
        return mView;
    }

    private void handleBackAction() {
       getActivity().finish();
    }

    private void handleSavAction() {

        if(sci != null)
        {
            CallUpdateStockCount searchItemAsync = new CallUpdateStockCount(this.getActivity());

            StockCount param = new StockCount();

            param.SiteItemId = sci.SiteItemId;
            param.StockItemSizeId = sci.StockItemSizes.get(uom.getSelectedItemPosition()).StockItemSizeId;


            if(quantityText.getText() != null && (!quantityText.getText().toString().equals("")))
                param.CurrentCount = Double.parseDouble(quantityText.getText().toString());
            else
                param.CurrentCount = null;

            if(sci.Count != null && sci.Count.size() > 0 && MatchStockItemSize(param.StockItemSizeId))
            {
                param.operation = StockCount.DBOperation.Update;

                for(StockCount c : sci.Count)
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
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

        for(StockCount c : sci.Count)
        {
            if(c.StockItemSizeId == stockItemSizeId)
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.stock_count_swipe, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean handled = true;
        int courseActionResourceId = COURSE_ACTION_NOT_SET;

        switch (item.getItemId()) {
            case R.id.action_save:
                handleSavAction();
                break;
            case R.id.action_reset:
                //courseActionResourceId = R.string.title_action_contents;
                break;
            case R.id.action_help:
                //courseActionResourceId = R.string.title_action_description;
                break;
            default:
                handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }
}
