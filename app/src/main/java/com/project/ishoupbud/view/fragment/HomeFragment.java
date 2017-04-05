package com.project.ishoupbud.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.ishoupbud.R;
import com.project.michael.base.views.BaseFragment;

/**
 * Created by michael on 4/4/17.
 */

public class HomeFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this._rootView == null){
            _rootView = inflater.inflate(R.layout.fragment_home,container,false);
        }
        return _rootView;
    }

    public static HomeFragment newInstance() {
        
        Bundle args = new Bundle();
        
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
