package com.pdsa.touristappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * LatLon class representing a geographical coordinate with latitude and longitude
 * Fields:
 * - lat: latitude
 * - lon: longitude
 * Used for representing locations in the application
 */
@Data
@AllArgsConstructor
public class LatLon {
    private double lat;
    private double lon;
}
