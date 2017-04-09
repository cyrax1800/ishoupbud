package com.project.ishoupbud.api.model;

import com.project.michael.base.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by michael on 4/9/17.
 */

public class Transaction {

    public int id;
    public String noTransaction;
    public Date date;
    public int type;
    public int totalPrice;
    public String vendor;

    public Transaction(){

    }

    public Transaction(int id, String noTransaction, int type, int totalPrice, Date date, String vendor){
        this.id = id;
        this.noTransaction = noTransaction;
        this.type = type;
        this.totalPrice = totalPrice;
        this.date = date;
        this.vendor = vendor;
    }

    public static Transaction createDummy(){
        return new Transaction(1,"asdsad", Utils.random(0,1),Utils.random(1,2) * 1000,new Date(), "hypermartd");
    }

    public static List<Transaction> getDummy(int count){
        List<Transaction> list = new ArrayList<>();
        for(int i = 0; i< count;i++){
            list.add(createDummy());
        }
        return list;
    }
}
