package com.project.ishoupbud.api.model;

import com.project.ishoupbud.api.Uber.model.UberPrices;
import com.project.ishoupbud.api.model.map.Direction;

import retrofit2.Call;

/**
 * Created by michael on 7/1/17.
 */

public class FetchingShipmentDataHelper<Model> {

    public int idx;
    public Call<Direction> callGetTime;
    public Call<UberPrices> callGetPrice;
    public Boolean status;
    public Boolean statusTime;
    public Boolean statusPrice;
    public onFetchingListener<Model> onFetchingListener;
    public Model data;

    public FetchingShipmentDataHelper(int position, onFetchingListener onFetchingListener){
        idx = position;
        this.onFetchingListener = onFetchingListener;
    }

    public void startFetch(Model data){
        this.data = data;
        status = false;
        statusTime = false;
        statusPrice = false;
    }

    public void doneTime(){
        statusTime = true;
        if(statusPrice){
            status = true;
            onFetchingListener.onCompleteFetch(idx, data);
        }
    }

    public void donePrice(){
        statusPrice = true;
        if(statusTime){
            status = true;
            onFetchingListener.onCompleteFetch(idx, data);
        }
    }

    public interface onFetchingListener<Model>{
        public void onCompleteFetch(int idx, Model data);
    }

}
