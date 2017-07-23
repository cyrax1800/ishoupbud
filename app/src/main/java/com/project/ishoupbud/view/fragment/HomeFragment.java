package com.project.ishoupbud.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.repositories.ProductRepo;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.activities.ListProductActivity;
import com.project.ishoupbud.view.activities.ProductActivity;
import com.project.ishoupbud.view.activities.ScanBarcodeActivity;
import com.project.ishoupbud.view.adapters.ProductAdapter;
import com.project.ishoupbud.view.dialog.CategoriesDialogFragment;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.views.BaseFragment;
import com.project.michael.base.views.adapters.BaseAdapter;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/4/17.
 */

public class HomeFragment extends BaseFragment {

    @BindView(R.id.btn_categories) Button btnCategories;
    @BindView(R.id.tv_see_all_new) TextView tvSeeAllNewProduct;
    @BindView(R.id.tv_see_all_promo) TextView tvSeeAllPromo;
    @BindView(R.id.tv_see_all_popular) TextView tvSeeAllPopular;
    @BindView(R.id.rv_new_product) RecyclerView rvNewProduct;
    @BindView(R.id.rv_promo_product) RecyclerView rvPromo;
    @BindView(R.id.rv_popular_product) RecyclerView rvPopular;
    @BindView(R.id.fab_barcode) FloatingActionButton fabBarcode;
    @BindView(R.id.nestedScroll) NestedScrollView nestedScrollView;
    @BindView(R.id.progress_new) ProgressBar progressNew;
    @BindView(R.id.progress_popular) ProgressBar progressPopular;
    @BindView(R.id.progress_last) ProgressBar progressLast;

    CategoriesDialogFragment categoriesDialogFragment;

    public ProductAdapter<Product> newProduct;
    public ProductAdapter<Product> promoProduct;
    public ProductAdapter<Product> popularProduct;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this._rootView == null){
            _rootView = inflater.inflate(R.layout.fragment_home,container,false);
            ButterKnife.bind(this,_rootView);

            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY) {
                        fabBarcode.hide();
                    } else {
                        fabBarcode.show();
                    }
                }
            });

            btnCategories.setOnClickListener(this);
            tvSeeAllNewProduct.setOnClickListener(this);
            tvSeeAllPromo.setOnClickListener(this);
            tvSeeAllPopular.setOnClickListener(this);
            fabBarcode.setOnClickListener(this);

            String htmlString ="<u>See All</u>";
            tvSeeAllNewProduct.setText(Html.fromHtml(htmlString));
            tvSeeAllPromo.setText(Html.fromHtml(htmlString));
            tvSeeAllPromo.setText(Html.fromHtml(htmlString));

            newProduct = new ProductAdapter<>();
            newProduct.setOnClickListener(new BaseAdapter.OnClickListener<Product>() {
                @Override
                public boolean onClick(View v, List<Product> products, Product product, int position) {
                    Intent i = new Intent(getContext(), ProductActivity.class);
                    i.putExtra(ConstClass.PRODUCT_EXTRA, GsonUtils.getJsonFromObject(product,Product.class));
                    startActivity(i);
                    return false;
                }
            });

            promoProduct = new ProductAdapter<>();
            promoProduct.setOnClickListener(new BaseAdapter.OnClickListener<Product>() {
                @Override
                public boolean onClick(View v, List<Product> products, Product product, int position) {
                    Intent i = new Intent(getContext(), ProductActivity.class);
                    i.putExtra(ConstClass.PRODUCT_EXTRA, GsonUtils.getJsonFromObject(product,Product.class));
                    startActivity(i);
                    return false;
                }
            });

            popularProduct = new ProductAdapter<>();
            popularProduct.setOnClickListener(new BaseAdapter.OnClickListener<Product>() {
                @Override
                public boolean onClick(View v, List<Product> products, Product product, int position) {
                    Intent i = new Intent(getContext(), ProductActivity.class);
                    i.putExtra(ConstClass.PRODUCT_EXTRA, GsonUtils.getJsonFromObject(product,Product.class));
                    startActivity(i);
                    return false;
                }
            });

            rvNewProduct.setAdapter(newProduct);
            rvPromo.setAdapter(promoProduct);
            rvPopular.setAdapter(popularProduct);

            categoriesDialogFragment = new CategoriesDialogFragment();

        }
        return _rootView;
    }

    public void fetchAllPromo(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("page",1);
        map.put("perpage", 10);
        Call<GenericResponse<List<Product>>> productCall = APIManager.getRepository(ProductRepo
                .class).getAllProduct("newest", map);
        productCall.enqueue(new APICallback<GenericResponse<List<Product>>>() {
            @Override
            public void onSuccess(Call<GenericResponse<List<Product>>> call, Response<GenericResponse<List<Product>>> response) {
                super.onSuccess(call, response);
                promoProduct.setNew(response.body().data);
                progressPopular.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Product>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    public void fetchAllNew(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("page",1);
        map.put("perpage", 10);
        Call<GenericResponse<List<Product>>> productCall = APIManager.getRepository(ProductRepo
                .class).getAllProduct("newest", map);
        productCall.enqueue(new APICallback<GenericResponse<List<Product>>>() {
            @Override
            public void onSuccess(Call<GenericResponse<List<Product>>> call, Response<GenericResponse<List<Product>>> response) {
                super.onSuccess(call, response);
                newProduct.setNew(response.body().data);
                progressNew.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Product>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    public void fetchAllPopular(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("page",1);
        map.put("perpage", 10);
        Call<GenericResponse<List<Product>>> productCall = APIManager.getRepository(
                ProductRepo.class).getAllProduct("trending", map);
        productCall.enqueue(new APICallback<GenericResponse<List<Product>>>() {
            @Override
            public void onSuccess(Call<GenericResponse<List<Product>>> call, Response<GenericResponse<List<Product>>> response) {
                super.onSuccess(call, response);
                popularProduct.setNew(response.body().data);
                progressLast.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Product>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    public static HomeFragment newInstance() {
        
        Bundle args = new Bundle();
        
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_categories:
                categoriesDialogFragment.show(getFragmentManager(),"categoriesDF");
                break;
            case R.id.fab_barcode:
                Intent i = new Intent(this.getContext(), ScanBarcodeActivity.class);
                startActivity(i);
                break;
            case R.id.tv_see_all_promo:
                Intent listIntent = new Intent(this.getContext(), ListProductActivity.class);
                listIntent.putExtra(ConstClass.PRODUCT_EXTRA, GsonUtils.getJsonFromObject
                        (promoProduct.getAllItem()));
                startActivity(listIntent);
                break;
            case R.id.tv_see_all_popular:
                Intent popularIntent = new Intent(this.getContext(), ListProductActivity.class);
                popularIntent.putExtra(ConstClass.PRODUCT_EXTRA, GsonUtils.getJsonFromObject
                        (popularProduct.getAllItem()));
                startActivity(popularIntent);
                break;
            case R.id.tv_see_all_new:
                Intent seeAllIntent = new Intent(this.getContext(), ListProductActivity.class);
                seeAllIntent.putExtra(ConstClass.PRODUCT_EXTRA, GsonUtils.getJsonFromObject
                        (newProduct.getAllItem()));
                startActivity(seeAllIntent);
                break;
        }
    }
}
