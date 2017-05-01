package com.project.michael.base.api;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by michael on 1/31/17.
 */

public abstract class APICallback<T> implements Callback<T> {

    private static final String TAG = "tmp-Retrofit Callback";

    private static final int OK = 200;
    private static final int CREATED = 201;
    private static final int NO_CONTENT = 204;
    private static final int BAD_REQUEST = 400;
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int UNPROCESSABLE_ENTITY = 422;
    private static final int INTERNAL_SERVER_ERROR = 500;

    public void onSuccess(Call<T> call, Response<T> response){
        Log.i(TAG, "onSuccess: " + response.message());
    }

    public void onCreated(Call<T> call, Response<T> response){
        Log.i(TAG, "onCreated: " + response.message());
    }

    public void onNotFound(Call<T> call, Response<T> response){
        Log.i(TAG, "onNotFound: " + response.message());
    }

    public void onUnauthorized(Call<T> call, Response<T> response){
        Log.i(TAG, "onUnauthorized: " + response.message());
    }

    public void onForbidden(Call<T> call, Response<T> response){
        Log.i(TAG, "onForbidden: " + response.message());
    }

    public void onError(Call<T> call, Response<T> response){
        switch (response.code()){
            case BAD_REQUEST:
                Log.i(TAG, "onError: Bad Request ");
                break;
            case INTERNAL_SERVER_ERROR:
                Log.i(TAG, "onError: Internal Server Error ");
                break;
        }

    }

    public void onUnprocessableEntity(Call<T> call, Response<T> response) {
        Log.i(TAG, "onUnprocessableEntity: " + response.message());
    }

    public void onNoContent(Call<T> call, Response<T> response) {
        Log.i(TAG, "onNoContent: " + response.message());
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        switch (response.code()){
            case OK:
                onSuccess(call, response);
                break;
            case CREATED:
                onCreated(call, response);
                break;
            case NOT_FOUND:
                onNotFound(call, response);
                break;
            case UNAUTHORIZED:
                onUnauthorized(call, response);
                break;
            case FORBIDDEN:
                onForbidden(call, response);
                break;
            case UNPROCESSABLE_ENTITY:
                onUnprocessableEntity(call, response);
                break;
            case NO_CONTENT:
                onNoContent(call, response);
                break;
            case BAD_REQUEST: case INTERNAL_SERVER_ERROR:
                onError(call, response);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Log.i(TAG, "onFailure: " + t.toString());
    }
}
