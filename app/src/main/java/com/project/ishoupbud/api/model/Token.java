package com.project.ishoupbud.api.model;

import com.project.michael.base.models.Response;

/**
 * Created by michael on 4/24/17.
 */

public class Token extends Response{

    public String token_type;
    public long expires_in;
    public String access_token;
    public String refresh_token;
    public User user;
}
