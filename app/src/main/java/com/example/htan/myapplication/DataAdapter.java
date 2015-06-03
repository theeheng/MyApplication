package com.example.htan.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DataAdapter extends BaseAdapter
{

    Context mContext;

    ArrayList<StockCountDisplay> result;

    private LayoutInflater mInflater;

    public DataAdapter(Context c, ArrayList<StockCountDisplay> db)

    {

        mContext=c;

        mInflater = LayoutInflater.from(c);

        result = db;

    }

    public int getCount()

    {

        return result.size();

    }

    public StockCountDisplay getItem(int position)
    {

        return result.get(position);

    }

    public long getItemId(int position)

    {

        return position;

    }

    public View getView(int position, View convertView, ViewGroup parent)

    {

        ViewHolder holder=null;

        if(convertView==null ||  convertView.getTag() == null )
        {

            convertView = mInflater.inflate(R.layout.customgrid,
                    parent,false);

            holder = new ViewHolder();

            holder.txtItemName=(TextView)convertView.findViewById(R.id.txtItemName);

            holder.txtItemName.setPadding(5, 5, 5 , 5);

            holder.txtUOM=(TextView)convertView.findViewById(R.id.txtUOM);

            holder.txtUOM.setPadding(5, 5, 5 , 5);

            holder.txtCurrentCount=(TextView)convertView.findViewById(R.id.txtCurrentCount);

            holder.txtCurrentCount.setPadding(5, 5, 5, 5);

            holder.txtPreviousCount=(TextView)convertView.findViewById(R.id.txtPreviousCount);

            holder.txtPreviousCount.setPadding(5, 5, 1, 5);

            if(position==0)
            {

                convertView.setTag(holder);

            }

        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtItemName.setText(result.get(position).ItemName);
        holder.txtItemName.setTextColor(Color.BLACK);

        holder.txtUOM.setText(result.get(position).Size + " "+result.get(position).UnitOfMeasureCode);
        holder.txtUOM.setTextColor(Color.BLACK);

        if(result.get(position).CurrentCount == null)
            holder.txtCurrentCount.setText("NULL");
        else
            holder.txtCurrentCount.setText(Double.toString(result.get(position).CurrentCount));

        holder.txtCurrentCount.setTextColor(Color.BLACK);

        if(result.get(position).PreviousCount == null)
            holder.txtPreviousCount.setText("NULL");
        else
            holder.txtPreviousCount.setText(Double.toString(result.get(position).PreviousCount));

        holder.txtPreviousCount.setTextColor(Color.BLACK);

        return convertView;

    }

    static class ViewHolder
    {

        TextView txtItemName;

        TextView txtUOM;

        TextView txtCurrentCount;

        TextView txtPreviousCount;


    }

}