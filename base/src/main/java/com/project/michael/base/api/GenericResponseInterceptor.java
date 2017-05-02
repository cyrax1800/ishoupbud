package com.project.michael.base.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by michael on 5/2/17.
 */

public class GenericResponseInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if(APIManager.JSONKeyToGenericResponse.size() != 0){
            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            String responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"));
            try{
                JSONObject jsonObject = new JSONObject(responseBodyString);
                for(int i = 0; i < APIManager.JSONKeyToGenericResponse.size(); i++){
                    if(jsonObject.has(APIManager.JSONKeyToGenericResponse.get(i))){
                        Object object = jsonObject.get(APIManager.JSONKeyToGenericResponse.get(i));
                        jsonObject.remove(APIManager.JSONKeyToGenericResponse.get(i));
                        jsonObject.put("data", object);
                        MediaType contentType = response.body().contentType();
                        ResponseBody body = ResponseBody.create(contentType, jsonObject.toString());
                        response = response.newBuilder().body(body).build();
                        break;
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return response;
    }
}