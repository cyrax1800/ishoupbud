package com.project.ishoupbud.api.repositories;

import com.project.ishoupbud.api.model.Compare;
import com.project.ishoupbud.api.model.Product;
import com.project.ishoupbud.api.model.Statistic;
import com.project.michael.base.models.GenericResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by michael on 4/23/17.
 */

public interface ProductRepo {

    @GET("product/barcode/{product_barcode}")
    @Headers("Accept: application/json")
    Call<GenericResponse<Product>> getProductByBarcode(@Path("product_barcode") String barcode);

    @GET("product/{product_id}")
    @Headers("Accept: application/json")
    Call<GenericResponse<Product>> getProductById(@Path("product_id") int id);

    @GET("product")
    @Headers("Accept: application/json")
    Call<GenericResponse<List<Product>>> getProductFilter(@QueryMap Map<String, Object> map);

    @GET("product")
    @Headers("Accept: application/json")
    Call<GenericResponse<List<Product>>> getProductFilterByName(
                                                          @Query("keyword") String keyword);

    @GET("product/{type}")
    @Headers("Accept: application/json")
    Call<GenericResponse<List<Product>>> getAllProduct(@Path("type") String type, @QueryMap
            Map<String, Object> data);

    @GET("statistic/all")
    @Headers("Accept: application/json")
    Call<Statistic> getStatistic(@QueryMap Map<String, Object> map);

    @GET("compare")
    @Headers("Accept: application/json")
    Call<Compare> compareProduct(@Query("product_id") int productId);
}
