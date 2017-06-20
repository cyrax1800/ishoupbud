package com.project.ishoupbud.api.model.map;

import com.google.maps.model.Bounds;

/**
 * Created by michael on 6/20/17.
 */

public class DirectionsRoute {
    public Bounds bounds;
    public DirectionsLeg[] legs;
    public int[] waypointOrder;
    public String[] warnings;
}