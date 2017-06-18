package com.project.ishoupbud.view.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.project.ishoupbud.view.fragment.CompleteTransactionFragment;
import com.project.ishoupbud.view.fragment.PendingTransactionFragment;
import com.project.ishoupbud.view.fragment.SaldoTransactionFragment;

/**
 * Created by michael on 4/9/17.
 */

public class TransactionPagerAdapter extends FragmentStatePagerAdapter {

    private static final int pages_count = 3;

    public PendingTransactionFragment pendingTransactionFragment;
    public CompleteTransactionFragment completeTransactionFragment;
    public SaldoTransactionFragment saldoTransactionFragment;

    public TransactionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                pendingTransactionFragment = PendingTransactionFragment.newInstance();
                return pendingTransactionFragment;
            case 1:
                completeTransactionFragment = CompleteTransactionFragment.newInstance();
                return completeTransactionFragment;
            case 2:
                saldoTransactionFragment = SaldoTransactionFragment.newInstance();
                return saldoTransactionFragment;
        }
        return null;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return pages_count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "In-Progress";
            case 1:
                return "Complete";
            case 2:
                return "Saldo";
        }
        return null;
    }
}
