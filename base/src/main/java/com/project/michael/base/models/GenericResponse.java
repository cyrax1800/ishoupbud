package com.project.michael.base.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by michael on 4/26/17.
 */

public class GenericResponse<T> extends Response{
    @SerializedName("data")
    public T data;
    public Pagination pagination;
}
