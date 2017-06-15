package com.project.ishoupbud.api.interceptor;

import com.project.michael.base.api.APIManager;
import com.project.michael.base.database.SharedPref;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by michael on 6/14/17.
 */

public class UberInterceptor implements Interceptor {

    private static final String TAG  = "OkHttp-Session";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if(!SharedPref.getValueString(SharedPref.ACCESS_TOKEN).isEmpty()){
            Request request = chain.request().newBuilder().addHeader(
                    APIManager.AUTHORIZATION_HEADER,
                    "Token " + "dMcBeMKnz79K9KF2l4X_KorAj8MfEazd8BlVt1aa")
                    .addHeader("Content-Type", "application/json")
                    .build();
            response = chain.proceed(request);
        }
        return response;
    }
}