package com.pdsa.touristappbackend.routing;

import com.pdsa.touristappbackend.model.Edge;
import com.pdsa.touristappbackend.model.OsmNodeData;
import com.pdsa.touristappbackend.util.Haversine;

import java.util.*;

/**
 * A* pathfinding algorithm implementation for routing on a graph.
 * Uses a heuristic based on Haversine distance for estimating costs.
 * Requires a LazyGraph instance to provide graph data.
 * Nodes are represented by their IDs, and edges have weights in meters.
 * Returns the shortest path and its distance in meters.
 * Handles finding the nearest node to given coordinates.
 * Optimized for performance with priority queue and hash maps.
 * Includes safety checks for disconnected nodes during nearest node search.
 * Backtracking is null-safe to avoid errors.
 * Result includes path as a list of coordinates (latitude and longitude) and total distance.
 * Designed for use in applications like mapping and navigation.
 * Thread-safe for concurrent use in multi-threaded environments.
 * Scalable for large graphs with many nodes and edges.
 * Can be extended with additional features like alternative routes or waypoints.
 */
public class AStarRouter {

    private final LazyGraph graph;

    public AStarRouter(LazyGraph graph) {
        this.graph = graph;
    }

    /**
     * Find the shortest path between two nodes using the A* algorithm.
     * @param startId - starting node ID
     * @param endId - ending node ID
     * @return - Result object containing the path and distance
     * @throws Exception - if graph access fails
     * priority queue - to select the next node with the lowest f value
     * map - to store all node records for quick access
     * NodeRecord - to keep track of each node's g, f, and parent
     * open set - nodes to be evaluated
     * closed set - nodes already evaluated (not explicitly used here)
     * reconstruct - to backtrack from end node to start node to build the path
     * If no path is found, returns an empty path with infinite distance
     */
    public Result shortestPath(long startId, long endId) throws Exception {
        PriorityQueue<NodeRecord> open = new PriorityQueue<>(Comparator.comparingDouble(r -> r.f));
        Map<Long, NodeRecord> allRecords = new HashMap<>();

        /**
         * Initialize the start node record and add it to the open set
         * with g=0 and f=heuristic estimate to the end node
         * g - cost from start to current node
         * f - estimated cost from start to end through current node
         */
        NodeRecord start = new NodeRecord(startId, null, 0,
                heuristic(startId, endId));
        open.add(start);
        allRecords.put(startId, start);

        // Main loop
        /**
         * While there are nodes to evaluate in the open set:
         *  - Poll the node with the lowest f value
         *  - If it's the end node, reconstruct and return the path
         *  - For each neighbor of the current node:
         *      - Calculate tentative g score
         *      - If this path to neighbor is better, update its record and add to open set
         *      f - estimated cost from start to end through current node
         *      g - cost from start to current node
         *      heuristic - estimated cost from current node to end node
         *      parent - previous node in the path
         *      Edge weight is in meters
         *      If neighbor not in records, create new record
         *      If neighbor already in records but new g is better, update it
         *      Add updated neighbor to open set
         *      If open set is empty and end not reached, return empty path with infinite distance
         */
        while (!open.isEmpty()) {
            NodeRecord current = open.poll();

            if (current.nodeId == endId) {
                return reconstruct(current, allRecords);
            }

            for (Edge edge : graph.neighbors(current.nodeId)) {
                double g = current.g + edge.getWeightMeters();
                NodeRecord next = allRecords.get(edge.getTo());

                if (next == null || g < next.g) {
                    if (next == null) {
                        next = new NodeRecord(edge.getTo(), current.nodeId, g,
                                g + heuristic(edge.getTo(), endId));
                        allRecords.put(edge.getTo(), next);
                        open.add(next);
                    } else {
                        open.remove(next);
                        next.g = g;
                        next.parent = current.nodeId;
                        next.f = g + heuristic(edge.getTo(), endId);
                        open.add(next);
                    }
                }
            }
        }
        return new Result(Collections.emptyList(), Double.POSITIVE_INFINITY);
    }

    /**
     * Heuristic function estimating cost from one node to another using Haversine distance.
     * @param fromId - starting node ID
     * @param toId - target node ID
     * @return - estimated cost in meters
     * @throws Exception - if node retrieval fails
     */
    private double heuristic(long fromId, long toId) throws Exception {
        OsmNodeData from = graph.getNode(fromId);
        OsmNodeData to = graph.getNode(toId);
        return Haversine.meters(from.getLat(), from.getLon(), to.getLat(), to.getLon());
    }

    private Result reconstruct(NodeRecord goal, Map<Long, NodeRecord> records) {
        List<Map<String, Double>> coords = new ArrayList<>();
        double dist = goal.g;

        NodeRecord cur = goal;
        while (cur != null) {
            OsmNodeData node = graph.getNode(cur.nodeId);
            if (node != null) {
                Map<String, Double> pt = new HashMap<>();
                pt.put("lat", node.getLat());
                pt.put("lon", node.getLon());
                coords.add(pt);
            }
            cur = records.get(cur.parent);
        }
        Collections.reverse(coords);
        return new Result(coords, dist);
    }

    public OsmNodeData findNearestNode(double lat, double lon) {
        OsmNodeData nearest = null;
        double min = Double.MAX_VALUE;

        for (OsmNodeData node : graph.getAllNodes()) {
            double d = Haversine.meters(lat, lon, node.getLat(), node.getLon());
            if (d < min) {
                min = d;
                nearest = node;
            }
        }
        return nearest;
    }

    public OsmNodeData findNearestConnectedNode(double lat, double lon) throws Exception {
        OsmNodeData nearest = null;
        double min = Double.MAX_VALUE;

        for (OsmNodeData node : graph.getAllNodes()) {
            List<Edge> edges = graph.neighbors(node.getId());
            if (edges.isEmpty()) continue; // skip orphan/disconnected nodes

            double d = Haversine.meters(lat, lon, node.getLat(), node.getLon());
            if (d < min) {
                min = d;
                nearest = node;
            }
        }
        return nearest;
    }

    public static class Result {
        public final List<Map<String, Double>> path;
        public final double distance;
        public Result(List<Map<String, Double>> path, double distance) {
            this.path = path;
            this.distance = distance;
        }
    }

    private static class NodeRecord {
        long nodeId;
        Long parent;
        double g;
        double f;

        NodeRecord(long nodeId, Long parent, double g, double f) {
            this.nodeId = nodeId;
            this.parent = parent;
            this.g = g;
            this.f = f;
        }
    }
}