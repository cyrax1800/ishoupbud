package com.project.ishoupbud.api.model.map;

import com.google.gson.annotations.SerializedName;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;

/**
 * Created by michael on 6/20/17.
 */

public class DirectionsStep {

    public Value distance;
    public Value duration;
    @SerializedName("start_location")
    public LatLng startLocation;
    @SerializedName("end_location")
    public LatLng endLocation;
    public EncodedPolyline polyline;
}
