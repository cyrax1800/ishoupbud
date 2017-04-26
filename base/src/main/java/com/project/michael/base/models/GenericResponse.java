package com.project.michael.base.models;

/**
 * Created by michael on 4/26/17.
 */

public class GenericResponse<T> extends Response{
    public T product;
    public T user;
    public T wishlist;
}
