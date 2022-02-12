package com.hyappinc.redxvpn;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.sdk.NotificationConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hyappinc.redxvpn.utils.AppOpenManager;
import com.hyappinc.redxvpn.utils.Constants;
import com.onesignal.OneSignal;

import java.util.Random;

public class MainApplication extends Application {

    private static final String CHANNEL_ID = "com.hyappinc.redxvpn";
    AppOpenManager appOpenManager;

    public static UnifiedSDK unifiedSDK;

    @Override
    public void onCreate() {
        super.onCreate();
        try {


            appOpenManager = new AppOpenManager(this);

            initHydraSdk();


            OneSignal.initWithContext(this);
            OneSignal.setAppId(getString(R.string.onsignalappid));

            FirebaseMessaging.getInstance().subscribeToTopic("all");

            MobileAds.initialize(
                    this,
                    initializationStatus -> {
                    });


        } catch (Exception ignored) {
        }


    }

    public void initHydraSdk() {
        Random random = new Random();
        int num = random.nextInt(2);

        try {
            createNotificationChannel();
           // String basecarrier = (num == 0 ? Constants.BASE_CARRIER_ID : Constants.BASE_CARRIER_ID_2);
            String basecarrier = Constants.BASE_CARRIER_ID ;
            System.out.println("baseccccccccc " + basecarrier);
            ClientInfo clientInfo = ClientInfo.newBuilder()
                    .carrierId(basecarrier)
                    .build();
            unifiedSDK = UnifiedSDK.getInstance(clientInfo);
            NotificationConfig notificationConfig = NotificationConfig.newBuilder()
                    .title(getResources().getString(R.string.app_name))
                    .channelId(CHANNEL_ID)
                    .build();
            UnifiedSDK.update(notificationConfig);

            UnifiedSDK.setLoggingLevel(Log.VERBOSE);
        } catch (Exception ignored) {
        }
    }



    public SharedPreferences getPrefs() {
        return getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    private void createNotificationChannel() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getResources().getString(R.string.app_name)+"";
            String description = getResources().getString(R.string.app_name)+" notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }




















}
