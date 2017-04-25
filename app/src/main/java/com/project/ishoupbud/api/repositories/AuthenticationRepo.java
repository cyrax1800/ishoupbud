package com.project.ishoupbud.api.repositories;

import com.project.ishoupbud.api.model.Token;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by michael on 4/23/17.
 */

public interface AuthenticationRepo {

    @Headers("Accept: application/json")
    @POST("oauth/register")
    Call<Token> register(@Body HashMap map);

    @Headers("Accept: application/json")
    @POST("oauth/login")
    Call<Token> login(@Body HashMap map);
}
