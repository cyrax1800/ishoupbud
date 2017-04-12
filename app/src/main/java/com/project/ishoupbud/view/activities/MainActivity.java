package com.project.ishoupbud.view.activities;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.ncapdevi.fragnav.FragNavController;
import com.project.ishoupbud.R;
import com.project.ishoupbud.view.fragment.HomeFragment;
import com.project.ishoupbud.view.fragment.ProfileFragment;
import com.project.ishoupbud.view.fragment.TransactionFragment;
import com.project.ishoupbud.view.fragment.WishlistFragment;
import com.project.michael.base.views.BaseActivity;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements FragNavController.RootFragmentListener{

    private final int HOME = FragNavController.TAB1;
    private final int WISHLIST = FragNavController.TAB2;
    private final int TRANSACTION = FragNavController.TAB3;
    private final int PROFILE = FragNavController.TAB4;

    private FragNavController mNavController;

    FloatingSearchView floatingSearchView;

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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }
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
