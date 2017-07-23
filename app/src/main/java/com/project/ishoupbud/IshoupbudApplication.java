package com.project.ishoupbud;

import com.crashlytics.android.Crashlytics;
import com.project.ishoupbud.api.AppRealmModul;
import com.project.ishoupbud.api.CategoryTransaction;
import com.project.ishoupbud.api.Migration;
import com.project.ishoupbud.api.Uber.UberAPI;
import com.project.ishoupbud.api.Uber.repositories.UberRepo;
import com.project.ishoupbud.api.interceptor.UberInterceptor;
import com.project.ishoupbud.api.repositories.AuthenticationRepo;
import com.project.ishoupbud.api.repositories.GoogleMapRepo;
import com.project.ishoupbud.api.repositories.ProductRepo;
import com.project.ishoupbud.api.repositories.ReviewRepo;
import com.project.ishoupbud.api.repositories.ShoppingCartRepo;
import com.project.ishoupbud.api.repositories.TransactionRepo;
import com.project.ishoupbud.api.repositories.UserRepo;
import com.project.ishoupbud.api.repositories.WishlistRepo;
import com.project.ishoupbud.helper.TextImageCircleHelper;
import com.project.ishoupbud.manager.PusherManager;
import com.project.michael.base.BaseApplication;
import com.project.michael.base.api.APIManager;
import com.project.michael.base.api.GenericResponseInterceptor;
import com.project.michael.base.api.SessionInterceptor;
import com.project.michael.base.database.RealmDb;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;

/**
 * Created by michael on 4/23/17.
 */

public class IshoupbudApplication extends BaseApplication {

    @Override
    public void onCreate() {
        APIManager.addJSONKeyForGeneric("product", "products", "user", "wishlist", "review",
                                        "reviews", "statistic", "carts", "cart", "transactions",
                                        "history", "transaction");
        APIManager.addInterceptor(new SessionInterceptor());
        APIManager.addInterceptor(new GenericResponseInterceptor());

        UberAPI.addInterceptor(new UberInterceptor());

        super.onCreate();
        Fabric.with(this, new Crashlytics());

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        UberAPI.SetUpRetrofit();
        UberAPI.registerRepository(UberRepo.class);

        APIManager.registerRepository(UserRepo.class);
        APIManager.registerRepository(ProductRepo.class);
        APIManager.registerRepository(TransactionRepo.class);
        APIManager.registerRepository(AuthenticationRepo.class);
        APIManager.registerRepository(WishlistRepo.class);
        APIManager.registerRepository(ShoppingCartRepo.class);
        APIManager.registerRepository(ReviewRepo.class);
        APIManager.registerRepository(GoogleMapRepo.class);
        PusherManager.getInstance().init();

        RealmDb.SetUpRealmDatabase(getApplicationContext(), new AppRealmModul());

        RealmDb.migrate(new Migration());

        if (RealmDb.getRealm().isEmpty()) {
            RealmDb.initiateData(new CategoryTransaction());
        }

        TextImageCircleHelper.getInstance().init();
    }
}
