package com.project.ishoupbud.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Transaction;
import com.project.ishoupbud.view.adapters.TransactionAdapter;
import com.project.michael.base.views.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/9/17.
 */

public class CompleteTransactionFragment extends BaseFragment {

    @BindView(R.id.rv_transaction_complete)
    RecyclerView rvTransaction;

    TransactionAdapter<Transaction> transactionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this._rootView == null) {
            _rootView = inflater.inflate(R.layout.fragment_transaction_complete, container, false);

            ButterKnife.bind(this, _rootView);

            transactionAdapter = new TransactionAdapter<>();
            transactionAdapter.setNew(Transaction.getDummy(10));

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);

            rvTransaction.setLayoutManager(layoutManager);
            rvTransaction.setAdapter(transactionAdapter);
        }

        return _rootView;
    }
    
    public void refresh(){
        Log.d(TAG, "refresh: ");
    }

    public static CompleteTransactionFragment newInstance() {

        Bundle args = new Bundle();

        CompleteTransactionFragment fragment = new CompleteTransactionFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
