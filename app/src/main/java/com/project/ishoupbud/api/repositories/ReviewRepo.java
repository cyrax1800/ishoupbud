package com.project.ishoupbud.api.repositories;

import com.project.ishoupbud.api.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by michael on 5/13/17.
 */

public interface ReviewRepo {

    @Headers("Accept: application/json")
    @GET("review")
    Call<User> getReview();

    @Headers("Accept: application/json")
    @POST("review")
    Call<User> submitReview();
}
