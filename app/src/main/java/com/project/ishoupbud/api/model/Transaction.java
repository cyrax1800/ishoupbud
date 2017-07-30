package com.project.ishoupbud.api.model;

import com.google.gson.annotations.SerializedName;
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
    @SerializedName("updated_at")
    public Date date;
    public String type;
    public int nominal;
    public int status;
    public int totalPrice;
    @SerializedName("debit_credit")
    public int debitCredit;
    public List<ShoppingCart> detail;
    public String vendor;
    public Shipment shipment;
    public PictureSize attachments;

    public Transaction(){

    }

    public Transaction(int id, String noTransaction, int type, int totalPrice, Date date, String vendor){
        this.id = id;
        this.noTransaction = noTransaction;
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
