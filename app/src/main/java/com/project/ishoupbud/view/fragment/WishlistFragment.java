package com.project.ishoupbud.view.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.model.WishList;
import com.project.ishoupbud.api.repositories.WishlistRepo;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.activities.ProductActivity;
import com.project.ishoupbud.view.adapters.WishListAdapter;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseFragment;
import com.project.michael.base.views.adapters.BaseAdapter;
import com.project.michael.base.views.helpers.GridSpacingItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/4/17.
 */

public class WishlistFragment extends BaseFragment {

    @BindView(R.id.rv_wishlist) RecyclerView rvWishlist;

    WishListAdapter<WishList> wishListAdapter;

    AlertDialog removeFavorite;

    int idToDeleteWishlist;
    int positionToDeleteWishlist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this._rootView == null) {
            _rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);

            ButterKnife.bind(this,_rootView);

            wishListAdapter = new WishListAdapter<>();
            wishListAdapter.setOnClickListener(new BaseAdapter.OnClickListener<WishList>() {
                @Override
                public boolean onClick(View v, List<WishList> wishLists, WishList wishList, int position) {
                    Intent i = new Intent(getContext(), ProductActivity.class);
                    i.putExtra(ConstClass.PRODUCT_EXTRA, GsonUtils.getJsonFromObject(wishList.product,Product.class));
                    startActivity(i);
                    return false;
                }
            });
            wishListAdapter.setOnLongClickListener(new BaseAdapter.OnLongClickListener<WishList>() {
                @Override
                public boolean onLongClick(View v, List<WishList> wishLists, WishList wishList, int position) {
                    removeFavorite.show();
                    positionToDeleteWishlist = position;
                    idToDeleteWishlist = wishList.id;
                    return false;
                }
            });

            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2, LinearLayoutManager.VERTICAL, false);
            rvWishlist.setLayoutManager(layoutManager);
            rvWishlist.addItemDecoration(new GridSpacingItemDecoration(2,Utils.dpToPx(16),true));
            rvWishlist.setAdapter(wishListAdapter);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Remove Favorite")
                    .setMessage("Are you sure want to Remove from WishList?")
                    .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            removeWishlist();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            removeFavorite.dismiss();
                        }
                    });

            removeFavorite = builder.create();
        }
        return _rootView;
    }

    public void fetchWishlist() {
        Call<GenericResponse<List<WishList>>> getWishlist = APIManager.getRepository(WishlistRepo.class).getWishlist();
        getWishlist.enqueue(new APICallback<GenericResponse<List<WishList>>>() {
            @Override
            public void onSuccess(Call<GenericResponse<List<WishList>>> call, Response<GenericResponse<List<WishList>>> response) {
                super.onSuccess(call, response);
                wishListAdapter.setNew(response.body().data);
            }

            @Override
            public void onFailure(Call<GenericResponse<List<WishList>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    public void removeWishlist() {
        Call<com.project.michael.base.models.Response> deleteWishlist = APIManager.getRepository(WishlistRepo.class).deleteWishlist(idToDeleteWishlist);
        deleteWishlist.enqueue(new APICallback<com.project.michael.base.models.Response>() {
            @Override
            public void onSuccess(Call<com.project.michael.base.models.Response> call, Response<com.project.michael.base.models.Response> response) {
                super.onSuccess(call, response);
                wishListAdapter.remove(positionToDeleteWishlist);
                Toast.makeText(getContext(), "Product successfully removed to wishlist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoContent(Call<com.project.michael.base.models.Response> call, Response<com.project.michael.base.models.Response> response) {
                super.onNoContent(call, response);
                Toast.makeText(getContext(), "Product successfully removed to wishlist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<com.project.michael.base.models.Response> call, Throwable t) {
                super.onFailure(call, t);
                Toast.makeText(getContext(), "Product failed removed to wishlist", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static WishlistFragment newInstance() {
        
        Bundle args = new Bundle();
        
        WishlistFragment fragment = new WishlistFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
