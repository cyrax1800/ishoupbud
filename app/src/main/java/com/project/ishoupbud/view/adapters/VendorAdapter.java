package com.project.ishoupbud.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Vendor;
import com.project.ishoupbud.view.holders.VendorHolder;
import com.project.michael.base.views.adapters.FastAdapter;

/**
 * Created by michael on 4/10/17.
 */

public class VendorAdapter<Model> extends FastAdapter<Model> {

    private RadioButton lastChecked = null;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendor,parent,false);
        return super.onPostCreateViewHolder(new VendorHolder(itemView), viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VendorHolder vendorHolder = (VendorHolder) holder;
        Vendor vendor = (Vendor) mModelList.get(position);

        vendorHolder.tvName.setText(vendor.name);
        vendorHolder.tvAddress.setText(vendor.address);
        vendorHolder.tvPrice.setText("Rp. " + vendor.price);
    }
}
