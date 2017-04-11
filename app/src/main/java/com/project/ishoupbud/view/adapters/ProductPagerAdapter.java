package com.project.ishoupbud.view.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.project.ishoupbud.view.fragment.ProductDetailFragment;
import com.project.ishoupbud.view.fragment.ProductReviewFragment;
import com.project.ishoupbud.view.fragment.ProductStatisticFragment;

/**
 * Created by michael on 4/10/17.
 */

public class ProductPagerAdapter extends FragmentStatePagerAdapter {

    private static final int pages_count = 3;

    public ProductDetailFragment productDetailFragment;
    public ProductReviewFragment productReviewFragment;
    public ProductStatisticFragment productStatisticFragment;

    public ProductPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                productDetailFragment = ProductDetailFragment.newInstance();
                return productDetailFragment;
            case 1:
                productReviewFragment = ProductReviewFragment.newInstance();
                return productReviewFragment;
            case 2:
                productStatisticFragment = ProductStatisticFragment.newInstance();
                return productStatisticFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return pages_count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Detail";
            case 1:
                return "Review";
            case 2:
                return "Statistic";
        }
        return null;
    }
}
