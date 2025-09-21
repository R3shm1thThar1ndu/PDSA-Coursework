package com.pdsa.touristappbackend.service;

import com.pdsa.touristappbackend.model.LatLon;
import com.pdsa.touristappbackend.routing.GraphProvider;
import com.pdsa.touristappbackend.routing.LazyGraph;
import com.pdsa.touristappbackend.routing.alg.AStar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

/**
 * RoutingService provides functionality to compute the shortest path between two geographical coordinates
 * using a graph representation of the road network. It utilizes the A* algorithm for pathfinding
 * and interacts with a SQLite database to retrieve graph nodes and edges.
 * The service includes methods to find the nearest connected graph nodes to given coordinates
 * and to compute the route between these nodes.
 * The results include the total distance and the list of coordinates along the path.
 * Error handling is implemented to manage exceptions during database access and pathfinding.
 * Designed for use in applications such as mapping, navigation, and location-based services.
 * Thread-safe for concurrent requests in a multi-threaded environment.
 * Scalable for large graphs with many nodes and edges.
 * Can be extended with additional features like alternative routes or waypoints.
 */
@Service
@RequiredArgsConstructor
public class RoutingService {
    private final GraphProvider graphProvider;

    public static class RouteResult {
        public double distanceMeters;
        public List<LatLon> path;

        public RouteResult(double d, List<LatLon> p) {
            distanceMeters = d;
            path = p;
        }
    }

    public RouteResult routeByCoords(double startLat, double startLon, double endLat, double endLon) {
        try {
            LazyGraph g = graphProvider.getLazyGraph();

            long s = findNearestConnected(g, startLat, startLon);
            long t = findNearestConnected(g, endLat, endLon);

            if (s == -1 || t == -1) {
                System.out.println("DEBUG: No valid start or end node found.");
                return new RouteResult(Double.POSITIVE_INFINITY, List.of());
            }

            System.out.println("DEBUG: Start node = " + s);
            System.out.println("DEBUG: End node   = " + t);

            AStar.Result res = AStar.shortestPath(g, s, t);

            List<LatLon> coords = res.getPathNodeIds().stream()
                    .map(id -> {
                        try {
                            var n = g.getNode(id);
                            return new LatLon(n.getLat(), n.getLon());
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();

            return new RouteResult(res.getDistanceMeters(), coords);

        } catch (Exception e) {
            e.printStackTrace();
            return new RouteResult(Double.POSITIVE_INFINITY, List.of());
        }
    }


    private long findNearestConnected(LazyGraph g, double lat, double lon) throws Exception {
        String fromCol = "from_node";
        String toCol = "to_node";

        try (Statement st = g.getConnection().createStatement();
             ResultSet rs = st.executeQuery("PRAGMA table_info(edges)")) {
            while (rs.next()) {
                String name = rs.getString("name").toLowerCase();
                if (name.equals("source")) fromCol = "source";
                if (name.equals("target")) toCol = "target";
            }
        }

        String sql = "SELECT n.id, n.lat, n.lon " +
                "FROM nodes n " +
                "WHERE EXISTS (SELECT 1 FROM edges e WHERE e." + fromCol + " = n.id OR e." + toCol + " = n.id) " +
                "ORDER BY ((n.lat-?)*(n.lat-?) + (n.lon-?)*(n.lon-?)) ASC LIMIT 1";

        try (PreparedStatement ps = g.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, lat);
            ps.setDouble(2, lat);
            ps.setDouble(3, lon);
            ps.setDouble(4, lon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        return -1;
    }
}
