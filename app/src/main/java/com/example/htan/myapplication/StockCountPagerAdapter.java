package com.example.htan.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.os.Bundle;

import java.util.ArrayList;


/**
 * Created by HT on 01/02/2015.
 */
public class StockCountPagerAdapter extends FragmentPagerAdapter {

    ArrayList<StockCountItem> data;
    Double quantityVoice;
    boolean updateVoice;

    public StockCountPagerAdapter(FragmentManager fm, ArrayList<StockCountItem> dbResult, Double qtyVoice, Boolean updVoice) {
        super(fm);
        data = dbResult;
        quantityVoice = qtyVoice;
        updateVoice = false;

        if(updVoice != null)
        {
            updateVoice = updVoice;
        }

        if(data.size() > 1 && updateVoice == true)
        {
            updateVoice = false;
        }
    }

    @Override
    public Fragment getItem(int position) {

        StockCountFragment scf = new StockCountFragment();
        Bundle b = new Bundle();

        b.putSerializable(StockCountActivity.EXTRA_STOCKCOUNTITEM,data.get(position));

        b.putString(StockCountActivity.EXTRA_STOCKCOUNTITEMUPDATE, Boolean.toString(updateVoice));

        if(quantityVoice != null)
            b.putString(StockCountActivity.EXTRA_STOCKCOUNTITEMQTY, Double.toString(quantityVoice));

        scf.setArguments(b);

        return scf;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        String title;

        if(data.size() == 1)
            title = data.get(position).ItemName;
        else
            title = data.get(position).ItemName + "\n\n "+ (position+1) + " of "+ data.size();

        return title;
    }
}

