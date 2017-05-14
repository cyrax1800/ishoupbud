package com.project.ishoupbud.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.project.ishoupbud.R;
import com.project.ishoupbud.api.model.User;
import com.project.ishoupbud.helper.TextImageCircleHelper;
import com.project.ishoupbud.utils.ConstClass;
import com.project.ishoupbud.view.activities.EditPasswordActivity;
import com.project.ishoupbud.view.activities.EditProfileActivity;
import com.project.ishoupbud.view.activities.LoginActivity;
import com.project.ishoupbud.view.activities.RegisterActivity;
import com.project.ishoupbud.view.dialog.TopUpDialogFragment;
import com.project.michael.base.database.SharedPref;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.Utils;
import com.project.michael.base.views.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michael on 4/4/17.
 */

public class ProfileFragment extends BaseFragment {

    public final static int REQUEST_LOGIN = 1;
    public final static int REQUEST_REGISTER = 2;
    public final static int REQUEST_EDIT_PROFILE = 3;
    public final static int REQUEST_EDIT_PASSWORD = 4;

    public final static int RESULT_SUCCESS = 1;
    public final static int RESULT_FOR_LOGIN = 2;
    public final static int RESULT_FOR_REGISTER = 3;
    public final static int RESULT_NO_CHANGES = 4;
    public final static int RESULT_CHANGES = 5;

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

    TopUpDialogFragment topUpDialogFragment;

    User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(this._rootView == null) {
            _rootView = inflater.inflate(R.layout.fragment_profile, container, false);

            ButterKnife.bind(this,_rootView);

            updateProfile();

            btnTopup.setOnClickListener(this);
            btnEditProfile.setOnClickListener(this);
            btnChangePassword.setOnClickListener(this);
            btnLogin.setOnClickListener(this);
            btnLogout.setOnClickListener(this);
            btnRegister.setOnClickListener(this);

            topUpDialogFragment = new TopUpDialogFragment();

            //TODO implemen to profile user data to view
        }
        return _rootView;
    }

    public void updateProfile(){
        if(SharedPref.getValueString(SharedPref.ACCESS_TOKEN).isEmpty()){
            llProfile.setVisibility(View.GONE);
            llNotLoggedIn.setVisibility(View.VISIBLE);
        }else{
            llProfile.setVisibility(View.VISIBLE);
            llNotLoggedIn.setVisibility(View.GONE);

            user = GsonUtils.getObjectFromJson(SharedPref.getValueString(ConstClass.USER), User.class);

            tvUsername.setText(user.name);
            tvSaldo.setText("Saldo: " + Utils.indonesiaFormat(user.saldo));

            Glide.with(getContext())
                    .load(user.getSmallImage())
                    .placeholder(TextImageCircleHelper.getInstance().getImage(user.name))
                    .centerCrop()
                    .crossFade()
                    .into(ivProfilePicture);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_REGISTER || requestCode == REQUEST_LOGIN){
            if(resultCode == RESULT_SUCCESS){
                user = GsonUtils.getObjectFromJson(data.getStringExtra(ConstClass.USER), User.class);
                SharedPref.save(ConstClass.USER,data.getStringExtra(ConstClass.USER));
                updateProfile();
            }else if(resultCode == RESULT_FOR_LOGIN){
                Intent i = new Intent(getContext(), LoginActivity.class);
                i.putExtra(ConstClass.LOGIN_EXTRA,true);
                getActivity().startActivityForResult(i,REQUEST_LOGIN);
            }else if(resultCode == RESULT_FOR_REGISTER){
                Intent registerIntent = new Intent(getContext(), RegisterActivity.class);
                registerIntent.putExtra(ConstClass.REGISTER_EXTRA,true);
                getActivity().startActivityForResult(registerIntent,REQUEST_REGISTER);
            }
        }else if(requestCode == REQUEST_EDIT_PROFILE){
            if(resultCode == RESULT_CHANGES){
                user = GsonUtils.getObjectFromJson(data.getStringExtra(ConstClass.USER), User.class);
                SharedPref.save(ConstClass.USER,data.getStringExtra(ConstClass.USER));
                updateProfile();
            }
        }else if(requestCode == REQUEST_EDIT_PASSWORD){
            if(resultCode == RESULT_CHANGES){
                user = GsonUtils.getObjectFromJson(data.getStringExtra(ConstClass.USER), User.class);
                SharedPref.save(ConstClass.USER,data.getStringExtra(ConstClass.USER));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_top_up:
                topUpDialogFragment.show(getFragmentManager(),"Top Up");
                break;
            case R.id.btn_login:
                Intent i = new Intent(getContext(), LoginActivity.class);
                i.putExtra(ConstClass.LOGIN_EXTRA,true);
                getActivity().startActivityForResult(i,REQUEST_LOGIN);
                break;
            case R.id.btn_logout:
                SharedPref.save(SharedPref.ACCESS_TOKEN,"");
                SharedPref.save(ConstClass.USER,"");
                updateProfile();
                break;
            case R.id.btn_register:
                Intent registerIntent = new Intent(getContext(), RegisterActivity.class);
                registerIntent.putExtra(ConstClass.REGISTER_EXTRA,true);
                getActivity().startActivityForResult(registerIntent,REQUEST_REGISTER);
                break;
            case R.id.btn_edit_profile:
                Intent editProfileIntent = new Intent(getContext(), EditProfileActivity.class);
                getActivity().startActivityForResult(editProfileIntent,REQUEST_EDIT_PROFILE);
                break;
            case R.id.btn_change_password:
                Intent editPasswordIntent = new Intent(getContext(), EditPasswordActivity.class);
                getActivity().startActivityForResult(editPasswordIntent,REQUEST_EDIT_PASSWORD);
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
