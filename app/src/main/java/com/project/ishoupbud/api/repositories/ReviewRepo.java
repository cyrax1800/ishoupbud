package com.project.ishoupbud.api.repositories;

import com.project.ishoupbud.api.response.ProductAllReviewResponse;
import com.project.ishoupbud.api.model.Review;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.models.Response;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by michael on 5/13/17.
 */

public interface ReviewRepo {

    @Headers("Accept: application/json")
    @GET("review")
    Call<ProductAllReviewResponse> getReview(@QueryMap Map<String, Object> query);

    @Headers("Accept: application/json")
    @POST("review")
    Call<GenericResponse<Review>> submitReview(@Body HashMap<String, Object> map);

    @FormUrlEncoded
    @Headers("Accept: application/json")
    @PUT("review/{id_review}")
    Call<GenericResponse<Review>> updateReview(@FieldMap Map<String, Object> map, @Path("id_review") int reviewId);


    @Headers("Accept: application/json")
    @DELETE("review/{id_review}")
    Call<Response> deleteReview(@Path("id_review") int reviewId);
}
