package com.project.ishoupbud.api.response;

import com.google.gson.annotations.SerializedName;
import com.project.ishoupbud.api.model.Review;
import com.project.michael.base.models.Response;

import java.util.List;

/**
 * Created by michael on 5/14/17.
 */

public class ProductAllReviewResponse extends Response {

    public Review youReview;
    @SerializedName("data")
    public List<Review> reviews;
}
