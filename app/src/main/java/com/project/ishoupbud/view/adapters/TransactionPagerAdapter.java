package com.project.ishoupbud.view.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.project.ishoupbud.view.fragment.CompleteTransactionFragment;
import com.project.ishoupbud.view.fragment.PendingTransactionFragment;

/**
 * Created by michael on 4/9/17.
 */

public class TransactionPagerAdapter extends FragmentPagerAdapter {

    private static final int pages_count = 2;

    public PendingTransactionFragment pendingTransactionFragment;
    public CompleteTransactionFragment completeTransactionFragment;

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
        }
        return null;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "In-Progress";
            case 1:
                return "Complete";
        }
        return null;
    }
}
