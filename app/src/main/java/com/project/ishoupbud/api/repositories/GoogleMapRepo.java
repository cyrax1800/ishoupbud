package com.project.ishoupbud.api.repositories;

import com.google.maps.model.DirectionsResult;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.models.Response;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by michael on 6/15/17.
 */

public interface GoogleMapRepo {

    @Headers("Accept: application/json")
    @POST("geo")
    Call<GenericResponse<DirectionsResult>> getDirection(@Body HashMap map);
}
