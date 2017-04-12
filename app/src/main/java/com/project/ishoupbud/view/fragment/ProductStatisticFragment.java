package com.project.ishoupbud.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.project.ishoupbud.R;
import com.project.michael.base.views.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by michael on 4/10/17.
 */

public class ProductStatisticFragment extends BaseFragment {

    @BindView(R.id.spinner_vendor) Spinner spinnerVendor;
    @BindView(R.id.segmented2) SegmentedGroup sgDayFilter;
    @BindView(R.id.chart) LineChart lineChart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this._rootView == null) {
            _rootView = inflater.inflate(R.layout.fragment_history_product, container, false);

            ButterKnife.bind(this, _rootView);

            sgDayFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                }
            });
        }

        return _rootView;
    }

    public static ProductStatisticFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ProductStatisticFragment fragment = new ProductStatisticFragment();
        fragment.setArguments(args);
        return fragment;
    }
}