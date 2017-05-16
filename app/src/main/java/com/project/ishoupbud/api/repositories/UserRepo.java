package com.project.ishoupbud.api.repositories;

import com.project.ishoupbud.api.model.User;
import com.project.michael.base.models.GenericResponse;
import com.project.michael.base.models.Response;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by michael on 4/23/17.
 */

public interface UserRepo {

    @Headers("Accept: application/json")
    @GET("me")
    Call<User> getOwnData();

    @Headers("Accept: application/json")
    @POST("user/change_password")
    Call<Response> changePassword(@Body HashMap map);

    @Multipart
    @Headers("Accept: application/json")
    @POST("user")
//    Call<GenericResponse<User>> editProfile(@FieldMap(encoded = true) Map<String, String> map);
    Call<GenericResponse<User>> editProfile(@PartMap Map<String, RequestBody> map, @Part MultipartBody.Part picture_url);


}
