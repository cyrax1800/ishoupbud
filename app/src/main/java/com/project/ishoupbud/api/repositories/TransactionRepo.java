package com.project.ishoupbud.api.repositories;

import com.project.ishoupbud.api.model.Transaction;
import com.project.michael.base.models.GenericResponse;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by michael on 4/23/17.
 */

public interface TransactionRepo {

    @Headers("Accept: application/json")
    @GET("transaction")
    Call<GenericResponse<List<Transaction>>> getTransaction(@Query("with") String withDetail);

    @Headers("Accept: application/json")
    @POST("transaction")
    Call<GenericResponse<List<Transaction>>> checkout();
}
