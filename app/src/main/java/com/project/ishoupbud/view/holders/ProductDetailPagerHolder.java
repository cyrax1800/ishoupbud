package com.project.ishoupbud.view.holders;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.project.ishoupbud.R;
import com.project.ishoupbud.view.StepperView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 6/26/17.
 */

public class ProductDetailPagerHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tab_layout)
    public TabLayout tabLayout;
    @BindView(R.id.view_pager)
    public ViewPager viewPager;

    public ProductDetailPagerHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
