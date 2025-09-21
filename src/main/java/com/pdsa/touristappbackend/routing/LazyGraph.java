package com.pdsa.touristappbackend.routing;

import com.pdsa.touristappbackend.model.Edge;
import com.pdsa.touristappbackend.model.OsmNodeData;
import com.pdsa.touristappbackend.util.Haversine;

import java.sql.*;
import java.util.*;

/**
 * LazyGraph class for managing a graph of OSM nodes and edges with lazy loading from SQLite
 * nodes are cached in memory, edges are loaded on demand
 * connected components are precomputed for quick access
 * Usage:
 * LazyGraph graph = new LazyGraph("path/to/sqlite.db");
 * OsmNodeData node = graph.getNode(nodeId);
 * List<Edge> edges = graph.neighbors(nodeId);
 * int compId = graph.getComponent(nodeId);
 * graph.getAllNodes();
 * graph.getConnection();
 * graph.close();
 */
public class LazyGraph {
    // SQLite connection
    private final Connection conn;
    // In-memory cache of nodes
    private final Map<Long, OsmNodeData> nodeCache = new HashMap<>();
    // Map of nodeId to connected component id
    private final Map<Long, Integer> componentMap = new HashMap<>();

    /**
     * Constructor to initialize LazyGraph with SQLite database path
     * Loads all nodes into memory and builds connected components
     * @param sqlitePath path to SQLite database file
     * @throws Exception if database connection or queries fail
     */
    public LazyGraph(String sqlitePath) throws Exception {
        Class.forName("org.sqlite.JDBC");
        String url = "jdbc:sqlite:" + sqlitePath;
        this.conn = DriverManager.getConnection(url);
        loadNodes();
        buildComponents();
    }

    /**
     * Close the database connection
     * @throws SQLException - if closing the connection fails
     */
    private void loadNodes() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, lat, lon FROM nodes")) {
            int count = 0;
            while (rs.next()) {
                long id = rs.getLong("id");
                double lat = rs.getDouble("lat");
                double lon = rs.getDouble("lon");
                nodeCache.put(id, new OsmNodeData(id, lat, lon));
                count++;
            }
            System.out.println("LazyGraph: cached " + count + " nodes in memory");
        }
    }

    /**
     * Retrieve a node by its ID
     * @param id - OSM node ID
     * @return OsmNodeData or null if not found
     */
    public OsmNodeData getNode(long id) {
        return nodeCache.get(id);
    }

    public Collection<OsmNodeData> getAllNodes() {
        return nodeCache.values();
    }

    /**
     * Retrieve neighbors (outgoing edges) of a node
     * Edges are loaded on demand from the database
     * @param id - OSM node ID
     * @return List of Edge objects representing outgoing edges
     * @throws Exception - if database query fails
     */
    public List<Edge> neighbors(long id) throws Exception {
        List<Edge> edges = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT to_node FROM edges WHERE from_node = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                OsmNodeData src = getNode(id);
                while (rs.next()) {
                    long toId = rs.getLong("to_node");
                    OsmNodeData dst = getNode(toId);
                    if (src != null && dst != null) {
                        double d = Haversine.meters(src.getLat(), src.getLon(),
                                dst.getLat(), dst.getLon());
                        edges.add(new Edge(toId, d));
                    }
                }
            }
        }
        return edges;
    }

    /**
     * Build connected components using BFS
     * Populates componentMap with nodeId to componentId mapping
     * @throws Exception - if any error occurs during processing
     */
    private void buildComponents() throws Exception {
        int compId = 0;
        Set<Long> visited = new HashSet<>();

        for (long nodeId : nodeCache.keySet()) {
            if (visited.contains(nodeId)) continue;

            Queue<Long> q = new ArrayDeque<>();
            q.add(nodeId);
            visited.add(nodeId);
            componentMap.put(nodeId, compId);

            while (!q.isEmpty()) {
                long u = q.poll();
                for (Edge e : neighbors(u)) {
                    if (!visited.contains(e.getTo())) {
                        visited.add(e.getTo());
                        componentMap.put(e.getTo(), compId);
                        q.add(e.getTo());
                    }
                }
            }
            compId++;
        }
        System.out.println("LazyGraph: built " + compId + " connected components");
    }

    public int getComponent(long nodeId) {
        return componentMap.getOrDefault(nodeId, -1);
    }

    public Connection getConnection() {
        return conn;
    }
}