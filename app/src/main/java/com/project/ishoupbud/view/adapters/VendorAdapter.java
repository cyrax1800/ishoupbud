package com.project.ishoupbud.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.ProductVendors;
import com.project.ishoupbud.api.model.Vendor;
import com.project.ishoupbud.view.holders.VendorHolder;
import com.project.michael.base.utils.StringUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.adapters.FastAdapter;

/**
 * Created by michael on 4/10/17.
 */

public class VendorAdapter<Model> extends FastAdapter<Model> {

    private RadioButton lastChecked = null;
    private int checkedIdx = -1;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendor,parent,false);
        return super.onPostCreateViewHolder(new VendorHolder(itemView), viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final VendorHolder vendorHolder = (VendorHolder) holder;
        ProductVendors vendor = (ProductVendors) mModelList.get(position);

        vendorHolder.tvName.setText(vendor.vendor.name);
        if(StringUtils.isNullOrEmpty(vendor.distance)){
            vendorHolder.tvAddress.setText("");
        }else{
            vendorHolder.tvAddress.setText(vendor.distance);
        }
        vendorHolder.tvPrice.setText(Utils.indonesiaFormat(vendor.price));
        vendorHolder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(lastChecked != null) lastChecked.setChecked(false);
                lastChecked = vendorHolder.radioButton;
                checkedIdx = vendorHolder.getAdapterPosition();
            }
        });
    }

    public int getCheckedIdx(){
        return checkedIdx;
    }


}
