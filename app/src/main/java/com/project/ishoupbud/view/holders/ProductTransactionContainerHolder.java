package com.project.ishoupbud.view.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.view.adapters.ProductTransactionAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 6/29/17.
 */

public class ProductTransactionContainerHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_vendor) public TextView tvVendor;
    @BindView(R.id.rv_item) public RecyclerView rvitem;
    @BindView(R.id.tv_biaya_kirim) public TextView tvBiayaKirim;
    @BindView(R.id.tv_jarak) public TextView tvJarak;
    @BindView(R.id.tv_estimasi_waktu) public TextView tvEstimasiWaktu;
    @BindView(R.id.total_progress_bar) public ProgressBar progressBar;
    @BindView(R.id.ll_detail_shipment) public LinearLayout llDetailShipment;
    public ProductTransactionAdapter<ShoppingCart> shoppingCartAdapter;


    public ProductTransactionContainerHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        shoppingCartAdapter = new ProductTransactionAdapter<>();

        progressBar.setVisibility(View.VISIBLE);
        llDetailShipment.setVisibility(View.GONE);
    }
}
