package com.pdsa.touristappbackend.service;

import com.pdsa.touristappbackend.model.LatLon;
import com.pdsa.touristappbackend.model.OsmNodeData;
import com.pdsa.touristappbackend.routing.GraphProvider;
import com.pdsa.touristappbackend.routing.LazyGraph;
import com.pdsa.touristappbackend.routing.alg.AStar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RoutingService {
    private final GraphProvider graphProvider;

    public static class RouteResult {
        public double distanceMeters;
        public List<LatLon> path;
        public RouteResult(double d, List<LatLon> p) {
            this.distanceMeters = d;
            this.path = p;
        }
    }

    public RouteResult routeByCoords(double startLat, double startLon, double endLat, double endLon) {
        try {
            LazyGraph g = graphProvider.getLazyGraph();

            long s = findNearest(g, startLat, startLon);
            long t = findNearest(g, endLat, endLon);
            if (s == -1 || t == -1)
                return new RouteResult(Double.POSITIVE_INFINITY, List.of());

            AStar.Result res = AStar.shortestPath(g, s, t);

            // Safety: no path found
            if (res.getPathNodeIds().isEmpty())
                return new RouteResult(Double.POSITIVE_INFINITY, List.of());

            List<LatLon> coords = res.getPathNodeIds().stream()
                    .map(id -> {
                        try {
                            OsmNodeData n = g.getNode(id);
                            return n != null ? new LatLon(n.getLat(), n.getLon()) : null;
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

    private long findNearest(LazyGraph g, double lat, double lon) throws Exception {
        try (PreparedStatement ps = g.getConnection().prepareStatement(
                "SELECT id FROM nodes " +
                        "ORDER BY ((lat-?)*(lat-?) + (lon-?)*(lon-?)) ASC LIMIT 1")) {
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
