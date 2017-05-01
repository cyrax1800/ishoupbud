package com.project.ishoupbud.view.activities;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.bumptech.glide.Glide;
import com.ncapdevi.fragnav.FragNavController;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.repositories.ProductRepo;
import com.project.ishoupbud.manager.GoogleAPIManager;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.fragment.HomeFragment;
import com.project.ishoupbud.view.fragment.ProfileFragment;
import com.project.ishoupbud.view.fragment.TransactionFragment;
import com.project.ishoupbud.view.fragment.WishlistFragment;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.views.BaseActivity;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements FragNavController.RootFragmentListener{

    private final int HOME = FragNavController.TAB1;
    private final int WISHLIST = FragNavController.TAB2;
    private final int TRANSACTION = FragNavController.TAB3;
    private final int PROFILE = FragNavController.TAB4;

    private FragNavController mNavController;

    FloatingSearchView floatingSearchView;

    Call<GenericResponse<List<Product>>> searchProductCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        floatingSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);

        List<Fragment> fragments = new ArrayList<>(4);

        fragments.add(HomeFragment.newInstance());
        fragments.add(WishlistFragment.newInstance());
        fragments.add(TransactionFragment.newInstance());
        fragments.add(ProfileFragment.newInstance());

        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.container)
                .rootFragments(fragments)
                .rootFragmentListener(this, 4)
                .build();

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home:
                        mNavController.switchTab(HOME);
                        break;
                    case R.id.tab_wishlist:
                        mNavController.switchTab(WISHLIST);
                        if(mNavController.getCurrentFrag() != null)
                            ((WishlistFragment)mNavController.getCurrentFrag()).fetchWishlist();
                        break;
                    case R.id.tab_transaction:
                        mNavController.switchTab(TRANSACTION);
                        break;
                    case R.id.tab_profile:
                        mNavController.switchTab(PROFILE);
                        break;
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                Log.d(TAG, "onTabReSelected: " + tabId);
            }
        });

        floatingSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if(item.getItemId() == R.id.action_shopping_cart){
                    Intent i = new Intent(getApplicationContext(), ShoppingCartActivity.class);
                    startActivity(i);
                }
            }
        });

        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                Log.d(TAG, "onSearchTextChanged: " + newQuery);
                if(newQuery.isEmpty()) return;
                searchProduct(1, newQuery);
            }
        });

        floatingSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                Product product = (Product)item;
                textView.setText(product.name);

                Glide
                    .with(getApplicationContext())
                    .load(product.pictureUrl.small)
                    .centerCrop()
                    .crossFade()
                    .into(leftIcon);
            }
        });

        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                Intent i = new Intent(getApplicationContext(), ProductActivity.class);
                i.putExtra(ConstClass.PRODUCT_EXTRA, GsonUtils.getJsonFromObject(searchSuggestion, Product.class));
                startActivity(i);
            }

            @Override
            public void onSearchAction(String currentQuery) {

            }
        });

        floatingSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

            }

            @Override
            public void onFocusCleared() {
                floatingSearchView.setSearchBarTitle("");
            }
        });

        GoogleAPIManager.getGoogleApi().initGoogleAPI(this);

        if(mNavController.getCurrentFrag() != null && mNavController.getCurrentFrag() instanceof HomeFragment){
            ((HomeFragment)mNavController.getCurrentFrag()).fetchAllNew();
            ((HomeFragment)mNavController.getCurrentFrag()).fetchAllPopular();
            ((HomeFragment)mNavController.getCurrentFrag()).fetchAllPromo();
        }
    }

    public void searchProduct(int categoryId, String text){
        if(searchProductCall != null && searchProductCall.isExecuted())
            searchProductCall.cancel();
        searchProductCall = APIManager.getRepository(ProductRepo.class).getProductFilter(categoryId, text);
        searchProductCall.enqueue(new APICallback<GenericResponse<List<Product>>>() {
            @Override
            public void onSuccess(Call<GenericResponse<List<Product>>> call, Response<GenericResponse<List<Product>>> response) {
                super.onSuccess(call, response);
                if(!floatingSearchView.isSearchBarFocused()) return;
                floatingSearchView.swapSuggestions(response.body().data);
            }

            @Override
            public void onNotFound(Call<GenericResponse<List<Product>>> call, Response<GenericResponse<List<Product>>> response) {
                super.onNotFound(call, response);
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Product>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode + " " + resultCode);
        if(requestCode == ProfileFragment.REQUEST_LOGIN ||
                requestCode == ProfileFragment.REQUEST_REGISTER ||
                requestCode == ProfileFragment.REQUEST_EDIT_PROFILE ||
                requestCode == ProfileFragment.REQUEST_EDIT_PASSWORD
                ){
            if(mNavController.getCurrentFrag() != null){
                mNavController.getCurrentFrag().onActivityResult(requestCode,resultCode,data);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        super.onBackPressed();
//        moveTaskToBack(true);
    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {
            case HOME:
                return HomeFragment.newInstance();
            case WISHLIST:
                return WishlistFragment.newInstance();
            case TRANSACTION:
                return TransactionFragment.newInstance();
            case PROFILE:
                return ProfileFragment.newInstance();
        }
        throw new IllegalStateException("Need to send an index that we know");
    }
}
