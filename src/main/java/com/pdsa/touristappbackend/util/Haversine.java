package com.pdsa.touristappbackend.util;


/**
 * * Utility class for calculating the Haversine distance between two geographic coordinates.
 * * The Haversine formula determines the great-circle distance between two points on a sphere given their longitudes and latitudes.
 * * This implementation assumes a spherical Earth with a radius of 6,371 kilometers.
 * * The distance is returned in meters.
 * * Reference: https://en.wikipedia.org/wiki/Haversine_formula
 * * Usage: Haversine.meters(lat1, lon1, lat2, lon2)
 * * where lat1, lon1 are the latitude and longitude of the first point,
 * * and lat2, lon2 are the latitude and longitude of the second point.
 */
public final class Haversine {
    private static final double R = 6371000.0;
    private Haversine() {}
    public static double meters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2)*Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}
