package com.project.michael.base.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.project.michael.base.utils.Settings;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by michael on 1/30/17.
 */

public class APIManager {

    private static String TAG = "tmp-ApiManager";

    private static Retrofit retrofit = null;

    private static HashMap<String,Object> repositories;

    public static void SetUpRetrofit(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        if(Settings.getRetrofitAPIUrl().isEmpty())
            throw new ExceptionInInitializerError("Api Url must be define in Settings.json file");

        retrofit = new Retrofit.Builder()
                .baseUrl(Settings.getRetrofitAPIUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        repositories = new HashMap<>();
    }

    public static void registerRepository(Class clazz){
        String name = clazz.getName();
        if(repositories.containsKey(name)){
            Log.d(TAG, "registerRepository: class with name : " + name + " already assigned, and will be override with new repository");
        }
        repositories.put(name,getclient().create(clazz));
    }

    public static <T>T getRepository(Class<T> clazz){
        String name = clazz.getName();
        if(!repositories.containsKey(name)){
            throw new NoClassDefFoundError("class with name: " + name + " has not been register to retrofit. please call APIManager.registerRepository(" + clazz.getSimpleName() + ".class) instead");
        }
        return clazz.cast(repositories.get(name));
    }

    private static Retrofit getclient(){
        if(!Settings.isUsingRetrofitAPI())
            throw new NullPointerException("Must Enable using Retrofit in Settings.json file");

        if(retrofit == null)
            SetUpRetrofit();

        return retrofit;
    }
}
