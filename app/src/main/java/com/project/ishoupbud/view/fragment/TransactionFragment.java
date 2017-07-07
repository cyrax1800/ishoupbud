package com.project.ishoupbud.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.Transaction;
import com.project.ishoupbud.api.repositories.TransactionRepo;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.adapters.TransactionPagerAdapter;
import com.project.michael.base.api.APICallback;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.views.BaseFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by michael on 4/4/17.
 */

public class TransactionFragment extends BaseFragment {

    public static final int REQUEST_TRANSACTION = 722;
    public static final int RESPONSE_TERIMA_BARANG = 950;
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
            viewPager.setOffscreenPageLimit(2);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 0) {

                    } else if (position == 1) {

                    } else if (position == 2) {

                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    tabLayout.setupWithViewPager(viewPager);
                }
            });
        }
        return _rootView;
    }

    public void fetchTransaction() {
        if(transactionAdapter.pendingTransactionFragment != null) transactionAdapter.pendingTransactionFragment.clearAdapter();
        if(transactionAdapter.completeTransactionFragment != null) transactionAdapter.completeTransactionFragment.clearAdapter();
        if(transactionAdapter.saldoTransactionFragment != null) transactionAdapter.saldoTransactionFragment.clearAdapter();
        Call<GenericResponse<List<Transaction>>> getTransactionCall = APIManager.getRepository(TransactionRepo.class).getTransaction("d");
        getTransactionCall.enqueue(new APICallback<GenericResponse<List<Transaction>>>() {
            @Override
            public void onSuccess(Call<GenericResponse<List<Transaction>>> call, Response<GenericResponse<List<Transaction>>> response) {
                super.onSuccess(call, response);
                List<Transaction> tmpTransactions = response.body().data;
                for(int i = 0; i< tmpTransactions.size(); i++){
                    if(tmpTransactions.get(i).status == 0){
                        if(transactionAdapter.pendingTransactionFragment != null)
                            transactionAdapter.pendingTransactionFragment.addTranscation(tmpTransactions.get(i));
                    }else{
                        if(transactionAdapter.completeTransactionFragment != null)
                            transactionAdapter.completeTransactionFragment.addTranscation(tmpTransactions.get(i));
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Transaction>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });

        Call<GenericResponse<List<Transaction>>> getSaldoTransaction = APIManager.getRepository(TransactionRepo.class).getSaldoTransaction();
        getSaldoTransaction.enqueue(new APICallback<GenericResponse<List<Transaction>>>() {
            @Override
            public void onSuccess(Call<GenericResponse<List<Transaction>>> call, Response<GenericResponse<List<Transaction>>> response) {
                super.onSuccess(call, response);
                List<Transaction> tmpTransactions = response.body().data;
                for(int i = 0; i< tmpTransactions.size(); i++){
                    if(tmpTransactions.get(i).debitCredit == 0)
                        if(transactionAdapter.saldoTransactionFragment != null)
                            transactionAdapter.saldoTransactionFragment.addTranscation(tmpTransactions.get(i));
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Transaction>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESPONSE_TERIMA_BARANG){
            Transaction transaction = GsonUtils.getObjectFromJson(data.getStringExtra(ConstClass
                    .TRANSACTION_EXTRA), Transaction.class);
            transactionAdapter.pendingTransactionFragment.removeTransaction(transaction);
            transactionAdapter.completeTransactionFragment.addTranscation(transaction);
        }
    }

    public static TransactionFragment newInstance() {
        
        Bundle args = new Bundle();
        
        TransactionFragment fragment = new TransactionFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
