package com.project.ishoupbud.api.model;

/**
 * Created by michael on 4/10/17.
 */

public class User {

    public int id;
    public String picture_url;
    public String name;
    public String email;
    public String phone;
    public String address;
    public String password;
    public Double latitude;
    public Double longitude;
    public Float saldo;
    public int role;

    public String getSmallImage(){
        return "https://shoupbud.xyz/image/medium/" + picture_url;
    }

    public String getMediumImage(){
        return "https://shoupbud.xyz/image/small/" + picture_url;
    }

}
