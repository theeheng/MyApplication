package com.example.htan.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by htan on 26/01/2015.
 */
public class UserProfileAdapter  extends BaseAdapter implements SpinnerAdapter {


    /**
     * The internal data (the ArrayList with the Objects).
     */
    private final List<UserProfile> data;
    private LayoutInflater mInflater;

    public UserProfileAdapter(Context c, List<UserProfile> data){
        this.data = data;
        this.mInflater = LayoutInflater.from(c);
    }

    /**
     * Returns the Size of the ArrayList
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * Returns one Element of the ArrayList
     * at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    /**
     * Returns the View that is shown when a element was
     * selected.
     */
    @Override
    public View getView(int position, View recycle, ViewGroup parent) {
        TextView text;
        if (recycle != null){
            // Re-use the recycled view here!
            text = (TextView) recycle;
        } else {
            // No recycled view, inflate the "original" from the platform:
            text = (TextView) mInflater.inflate(
                    R.layout.dropdownitem, parent, false
            );
        }
        text.setTextColor(Color.BLACK);
        text.setText(data.get(position).ProfileFullName);

        return text;
    }

}

