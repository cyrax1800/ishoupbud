package com.project.ishoupbud.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.activities.ProductActivity;
import com.project.ishoupbud.view.adapters.ProductAdapter;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseFragment;
import com.project.michael.base.views.adapters.BaseAdapter;
import com.project.michael.base.views.helpers.GridSpacingItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/4/17.
 */

public class WishlistFragment extends BaseFragment {

    @BindView(R.id.rv_wishlist) RecyclerView rvWishlist;

    ProductAdapter<Product> wishListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this._rootView == null) {
            _rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);

            ButterKnife.bind(this,_rootView);

            wishListAdapter = new ProductAdapter<>();
            wishListAdapter.setOnClickListener(new BaseAdapter.OnClickListener<Product>() {
                @Override
                public boolean onClick(View v, List<Product> products, Product product, int position) {
                    Intent i = new Intent(getContext(), ProductActivity.class);
                    i.putExtra(ConstClass.PRODUCT_EXTRA, GsonUtils.getJsonFromObject(product,Product.class));
                    startActivity(i);
                    return false;
                }
            });
            wishListAdapter.setNew(Product.getDummy(11));

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2, LinearLayoutManager.VERTICAL, false);
            rvWishlist.setLayoutManager(layoutManager);
            rvWishlist.addItemDecoration(new GridSpacingItemDecoration(2,Utils.dpToPx(16),true));
            rvWishlist.setAdapter(wishListAdapter);
        }
        return _rootView;
    }

    public static WishlistFragment newInstance() {
        
        Bundle args = new Bundle();
        
        WishlistFragment fragment = new WishlistFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
