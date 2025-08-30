package com.pdsa.touristappbackend.routing.index;

import com.pdsa.touristappbackend.model.Graph;
import com.pdsa.touristappbackend.model.OsmNodeData;
import com.pdsa.touristappbackend.util.Haversine;

import java.util.*;

public class GridIndex {
    private final double cellSizeDeg;
    private final Map<String, List<Long>> cells = new HashMap<>();
    private final Graph graph;

    public GridIndex(Graph graph, double cellSizeDeg) {
        this.graph = graph;
        this.cellSizeDeg = cellSizeDeg;
        build();
    }

    private String key(int cx, int cy) { return cx + ":" + cy; }
    private int cellX(double lon) { return (int) Math.floor(lon / cellSizeDeg); }
    private int cellY(double lat) { return (int) Math.floor(lat / cellSizeDeg); }

    private void build() {
        for (OsmNodeData n : graph.getNodes().values()) {
            int cx = cellX(n.getLon());
            int cy = cellY(n.getLat());
            cells.computeIfAbsent(key(cx, cy), k -> new ArrayList<>()).add(n.getId());
        }
        System.out.println("GridIndex built: cells=" + cells.size());
    }

    public long nearest(double lat, double lon) {
        if (graph.getNodes().isEmpty()) return -1L;
        int cx = cellX(lon), cy = cellY(lat);
        double best = Double.POSITIVE_INFINITY;
        long bestId = -1L;
        int maxRadius = 8;

        for (int r = 0; r <= maxRadius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    List<Long> list = cells.get(key(cx + dx, cy + dy));
                    if (list == null) continue;
                    for (Long id : list) {
                        OsmNodeData node = graph.getNodes().get(id);
                        double d = Haversine.meters(lat, lon, node.getLat(), node.getLon());
                        if (d < best) { best = d; bestId = id; }
                    }
                }
            }
            if (bestId != -1L) break;
        }

        if (bestId == -1L) {
            for (OsmNodeData node : graph.getNodes().values()) {
                double d = Haversine.meters(lat, lon, node.getLat(), node.getLon());
                if (d < best) { best = d; bestId = node.getId(); }
            }
        }
        return bestId;
    }
}
