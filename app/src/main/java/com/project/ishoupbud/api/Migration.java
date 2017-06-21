package com.project.ishoupbud.api;

import android.util.Log;

import com.project.ishoupbud.api.model.PictureSize;
import com.project.ishoupbud.api.model.ProductSummary;
import com.project.ishoupbud.api.model.ProductVendors;

import java.util.List;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmList;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by michael on 5/1/17.
 */

public class Migration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        Log.d("tmp", "migrate: " + oldVersion);

        if(oldVersion == 0){
            if(!schema.contains("Category")){
                Log.d("tmp", "migrate: ");
                schema.create("Category")
                        .addField("id", int.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.INDEXED)
                        .addField("name", String.class, FieldAttribute.REQUIRED);

                schema.create("PictureSize")
                        .addField("id", int.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.INDEXED)
                        .addField("small", String.class)
                        .addField("medium", String.class)
                        .addField("large", String.class);

                schema.create("Product")
                        .addField("id", int.class, FieldAttribute.PRIMARY_KEY, FieldAttribute.INDEXED)
                        .addField("barcode", String.class, FieldAttribute.REQUIRED)
                        .addField("name", String.class, FieldAttribute.REQUIRED)
                        .addField("price", int.class, FieldAttribute.REQUIRED)
                        .addField("totalRating", double.class)
                        .addField("totalReview", int.class)
                        .addField("totalReview", int.class)
                        .addField("description", String.class)
                        .addField("pictureUrl", PictureSize.class, FieldAttribute.REQUIRED)
                        .addField("liked", Boolean.class)
                        .addField("vendors", RealmList.class);
//                        .addField("productSummary", ProductSummary.class);
            }
        }
    }
}
