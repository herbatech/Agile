package com.herba.sdk.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;


public class AgileMessagingService extends FirebaseMessagingService {

    Bitmap bitmap;
    private static final String TAG = AgileMessagingService.class.getSimpleName() ;
    LocalBroadcastManager localBroadcastManager;
    @Override
    public void onCreate() {
         localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

         if (remoteMessage.getData().size() > 0)  {
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("body");
            String img_url = remoteMessage.getData().get("img_url");
            String segment_id = remoteMessage.getData().get("segment_id");
            String click_action = remoteMessage.getData().get("click_action");
            String external_url_flag = remoteMessage.getData().get("external_url_flag");
            String external_url = remoteMessage.getData().get("external_url");
            bitmap = getBitmapfromUrl(img_url);
            sendNotification(title,message,bitmap,segment_id,click_action,external_url_flag,external_url);

        }
    }
    private void sendNotification(String title,String message, Bitmap image,String segmentid,String click_action,String external_url_flag,String external_url) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("segment_id",segmentid);
        intent.putExtra("click_action",click_action);
        intent.putExtra("external_url_flag",external_url_flag);
        intent.putExtra("external_url",external_url);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))/*Notification icon image*/
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Random r = new Random();
           int i1 = r.nextInt(80 - 65) + 65;
            notificationManager.notify(i1 /* ID of notification */, notificationBuilder.build());
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}