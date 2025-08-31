package com.pdsa.touristappbackend.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class PoiSqliteRepository {

    private final String dbPath = "src/main/resources/data/poi.db";
    private Connection conn;

    @PostConstruct
    public void init() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            System.out.println("Connected to POI SQLite: " + dbPath);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to POI SQLite", e);
        }
    }


    public List<Map<String, Object>> findPoisByCategoryNearPath(
            List<Map<String, Double>> path, List<String> categories) {

        List<Map<String, Object>> results = new ArrayList<>();

        if (path == null || path.isEmpty() || categories == null || categories.isEmpty()) {
            return results;
        }

        try {
            String sql = "SELECT id, name, category, lat, lon FROM pois WHERE category IN ("
                    + String.join(",", Collections.nCopies(categories.size(), "?")) + ")";
            PreparedStatement ps = conn.prepareStatement(sql);

            for (int i = 0; i < categories.size(); i++) {
                ps.setString(i + 1, categories.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double lat = rs.getDouble("lat");
                double lon = rs.getDouble("lon");

                // check if close to any path point (within ~500m)
                boolean near = path.stream().anyMatch(p -> haversine(
                        p.get("lat"), p.get("lon"), lat, lon) < 500);

                if (near) {
                    Map<String, Object> poi = new HashMap<>();
                    poi.put("id", rs.getLong("id"));
                    poi.put("name", rs.getString("name"));
                    poi.put("category", rs.getString("category"));
                    poi.put("lat", lat);
                    poi.put("lon", lon);
                    results.add(poi);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query POIs", e);
        }

        return results;
    }

    // Haversine distance (m)
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}
