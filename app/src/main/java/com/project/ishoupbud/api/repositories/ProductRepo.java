package com.project.ishoupbud.api.repositories;

import com.project.ishoupbud.api.model.Product;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by michael on 4/23/17.
 */

public interface ProductRepo {

    @GET("product/barcode/{product_barcode}")
    @Headers("Accept: application/json")
    Call<Product> getProductByBarcode(@Path("product_barcode") String barcode);
}
