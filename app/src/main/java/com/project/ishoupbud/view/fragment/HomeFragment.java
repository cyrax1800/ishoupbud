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
import android.widget.TextView;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.activities.ProductActivity;
import com.project.ishoupbud.view.activities.ScanBarcodeActivity;
import com.project.ishoupbud.view.adapters.ProductAdapter;
import com.project.ishoupbud.view.dialog.CategoriesDialogFragment;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.views.BaseFragment;
import com.project.michael.base.views.adapters.BaseAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
            newProduct.setNew(Product.getDummy(10));

            promoProduct = new ProductAdapter<>();
            promoProduct.setNew(Product.getDummy(10));

            popularProduct = new ProductAdapter<>();
            popularProduct.setNew(Product.getDummy(10));

            rvNewProduct.setAdapter(newProduct);
            rvPromo.setAdapter(promoProduct);
            rvPopular.setAdapter(popularProduct);

            categoriesDialogFragment = new CategoriesDialogFragment();

        }
        return _rootView;
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
                break;
            case R.id.tv_see_all_popular:
                break;
            case R.id.tv_see_all_new:
                break;
        }
    }
}
