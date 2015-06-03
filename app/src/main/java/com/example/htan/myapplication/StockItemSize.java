package com.example.htan.myapplication;

import java.io.Serializable;

/**
 * Created by htan on 22/01/2015.
 */
public class StockItemSize implements Serializable {

    public int StockItemSizeId;
    public int StockItemId;
    public double Size;
    public String UnitOfMeasureCode;
    public int UnitOfMeasureId;
    public double ConversionRatio;
    public String CaseSizeDescription;
    public boolean IsDefault;
}
