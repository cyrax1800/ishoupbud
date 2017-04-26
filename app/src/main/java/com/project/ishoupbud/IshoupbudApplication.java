package com.project.ishoupbud;

import com.project.ishoupbud.api.repositories.AuthenticationRepo;
import com.project.ishoupbud.api.repositories.ProductRepo;
import com.project.ishoupbud.api.repositories.ShoppingCartRepo;
import com.project.ishoupbud.api.repositories.TransactionRepo;
import com.project.ishoupbud.api.repositories.UserRepo;
import com.project.ishoupbud.api.repositories.WishlistRepo;
import com.project.ishoupbud.helper.TextImageCircleHelper;
import com.project.ishoupbud.network.SessionInterceptor;
import com.project.michael.base.BaseApplication;
import com.project.michael.base.api.APIManager;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by michael on 4/23/17.
 */

public class IshoupbudApplication extends BaseApplication {

    @Override
    public void onCreate() {

        APIManager.addInterceptor(new SessionInterceptor());

        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        APIManager.registerRepository(UserRepo.class);
        APIManager.registerRepository(ProductRepo.class);
        APIManager.registerRepository(TransactionRepo.class);
        APIManager.registerRepository(AuthenticationRepo.class);
        APIManager.registerRepository(WishlistRepo.class);
        APIManager.registerRepository(ShoppingCartRepo.class);

        TextImageCircleHelper.getInstance().init();
    }
}
