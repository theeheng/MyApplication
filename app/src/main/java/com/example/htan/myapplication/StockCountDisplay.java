package com.example.htan.myapplication;

import java.io.Serializable;

/**
 * Created by htan on 26/01/2015.
 */
public class StockCountDisplay implements Serializable {
    public String ItemName;
    public double Size;
    public String UnitOfMeasureCode;
    public Double CurrentCount;
    public Double PreviousCount;
    public Integer SiteItemId;
    public Integer StockItemSizeId;
}
