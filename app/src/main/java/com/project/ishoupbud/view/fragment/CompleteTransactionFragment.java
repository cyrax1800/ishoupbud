package com.project.ishoupbud.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Transaction;
import com.project.ishoupbud.helper.InsetDividerItemDecoration;
import com.project.ishoupbud.view.activities.DetailTransactionActivity;
import com.project.ishoupbud.view.adapters.TransactionAdapter;
import com.project.michael.base.views.BaseFragment;
import com.project.michael.base.views.adapters.BaseAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/9/17.
 */

public class CompleteTransactionFragment extends BaseFragment {

    @BindView(R.id.rv_transaction) RecyclerView rvTransaction;

    TransactionAdapter<Transaction> transactionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this._rootView == null) {
            _rootView = inflater.inflate(R.layout.fragment_list_transaction, container, false);

            ButterKnife.bind(this, _rootView);

            transactionAdapter = new TransactionAdapter<>();
            transactionAdapter.setOnClickListener(new BaseAdapter.OnClickListener<Transaction>() {
                @Override
                public boolean onClick(View v, List<Transaction> transactions, Transaction transaction, int position) {
                    Intent i = new Intent(getContext(), DetailTransactionActivity.class);
                    startActivity(i);
                    return false;
                }
            });
            transactionAdapter.setNew(Transaction.getDummy(10));

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);

            rvTransaction.addItemDecoration(new InsetDividerItemDecoration(getContext()));
            rvTransaction.setLayoutManager(layoutManager);
            rvTransaction.setAdapter(transactionAdapter);
        }

        return _rootView;
    }
    
    public static CompleteTransactionFragment newInstance() {

        Bundle args = new Bundle();

        CompleteTransactionFragment fragment = new CompleteTransactionFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
