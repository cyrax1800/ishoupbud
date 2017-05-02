package com.project.ishoupbud.api.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by michael on 4/23/17.
 */

public class Category extends RealmObject {

    @PrimaryKey
    public int id;
    public String name;
}
