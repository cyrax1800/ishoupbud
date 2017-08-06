package com.project.ishoupbud.utils;

import com.project.ishoupbud.api.model.Product;
import com.project.michael.base.database.SharedPref;
import com.project.michael.base.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 4/9/17.
 */

public class ConstClass {

    // Shared Preference
    public static String USER = "user";
    public static String LAST_PRODUCT = "last_product";

    // Extra
    public static String REGISTER_EXTRA = "register";
    public static String LOGIN_EXTRA = "register";
    public static String PRODUCT_EXTRA = "product";
    public static String CATEGORY_EXTRA = "category";
    public static String ADDRESS_EXTRA = "address";
    public static String LOCATION_EXTRA = "location";
    public static String LATITUDE_EXTRA = "latitude";
    public static String LONGITUDE_EXTRA = "longitude";
    public static String TRANSACTION_EXTRA = "transaction";
    public static String CART_EXTRA = "cart";
    public static String FROM_CHECKOUT_EXTRA = "checkout";
    public static String COMPARE_EXTRA = "compare";
    public static String KEYWORD_EXTRA = "keyword";
    public static String PAGING_EXTRA = "paging";

    public static List<Product> lastSeenProduct = new ArrayList<>();

    public static void addLastSeenProduct(Product product){
        if(lastSeenProduct == null) lastSeenProduct = new ArrayList<>();
        for(int i = 0; i< lastSeenProduct.size(); i++){
            if(lastSeenProduct.get(i).id == product.id){
                lastSeenProduct.set(i,product);
                return;
            }
        }
        if(lastSeenProduct.size() >= 10){
            lastSeenProduct.remove(lastSeenProduct.size() - 1);
        }
        lastSeenProduct.add(0, product);

        SharedPref.save(LAST_PRODUCT, GsonUtils.getJsonFromObject(lastSeenProduct));
    }

}
