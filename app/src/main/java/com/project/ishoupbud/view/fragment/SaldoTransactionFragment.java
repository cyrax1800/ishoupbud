package com.project.ishoupbud.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Transaction;
import com.project.ishoupbud.helper.InsetDividerItemDecoration;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.activities.BuktiTransferActivity;
import com.project.ishoupbud.view.activities.DetailTransactionActivity;
import com.project.ishoupbud.view.adapters.TransactionAdapter;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.views.BaseFragment;
import com.project.michael.base.views.adapters.BaseAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 6/18/17.
 */

public class SaldoTransactionFragment extends BaseFragment {

    public static final int CEK_SALDO = 568;
    public static final int BATAL_TRASASKSI = 652;

    @BindView(R.id.rv_transaction)
    RecyclerView rvTransaction;

    TransactionAdapter<Transaction> transactionAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (this._rootView == null) {
            _rootView = inflater.inflate(R.layout.fragment_list_transaction, container, false);

            ButterKnife.bind(this, _rootView);

            transactionAdapter = new TransactionAdapter<>();
            transactionAdapter.setOnClickListener(new BaseAdapter.OnClickListener<Transaction>() {
                @Override
                public boolean onClick(View v, List<Transaction> transactions, Transaction transaction, int position) {
                    Intent i = new Intent(getContext(), BuktiTransferActivity.class);
                    i.putExtra(ConstClass.TRANSACTION_EXTRA, GsonUtils.getJsonFromObject(transaction, Transaction.class));
                    getActivity().startActivityForResult(i, CEK_SALDO);
                    return false;
                }
            });

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

            rvTransaction.addItemDecoration(new InsetDividerItemDecoration(getContext()));
            rvTransaction.setLayoutManager(layoutManager);
            rvTransaction.setAdapter(transactionAdapter);
        }

        return _rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CEK_SALDO){
            if(resultCode == BATAL_TRASASKSI){
                int idx = data.getIntExtra(ConstClass.TRANSACTION_EXTRA,-1);
                if(idx == -1) return;
                Transaction tmpTransaction = GsonUtils.getObjectFromJson(data.getStringExtra(
                        ConstClass.TRANSACTION_EXTRA), Transaction.class);
                for(int i = 0; i< transactionAdapter.getItemCount(); i++){
                    Transaction transaksi = transactionAdapter.getItemAt(idx);
                    if(transaksi.id == tmpTransaction.id){
                        transaksi.status = 4;
                        transactionAdapter.set(i, transaksi);
                        break;
                    }
                }
            }
        }
    }

    public void addTranscation(Transaction transaction){
        transactionAdapter.add(transaction);
    }

    public void clearAdapter(){
        transactionAdapter.clear();
    }

    public static SaldoTransactionFragment newInstance() {

        Bundle args = new Bundle();

        SaldoTransactionFragment fragment = new SaldoTransactionFragment();
        fragment.setArguments(args);
        return fragment;
    }
}