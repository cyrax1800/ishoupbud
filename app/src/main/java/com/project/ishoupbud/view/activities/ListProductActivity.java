package com.project.ishoupbud.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.adapters.ProductAdapter;
import com.project.ishoupbud.view.dialog.CategoriesDialogFragment;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;
import com.project.michael.base.views.adapters.BaseAdapter;
import com.project.michael.base.views.helpers.GridSpacingItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/13/17.
 */

public class ListProductActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rv_product) RecyclerView rvProduct;
    @BindView(R.id.fab_scrol_up) FloatingActionButton fabMoveUp;
    @BindView(R.id.btn_categories) Button btnCategories;

    ProductAdapter<Product> productAdapter;

    CategoriesDialogFragment categoriesDialogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        ButterKnife.bind(this);

        toolbar.setTitle("List Product");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        categoriesDialogFragment = new CategoriesDialogFragment();

        productAdapter = new ProductAdapter<>();
        productAdapter.setOnClickListener(new BaseAdapter.OnClickListener<Product>() {
            @Override
            public boolean onClick(View v, List<Product> products, Product product, int position) {
                Intent i = new Intent(getApplicationContext(), ProductActivity.class);
                i.putExtra(ConstClass.PRODUCT_EXTRA, GsonUtils.getJsonFromObject(product,Product.class));
                startActivity(i);
                return false;
            }
        });
        productAdapter.setNew(Product.getDummy(11));

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL, false);
        rvProduct.setLayoutManager(layoutManager);
        rvProduct.addItemDecoration(new GridSpacingItemDecoration(2, Utils.dpToPx(16),true));
        rvProduct.setAdapter(productAdapter);

        btnCategories.setOnClickListener(this);
        fabMoveUp.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_categories:
                categoriesDialogFragment.show(getSupportFragmentManager(),"categoriesDF");
                break;
        }
    }
}
