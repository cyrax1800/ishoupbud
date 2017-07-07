package com.project.ishoupbud.api.repositories;

import com.project.ishoupbud.api.model.Transaction;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.models.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by michael on 4/23/17.
 */

public interface TransactionRepo {

    @Headers("Accept: application/json")
    @GET("transaction")
    Call<GenericResponse<List<Transaction>>> getTransaction(@Query("with") String withDetail);

    @Headers("Accept: application/json")
    @GET("saldo")
    Call<GenericResponse<List<Transaction>>> getSaldoTransaction();

    @Headers("Accept: application/json")
    @POST("transaction")
    Call<List<Transaction>> checkout(@Body Map map);

    @Headers("Accept: application/json")
    @POST("admin/transaction/{transaction}/approve")
    Call<Response> approve(@Path("transaction") int transactionId);
}
