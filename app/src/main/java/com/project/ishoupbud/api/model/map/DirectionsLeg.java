package com.project.ishoupbud.api.model.map;

import com.google.gson.annotations.SerializedName;
import com.google.maps.model.LatLng;

/**
 * Created by michael on 6/20/17.
 */

public class DirectionsLeg {

    public DirectionsStep[] steps;
    public Value distance;
    public Value duration;
    @SerializedName("duration_in_traffic")
    public Value durationInTraffic;
    @SerializedName("start_location")
    public LatLng startLocation;
    @SerializedName("end_location")
    public LatLng endLocation;
    @SerializedName("start_address")
    public String startAddress;
    @SerializedName("end_address")
    public String endAddress;

}