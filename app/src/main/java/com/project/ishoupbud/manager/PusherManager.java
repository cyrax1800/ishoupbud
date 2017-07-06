package com.project.ishoupbud.manager;

import android.util.Log;

import com.project.ishoupbud.api.model.User;
import com.project.ishoupbud.api.model.pusher.SaldoPusher;
import com.project.ishoupbud.utils.ConstClass;
import com.project.michael.base.database.SharedPref;
import com.project.michael.base.utils.GsonUtils;
import com.project.michael.base.utils.StringUtils;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.util.HttpAuthorizer;

import java.util.HashMap;

/**
 * Created by michael on 7/1/17.
 */

public class PusherManager {

    private static final String TAG = "tmp-Pusher";
    private static PusherManager instance;
    //    private final String app_id = "360749";
//    private final String key = "381b329c74e496a4f663";
//    private final String secret = "7cf6ea2e8871c0bf7805";
//    private final String cluster = "ap1";
    private final String app_id = "361499";
    private final String key = "55e2ab7c65e7cf67dcb8";
    private final String secret = "a0be0400c553ba7037e6";
    private final String cluster = "mt1";

    private Pusher pusher;
    private Channel productChannel;
    private Channel userChannel;

    public PusherManager() {

    }

    public static synchronized PusherManager getInstance() {
        if (instance == null) {
            instance = new PusherManager();
        }
        return instance;
    }

    public void init() {
        HttpAuthorizer authorizer = new HttpAuthorizer("https://shoupbud.xyz/broadcasting/auth");
        HashMap<String, String> map = new HashMap<>();
        map.put("Authorization", "Bearer " + SharedPref.getValueString(SharedPref.ACCESS_TOKEN));
        authorizer.setHeaders(map);
        PusherOptions options = new PusherOptions();
        options.setCluster(cluster);
        options.setAuthorizer(authorizer);
        options.setEncrypted(true);

        pusher = new Pusher(key, options);
        pusher.connect();

    }

    public void listenToSaldoChannel() {
        if (StringUtils.isNullOrEmpty(SharedPref.getValueString(SharedPref.ACCESS_TOKEN))) return;
        final User user = GsonUtils.getObjectFromJson(SharedPref.getValueString(ConstClass.USER),
                User.class);
        if(userChannel != null){
            pusher.unsubscribe(userChannel.getName());
        }
        pusher.unsubscribe("saldo.user." + user.id);
        userChannel = pusher.subscribe("saldo.user." + user.id, new ChannelEventListener() {
            @Override
            public void onSubscriptionSucceeded(String s) {
                Log.d(TAG, "success subscribe");
            }

            @Override
            public void onEvent(String channel, String event, String data) {
                Log.d(TAG, "onEvent: Saldo " + data);
                SaldoPusher saldoPusher = GsonUtils.getObjectFromJson(data,
                        SaldoPusher.class);
                user.saldo = saldoPusher.saldo.nominal;
                SharedPref.save(ConstClass.USER, GsonUtils.getJsonFromObject
                        (user, User.class));
            }
        }, "saldo.updated");
        Log.d(TAG, "listenToSaldoChannel: ");
    }

    public void listenToProduct(int id, ChannelEventListener eventListener) {
        if (productChannel != null) {
            pusher.unsubscribe(productChannel.getName());
        }
        productChannel = pusher.subscribe("review.product." + id, eventListener,
                "review.updated", "review.created");
    }

    public void disconnectListenToProduct() {
        pusher.unsubscribe(productChannel.getName());
    }
}
