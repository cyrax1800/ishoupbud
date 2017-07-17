package com.project.ishoupbud.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.reflect.TypeToken;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.model.ShoppingCartContainer;
import com.project.ishoupbud.api.repositories.ProductRepo;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.adapters.ProductAdapter;
import com.project.ishoupbud.view.dialog.CategoriesDialogFragment;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseActivity;
import com.project.michael.base.views.adapters.BaseAdapter;
import com.project.michael.base.views.helpers.GridSpacingItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/13/17.
 */

public class ListProductActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rv_product) RecyclerView rvProduct;
    @BindView(R.id.fab_scrol_up) FloatingActionButton fabMoveUp;
    @BindView(R.id.btn_categories) Button btnCategories;
    @BindView(R.id.nestedScroll) NestedScrollView nestedScrollView;
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;

    ProductAdapter<Product> productAdapter;

    CategoriesDialogFragment categoriesDialogFragment;

    int categoryID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        ButterKnife.bind(this);

        categoryID = getIntent().getIntExtra(ConstClass.CATEGORY_EXTRA, -1);

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
//        productAdapter.setNew(Product.getDummy(11));

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL, false);
        rvProduct.setLayoutManager(layoutManager);
        rvProduct.addItemDecoration(new GridSpacingItemDecoration(2, Utils.dpToPx(16),true));
        rvProduct.setAdapter(productAdapter);

        btnCategories.setOnClickListener(this);
        fabMoveUp.setOnClickListener(this);

        if(getIntent().hasExtra(ConstClass.PRODUCT_EXTRA)){
            @SuppressWarnings("unchecked")
            List<Product> tmpProduct = (List<Product>) GsonUtils.getObjectFromJson
                    (getIntent().getStringExtra(ConstClass.PRODUCT_EXTRA),
                            new TypeToken<List<Product>>() {}.getType());
            productAdapter.setNew(tmpProduct);
        }else{
            fetchProduct();
        }

    }

    public void fetchProduct(){
        Call<GenericResponse<List<Product>>> categoryProductCall = APIManager.getRepository
                (ProductRepo.class).getProductFilter(categoryID + 1, null);
        categoryProductCall.enqueue(new APICallback<GenericResponse<List<Product>>>() {
            @Override
            public void onSuccess(Call<GenericResponse<List<Product>>> call, Response<GenericResponse<List<Product>>> response) {
                super.onSuccess(call, response);
                productAdapter.setNew(response.body().data);
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Product>>> call, Throwable t) {
                super.onFailure(call, t);
            }

            @Override
            public void onNotFound(Call<GenericResponse<List<Product>>> call, Response<GenericResponse<List<Product>>> response) {
                super.onNotFound(call, response);
            }
        });
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
            case R.id.fab_scrol_up:
                nestedScrollView.smoothScrollTo(0, 0);
                break;
        }
    }
}
