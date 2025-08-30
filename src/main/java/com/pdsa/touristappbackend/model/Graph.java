package com.pdsa.touristappbackend.model;

import java.io.Serializable;
import java.util.*;

public class Graph implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Long, OsmNodeData> nodes = new HashMap<>();
    private final Map<Long, List<Edge>> adj = new HashMap<>();

    public Map<Long, OsmNodeData> getNodes() { return nodes; }
    public Map<Long, List<Edge>> getAdj() { return adj; }

    public void addNode(OsmNodeData n) {
        nodes.put(n.getId(), n);
        adj.computeIfAbsent(n.getId(), k -> new ArrayList<>());
    }

    public void addEdge(long from, long to, double weightMeters) {
        adj.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, weightMeters));
    }

    public List<Edge> neighbors(long id) {
        return adj.getOrDefault(id, Collections.emptyList());
    }
}
