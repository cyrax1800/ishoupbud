package com.project.ishoupbud.api.repositories;

import com.project.ishoupbud.api.model.ShoppingCart;
import com.project.ishoupbud.api.response.ShoppingCartResponse;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.models.Response;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by michael on 4/23/17.
 */

public interface ShoppingCartRepo {

    @Headers("Accept: application/json")
    @GET("cart")
    Call<GenericResponse<ShoppingCartResponse>> getCart();

    @Headers("Accept: application/json")
    @POST("cart")
    Call<ShoppingCart> addCart(@Body HashMap map);

    @Headers("Accept: application/json")
    @DELETE("cart/{cart_id}")
    Call<Response> deleteCart(@Path("cart_id") int id);
}
