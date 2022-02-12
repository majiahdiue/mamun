package com.hyappinc.redxvpn.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.hyappinc.redxvpn.adapter.RegionListAdapter;
import com.hyappinc.redxvpn.databinding.ActivityChooseServerBinding;
import com.hyappinc.redxvpn.utils.AdsManager;
import com.hyappinc.redxvpn.utils.Constants;
import com.hyappinc.redxvpn.utils.SharedPrefs;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ChooseServerActivity extends AppCompatActivity {

    private RegionListAdapter regionAdapter;
    private RegionChooserInterface regionChooserInterface;
    private ActivityChooseServerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChooseServerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);


        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        AdsManager.loadVideoAdAdmob(ChooseServerActivity.this);

        regionChooserInterface = (item, ispremium) -> {
            try {
            System.out.println("mycountry__datais " + item.getCountry() + " " + ispremium);

            Map<String, String> map = new HashMap<>();

            map.put(SharedPrefs.PREFERENCE_selectedcountry, item.getCountry());

            //  AdsManager.showVideoAdAdmob(ChooseServerActivity.this);


            SharedPrefs sharedPrefsFor = new SharedPrefs(ChooseServerActivity.this);

            sharedPrefsFor.setPreference(map);


                Intent intent = new Intent();
                intent.putExtra(Constants.COUNTRYDATA, item.getCountry());
                setResult(RESULT_OK, intent);
                finish();

            }catch (Exception ignored){}
        };

        binding.chooseServerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        regionAdapter = new RegionListAdapter((item, ispremium) -> {
          //  System.out.println("Clicked on premium = "+item.getCountry()+" "+ispremium);

            regionChooserInterface.onRegionSelected(item, ispremium);
        }, ChooseServerActivity.this);

        binding.chooseServerRecyclerView.setAdapter(regionAdapter);

        binding.swipelayout.setOnRefreshListener(() -> {

            loadServers();

            Toast.makeText(ChooseServerActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> binding.swipelayout.setRefreshing(false), 2000);
        });

        binding.refreshservers.setOnClickListener(v -> {
            loadServers();

            Toast.makeText(ChooseServerActivity.this, "Refreshed", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> binding.swipelayout.setRefreshing(false), 2000);
        });


        loadServers();


    }



    private void loadServers() {
        showProgress();
        UnifiedSDK.getInstance().getBackend().countries(new Callback<AvailableCountries>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void success(@NonNull final AvailableCountries countries) {
                hideProress();
                regionAdapter.setRegions(countries.getCountries());
                regionAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(@NonNull VpnException e) {
                hideProress();
            }
        });
    }

    private void showProgress() {
        binding.chooseServerProgress.setVisibility(View.VISIBLE);
        binding.chooseServerRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideProress() {
        binding.chooseServerProgress.setVisibility(View.GONE);
        binding.chooseServerRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public interface RegionChooserInterface {
        void onRegionSelected(Country item, boolean ispremium);
    }

}
