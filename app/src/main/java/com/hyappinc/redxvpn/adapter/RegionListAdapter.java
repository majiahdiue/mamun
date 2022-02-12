package com.hyappinc.redxvpn.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadrosid.svgloader.SvgLoader;
import com.anchorfree.partner.api.data.Country;
import com.hyappinc.redxvpn.R;
import com.hyappinc.redxvpn.activity.ChooseServerActivity;
import com.hyappinc.redxvpn.activity.SubscriptionActivity;
import com.hyappinc.redxvpn.utils.AdsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RegionListAdapter extends RecyclerView.Adapter<RegionListAdapter.ViewHolder> {

    private String nn;
    public Context context;
    private List<Country> regions;
    private ChooseServerActivity.RegionChooserInterface listAdapterInterface;

    public RegionListAdapter(ChooseServerActivity.RegionChooserInterface listAdapterInterface, Activity cntec) {
        this.listAdapterInterface = listAdapterInterface;
        this.context = cntec;
        try {
            SharedPreferences prefs = cntec.getSharedPreferences("whatsapp_pref",
                    Context.MODE_PRIVATE);
            nn = prefs.getString("inappads", "nnn");//"No name defined" is the default value.

        }catch (Exception e){

            nn= "nnn";
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_server_list_item, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
     try {


         final Country data = this.regions.get(position);
         Locale locale = new Locale("", data.getCountry());

         if (position == 0) {

             holder.flag.setImageResource(R.drawable.earth);
             holder.app_name.setText("Best Server");
             holder.app_name.setTextColor(context.getResources().getColor(R.color.white));
             holder.limit.setVisibility(View.GONE);
             holder.premium_img.setVisibility(View.GONE);

         } else {
             String url = "https://cdnjs.cloudflare.com/ajax/libs/flag-icon-css/3.4.3/flags/4x3/" + data.getCountry().toLowerCase() + ".svg";
             SvgLoader.pluck()
                     .with((Activity) context)
                     .setPlaceHolder(R.drawable.earth, R.drawable.earth)
                     .load(url, holder.flag);

             holder.app_name.setText(locale.getDisplayCountry());
             holder.limit.setVisibility(View.VISIBLE);

             if (position > 30) {
                 if (!nn.equals("nnn")) {
                     holder.premium_img.setVisibility(View.GONE);
                 } else {
                     holder.premium_img.setVisibility(View.VISIBLE);
                 }
             } else {
                 holder.premium_img.setVisibility(View.GONE);
             }

         }

         holder.itemView.setOnClickListener(view -> {

             if (position > 30) {
                 if (!nn.equals("nnn")) {
                     listAdapterInterface.onRegionSelected(regions.get(position), true);
                 } else {

                     final Dialog dialog = new Dialog(context);
                     dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                     dialog.setCancelable(true);
                     dialog.setContentView(R.layout.dialog_premium_videoads);

                     Button btn_yes = dialog.findViewById(R.id.btn_yes);
                     btn_yes.setOnClickListener(v -> {
                         dialog.dismiss();

                         AdsManager.showVideoAdAdmob((Activity) context, rewardItem -> listAdapterInterface.onRegionSelected(regions.get(position), true));

                     });

                     Button btn_no = dialog.findViewById(R.id.btn_no);
                     btn_no.setOnClickListener(v -> {
                         dialog.dismiss();
                         context.startActivity(new Intent(context, SubscriptionActivity.class));

                     });

                     dialog.show();
                 }
             } else {
                 listAdapterInterface.onRegionSelected(regions.get(position), false);

             }

         });

     }catch (Exception ignored){}
    }

    @Override
    public int getItemCount() {
        return regions != null ? regions.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRegions(List<Country> list) {
        regions = new ArrayList<>();
        regions.add(new Country(""));
        regions.addAll(list);
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView app_name;
        ImageView flag;
        ImageView limit;
        ImageView premium_img;

        ViewHolder(View v) {
            super(v);
            this.app_name = itemView.findViewById(R.id.country_name);
            this.limit = itemView.findViewById(R.id.signal_img);
            this.flag = itemView.findViewById(R.id.flag_img);
            this.premium_img = itemView.findViewById(R.id.premium_img);
        }
    }
}
