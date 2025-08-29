package com.pdsa.touristappbackend.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RouteResponse {
    private double distance;
    private double time;
    private List<Coordinate> path = new ArrayList<>();
}
