package com.project.ishoupbud.api.Uber.repositories;

import com.project.ishoupbud.api.Uber.model.UberPrice;
import com.project.ishoupbud.api.Uber.model.UberPrices;
import com.project.michael.base.models.Response;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by michael on 6/14/17.
 */

public interface UberRepo {

    @GET("estimates/price")
    Call<UberPrices> getEstimatePrice(@QueryMap HashMap<String, Object> query);
}
