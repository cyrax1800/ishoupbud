package com.project.ishoupbud.api.repositories;

import com.project.ishoupbud.api.model.WishList;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.models.Response;

import java.util.List;
import java.util.Map;

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

public interface WishlistRepo {

    @Headers("Accept: application/json")
    @GET("wishlist")
    Call<GenericResponse<List<WishList>>> getWishlist();

    @Headers("Accept: application/json")
    @POST("wishlist")
    Call<GenericResponse<WishList>> addWishlist(@Body Map map);

    @Headers("Accept: application/json")
    @DELETE("wishlist/{id}")
    Call<Response> deleteWishlist(@Path("id") int id);
}
