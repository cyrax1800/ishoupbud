package com.project.ishoupbud.api.model;

import io.realm.RealmObject;

/**
 * Created by michael on 4/26/17.
 */

public class PictureSize extends RealmObject{

    public int id;
    public String small;
    public String medium;
    public String large;

    public PictureSize(){

    }
}
