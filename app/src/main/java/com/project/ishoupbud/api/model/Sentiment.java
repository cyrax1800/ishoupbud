package com.project.ishoupbud.api.model;

import io.realm.RealmObject;

/**
 * Created by michael on 5/13/17.
 */

public class Sentiment extends RealmObject {
    public double pos;
    public double neu;
    public double neg;
}
