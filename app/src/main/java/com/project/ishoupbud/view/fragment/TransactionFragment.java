package com.project.ishoupbud.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.ishoupbud.R;
import com.project.ishoupbud.view.adapters.TransactionPagerAdapter;
import com.project.michael.base.views.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/4/17.
 */

public class TransactionFragment extends BaseFragment {

    @BindView(R.id.tab_layout_transaction) TabLayout tabLayout;
    @BindView(R.id.view_pager_transaction) ViewPager viewPager;

    TransactionPagerAdapter transactionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this._rootView == null) {
            _rootView = inflater.inflate(R.layout.fragment_transaction, container, false);

            ButterKnife.bind(this, _rootView);

            transactionAdapter = new TransactionPagerAdapter(getChildFragmentManager());

            viewPager.setAdapter(transactionAdapter);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 0) {
                        transactionAdapter.pendingTransactionFragment.refresh();
                    } else if (position == 1) {
                        transactionAdapter.completeTransactionFragment.refresh();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: ");
                    transactionAdapter.notifyDataSetChanged();
                    tabLayout.setupWithViewPager(viewPager);
                }
            });
        }
        return _rootView;
    }

    public static TransactionFragment newInstance() {
        
        Bundle args = new Bundle();
        
        TransactionFragment fragment = new TransactionFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
