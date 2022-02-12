package com.hyappinc.redxvpn.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.hyappinc.redxvpn.adapter.MyProductAdapter;
import com.hyappinc.redxvpn.databinding.ActivitySubscriptionBinding;
import com.hyappinc.redxvpn.inappbilling.BillingClientSetup;
import com.hyappinc.redxvpn.utils.Constants;

import java.util.List;


public class SubscriptionActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private ActivitySubscriptionBinding binding;

    BillingClient billingClient;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    private MyProductAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        try {
            init();
            setupBillingClient();


            adapter = new MyProductAdapter(SubscriptionActivity.this, Constants.SkuDetailsList, billingClient);
            binding.recyclerViewSub.setAdapter(adapter);

        } catch (Exception ignored) {
        }
    }


    private void init() {

        binding.recyclerViewSub.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        binding.backImg.setOnClickListener(view -> {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        });


    }

    private void setupBillingClient() {



        acknowledgePurchaseResponseListener = billingResult -> {
            System.out.println("mypurchase 4 = ");

                binding.textPremium.setVisibility(View.VISIBLE);
                binding.recyclerViewSub.setVisibility(View.GONE);

                SharedPreferences.Editor prefs = getSharedPreferences("whatsapp_pref",
                        Context.MODE_PRIVATE).edit();
                prefs.putString("inappads", "ppp");
                prefs.apply();
                System.out.println("mypurchase 9 ppdone 1= ");


        };
        billingClient = BillingClientSetup.getInstance(SubscriptionActivity.this, SubscriptionActivity.this);
        billingClient.startConnection(new BillingClientStateListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                System.out.println("mypurchase 2 = ");


                binding.textPremium.setVisibility(View.GONE);
                binding.recyclerViewSub.setVisibility(View.VISIBLE);
              //  loadAllSubscribePackage();
                adapter.notifyDataSetChanged();


                billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult1, purchases) -> {
                    System.out.println("mypurchase 6.5 = " );


                    if (purchases.size() > 0) {
                        Constants.isSubactive = true;
                        binding.recyclerViewSub.setVisibility(View.GONE);
                        for (Purchase purchase : purchases)
                            handleitemAlreadyPuchase(purchase);

                    } else {
                        Constants.isSubactive = false;
                        System.out.println("mypurchase ttt = ");

                        binding.textPremium.setVisibility(View.GONE);
                        binding.recyclerViewSub.setVisibility(View.VISIBLE);
                       // loadAllSubscribePackage();
                        adapter.notifyDataSetChanged();

                        System.out.println("mypurchase 4 = "+ billingResult1.getResponseCode());
                        SharedPreferences.Editor prefs = getSharedPreferences("whatsapp_pref",
                                Context.MODE_PRIVATE).edit();
                        prefs.putString("inappads", "nnn");
                        prefs.apply();
                        System.out.println("mypurchase 9 nnndd= "+purchases.get(0).getSkus());
                    }


                    });


            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(SubscriptionActivity.this, "You are disconnected from Billing Service"
                        , Toast.LENGTH_SHORT).show();

            }
        });
    }

//    private void loadAllSubscribePackage() {
//
//        if (billingClient.isReady()) {
//
//            List<String> skuList = new ArrayList<> ();
//            skuList.add(getString(R.string.playstoresubscription_premium1month));
//            skuList.add(getString(R.string.playstoresubscription_premium6months));
//            skuList.add(getString(R.string.playstoresubscription_premium12months));
//            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
//            params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
//            billingClient.querySkuDetailsAsync(params.build(),
//                    new SkuDetailsResponseListener() {
//                        @Override
//                        public void onSkuDetailsResponse(BillingResult billingResult,
//                                                         List<SkuDetails> skuDetailsList) {
//                            System.out.println("mypurchase 1 = ");
//                            try {
//                                System.out.println("mypurchase 0 = " + skuDetailsList.get(0));
//                            }catch (Exception e){}
//
//                                adapter = new MyProductAdapter(SubscriptionActivity.this, skuDetailsList, billingClient);
//                                binding.recyclerViewSub.setAdapter(adapter);
//
//                        }
//                    });
//
//        } else {
//            Toast.makeText(SubscriptionActivity.this, "Billing Client not ready", Toast.LENGTH_SHORT).show();
//        }
//
//
//    }

    @SuppressLint("SetTextI18n")
    private void handleitemAlreadyPuchase(Purchase purchases) {
        if (purchases.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {

            if (!purchases.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchases.getPurchaseToken())
                        .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            } else {

                binding.textPremium.setVisibility(View.VISIBLE);
                binding.textPremium.setText("You are Already Subscribed !!!");
                binding.recyclerViewSub.setVisibility(View.GONE);
                SharedPreferences.Editor prefs = getSharedPreferences("whatsapp_pref",
                        Context.MODE_PRIVATE).edit();
                prefs.putString("inappads", "ppp");
                prefs.apply();
                System.out.println("mypurchase 9 ppdone 0= ");

            }
        }
    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        try {
            if (list != null) {
                for (Purchase purchase : list)
                    handleitemAlreadyPuchase(purchase);
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED)
                Toast.makeText(this, "User has been cancelled", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Error" + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
        } catch (Exception ignored) {
        }
    }



}
