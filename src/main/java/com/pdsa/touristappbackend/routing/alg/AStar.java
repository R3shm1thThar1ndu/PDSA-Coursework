package com.pdsa.touristappbackend.routing.alg;

import com.pdsa.touristappbackend.model.Edge;
import com.pdsa.touristappbackend.model.OsmNodeData;
import com.pdsa.touristappbackend.routing.LazyGraph;
import com.pdsa.touristappbackend.util.Haversine;

import java.util.*;

/**
 * A* over LazyGraph (neighbors and nodes fetched lazily from SQLite).
 * Optimizations:
 *  - Heuristic scaling for faster convergence (slightly non-admissible, but faster in practice)
 *  - Optional expansion cap to avoid worst-case blowups
 *  - Null-safe backtracking
 */
public class AStar {

    // Tune these if needed
    private static final double HEURISTIC_SCALE = 1.2;     // >1.0 speeds up, may degrade optimality slightly
    private static final int MAX_EXPANSIONS = 1_000_000;   // set lower if you want to hard-cap search effort

    public static class Result {
        private final double distanceMeters;
        private final List<Long> pathNodeIds;

        public Result(double distanceMeters, List<Long> pathNodeIds) {
            this.distanceMeters = distanceMeters;
            this.pathNodeIds = pathNodeIds;
        }

        public double getDistanceMeters() {
            return distanceMeters;
        }

        public List<Long> getPathNodeIds() {
            return pathNodeIds;
        }
    }

    private static class NodeEntry {
        long id;
        double f;

        NodeEntry(long id, double f) {
            this.id = id;
            this.f = f;
        }
    }

    public static Result shortestPath(LazyGraph g, long source, long target) throws Exception {
        if (source == target) return new Result(0.0, List.of(source));

        Map<Long, Double> gScore = new HashMap<>();
        Map<Long, Double> fScore = new HashMap<>();
        Map<Long, Long> cameFrom = new HashMap<>();
        PriorityQueue<NodeEntry> open = new PriorityQueue<>(Comparator.comparingDouble(ne -> ne.f));
        Set<Long> closed = new HashSet<>();

        gScore.put(source, 0.0);
        double h0 = heuristic(g, source, target);
        fScore.put(source, h0);
        open.add(new NodeEntry(source, h0));

        int expansions = 0;

        while (!open.isEmpty()) {
            NodeEntry cur = open.poll();
            if (closed.contains(cur.id)) continue;
            if (cur.id == target) break;
            closed.add(cur.id);

            // Optional: protect against worst-case blowups
            if (++expansions > MAX_EXPANSIONS) {
                // Give up and return "not found" (infinite)
                return new Result(Double.POSITIVE_INFINITY, List.of());
            }

            for (Edge e : g.neighbors(cur.id)) {
                long nb = e.getTo();
                if (closed.contains(nb)) continue;

                double tentativeG = gScore.getOrDefault(cur.id, Double.POSITIVE_INFINITY) + e.getWeightMeters();
                if (tentativeG < gScore.getOrDefault(nb, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(nb, cur.id);
                    gScore.put(nb, tentativeG);
                    double f = tentativeG + HEURISTIC_SCALE * heuristic(g, nb, target);
                    fScore.put(nb, f);
                    open.add(new NodeEntry(nb, f));
                }
            }
        }

        if (!gScore.containsKey(target)) {
            // No path found
            return new Result(Double.POSITIVE_INFINITY, List.of());
        }

        // Reconstruct path (null-safe)
        LinkedList<Long> path = new LinkedList<>();
        long cur = target;
        path.addFirst(cur);
        while (cur != source) {
            Long prev = cameFrom.get(cur);
            if (prev == null) {
                // Couldn't backtrack -> treat as not found to be safe
                return new Result(Double.POSITIVE_INFINITY, List.of());
            }
            cur = prev;
            path.addFirst(cur);
        }

        return new Result(gScore.get(target), path);
    }

    private static double heuristic(LazyGraph g, long aId, long bId) throws Exception {
        OsmNodeData a = g.getNode(aId);
        OsmNodeData b = g.getNode(bId);
        if (a == null || b == null) return 0.0;
        return Haversine.meters(a.getLat(), a.getLon(), b.getLat(), b.getLon());
    }
}
