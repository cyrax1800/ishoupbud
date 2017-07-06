package com.project.ishoupbud.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by michael on 6/6/17.
 */

public class Saldo {
    public int id;
    public boolean status;
    @SerializedName(value="nominal", alternate={"saldo"})
    public float nominal;
    public Date issueDate;
}
