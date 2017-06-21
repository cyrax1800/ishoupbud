package com.project.ishoupbud.api.model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by michael on 6/21/17.
 */

public class ProductSummary extends RealmObject {

    public Sentiment mean;
    public Sentiment count;
}
