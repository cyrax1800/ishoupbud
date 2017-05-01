package com.project.ishoupbud.api;

import com.project.ishoupbud.api.model.Category;

import io.realm.Realm;

/**
 * Created by michael on 5/1/17.
 */

public class CategoryTransaction implements Realm.Transaction {
    @Override
    public void execute(Realm realm) {
        Category a = realm.createObject(Category.class, 1);
        a.name = "All";
        Category b = realm.createObject(Category.class, 2);
        b.name = "Minuman";
        Category c = realm.createObject(Category.class, 3);
        c.name = "Makanan";
        Category d = realm.createObject(Category.class, 4);
        d.name = "Snacks";
        Category e = realm.createObject(Category.class, 5);
        e.name = "Kosmetik";
        Category f = realm.createObject(Category.class, 6);
        f.name = "Kebutuhan diri";
        Category g = realm.createObject(Category.class, 7);
        g.name = "Kebutuhan Rumah Tangga";
        Category h = realm.createObject(Category.class, 8);
        h.name = "Alat Tulis";
        Category i = realm.createObject(Category.class, 9);
        i.name = "Lain-lain";
    }
}
