package com.example.htan.myapplication;

import java.io.Serializable;

/**
 * Created by htan on 22/01/2015.
 */
public class StockCount implements Serializable {

    public int SiteItemId;
    public Double CurrentCount;
    public Double PreviousCount;
    public int StockItemSizeId;
    public DBOperation operation;
    public boolean Updated;

    public enum DBOperation
    {
        Insert,
        Update
    }

}


