package com.project.ishoupbud.view.adapters;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.model.Vendor;
import com.project.ishoupbud.view.activities.ProductActivity;
import com.project.ishoupbud.view.holders.ProductDetailHolder;
import com.project.ishoupbud.view.holders.ProductDetailPagerHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 6/26/17.
 */

public class ProductDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static String TAG = "tmp-detail";

    public Product product;
    ProductActivity productActivity;

    public ProductDetailAdapter(Product product, ProductActivity productActivity){
        this.product = product;
        this.productActivity = productActivity;
        Log.d(TAG, "ProductDetailAdapter: ");
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: " + viewType);
        if(viewType == 0){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_detail, parent, false);
            return new ProductDetailHolder(itemView);
        }else{
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_detail_pager, parent, false);
            return new ProductDetailPagerHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ProductDetailHolder){
            ProductDetailHolder productDetailHolder = (ProductDetailHolder) holder;
            productDetailHolder.tvProductName.setText(product.name);
            productDetailHolder.tvRatingSummary.setText(String.valueOf(product.totalRating));
            productDetailHolder.tvTotalRater.setText("(" + product.totalReview + " Reviews)");
            productDetailHolder.ratingBar.setRating((float) product.totalRating);
            if(product.productSummary == null){
                productDetailHolder.tvSentiment.setText("OverAll: Netral");
            }else{
                productDetailHolder.tvSentiment.setText(productActivity.calculateSummary());
            }
            productDetailHolder.rvVendor.setLayoutManager(new LinearLayoutManager(productActivity.getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            productDetailHolder.rvVendor.setAdapter(productActivity.vendorAdapter);
            productDetailHolder.btnAddToCart.setOnClickListener(productActivity);
            productDetailHolder.btnCompare.setOnClickListener(productActivity);
            productDetailHolder.stepperView.setValue(productActivity.productQuantity);
        }else{
            final ProductDetailPagerHolder productDetailPagerHolder = (ProductDetailPagerHolder) holder;
            productDetailPagerHolder.viewPager.setAdapter(productActivity.productPagerAdapter);
            productDetailPagerHolder.viewPager.setOffscreenPageLimit(2);
            productDetailPagerHolder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 0) {
                        productActivity.productPagerAdapter.productDetailFragment.updateDetail(product.description);
                    } else if (position == 1) {
                    } else if (position == 2) {
                        productActivity.productPagerAdapter.productStatisticFragment.initialRequest();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            productDetailPagerHolder.tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    productDetailPagerHolder.tabLayout.setupWithViewPager(productDetailPagerHolder.viewPager);
                    productActivity.productPagerAdapter.productDetailFragment.updateDetail(product.description);
                    productActivity.productPagerAdapter.productReviewFragment.setProductId(product.id);
                    List<Vendor> vendors = new ArrayList<>();
                    for (int i = 0; i < product.vendors.size(); i++) {
                        vendors.add(product.vendors.get(i).vendor);
                    }
                    productActivity.productPagerAdapter.productReviewFragment.setVendor(vendors);
                    productActivity. productPagerAdapter.productStatisticFragment.setVendor(vendors);
                    productActivity.productPagerAdapter.productStatisticFragment.setProductId(product.id);
                    productActivity.showDialog("Fetching product data");
                    productActivity.requestProduct();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
