package com.example.htan.myapplication;

import java.io.Serializable;
import java.util.List;

/**
 * Created by htan on 22/01/2015.
 */
public class StockCountItem implements Serializable {

   public int SiteItemId;
   public int CategoryId;
   public String ItemName;
   public String CategoryName;
   public String CategoryHierarchy;
   public int  SupplierId;
   public int SiteId;
   public int StockItemId;
   public double CostPrice;
   public List<StockItemSize> StockItemSizes;
    public List<StockCount> Count;
    // Parcelling part
 //   public StockCountItem(Parcel in){
 //       String[] data = new String[3];

  //      in.readStringArray(data);
//        this.id = data[0];
//        this.name = data[1];
//        this.grade = data[2];
//    }

 //   @Override
 //   public int describeContents(){
 //       return 0;
 //   }

//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        //dest.writeStringArray(new String[] {this.id,
//        //        this.name,
//        //        this.grade});
//    }
//    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
//        public StockCountItem createFromParcel(Parcel in) {
//            return new StockCountItem(in);
//        }
//
//        public StockCountItem[] newArray(int size) {
//            return new StockCountItem[size];
//        }
//    };
}
