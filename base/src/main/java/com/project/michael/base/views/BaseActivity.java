package com.project.michael.base.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.project.michael.base.database.RealmDb;
import com.project.michael.base.utils.Settings;

import io.realm.Realm;

/**
 * Created by michael on 1/29/17.
 */

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "tmp-Activity";
    private Realm realm;

    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Settings.isUsingRealmDatabase())
            realm = RealmDb.getRealm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Settings.isUsingRealmDatabase() && realm != null)
            realm.close();
    }

    public void changeActivityForResult(Context context, Class clazz, int requestCode, Bundle option){
        Intent i = new Intent(context,clazz);
        startActivityForResult(i,requestCode,option);
    }

    public void changeActivityForResult(Context context, Class clazz, int requestCode){
        Intent i = new Intent(context,clazz);
        startActivityForResult(i,requestCode);
    }

    public void initProgressDialog(String message){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
    }

    public void initProgressDialog(String title, String message){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
    }

    @Override
    public void onClick(View v) {

    }
}
