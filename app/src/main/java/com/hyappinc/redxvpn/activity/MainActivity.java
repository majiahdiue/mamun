package com.hyappinc.redxvpn.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.ahmadrosid.svgloader.SvgLoader;
import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.SessionInfo;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.exceptions.PartnerApiException;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.callbacks.TrafficListener;
import com.anchorfree.vpnsdk.callbacks.VpnStateListener;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionDeniedException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionRevokedException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.anchorfree.vpnsdk.transporthydra.HydraVpnTransportException;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetailsParams;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.material.navigation.NavigationView;
import com.hyappinc.redxvpn.BuildConfig;
import com.hyappinc.redxvpn.MainApplication;
import com.hyappinc.redxvpn.R;
import com.hyappinc.redxvpn.databinding.ActivityMainBinding;
import com.hyappinc.redxvpn.inappbilling.BillingClientSetup;
import com.hyappinc.redxvpn.utils.AdsManager;
import com.hyappinc.redxvpn.utils.BetterActivityResult;
import com.hyappinc.redxvpn.utils.Constants;
import com.hyappinc.redxvpn.utils.Converter;
import com.hyappinc.redxvpn.utils.SharedPrefs;
import com.northghost.caketube.CaketubeTransport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
public class MainActivity extends AppCompatActivity implements VpnStateListener, TrafficListener, NavigationView.OnNavigationItemSelectedListener {

