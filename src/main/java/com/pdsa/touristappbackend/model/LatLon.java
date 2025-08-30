package com.pdsa.touristappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LatLon {
    private double lat;
    private double lon;
}
