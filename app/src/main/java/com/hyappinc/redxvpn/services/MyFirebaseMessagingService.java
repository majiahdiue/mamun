package com.hyappinc.redxvpn.services;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hyappinc.redxvpn.R;
import com.hyappinc.redxvpn.activity.MainActivity;
import com.hyappinc.redxvpn.activity.RateUsWebView;
import com.hyappinc.redxvpn.utils.OreoNotification;

import java.util.Objects;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String MyTag = "MessagingService";
    static WindowManager windowManager2;
    static WindowManager.LayoutParams params;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.d("my token", "Refreshed token: " + s);
        sendRegistrationToServer(s);

    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        Log.d(MyTag, "Message Notification Body token is : " + token);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        try {

            // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
            Log.d(MyTag, "From: " + remoteMessage.getFrom());


            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.d(MyTag, "Message Notification Body: " + remoteMessage.getNotification().getBody());


                if (Objects.requireNonNull(remoteMessage.getNotification().getTitle()).trim().equalsIgnoreCase("rate")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        sendOreoNotificationWithURL(this, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getBody());
                    } else {
                        sendNotificationWithURL(this, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getBody());
                    }

                } else if (remoteMessage.getNotification().getTitle().trim().equalsIgnoreCase("rate2")) {


                    System.out.println("Rate2 Working here");


                    new Handler(Looper.getMainLooper()).post(() -> {

                        Toast toast = Toast.makeText(MyFirebaseMessagingService.this, "Rate Us", Toast.LENGTH_SHORT);
                        toast.show();
                        showInAppRateDialog(MyFirebaseMessagingService.this);

                    });

                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        sendOreoNotification(this, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
                    } else {
                        sendNotification(this, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
                    }
                }


            }

        } catch (Exception ignored) {
        }
    }


    public void sendNotification(Context context, String notificationTitle, String notificationBody) {

        Random random1 = new Random();
        int j = random1.nextInt(5);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(context, j, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_appicon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();
        int num = random.nextInt(5);

        notification.notify(num, builder.build());
    }


    public void sendOreoNotification(Context context, String notificationTitle, String notificationBody) {

        Random random1 = new Random();
        int j = random1.nextInt(5);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(context, j, intent, PendingIntent.FLAG_ONE_SHOT);

        OreoNotification oreoNotification = new OreoNotification(context);
        Notification.Builder builder = oreoNotification.getOreoNotification(notificationTitle, notificationBody, pendingIntent,
                R.drawable.ic_appicon);

        Random random = new Random();
        int num = random.nextInt(5);

        oreoNotification.getManager().notify(num, builder.build());

    }


    public void sendNotificationWithURL(Context context, String notificationTitle, String notificationBody, String link) {

        Random random1 = new Random();
        int j = random1.nextInt(5);

        Intent intent = new Intent(context, RateUsWebView.class);
        intent.putExtra("link", link);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(context, j, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_appicon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();
        int num = random.nextInt(5);

        notification.notify(num, builder.build());
    }


    public void sendOreoNotificationWithURL(Context context, String notificationTitle, String notificationBody, String link) {

        Random random1 = new Random();
        int j = random1.nextInt(5);

        Intent intent = new Intent(context, RateUsWebView.class);
        intent.putExtra("link", link);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(context, j, intent, PendingIntent.FLAG_ONE_SHOT);

        OreoNotification oreoNotification = new OreoNotification(context);
        Notification.Builder builder = oreoNotification.getOreoNotification(notificationTitle, notificationBody, pendingIntent, R.drawable.ic_appicon);

        Random random = new Random();
        int num = random.nextInt(5);

        oreoNotification.getManager().notify(num, builder.build());

    }


    public void showInAppRateDialog(Context Mcontext) {

        final Dialog dialog = new Dialog(Mcontext);

        windowManager2 = (WindowManager) Mcontext.getSystemService(WINDOW_SERVICE);

        int weidthParams;
        int heightParams;

        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) Mcontext).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displayMetrics);

            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            heightParams = height / 2;

            weidthParams = width / 2;

        } catch (Exception e) {
            weidthParams = WindowManager.LayoutParams.WRAP_CONTENT;
            heightParams = WindowManager.LayoutParams.WRAP_CONTENT;

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    weidthParams,
                    heightParams,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);

        } else {
            params = new WindowManager.LayoutParams(
                    weidthParams,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);

        }
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.x = 0;
        params.y = 100;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Objects.requireNonNull(dialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            Objects.requireNonNull(dialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        dialog.getWindow().setAttributes(params);


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_rateus);
        dialog.findViewById(R.id.notnow_dialog).setOnClickListener(view -> dialog.dismiss());
        dialog.findViewById(R.id.rateus_dialog).setOnClickListener(view -> {


            try {
                Mcontext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Mcontext.getPackageName())).setFlags(FLAG_ACTIVITY_NEW_TASK));
            } catch (android.content.ActivityNotFoundException e) {

                Mcontext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Mcontext.getPackageName())).setFlags(FLAG_ACTIVITY_NEW_TASK));
            }

            dialog.dismiss();
        });

        dialog.show();
    }

}
