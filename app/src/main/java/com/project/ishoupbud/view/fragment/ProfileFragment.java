package com.project.ishoupbud.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.ishoupbud.R;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.activities.EditPasswordActivity;
import com.project.ishoupbud.view.activities.EditProfileActivity;
import com.project.ishoupbud.view.activities.LoginActivity;
import com.project.michael.base.database.SharedPref;
import com.project.michael.base.views.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/4/17.
 */

public class ProfileFragment extends BaseFragment {

    @BindView(R.id.container_profile) LinearLayout llProfile;
    @BindView(R.id.container_not_logged_in) LinearLayout llNotLoggedIn;
    @BindView(R.id.btn_top_up) Button btnTopup;
    @BindView(R.id.btn_edit_profile) Button btnEditProfile;
    @BindView(R.id.btn_change_password) Button btnChangePassword;
    @BindView(R.id.btn_logout) Button btnLogout;
    @BindView(R.id.btn_login) Button btnLogin;
    @BindView(R.id.btn_register) Button btnRegister;
    @BindView(R.id.iv_profile_pic) ImageView ivProfilePicture;
    @BindView(R.id.tv_profile_name) TextView tvUsername;
    @BindView(R.id.tv_profile_saldo) TextView tvSaldo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this._rootView == null) {
            _rootView = inflater.inflate(R.layout.fragment_profile, container, false);

            ButterKnife.bind(this,_rootView);

//            if(SharedPref.getValueString(ConstClass.ACCESS_TOKEN).isEmpty()){
//                llProfile.setVisibility(View.GONE);
//                llNotLoggedIn.setVisibility(View.VISIBLE);
//            }else{
                llProfile.setVisibility(View.VISIBLE);
                llNotLoggedIn.setVisibility(View.GONE);
//            }

            btnTopup.setOnClickListener(this);
            btnEditProfile.setOnClickListener(this);
            btnChangePassword.setOnClickListener(this);
            btnLogin.setOnClickListener(this);
            btnLogout.setOnClickListener(this);
            btnRegister.setOnClickListener(this);

            //TODO implemen to profile user data to view
        }
        return _rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_top_up:
                break;
            case R.id.btn_login:
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                break;
            case R.id.btn_logout:
                break;
            case R.id.btn_register:
                Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                loginIntent.putExtra(ConstClass.REGISTER_EXTRA,true);
                startActivity(loginIntent);
                break;
            case R.id.btn_edit_profile:
                Intent editProfileIntent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(editProfileIntent);
                break;
            case R.id.btn_change_password:

                Intent editPasswordIntent = new Intent(getContext(), EditPasswordActivity.class);
                startActivity(editPasswordIntent);
                break;
        }
    }

    public static ProfileFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
