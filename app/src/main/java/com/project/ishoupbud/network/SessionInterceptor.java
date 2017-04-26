package com.project.ishoupbud.network;

import android.util.Log;

import com.project.ishoupbud.utils.ConstClass;
import com.project.michael.base.database.SharedPref;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by michael on 4/26/17.
 */

public class SessionInterceptor implements Interceptor {

    private static final String TAG  = "OkHttp-Session";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if(!SharedPref.getValueString(ConstClass.ACCESS_TOKEN).isEmpty()){
            Request request = chain.request().newBuilder().addHeader(
                    ConstClass.AUTHORIZATION_HEADER,
                    "Bearer " + SharedPref.getValueString(ConstClass.ACCESS_TOKEN))
                    .build();
            response = chain.proceed(request);
        }
        /*Log.d(TAG, "<-- " + chain.request().url());
        Log.d(TAG, "response ms: " + response.receivedResponseAtMillis());
        Log.d(TAG, "request ms: " + response.sentRequestAtMillis());
        Log.d(TAG, "sub ms: " + (response.receivedResponseAtMillis() - response.sentRequestAtMillis()));
        Log.d(TAG, "<-- END");*/
        return response;
    }
}
