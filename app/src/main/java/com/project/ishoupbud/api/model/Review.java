package com.project.ishoupbud.api.model;

import java.util.Date;

/**
 * Created by michael on 4/10/17.
 */

public class Review {

    public int id;
    public User user;
    public Vendor vendor;
    public String description;
    public double rating;
    public Date date;
    public Sentiment sentiment;
    public boolean status;
}
