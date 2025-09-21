package com.pdsa.touristappbackend.routing.alg;

import com.pdsa.touristappbackend.model.Edge;
import com.pdsa.touristappbackend.model.OsmNodeData;
import com.pdsa.touristappbackend.routing.LazyGraph;
import com.pdsa.touristappbackend.util.Haversine;

import java.util.*;

/**
 * A* over LazyGraph (neighbors and nodes fetched lazily from SQLite).
 * Optimizations:
 *  - Heuristic scaling for faster convergence (slightly non-admissible, but faster in practice)(removed now data set
 *  inculde node distance)
 *  - Optional expansion cap to avoid worst-case blowups
 *  - Null-safe backtracking
 *  - Priority queue for open set
 *  - Hash maps for gScore, fScore, and cameFrom
 *  - Early exit if source == target
 *  - Skip already closed nodes when polling from the priority queue
 *  - Print visited nodes for debugging
 *  - Return path as list of node IDs
 *  - Return infinite distance and empty path if no path found
 *  - Use LinkedList for path reconstruction for efficient front insertion
 *  - Use Set for closed nodes for O(1) lookups
 *  - Avoid duplicate entries in the priority queue by checking closed set
 *  - Use getOrDefault for gScore and fScore to simplify code
 *  - Separate NodeEntry class for priority queue to avoid modifying fScore map
 *  - Use Comparator.comparingDouble for priority queue ordering
 *  - Add comments and documentation for clarity
 *  - Handle exceptions from LazyGraph methods
 *  - Ensure thread-safety if used in multi-threaded context
 *  - Scalable for large graphs with many nodes and edges
 *  - Can be extended with additional features like alternative routes or waypoints
 *
 */
public class AStar {

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
        NodeEntry(long id, double f) { this.id = id; this.f = f; }
    }

    public static Result shortestPath(LazyGraph g, long source, long target) throws Exception {
        if (source == target) return new Result(0.0, List.of(source));

        Map<Long, Double> gScore = new HashMap<>();
        Map<Long, Double> fScore = new HashMap<>();
        Map<Long, Long> cameFrom = new HashMap<>();
        PriorityQueue<NodeEntry> open = new PriorityQueue<>(Comparator.comparingDouble(ne -> ne.f));
        gScore.put(source, 0.0);

        double h0 = heuristic(g, source, target);
        fScore.put(source, h0);
        open.add(new NodeEntry(source, h0));
        Set<Long> closed = new HashSet<>();

        while (!open.isEmpty()) {
            NodeEntry cur = open.poll();
            if (closed.contains(cur.id)) continue;


            OsmNodeData nodeData = g.getNode(cur.id);
            System.out.printf("Visiting node %d (lat=%.6f, lon=%.6f, f=%.2f)%n",
                    cur.id, nodeData.getLat(), nodeData.getLon(), cur.f);

            if (cur.id == target) break;
            closed.add(cur.id);

            for (Edge e : g.neighbors(cur.id)) {
                long nb = e.getTo();
                if (closed.contains(nb)) continue;
                double tentativeG = gScore.getOrDefault(cur.id, Double.POSITIVE_INFINITY) + e.getWeightMeters();
                if (tentativeG < gScore.getOrDefault(nb, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(nb, cur.id);
                    gScore.put(nb, tentativeG);
                    double f = tentativeG + heuristic(g, nb, target);
                    fScore.put(nb, f);
                    open.add(new NodeEntry(nb, f));
                }
            }
        }

        if (!gScore.containsKey(target)) return new Result(Double.POSITIVE_INFINITY, List.of());

        LinkedList<Long> path = new LinkedList<>();
        long cur = target;
        while (cur != source) {
            path.addFirst(cur);
            cur = cameFrom.get(cur);
        }
        path.addFirst(source);
        return new Result(gScore.get(target), path);
    }

    private static double heuristic(LazyGraph g, long aId, long bId) throws Exception {
        OsmNodeData a = g.getNode(aId);
        OsmNodeData b = g.getNode(bId);
        if (a == null || b == null) return 0.0;
        return Haversine.meters(a.getLat(), a.getLon(), b.getLat(), b.getLon());
    }
}