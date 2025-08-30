package com.pdsa.touristappbackend.routing;

import com.pdsa.touristappbackend.model.Graph;
import com.pdsa.touristappbackend.model.OsmNodeData;
import com.pdsa.touristappbackend.util.Haversine;

import java.sql.*;

public class GraphLoaderFromSqlite {

    public Graph load(String sqlitePath) throws Exception {
        Class.forName("org.sqlite.JDBC");
        String url = "jdbc:sqlite:" + sqlitePath;
        try (Connection conn = DriverManager.getConnection(url)) {
            Graph g = new Graph();

            // Nodes
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT id, lat, lon FROM nodes")) {
                int nodeCount = 0;
                while (rs.next()) {
                    long id = rs.getLong("id");
                    double lat = rs.getDouble("lat");
                    double lon = rs.getDouble("lon");
                    g.addNode(new OsmNodeData(id, lat, lon));
                    nodeCount++;
                }
                System.out.println("GraphLoader: loaded nodes=" + nodeCount);
            }

            // Edges: compute weight using haversine between node coords
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT from_node, to_node FROM edges")) {

                int edgeCount = 0;
                while (rs.next()) {
                    long a = rs.getLong("from_node");
                    long b = rs.getLong("to_node");
                    OsmNodeData na = g.getNodes().get(a);
                    OsmNodeData nb = g.getNodes().get(b);
                    if (na == null || nb == null) continue;
                    double d = Haversine.meters(na.getLat(), na.getLon(), nb.getLat(), nb.getLon());
                    g.addEdge(a, b, d);
                    edgeCount++;
                }
                System.out.println("GraphLoader: loaded edges=" + edgeCount);
            }
            return g;
        }
    }
}
