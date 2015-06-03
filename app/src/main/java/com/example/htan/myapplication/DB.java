package com.example.htan.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;


public class DB extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "data/data/com.example.htan.myapplication/databases/";
    private static String DB_NAME = "OLTP";

    private final Context context;
    private SQLiteDatabase db;


    // constructor
    public DB(Context context) {

        super( context , DB_NAME , null , 1);
        this.context = context;

    }


    // Creates a empty database on the system and rewrites it with your own database.
    public void create() throws IOException{

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    // Check if the database exist to avoid re-copy the data
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{


            String path = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            // database don't exist yet.
            e.printStackTrace();

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    // copy your assets db to the new system DB
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    //Open the database
    public boolean open() {

        try {
            String myPath = DB_PATH + DB_NAME;
            db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            return true;

        } catch(SQLException sqle) {
            db = null;
            return false;
        }

    }

    @Override
    public synchronized void close() {

        if(db != null)
            db.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    //
    // PUBLIC METHODS TO ACCESS DB CONTENT
    //

    public List<StockCountDisplay> getStockCountDisplay(int siteId) {

        List<StockCountDisplay> items = null;

        try {

             String query  = "SELECT A.ItemName, B.Size, B.UnitOfMeasureCode,C.CurrentCount, C.PreviousCount, A.SiteItemId FROM StockCountItem A INNER JOIN StockItemSize B ON A.StockItemID = B.StockItemID LEFT OUTER JOIN StockCount C on A.SiteItemID = C.SiteItemID AND B.StockItemSizeID = C.StockItemSizeID ";

                     if(siteId != 0)
                     {
                         query = query + "WHERE A.SiteId = "+siteId+ " ";
                     }

            query = query + " ORDER BY A.ItemName ASC";

                SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);
                Cursor      cursor  = db.rawQuery(query, null);

                // go over each row, build elements and add it to list
                items = new LinkedList<StockCountDisplay>();

                if (cursor.moveToFirst()) {
                    do {

                        StockCountDisplay item  = new StockCountDisplay();

                        /*SiteItemId
	CategoryId,
	ItemName,
	CategoryName,
	CategoryHierarchy,
	SupplierId,
	SiteId,
	StockItemId,*/

                        item.ItemName    =  cursor.getString(0);
                        item.Size = Double.parseDouble(cursor.getString(1));
                        item.UnitOfMeasureCode =  cursor.getString(2);
                        item.CurrentCount =  (cursor.getString(3)==null) ? null : Double.parseDouble(cursor.getString(3)) ;
                        item.PreviousCount =  (cursor.getString(4)==null) ? null : Double.parseDouble(cursor.getString(4)) ;
                        item.SiteItemId = Integer.parseInt(cursor.getString(5)) ;
                        items.add(item);

                    } while (cursor.moveToNext());
                }

                cursor.close();
                db.close();

        } catch(Exception e) {
            // sql error
        }

        return items;
    }

    public List<StockCountDisplay> getStockCountUpload(int siteId) {

        List<StockCountDisplay> items = null;

        try {

            String query  = "SELECT A.ItemName, B.Size, B.UnitOfMeasureCode,C.CurrentCount, C.PreviousCount, A.SiteItemId, B.StockItemSizeId  FROM StockCountItem A INNER JOIN StockItemSize B ON A.StockItemID = B.StockItemID INNER JOIN StockCount C on A.SiteItemID = C.SiteItemID AND B.StockItemSizeID = C.StockItemSizeID WHERE C.Updated = 1 AND A.SiteId = "+siteId;
            SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);
            Cursor      cursor  = db.rawQuery(query, null);

            // go over each row, build elements and add it to list
            items = new LinkedList<StockCountDisplay>();

            if (cursor.moveToFirst()) {
                do {

                    StockCountDisplay item  = new StockCountDisplay();

                        /*SiteItemId
	CategoryId,
	ItemName,
	CategoryName,
	CategoryHierarchy,
	SupplierId,
	SiteId,
	StockItemId,*/

                    item.ItemName    =  cursor.getString(0);
                    item.Size = Double.parseDouble(cursor.getString(1));
                    item.UnitOfMeasureCode =  cursor.getString(2);
                    item.CurrentCount =  (cursor.getString(3)==null) ? null : Double.parseDouble(cursor.getString(3)) ;
                    item.PreviousCount =  (cursor.getString(4)==null) ? null : Double.parseDouble(cursor.getString(4)) ;
                    item.SiteItemId =  Integer.parseInt(cursor.getString(5)) ;
                    item.StockItemSizeId =  Integer.parseInt(cursor.getString(6)) ;

                    items.add(item);

                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

        } catch(Exception e) {
            // sql error
        }

        return items;
    }


    public List<StockCountItem> getStockCountItemByBarcode(String barcodeCotent, String barcodeFormat ) {

        List<StockCountItem> items = null;
        List<StockItemSize> itemSizes = null;
        try {

            itemSizes =  getStockItemSizes(barcodeCotent,barcodeFormat);

            if(itemSizes != null && itemSizes.size() > 0)
            {
                String query  = "SELECT DISTINCT A.SiteItemId, A.CategoryId, A.ItemName, A.CategoryName, A.CategoryHierarchy, A.SupplierId, A.SiteId, A.StockItemId, A.CostPrice FROM StockCountItem A WHERE A.StockItemId = "+itemSizes.get(0).StockItemId;
                SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);
                Cursor      cursor  = db.rawQuery(query, null);

                // go over each row, build elements and add it to list
                items = new LinkedList<StockCountItem>();

                if (cursor.moveToFirst()) {
                    do {

                        StockCountItem item  = new StockCountItem();

                        /*SiteItemId
	CategoryId,
	ItemName,
	CategoryName,
	CategoryHierarchy,
	SupplierId,
	SiteId,
	StockItemId,*/

                        item.SiteItemId      = Integer.parseInt(cursor.getString(0));
                        item.CategoryId    = Integer.parseInt(cursor.getString(1));
                        item.ItemName    =  cursor.getString(2);
                        item.CategoryName    = cursor.getString(3);
                        item.CategoryHierarchy    = cursor.getString(4);
                        item.SupplierId  = (cursor.getString(5)==null) ? 0 : Integer.parseInt(cursor.getString(5));
                        item.SiteId    = Integer.parseInt(cursor.getString(6));
                        item.StockItemId    = Integer.parseInt(cursor.getString(7));
                        item.CostPrice  = Double.parseDouble(cursor.getString(8));
                        item.StockItemSizes = itemSizes;

                        item.Count =  getStockItemCount(item.SiteItemId);

                        items.add(item);

                    } while (cursor.moveToNext());
                }

                cursor.close();
                db.close();
            }
        } catch(Exception e) {
            // sql error
        }

        return items;
    }

    public List<StockCountItem> getStockCountItemByItemName(String itemName) {

        List<StockCountItem> items = null;
        List<StockItemSize> itemSizes = null;
        try {

           String query  = "SELECT DISTINCT A.SiteItemId, A.CategoryId, A.ItemName, A.CategoryName, A.CategoryHierarchy, A.SupplierId, A.SiteId, A.StockItemId, A.CostPrice FROM StockCountItem A WHERE A.ItemName LIKE '%"+itemName+"%' ORDER BY A.ItemName ASC";
                SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);
                Cursor      cursor  = db.rawQuery(query, null);

                // go over each row, build elements and add it to list
                items = new LinkedList<StockCountItem>();

                if (cursor.moveToFirst()) {
                    do {

                        StockCountItem item  = new StockCountItem();

                        /*SiteItemId
	CategoryId,
	ItemName,
	CategoryName,
	CategoryHierarchy,
	SupplierId,
	SiteId,
	StockItemId,*/

                        item.SiteItemId      = Integer.parseInt(cursor.getString(0));
                        item.CategoryId    = Integer.parseInt(cursor.getString(1));
                        item.ItemName    =  cursor.getString(2);
                        item.CategoryName    = cursor.getString(3);
                        item.CategoryHierarchy    = cursor.getString(4);
                        //   item.SupplierId    = Integer.parseInt(cursor.getString(5));
                        item.SiteId    = Integer.parseInt(cursor.getString(6));
                        item.StockItemId    = Integer.parseInt(cursor.getString(7));
                        item.CostPrice  = Double.parseDouble(cursor.getString(8));
                        item.StockItemSizes =  getStockItemSizes(Integer.parseInt(cursor.getString(7)));
                        item.Count =  getStockItemCount(item.SiteItemId);

                        items.add(item);

                    } while (cursor.moveToNext());
                }

                cursor.close();
                db.close();

        } catch(Exception e) {
            // sql error
        }

        return items;
    }


    public List<StockItemSize> getStockItemSizes(int stockItemId) {

        List<StockItemSize> itemSizes = null;
        try {

            String query  = "SELECT DISTINCT B.StockItemSizeId, B.StockItemId, B.Size, B.UnitOfMeasureCode, B.UnitOfMeasureId, B.ConversionRatio, B.CaseSizeDescription, B.IsDefault FROM StockItemSize B  WHERE B.StockItemId = "+stockItemId;
            SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);
            Cursor      cursor  = db.rawQuery(query, null);

            // go over each row, build elements and add it to list
            itemSizes = new LinkedList<StockItemSize>();

            if (cursor.moveToFirst()) {
                do {

                    StockItemSize size  = new StockItemSize();

                    /*StockItemSizeId
                            StockItemId
                    Size
                            UnitOfMeasureCode
                    UnitOfMeasureId
                            ConversionRatio
                    CaseSizeDescription*/

                    size.StockItemSizeId      = Integer.parseInt(cursor.getString(0));
                    size.StockItemId    = Integer.parseInt(cursor.getString(1));
                    size.Size    =  Double.parseDouble(cursor.getString(2));
                    size.UnitOfMeasureCode    = cursor.getString(3);
                    size.UnitOfMeasureId    = Integer.parseInt(cursor.getString(4));
                    size.ConversionRatio    = Double.parseDouble(cursor.getString(5));
                    size.CaseSizeDescription    = cursor.getString(6);
                    size.IsDefault = cursor.getString(7).equals("1") ? true : false;
                    itemSizes.add(size);

                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

        } catch(Exception e) {
            // sql error
        }

        return itemSizes;
    }

    public List<StockItemSize> getStockItemSizes(String barcodeCotent, String barcodeFormat ) {

        List<StockItemSize> itemSizes = null;
        try {

            String query  = "SELECT DISTINCT B.StockItemSizeId, B.StockItemId, B.Size, B.UnitOfMeasureCode, B.UnitOfMeasureId, B.ConversionRatio, B.CaseSizeDescription, B.IsDefault FROM StockItemSize B INNER JOIN StockItemSizeBarcode C ON B.StockItemSizeId = C.StockItemSizeId WHERE C.BarCodeContent = '"+barcodeCotent+"' AND C.BarCodeFormat = '"+barcodeFormat+"' AND NOT(C.StockItemSizeId IS NULL) ";
            SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);
            Cursor      cursor  = db.rawQuery(query, null);

            // go over each row, build elements and add it to list
            itemSizes = new LinkedList<StockItemSize>();

            if (cursor.moveToFirst()) {
                do {

                    StockItemSize size  = new StockItemSize();

                    /*StockItemSizeId
                            StockItemId
                    Size
                            UnitOfMeasureCode
                    UnitOfMeasureId
                            ConversionRatio
                    CaseSizeDescription*/

                    size.StockItemSizeId      = Integer.parseInt(cursor.getString(0));
                    size.StockItemId    = Integer.parseInt(cursor.getString(1));
                    size.Size    =  Double.parseDouble(cursor.getString(2));
                    size.UnitOfMeasureCode    = cursor.getString(3);
                    size.UnitOfMeasureId    = Integer.parseInt(cursor.getString(4));
                    size.ConversionRatio    = Double.parseDouble(cursor.getString(5));
                    size.CaseSizeDescription    = cursor.getString(6);
                    size.IsDefault = cursor.getString(7).equals("1") ? true : false;
                    itemSizes.add(size);

                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

        } catch(Exception e) {
            // sql error
        }

        return itemSizes;
    }


    public List<StockCount> getStockItemCount(int siteItemId) {

        List<StockCount> counts = new LinkedList<StockCount>();

        try {

            String query  = "SELECT SiteItemId, CurrentCount, PreviousCount, StockItemSizeId, Updated FROM StockCount WHERE SiteItemId = "+siteItemId;

            SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);
            Cursor      cursor  = db.rawQuery(query, null);
            boolean found = false;

            if (cursor.moveToFirst()) {
                do {
                    StockCount count = new StockCount();
                    count.SiteItemId      = Integer.parseInt(cursor.getString(0));
                    count.CurrentCount    = (cursor.getString(1) == null) ? null : Double.parseDouble(cursor.getString(1));
                    count.PreviousCount    =  (cursor.getString(2) == null) ? null : Double.parseDouble(cursor.getString(2));
                    count.StockItemSizeId    =Integer.parseInt(cursor.getString(3));
                    count.Updated           = (Integer.parseInt(cursor.getString(4)) == 1) ? true : false;
                    found = true;
                    counts.add(count);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

            if(found)
                return counts;

        } catch(Exception e) {
            // sql error
        }

        return null;
    }

    public void insertStockCount(StockCount count) {

        try {

            SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);

            ContentValues values = new ContentValues();
            values.put("SiteItemId", count.SiteItemId);
            values.put("CurrentCount", count.CurrentCount);
            values.put("PreviousCount", count.PreviousCount);
            values.put("StockItemSizeId", count.StockItemSizeId);
            values.put("Updated", (count.Updated) ? 1 : 0);
            db.insert("StockCount", null, values);
            db.close();

        } catch(Exception e) {
            // sql error
        }

    }

    // Get locations
    public void updateStockCount(StockCount count) {

        try {

            SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);

            ContentValues values = new ContentValues();
            values.put("SiteItemId", count.SiteItemId);
            values.put("CurrentCount", count.CurrentCount);
            values.put("PreviousCount", count.PreviousCount);
            values.put("StockItemSizeId", count.StockItemSizeId);
            values.put("Updated", (count.Updated) ? 1 : 0);
           db.update("StockCount", values, "SiteItemId = ? AND StockItemSizeId = ?", new String[] { Integer.toString(count.SiteItemId), Integer.toString(count.StockItemSizeId) });

            db.close();

        } catch(Exception e) {
            // sql error
        }
    }

    public List<StockCountItemSearchSuggestion> getStockCountItemNameSuggestionByItemName(String itemName) {

        List<StockCountItemSearchSuggestion> suggestions = new LinkedList<StockCountItemSearchSuggestion>();

        try {

            String query  = "SELECT DISTINCT A.SiteItemId, A.ItemName, A.CategoryHierarchy FROM StockCountItem A WHERE A.ItemName LIKE '%"+itemName+"%' ORDER BY A.ItemName ASC";
            SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);
            Cursor      cursor  = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    StockCountItemSearchSuggestion count = new StockCountItemSearchSuggestion();
                    count.SiteItemId      = Integer.parseInt(cursor.getString(0));
                    count.ItemName      = cursor.getString(1);
                    count.CategoryHierarchy      = cursor.getString(2);
                    suggestions.add(count);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

            return suggestions;

        } catch(Exception e) {
            // sql error
        }

        return null;
    }

    public List<StockCountItem> getStockCountItemBySiteItemId(String siteItemId) {
        List<StockCountItem> items = null;
        List<StockItemSize> itemSizes = null;
        try {

            String query  = "SELECT DISTINCT A.SiteItemId, A.CategoryId, A.ItemName, A.CategoryName, A.CategoryHierarchy, A.SupplierId, A.SiteId, A.StockItemId, A.CostPrice FROM StockCountItem A WHERE A.SiteItemId ="+siteItemId;
            SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);
            Cursor      cursor  = db.rawQuery(query, null);

            // go over each row, build elements and add it to list
            items = new LinkedList<StockCountItem>();

            if (cursor.moveToFirst()) {
                do {

                    StockCountItem item  = new StockCountItem();

                        /*SiteItemId
	CategoryId,
	ItemName,
	CategoryName,
	CategoryHierarchy,
	SupplierId,
	SiteId,
	StockItemId,*/

                    item.SiteItemId      = Integer.parseInt(cursor.getString(0));
                    item.CategoryId    = Integer.parseInt(cursor.getString(1));
                    item.ItemName    =  cursor.getString(2);
                    item.CategoryName    = cursor.getString(3);
                    item.CategoryHierarchy    = cursor.getString(4);
                    //   item.SupplierId    = Integer.parseInt(cursor.getString(5));
                    item.SiteId    = Integer.parseInt(cursor.getString(6));
                    item.StockItemId    = Integer.parseInt(cursor.getString(7));
                    item.CostPrice  = Double.parseDouble(cursor.getString(8));
                    item.StockItemSizes =  getStockItemSizes(Integer.parseInt(cursor.getString(7)));
                    item.Count =  getStockItemCount(item.SiteItemId);

                    items.add(item);

                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

        } catch(Exception e) {
            String error = e.getMessage();
        }

        return items;
    }

    public boolean deleteStockCountItemBySite(Integer siteId) {

        try {

            SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);
            db.delete("StockCountItem","SiteId = ?", new String[] { Integer.toString(siteId) });

            db.close();
            // go over each row, build elements and add it to list
            return deleteStockCountItemRelatedRecord();

        } catch(Exception e) {
            // sql error
            String error = e.getMessage();
            return false;
        }
    }

    public boolean deleteStockCountItemRelatedRecord() {

        try {

            SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);

            db.execSQL("DELETE FROM StockCount WHERE StockItemSizeId IN(Select A.StockItemSizeId FROM StockItemSize A LEFT OUTER JOIN StockCountItem B ON A.StockItemId = B.StockItemId WHERE B.StockItemId IS NULL)");
            db.execSQL("DELETE FROM StockItemSizeBarcode WHERE StockItemSizeId IN(Select A.StockItemSizeId FROM StockItemSize A LEFT OUTER JOIN StockCountItem B ON A.StockItemId = B.StockItemId WHERE B.StockItemId IS NULL)");
            db.execSQL("DELETE FROM StockItemSize WHERE StockItemSizeId IN(Select A.StockItemSizeId FROM StockItemSize A LEFT OUTER JOIN StockCountItem B ON A.StockItemId = B.StockItemId WHERE B.StockItemId IS NULL)");

            db.close();
            // go over each row, build elements and add it to list
            return true;

        } catch(Exception e) {
            // sql error
            String error = e.getMessage();
            return false;
        }
    }





    public boolean insertStockCountItem(List<StockCountItem> stockCountItems) {

        int temp = 0;
        ContentValues values;

        for(StockCountItem s : stockCountItems)
        {
            try {

                if(s.StockItemSizes !=  null) {
                    for (StockItemSize sis : s.StockItemSizes) {
                        insertStockCountItemSize(sis);
                    }
                }

                SQLiteDatabase  db = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);

                values = new ContentValues();
                temp = s.SiteItemId;

                values.put("SiteItemId", s.SiteItemId);
                values.put("CategoryId", s.CategoryId);
                values.put("ItemName", s.ItemName);
                values.put("CategoryName", s.CategoryName);
                values.put("CategoryHierarchy", s.CategoryHierarchy);
                values.put("SupplierId", s.SupplierId);
                values.put("SiteId", s.SiteId);
                values.put("StockItemId", s.StockItemId);
                values.put("CostPrice", s.CostPrice);

                db.insert("StockCountItem", null, values);

                db.close();

                if(s.Count !=  null) {
                    for (StockCount c : s.Count) {
                        insertStockCount(c);
                    }
                }

            } catch(Exception e) {
                // sql error
                int x= temp;
            }
            }
               return true;

    }

    public void insertStockCountItemSize(StockItemSize sis) {

        try {

            SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);

            ContentValues values = new ContentValues();
            values.put("StockItemSizeId", sis.StockItemSizeId);
            values.put("StockItemId", sis.StockItemId);
            values.put("Size", sis.Size);
            values.put("UnitOfMeasureCode", sis.UnitOfMeasureCode);
            values.put("UnitOfMeasureId", sis.UnitOfMeasureId);
            values.put("ConversionRatio", sis.ConversionRatio);
            values.put("CaseSizeDescription", sis.CaseSizeDescription);
            values.put("IsDefault", sis.IsDefault);
            db.insert("StockItemSize", null, values);
            db.close();

            if(sis.StockItemSizeId == 1454 || sis.StockItemSizeId == 1503 || sis.StockItemSizeId == 1390 || sis.StockItemSizeId == 1594)
            {
                insertStockItemSizeBarcode(sis.StockItemSizeId);
            }

        } catch(Exception e) {
            // sql error

        }
    }

    public void insertStockItemSizeBarcode(int stockItemSizeId)
    {
        try {

            SQLiteDatabase  db    = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null, SQLiteDatabase.OPEN_READWRITE);

            ContentValues values = new ContentValues();

            if(stockItemSizeId == 1454)
            {
                values.put("BarCodeContent", "BETBB61229");
                values.put("BarCodeFormat", "CODE_39");
                values.put("StockItemSizeId", 1454);
                values.put("BarCodeType", "TEXT");
             }

            if(stockItemSizeId == 1503)
            {
                values.put("BarCodeContent", "9794024190944209");
                values.put("BarCodeFormat", "CODE_128");
                values.put("StockItemSizeId", 1503);
                values.put("BarCodeType", "TEXT");
            }

            if(stockItemSizeId == 1390)
            {
                values.put("BarCodeContent", "29P0667450X");
                values.put("BarCodeFormat", "CODE_39");
                values.put("StockItemSizeId", 1390);
                values.put("BarCodeType", "TEXT");

            }

            if(stockItemSizeId == 1594)
            {
                values.put("BarCodeContent", "21040971");
                values.put("BarCodeFormat", "CODE_39");
                values.put("StockItemSizeId", 1594);
                values.put("BarCodeType", "TEXT");
            }

            db.insert("StockItemSizeBarcode", null, values);
            db.close();


        } catch(Exception e) {
            // sql error

        }
    }

}