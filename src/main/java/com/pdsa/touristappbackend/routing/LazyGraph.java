package com.pdsa.touristappbackend.routing;

import com.pdsa.touristappbackend.model.Edge;
import com.pdsa.touristappbackend.model.Graph;
import com.pdsa.touristappbackend.model.OsmNodeData;
import com.pdsa.touristappbackend.util.Haversine;

import java.sql.*;
import java.util.*;

public class LazyGraph extends Graph {
    private final String sqlitePath;
    private final Connection conn;

    public LazyGraph(String sqlitePath) throws Exception {
        this.sqlitePath = sqlitePath;
        Class.forName("org.sqlite.JDBC");
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + sqlitePath);
        System.out.println("LazyGraph initialized with " + sqlitePath);
    }

    public OsmNodeData getNode(long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, lat, lon FROM nodes WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new OsmNodeData(rs.getLong("id"),
                            rs.getDouble("lat"),
                            rs.getDouble("lon"));
                }
            }
        }
        return null;
    }

    public List<Edge> getNeighbors(long id) throws SQLException {
        List<Edge> edges = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT to_node FROM edges WHERE from_node=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                OsmNodeData na = getNode(id);
                while (rs.next()) {
                    long to = rs.getLong("to_node");
                    OsmNodeData nb = getNode(to);
                    if (na != null && nb != null) {
                        double d = Haversine.meters(
                                na.getLat(), na.getLon(),
                                nb.getLat(), nb.getLon());
                        edges.add(new Edge(to, d));
                    }
                }
            }
        }
        return edges;
    }

    /** SQL nearest node search */
    public long findNearest(double lat, double lon) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, lat, lon FROM nodes " +
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

    public Connection getConnection() {
        return conn;
    }
}
