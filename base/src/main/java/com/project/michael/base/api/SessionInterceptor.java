package com.project.michael.base.api;

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
        Request request = chain.request();

        if(!SharedPref.getValueString(SharedPref.ACCESS_TOKEN).isEmpty()){
            request = request.newBuilder().addHeader(
                    APIManager.AUTHORIZATION_HEADER,
                    "Bearer " + SharedPref.getValueString(SharedPref.ACCESS_TOKEN))
                    .build();
        }

        return chain.proceed(request);
    }
}