    private String selectedCountry;
    private String selectedCountryName;
    protected static final String TAG = MainActivity.class.getSimpleName();
    private boolean isActivateButton = true;
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);
    private ActivityMainBinding binding;
    private BillingClient billingClient;

    private AdLoader.Builder builder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        try {

            initIInAppBillingacknologement();

            initAds();
            clickListners();
            setupDrawer();






        } catch (Exception ignored) {
        }


    }


    private void initIInAppBillingacknologement() {

        System.out.println("mypurchase12 2 = ");


        billingClient = BillingClientSetup.getInstance(MainActivity.this, (billingResult, list) -> {
            if (list != null) {
                for (Purchase purchase : list)
                    handleitemAlreadyPuchase(purchase);
            }
        });


        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                System.out.println("mypurchase 2 = ");


                loadAllSubscribePackage();

                billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult1, purchases) -> {
                    System.out.println("mypurchase 6.5 = ");


                    if (purchases.size() > 0) {
                        Constants.isSubactive = true;
                        binding.mycontentview.llSubscription.setVisibility(View.GONE);
                        for (Purchase purchase : purchases)
                            handleitemAlreadyPuchase(purchase);

                    } else {
                        Constants.isSubactive = false;
                        binding.mycontentview.llSubscription.setVisibility(View.VISIBLE);
                        System.out.println("mypurchase 4 = " + billingResult1.getResponseCode());
                        SharedPreferences.Editor prefs = getSharedPreferences("whatsapp_pref",
                                Context.MODE_PRIVATE).edit();
                        prefs.putString("inappads", "nnn");
                        prefs.apply();
                        System.out.println("mypurchase 9 nnndd= " + purchases.get(0).getSkus());
                    }

                });


            }

            @Override
            public void onBillingServiceDisconnected() {
//                Toast.makeText(MainActivity.this, "You are disconnected from Billing Service"
//                        , Toast.LENGTH_SHORT).show();

            }
        });



    }

    private void loadAllSubscribePackage() {

        if (billingClient.isReady()) {

            List<String> skuList = new ArrayList<> ();
            skuList.add(getString(R.string.playstoresubscription_premium1month));
            skuList.add(getString(R.string.playstoresubscription_premium3month));
            skuList.add(getString(R.string.playstoresubscription_premium6months));
            skuList.add(getString(R.string.playstoresubscription_premium12months));
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
            billingClient.querySkuDetailsAsync(params.build(),
                    (billingResult, skuDetailsList) -> {
                        System.out.println("mypurchase 1 = ");
                        try {
                            Constants.SkuDetailsList = skuDetailsList;

                            assert skuDetailsList != null;
                            System.out.println("mypurchase 0 = " + skuDetailsList.get(0));
                        } catch (Exception ignored) {
                        }


                    });

        } else {
            Toast.makeText(MainActivity.this, "Billing Client not ready", Toast.LENGTH_SHORT).show();
        }


    }

    private void handleitemAlreadyPuchase(Purchase purchases) {
        if (purchases.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {

            Constants.isSubactive = true;
            binding.mycontentview.llSubscription.setVisibility(View.INVISIBLE);
            SharedPreferences.Editor prefs = getSharedPreferences("whatsapp_pref",
                    Context.MODE_PRIVATE).edit();
            prefs.putString("inappads", "ppp");
            prefs.apply();

        }
    }


    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, binding.drawerLayout, null, 0, 0);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
    }

    private void clickListners() {
        binding.mycontentview.cvActivation.setOnClickListener(view -> vpnActivation());
        binding.mycontentview.cvCountrySelected.setOnClickListener(view -> MainApplication.unifiedSDK.getBackend().isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean isLoggedIn) {
                if (isActivateButton) {
                    if (isLoggedIn) {
                        try {
                            Intent i = new Intent(MainActivity.this, ChooseServerActivity.class);

                            activityLauncher.launch(i);
                            activityLauncher.setOnActivityResult(result -> {
                                if (result.getResultCode() == Activity.RESULT_OK) {
                                    String country = Objects.requireNonNull(Objects.requireNonNull(result.getData()).getStringExtra(Constants.COUNTRYDATA)).toLowerCase();

                                    System.out.println("mycountrydatais" + country + "_");

                                    //last time selected servers
                                    Locale locale = new Locale("", country);
                                    if (country.equals("")) {

                                        binding.mycontentview.txtCountry.setText("Best Server");
                                        binding.mycontentview.ibCountryFlag.setImageResource(MainActivity.this.getResources().getIdentifier("drawable/earth", "drawable", MainActivity.this.getPackageName()));
                                        System.out.println("mycountrydatais 00");


                                    } else {

                                        String url = "https://cdnjs.cloudflare.com/ajax/libs/flag-icon-css/3.4.3/flags/4x3/" + country + ".svg";

                                        SvgLoader.pluck()
                                                .with(MainActivity.this)
                                                .setPlaceHolder(R.drawable.earth, R.drawable.earth)
                                                .load(url, binding.mycontentview.ibCountryFlag);

                                        binding.mycontentview.txtCountry.setText(locale.getDisplayCountry());
//                                    binding.mycontentview.ibCountryFlag.setImageResource(MainActivity.this.getResources().getIdentifier("drawable/" + country, "drawable", MainActivity.this.getPackageName()));

                                    }
                                    selectedCountryName = locale.getDisplayCountry();
                                    selectedCountry = country;
                                    updateUI();

                                    UnifiedSDK.getVpnState(new Callback<VPNState>() {
                                        @Override
                                        public void success(@NonNull VPNState state) {
                                            if (state == VPNState.CONNECTED) {
                                                showMessage("Reconnecting to VPN with ");
                                                binding.mycontentview.txtStatus.setText("Reconnecting to VPN");
                                                MainApplication.unifiedSDK.getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                                                    @Override
                                                    public void complete() {
                                                        connectToVpn();
                                                    }

                                                    @Override
                                                    public void error(@NonNull VpnException e) {
                                                        // In this case we try to reconnect
                                                        selectedCountry = "";
                                                        connectToVpn();
                                                        handleError(e);
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void failure(@NonNull VpnException e) {
                                            handleError(e);
                                        }
                                    });
                                }
                            });
                        } catch (Exception ignored) {
                        }

                    } else {
                        showMessage("Connecting to Server");
                        login();
                    }
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                handleError(e);
            }
        }));

        binding.mycontentview.llSubscription.setOnClickListener(view -> {
            try {
                Intent i = new Intent(MainActivity.this, SubscriptionActivity.class);
                startActivity(i);
            } catch (Exception ignored) {
            }
        });

        binding.mycontentview.ibDower.setOnClickListener(view -> binding.drawerLayout.openDrawer(GravityCompat.START));


    }

    private void vpnActivation() {
        try {


            UnifiedSDK.getVpnState(new Callback<VPNState>() {
                @Override
                public void success(@NonNull VPNState vpnState) {
                    if (vpnState == VPNState.CONNECTED) {

                        //TODO Ad video ad
                        disconnectToVpn();
                    } else {
                        connectToVpn();
                    }
                }

                @Override
                public void failure(@NonNull VpnException e) {
                    Toast.makeText(MainActivity.this, "VPN Checking failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ignored) {
        }
    }



    private void login() {
        try {

            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait ... ");
            progressDialog.show();

            runOnUiThread(() -> {
//                if (nn.equals("nnn")) {
                AuthMethod authMethod = AuthMethod.anonymous();
                //authMethod = AuthMethod.custom("1uqgtl3uj8pvavdc47qtkl8cf27fqgk2dpnuojdc3taa4b7eaaak", Constants.BASE_OAUTH_METHOD);
//
//                }else {
//                    authMethod = AuthMethod.custom("1uqgtl3uj8pvavdc47qtkl8cf27fqgk2dpnuojdc3taa4b7eaaak", Constants.BASE_OAUTH_METHOD);
//
//                }


                MainApplication.unifiedSDK.getBackend().login(authMethod, new Callback<User>() {
                    @Override
                    public void success(@NonNull User user) {

                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Succesfully Connected, Try Connect VPN Now", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void failure(@NonNull VpnException e) {
                        progressDialog.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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


    private void disconnectToVpn() {

        try {
            MainApplication.unifiedSDK.getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                @Override
                public void complete() {
                    Toast.makeText(MainActivity.this, "VPN Disconnected", Toast.LENGTH_SHORT).show();
                    AdsManager.loadInterstitialAd(MainActivity.this);


                }

                @Override
                public void error(@NonNull VpnException e) {
                    handleError(e);

                }
            });
        } catch (Exception ignored) {
        }
    }

    private void connectToVpn() {

        try {
            MainApplication.unifiedSDK.getBackend().isLoggedIn(new Callback<Boolean>() {
                @Override
                public void success(@NonNull Boolean isLoggedIn) {
                    if (isLoggedIn) {
                        List<String> fallbackOrder = new ArrayList<>();
                        fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                        fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
                        fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
                        List<String> bypassDomains = new LinkedList<>();
                        bypassDomains.add("*facebook.com");
                        bypassDomains.add("*wtfismyip.com");
                        UnifiedSDK.getInstance().getVPN().start(new SessionConfig.Builder()
                                .withReason(TrackingConstants.GprReasons.M_UI)
                                .withTransportFallback(fallbackOrder)
                                .withVirtualLocation(selectedCountry)
                                .withCountry(selectedCountry)
                                .withTransport(HydraTransport.TRANSPORT_ID)
                                .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains))
                                .build(), new CompletableCallback() {
                            @Override
                            public void complete() {
                                //btn_activation.setText("Deactive");
                                Toast.makeText(MainActivity.this, "VPN Connected", Toast.LENGTH_SHORT).show();

                                AdsManager.loadInterstitialAd(MainActivity.this);

                            }

                            @Override
                            public void error(@NonNull VpnException e) {
                                Toast.makeText(MainActivity.this, "First Select a Country and Allow VPN Permission", Toast.LENGTH_LONG).show();

                            }
                        });
                    } else {
                       // Toast.makeText(MainActivity.this, "Restart VPN", Toast.LENGTH_SHORT).show();
                        showMessage("Login is not done please, wait");
                        login();
                    }

                }

                @Override
                public void failure(@NonNull VpnException e) {
                    handleError(e);


                }
            });

        } catch (Exception ignored) {
        }
    }

    public void handleError(Throwable e) {

        try {
            Log.w(TAG, e);
            if (e instanceof VpnException) {
                if (e instanceof HydraVpnTransportException) {
                    HydraVpnTransportException hydraVpnTransportException = (HydraVpnTransportException) e;
                    if (hydraVpnTransportException.getCode() == HydraVpnTransportException.HYDRA_DCN_BLOCKED_BW) {
                        showMessage("Client traffic exceeded");
                    }
                }
            } else if (e instanceof PartnerApiException) {
                switch (((PartnerApiException) e).getContent()) {
                    case PartnerApiException.CODE_NOT_AUTHORIZED:
                        showMessage("User unauthorized");
                        break;
                    case PartnerApiException.CODE_TRAFFIC_EXCEED:
                        showMessage("Server unavailable");
                        break;
                    default:
                        showMessage("Other error. Check PartnerApiException constants");
                        break;
                }
            }
        } catch (Exception f) {
            f.printStackTrace();
        }
    }

    private void showMessage(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    private void initAds() {
//        List<String> deviceIds = new ArrayList<>();
//        deviceIds.add("9ebfbaf96b856bdba6dc80cb503ca3b3a06ccc22");
//        RequestConfiguration requestConfiguration = new RequestConfiguration
//                .Builder()
//                .setTestDeviceIds(deviceIds)
//                .build();
//        MobileAds.setRequestConfiguration(requestConfiguration);


        try {
            SharedPreferences prefs = getSharedPreferences("whatsapp_pref",
                    Context.MODE_PRIVATE);
            String nn = prefs.getString("inappads", "nnn");//"No name defined" is the default value.


            if (nn != null && nn.equals("nnn") && Constants.show_Ads && !Constants.isSubactive) {

                MobileAds.initialize(this, initializationStatus -> {
                    Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                    for (String adapterClass : statusMap.keySet()) {
                        AdapterStatus status = statusMap.get(adapterClass);
                        if (status != null) {
                            Log.d("MyApp", String.format(
                                    "Adapter name: %s, Description: %s, Latency: %d",
                                    adapterClass, status.getDescription(), status.getLatency()));
                        }
                    }

                    AdsManager.loadAdmobNativeAd(MainActivity.this,binding.mycontentview.flAdplaceholder);
                    AdsManager.loadBannerAdsAdapter(MainActivity.this,binding.mycontentview.bannerContainer);



                });


            } else {
                binding.mycontentview.flAdplaceholder.setVisibility(View.GONE);
                binding.mycontentview.bannerContainer.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }





    @Override
    public void vpnStateChanged(@NonNull VPNState vpnState) {
        updateUI();
    }

    private void updateUI() {
        try {


            UnifiedSDK.getVpnState(new Callback<VPNState>() {
                @Override
                public void success(@NonNull VPNState vpnState) {

                    switch (vpnState) {
                        case IDLE: {
                            // binding.mycontentview.pulsator.stop();

                            binding.mycontentview.animationView.setVisibility(View.GONE);
                            binding.mycontentview.animationView.pauseAnimation();
                            binding.mycontentview.cvActivation.setVisibility(View.VISIBLE);

                            binding.mycontentview.txtStatus.setText("");
                            binding.mycontentview.rlActivation.setBackground(getResources().getDrawable(R.drawable.activation_disable_bg));

                            isActivateButton = true;
                            binding.mycontentview.txtConnectionTraffic.setVisibility(View.INVISIBLE);
                            binding.mycontentview.txtConnectionSpeed.setVisibility(View.INVISIBLE);
                            break;
                    }
                    case CONNECTED: {
                        // binding.mycontentview.pulsator.stop();
                        runOnUiThread(() -> binding.mycontentview.versionname.performClick());
                        binding.mycontentview.animationView.setVisibility(View.GONE);
                        binding.mycontentview.animationView.pauseAnimation();
                        binding.mycontentview.cvActivation.setVisibility(View.VISIBLE);

                        binding.mycontentview.txtStatus.setText("Connected");
                        binding.mycontentview.rlActivation.setBackground(getResources().getDrawable(R.drawable.activation_bg));
                        isActivateButton = true;

                        binding.mycontentview.txtConnectionTraffic.setVisibility(View.VISIBLE);
                        binding.mycontentview.txtConnectionSpeed.setVisibility(View.VISIBLE);
                        checkRemainingTraffic();

                        break;
                    }
                    case CONNECTING_VPN: {
                        runOnUiThread(() -> binding.mycontentview.versionname.performClick());
                        binding.mycontentview.txtStatus.setText("Security Checking");
                        binding.mycontentview.rlActivation.setClickable(false);
                        binding.mycontentview.rlActivation.setBackground(getResources().getDrawable(R.drawable.activation_bg));
                        isActivateButton = false;
                        binding.mycontentview.cvActivation.setVisibility(View.GONE);

                        binding.mycontentview.txtConnectionTraffic.setVisibility(View.INVISIBLE);
                        binding.mycontentview.txtConnectionSpeed.setVisibility(View.INVISIBLE);
                        binding.mycontentview.animationView.setVisibility(View.VISIBLE);
                        binding.mycontentview.animationView.playAnimation();
                        break;
                    }
                    case CONNECTING_CREDENTIALS: {
                        runOnUiThread(() -> binding.mycontentview.versionname.performClick());
                        binding.mycontentview.txtStatus.setText("Getting Server Info");
                        isActivateButton = false;
                        binding.mycontentview.txtConnectionTraffic.setVisibility(View.INVISIBLE);
                        binding.mycontentview.txtConnectionSpeed.setVisibility(View.INVISIBLE);
                        binding.mycontentview.cvActivation.setVisibility(View.GONE);
                        binding.mycontentview.animationView.setVisibility(View.VISIBLE);
                        binding.mycontentview.animationView.playAnimation();
                        break;
                    }
                    case CONNECTING_PERMISSIONS: {
                        runOnUiThread(() -> binding.mycontentview.versionname.performClick());
                        binding.mycontentview.txtStatus.setText("Connecting VPN");
                        isActivateButton = false;
                        binding.mycontentview.txtConnectionTraffic.setVisibility(View.INVISIBLE);
                        binding.mycontentview.txtConnectionSpeed.setVisibility(View.INVISIBLE);
                        //  binding.mycontentview.pulsator.start();
                        binding.mycontentview.cvActivation.setVisibility(View.GONE);

                        binding.mycontentview.animationView.setVisibility(View.VISIBLE);
                        binding.mycontentview.animationView.playAnimation();
                        binding.mycontentview.cvActivation.setVisibility(View.GONE);

                        break;
                    }
                        case PAUSED: {
                            binding.mycontentview.txtConnectionTraffic.setVisibility(View.INVISIBLE);
                            binding.mycontentview.txtConnectionSpeed.setVisibility(View.INVISIBLE);
                            binding.mycontentview.txtStatus.setText("Paused");
                            binding.mycontentview.rlActivation.setBackground(getResources().getDrawable(R.drawable.activation_bg));
                            isActivateButton = true;
                            break;
                        }
                        default: {

                            binding.mycontentview.animationView.setVisibility(View.GONE);
                            binding.mycontentview.animationView.pauseAnimation();
                            binding.mycontentview.cvActivation.setVisibility(View.VISIBLE);

                            binding.mycontentview.txtStatus.setText("Disconnecting");
                            binding.mycontentview.rlActivation.setBackground(getResources().getDrawable(R.drawable.activation_disable_bg));

                            isActivateButton = true;
                            binding.mycontentview.txtConnectionTraffic.setVisibility(View.INVISIBLE);
                            binding.mycontentview.txtConnectionSpeed.setVisibility(View.INVISIBLE);

                            break;
                        }
                    }
            }

            @Override
            public void failure(@NonNull VpnException e) {
              //  binding.mycontentview.pulsator.stop();
                binding.mycontentview.animationView.setVisibility(View.GONE);
                binding.mycontentview.animationView.pauseAnimation();
                binding.mycontentview.cvActivation.setVisibility(View.VISIBLE);

                binding.mycontentview.rlActivation.setClickable(true);
                binding.mycontentview.txtStatus.setText("VPN can't connect, please try again..");
                binding.mycontentview.rlActivation.setBackground(getResources().getDrawable(R.drawable.activation_disable_bg));
            }
        });
        MainApplication.unifiedSDK.getBackend().isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean isLoggedIn) {
                System.out.println("Succesfully Connected");
                //make connect button enabled
            }

            @Override
            public void failure(@NonNull VpnException e) {
                System.out.println("Connecting failed "+e.getMessage());

            }
        });
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState state) {
                if (state == VPNState.CONNECTED) {
                    UnifiedSDK.getStatus(new Callback<SessionInfo>() {
                        @Override
                        public void success(@NonNull SessionInfo sessionInfo) {
                            runOnUiThread(() -> {
                                if (selectedCountryName != null) {
                                    if (selectedCountryName.equals("")) {
                                        binding.mycontentview.txtCountry.setText("Best Server");
                                        binding.mycontentview.ibCountryFlag.setImageResource(R.drawable.earth);
                                    } else {
                                        binding.mycontentview.txtCountry.setText(selectedCountryName );

                                        String url = "https://cdnjs.cloudflare.com/ajax/libs/flag-icon-css/3.4.3/flags/4x3/" + selectedCountry + ".svg";

                                        SvgLoader.pluck()
                                                .with(MainActivity.this)
                                                .setPlaceHolder(R.drawable.earth, R.drawable.earth)
                                                .load(url, binding.mycontentview.ibCountryFlag);
                                    }

                                } else {
                                    binding.mycontentview.txtCountry.setText("Select a Server");
                                    binding.mycontentview.ibCountryFlag.setImageResource(R.drawable.earth);
                                    String url = "https://cdnjs.cloudflare.com/ajax/libs/flag-icon-css/3.4.3/flags/4x3/" + selectedCountry + ".svg";

                                    SvgLoader.pluck()
                                            .with(MainActivity.this)
                                            .setPlaceHolder(R.drawable.earth, R.drawable.earth)
                                            .load(url, binding.mycontentview.ibCountryFlag);
                                }
                            });

                        }

                        @Override
                        public void failure(@NonNull VpnException e) {
                            runOnUiThread(() -> {
                                if (selectedCountryName != null) {
                                    if (selectedCountryName.equals("")) {
                                        binding.mycontentview.txtCountry.setText("Best Server");
                                        binding.mycontentview.ibCountryFlag.setImageResource(R.drawable.earth);
                                    } else {
                                        binding.mycontentview.txtCountry.setText(selectedCountryName );

                                        String url = "https://cdnjs.cloudflare.com/ajax/libs/flag-icon-css/3.4.3/flags/4x3/" + selectedCountry + ".svg";

                                        SvgLoader.pluck()
                                                .with(MainActivity.this)
                                                .setPlaceHolder(R.drawable.earth, R.drawable.earth)
                                                .load(url, binding.mycontentview.ibCountryFlag);
                                    }

                                } else {
                                    binding.mycontentview.txtCountry.setText("Select a Server");
                                    binding.mycontentview.ibCountryFlag.setImageResource(R.drawable.earth);
                                    String url = "https://cdnjs.cloudflare.com/ajax/libs/flag-icon-css/3.4.3/flags/4x3/" + selectedCountry + ".svg";

                                    SvgLoader.pluck()
                                            .with(MainActivity.this)
                                            .setPlaceHolder(R.drawable.earth, R.drawable.earth)
                                            .load(url, binding.mycontentview.ibCountryFlag);
                                }
                            });
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        if (selectedCountryName != null) {
                            if (selectedCountryName.equals("")) {
                                binding.mycontentview.txtCountry.setText("Best Server");
                                binding.mycontentview.ibCountryFlag.setImageResource(R.drawable.earth);
                            } else {
                                binding.mycontentview.txtCountry.setText(selectedCountryName );

                                String url = "https://cdnjs.cloudflare.com/ajax/libs/flag-icon-css/3.4.3/flags/4x3/" + selectedCountry + ".svg";

                                SvgLoader.pluck()
                                        .with(MainActivity.this)
                                        .setPlaceHolder(R.drawable.earth, R.drawable.earth)
                                        .load(url, binding.mycontentview.ibCountryFlag);
                            }

                        } else {
                            binding.mycontentview.txtCountry.setText("Select a Server");
                            binding.mycontentview.ibCountryFlag.setImageResource(R.drawable.earth);
                            String url = "https://cdnjs.cloudflare.com/ajax/libs/flag-icon-css/3.4.3/flags/4x3/" + selectedCountry + ".svg";

                            SvgLoader.pluck()
                                    .with(MainActivity.this)
                                    .setPlaceHolder(R.drawable.earth, R.drawable.earth)
                                    .load(url, binding.mycontentview.ibCountryFlag);
                        }
                    });
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                binding.mycontentview.txtCountry.setText("Select a Server");
                binding.mycontentview.ibCountryFlag.setImageDrawable(getResources().getDrawable(R.drawable.select_flag_image));
            }
        });

        } catch (Exception g) {
            g.printStackTrace();
        }
    }

    @Override
    public void vpnError(@NonNull VpnException e) {
        try{
        updateUI();
        handleError(e);
        }catch (Exception ignored){}
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {

            AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(MainActivity.this)
                    .withListener(new AppUpdaterUtils.UpdateListener() {
                        @Override
                        public void onSuccess(Update update, Boolean isUpdateAvailable) {
                            Log.d("Is update available?", Boolean.toString(isUpdateAvailable));
                            if (isUpdateAvailable) {

                                launchUpdateDialog(update.getLatestVersion());


                            }

                        }

                        @Override
                        public void onFailed(AppUpdaterError error) {
                            Log.d("AppUpdater Error", "Something went wrong");
                        }
                    });
            appUpdaterUtils.start();


            SharedPrefs sharedPrefsFor = new SharedPrefs(this);
            Map<String, String> map = sharedPrefsFor.getPreference(SharedPrefs.PREFERENCE);
            if (map != null) {
                String country;
                if (Constants.isSubactive) {
                    country = map.get(SharedPrefs.PREFERENCE_selectedcountry) + "";
                } else {
                    country = map.get(SharedPrefs.PREFERENCE_selectedcountry) + "";
                }

                Locale locale = new Locale("", country);

                selectedCountryName = locale.getDisplayCountry();
                selectedCountry = locale.getCountry().toLowerCase();
            }
            UnifiedSDK.addVpnStateListener(this);
            UnifiedSDK.addTrafficListener(this);
        } catch (Exception ignored) {
        }
    }

    private void launchUpdateDialog(String latestVersion) {

        try {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.updqteavaliable))
                    .setCancelable(false)
                    .setMessage(
                            getString(R.string.update) + " " + latestVersion + " " + getString(R.string.updateisavaliabledownload) + getString(
                                    R.string.app_name
                            )
                    )

                    .setPositiveButton(getResources().getString(R.string.update_now), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(
                                    new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://play.google.com/store/apps/details?id=com.hyappinc.redxvpn")
                                    )
                            );
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            }).setIcon(R.drawable.ic_appicon).show();


        } catch (Exception e) {

            System.out.println("appupdater error rrrr " + e);
            e.printStackTrace();
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        try {
            UnifiedSDK.removeVpnStateListener(this);
        } catch (Exception ignored) {
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try{
        int id = item.getItemId();

        if (id == R.id.nav_unlock) {

            if (Constants.isSubactive) {

                Toast.makeText(this, "You have already taken subscription..", Toast.LENGTH_SHORT).show();
            } else {
            Intent i = new Intent(MainActivity.this, SubscriptionActivity.class);
            startActivity(i);
            }

        } else if (id == R.id.nav_helpus) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"inappsstudio@gmail.com\n"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Improvement Comments For Quick VPN");
            intent.putExtra(Intent.EXTRA_TEXT, "Enter your message here");

            try {
                startActivity(Intent.createChooser(intent, "send mail"));
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(this, "No mail app found!!!", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(this, "Unexpected Error!!!", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_rate) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));


        } else if (id == R.id.nav_share) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share app");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I am using "+getString(R.string.app_name)+" , it gives me VIP high speed servers at free https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                System.out.println("mydatas = errors");

            }
        } else if (id == R.id.nav_policy) {
            Uri uri = Uri.parse(getResources().getString(R.string.privacy_policy_link)); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;

        }catch (Exception ignored){
            return true;
        }
    }

    @Override
    public void onTrafficUpdate(long l, long l1) {
        try{
        System.out.println("mydatas = "+l);
        String outString = Converter.humanReadableByteCountOld(l, false);
        String inString = Converter.humanReadableByteCountOld(l1, false);

        String speedtext= getResources().getString(R.string.traffic_stats, outString, inString);
         binding.mycontentview.txtConnectionSpeed.setText(speedtext);

//        NotificationConfig.Builder builder = NotificationConfig.newBuilder();
//        builder.inConnected("Connected","Vpn is Connected \n "+speedtext);
//        builder.inConnecting("Connecting","Vpn is Connecting");
//        UnifiedSDK.update(builder.build());
        checkRemainingTraffic();
        }catch (Exception ignored){}
    }


    public void checkRemainingTraffic() {
        try{
        UnifiedSDK.getInstance().getBackend().remainingTraffic(new Callback<RemainingTraffic>() {
            @Override
            public void success(@NonNull RemainingTraffic remainingTraffic) {
                updateRemainingTraffic(remainingTraffic);
            }

            @Override
            public void failure(@NonNull VpnException e) {
                updateUI();

                handleError(e);
            }
        });
        }catch (Exception ignored){}}


    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        try {
            if (remainingTrafficResponse.isUnlimited()) {
                binding.mycontentview.txtConnectionTraffic.setText("UNLIMITED");
            } else {
                String trafficUsed = Converter.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
                String trafficLimit = Converter.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";

                binding.mycontentview.txtConnectionTraffic.setText(getResources().getString(R.string.traffic_limit, trafficUsed, trafficLimit));
            }
        } catch (Exception ignored) {
        }
    }




}

