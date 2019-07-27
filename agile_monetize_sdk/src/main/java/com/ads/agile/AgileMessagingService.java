package com.ads.agile;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
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
            String title = remoteMessage.getData().get(AgileEventParameter.AGILE__NOTIFICATION_TITLE);
            String message = remoteMessage.getData().get(AgileEventParameter.AGILE__NOTIFICATION_BODY);
            String img_url = remoteMessage.getData().get(AgileEventParameter.AGILE__NOTIFICATION_IMAGE);
            String segment_id = remoteMessage.getData().get(AgileEventParameter.AGILE__NOTIFICATION_CONTENT);
            String click_action = remoteMessage.getData().get(AgileEventParameter.AGILE__NOTIFICATION_ACTION);
            String external_url_flag = remoteMessage.getData().get(AgileEventParameter.AGILE__NOTIFICATION_FLAG);
            String external_url = remoteMessage.getData().get(AgileEventParameter.AGILE__NOTIFICATION_URL);
            bitmap = getBitmapfromUrl(img_url);
            sendNotification(title,message,bitmap,segment_id,click_action,external_url_flag,external_url);

        }
    }
    private String getLauncherActivityName() {
        String activityName = "";
        final PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(getPackageName());
        List<ResolveInfo> activityList = pm.queryIntentActivities(intent, 0);
        if (activityList != null) {
            activityName = activityList.get(0).activityInfo.name;
        }
        return activityName;
    }
    private void sendNotification(String title,String message, Bitmap image,String segmentid,String click_action,String external_url_flag,String external_url) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName(getPackageName(),getLauncherActivityName()));
        intent.putExtra(AgileEventParameter.AGILE__NOTIFICATION_CONTENT,segmentid);
        intent.putExtra(AgileEventParameter.AGILE__NOTIFICATION_ACTION,click_action);
        intent.putExtra(AgileEventParameter.AGILE__NOTIFICATION_FLAG,external_url_flag);
        intent.putExtra(AgileEventParameter.AGILE__NOTIFICATION_URL,external_url);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try
        {

            Drawable icon = getPackageManager().getApplicationIcon(getPackageName());


            Bitmap bitmap;
            bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            icon.draw(canvas);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setLargeIcon(bitmap)/*Notification icon image*/
                    .setSmallIcon(getApplicationInfo().icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Random r = new Random();
            int i1 = r.nextInt(80 - 65) + 65;
            notificationManager.notify(i1 /* ID of notification */, notificationBuilder.build());

        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

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