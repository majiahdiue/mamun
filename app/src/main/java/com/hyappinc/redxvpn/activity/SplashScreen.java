package com.hyappinc.redxvpn.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.hyappinc.redxvpn.BuildConfig;
import com.hyappinc.redxvpn.MainApplication;
import com.hyappinc.redxvpn.R;
import com.hyappinc.redxvpn.databinding.ActivitySplashScreenBinding;
import com.hyappinc.redxvpn.utils.NetworkState;

public class SplashScreen extends AppCompatActivity {


    private AuthMethod authMethod;
    private boolean isfirst;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       ActivitySplashScreenBinding binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

       binding.versionname.setText(String.format("%s %s", getString(R.string.version), BuildConfig.VERSION_NAME));


//
//        try {
//            PackageManager pm = getPackageManager();
//            pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), AFVpnService.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//
//        }catch (Exception e){}


        SharedPreferences prefs = getSharedPreferences("whatsapp_pref",
                Context.MODE_PRIVATE);
        isfirst = prefs.getBoolean("isfirsttime", false);


        if (isfirst) {


            MainApplication.unifiedSDK.getBackend().isLoggedIn(new Callback<Boolean>() {
                @Override
                public void success(@NonNull Boolean isLoggedIn) {

                        if (isLoggedIn) {
                            startMain();

                        } else {
                            login();
                        }

                }

                @Override
                public void failure(@NonNull VpnException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                    builder.setTitle(getString(R.string.network_error))
                            .setMessage(e.getMessage())
                            .setNegativeButton(getString(R.string.ok),
                                    (dialog, id) -> {
                                        dialog.cancel();
                                        onBackPressed();
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

         //   Toast.makeText(SplashScreen.this, "first", Toast.LENGTH_SHORT).show();
        } else {
           // Toast.makeText(SplashScreen.this, "noooooofirst", Toast.LENGTH_SHORT).show();

            login();
        }

    }
    private void login() {
        try {
            runOnUiThread(() -> {
//                if (nn.equals("nnn")) {
                    authMethod = AuthMethod.anonymous();
//
//                }else {
//                    authMethod = AuthMethod.custom("1uqgtl3uj8pvavdc47qtkl8cf27fqgk2dpnuojdc3taa4b7eaaak", Constants.BASE_OAUTH_METHOD);
//
//                }


                MainApplication.unifiedSDK.getBackend().login(authMethod, new Callback<User>() {
                    @Override
                    public void success(@NonNull User user) {


                        SharedPreferences.Editor prefsed = getSharedPreferences("whatsapp_pref",
                                Context.MODE_PRIVATE).edit();
                        prefsed.putBoolean("isfirsttime", true);
                        prefsed.apply();

                        startMain();
                    }

                    @Override
                    public void failure(@NonNull VpnException e) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                        builder.setTitle(getString(R.string.network_error))
                                .setMessage(e.getMessage())
                                .setNegativeButton(getString(R.string.ok),
                                        (dialog, id) -> {
                                            dialog.cancel();
                                            onBackPressed();
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();


                    }
                });

            });
        } catch (final Exception ex) {
            ex.printStackTrace();

            runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                builder.setTitle(getString(R.string.network_error))
                        .setMessage(ex.getMessage())
                        .setNegativeButton(getString(R.string.ok),
                                (dialog, id) -> {
                                    dialog.cancel();
                                    onBackPressed();
                                });
                AlertDialog alert = builder.create();
                alert.show();
            });
        }

    }




    private void startMain() {
        if (NetworkState.isNetworkAvailable(this)) {

            new CountDownTimer(1000, 2000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    System.out.println("ticking");
                }

                @Override
                public void onFinish() {
                    try {


                        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(myIntent);
                        finish();
                    } catch (Exception ignored) {
                    }
                }
            }.start();


        } else {

            runOnUiThread(() -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.network_error))
                        .setMessage(getString(R.string.network_error_message))
                        .setNegativeButton(getString(R.string.ok),
                                (dialog, id) -> {
                                    dialog.cancel();
                                    onBackPressed();
                                });
                AlertDialog alert = builder.create();
                alert.show();
            });
        }
    }



}