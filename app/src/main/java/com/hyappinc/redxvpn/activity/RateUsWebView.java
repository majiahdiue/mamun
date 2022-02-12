package com.hyappinc.redxvpn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hyappinc.redxvpn.databinding.ActivityRateUsWebViewBinding;


public class RateUsWebView extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityRateUsWebViewBinding binding = ActivityRateUsWebViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);
            try {
                Intent i = getIntent();
                String url = i.getStringExtra("link");
                if (url != null) {
                    binding.webviewView.loadUrl(url);
                } else {
                    binding.webviewView.loadUrl("https://www.google.com");
                }
            } catch (Exception ignored) {
            }
    }


}