package com.project.ishoupbud.api.repositories;

import com.project.ishoupbud.api.model.Product;
import com.project.michael.base.models.GenericResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by michael on 4/23/17.
 */

public interface ProductRepo {

    @GET("product/barcode/{product_barcode}")
    @Headers("Accept: application/json")
    Call<GenericResponse<Product>> getProductByBarcode(@Path("product_barcode") String barcode);

    @GET("product")
    @Headers("Accept: application/json")
    Call<GenericResponse<List<Product>>> getProductFilter(@Query("category") int categoryId, @Query("keyword") String keyword);

    @GET("product")
    @Headers("Accept: application/json")
    Call<GenericResponse<List<Product>>> getAllProduct();
}
