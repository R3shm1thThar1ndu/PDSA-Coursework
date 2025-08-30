package com.pdsa.touristappbackend.service;

import com.pdsa.touristappbackend.model.LatLon;
import com.pdsa.touristappbackend.routing.GraphProvider;
import com.pdsa.touristappbackend.routing.LazyGraph;
import com.pdsa.touristappbackend.routing.alg.AStar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RoutingService {
    private final GraphProvider graphProvider;

    public static class RouteResult {
        public double distanceMeters;
        public List<LatLon> path;
        public RouteResult(double d, List<LatLon> p) { distanceMeters = d; path = p; }
    }

    public RouteResult routeByCoords(double startLat, double startLon, double endLat, double endLon) {
        try {
            LazyGraph g = graphProvider.getLazyGraph();

            long s = g.findNearest(startLat, startLon);
            long t = g.findNearest(endLat, endLon);
            if (s == -1 || t == -1) return new RouteResult(Double.POSITIVE_INFINITY, List.of());

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
}
