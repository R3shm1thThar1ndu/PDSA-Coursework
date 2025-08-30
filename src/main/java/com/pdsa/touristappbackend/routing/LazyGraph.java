package com.pdsa.touristappbackend.routing;

import com.pdsa.touristappbackend.model.Edge;
import com.pdsa.touristappbackend.model.OsmNodeData;
import com.pdsa.touristappbackend.util.Haversine;

import java.sql.*;
import java.util.*;

public class LazyGraph {
    private final Connection conn;
    private final Map<Long, OsmNodeData> nodeCache = new HashMap<>();

    public LazyGraph(String sqlitePath) throws Exception {
        Class.forName("org.sqlite.JDBC");
        String url = "jdbc:sqlite:" + sqlitePath;
        this.conn = DriverManager.getConnection(url);

        loadNodes();
    }

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

    public OsmNodeData getNode(long id) {
        return nodeCache.get(id); // ✅ Instant lookup
    }

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

    public Connection getConnection() {
        return conn;
    }
}
