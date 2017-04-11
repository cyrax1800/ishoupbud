package com.project.ishoupbud.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Transaction;
import com.project.ishoupbud.helper.InsetDividerItemDecoration;
import com.project.ishoupbud.view.adapters.TransactionAdapter;
import com.project.michael.base.views.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/10/17.
 */

public class ProductDetailFragment extends BaseFragment {

    @BindView(R.id.tv_detail_product) TextView tvDetail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this._rootView == null) {
            _rootView = inflater.inflate(R.layout.fragment_detail_product, container, false);

            ButterKnife.bind(this, _rootView);

        }

        return _rootView;
    }

    public static ProductDetailFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ProductDetailFragment fragment = new ProductDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
