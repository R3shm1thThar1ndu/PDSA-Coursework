package com.pdsa.touristappbackend.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteRequest {
    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;

}
