package com.project.ishoupbud.api.repositories;

import com.project.ishoupbud.api.model.User;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by michael on 4/23/17.
 */

public interface UserRepo {

    @Headers("Accept: application/json")
    @GET("me")
    Call<User> getOwnData(@Header("Authorization") String authorizaition);
}
