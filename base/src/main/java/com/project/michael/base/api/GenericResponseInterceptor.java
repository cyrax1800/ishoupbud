package com.project.michael.base.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by michael on 5/2/17.
 */

public class GenericResponseInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if(APIManager.JSONKeyToGenericResponse.size() != 0){
            String stringJson = response.body().string();
            try{
                JSONObject jsonObject = new JSONObject(stringJson);
                for(int i = 0; i < APIManager.JSONKeyToGenericResponse.size(); i++){
                    if(jsonObject.has(APIManager.JSONKeyToGenericResponse.get(i))){
                        Object object = jsonObject.get(APIManager.JSONKeyToGenericResponse.get(i));
                        jsonObject.remove(APIManager.JSONKeyToGenericResponse.get(i));
                        jsonObject.put("data", object);
                        MediaType contentType = response.body().contentType();
                        ResponseBody body = ResponseBody.create(contentType, jsonObject.toString());
                        response = response.newBuilder().body(body).build();
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return response;
    }
}