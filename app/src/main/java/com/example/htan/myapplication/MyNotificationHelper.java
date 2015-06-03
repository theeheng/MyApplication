package com.example.htan.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;

/**
 * Created by Jim on 9/28/13.
 */
public class MyNotificationHelper {
    public static final int DOWNLOAD_NOTIFICATION_ID = 1;

    public static void displayNotification(Context context, String contentTitle, String contentText, int progress)  //, Location location)
    {
       // String mapUri =
       //         String.format("http://maps.google.com/maps?q=%.6f,%.6f",
       //                 location.getLatitude(), location.getLongitude());
        //Uri uri = Uri.parse(mapUri);
        //Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mapIntent, 0);

        //String message = formatNotificationMessage(location.getProvider(), location.getLatitude(), location.getLongitude());

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setProgress(100, progress, false);
                //.setContentIntent(pendingIntent);

        Notification notification = builder.getNotification();

        NotificationManager mgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//    String tag = this.getClass().getName();
        mgr.notify(DOWNLOAD_NOTIFICATION_ID, notification);
    }

    public static void removeNotification(Context context, String contentTitle, String contentText) {

        try {
            NotificationManager mgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setProgress(100, 100, false);
            //.setContentIntent(pendingIntent);

            Notification notification = builder.getNotification();

            mgr.notify(DOWNLOAD_NOTIFICATION_ID, notification);

            Thread.sleep(5000);

            mgr.cancel(DOWNLOAD_NOTIFICATION_ID);
        }
        catch (Exception ex) {

        }
    }

    //private static String formatNotificationMessage(String provider, double latitude, double longitude) {
    //    return String.format("%.6f/%.6f Provider:%s", latitude, longitude, provider);
    //}

}