package com.pdsa.touristappbackend.controller;

import com.pdsa.touristappbackend.model.OsmNodeData;
import com.pdsa.touristappbackend.routing.AStarRouter;
import com.pdsa.touristappbackend.routing.LazyGraph;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * MultiStopController class handling multi-stop routing
 * Endpoint:
 * - GET /api/route/multi-stop: calculate route with multiple stops
 * Example request:
 * /api/route/multi-stop?startLat=...&startLon=...&endLat=...&endLon=...&stopLat=...&stopLon=...
 * (stopLat and stopLon can be repeated for multiple stops)
 * Response:
 * {
 *   "distanceMeters": total_distance,
 *   "path": [ { "lat": ..., "lon": ... }, ... ],
 *   "stopsCount": number_of_stops
 * }
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/route")
public class MultiStopController {

    private final LazyGraph graph;

    public MultiStopController(LazyGraph graph) {
        this.graph = graph;
    }

    @GetMapping("/multi-stop")
    public Map<String, Object> multiStop(
            @RequestParam double startLat,
            @RequestParam double startLon,
            @RequestParam double endLat,
            @RequestParam double endLon,
            @RequestParam List<Double> stopLat,
            @RequestParam List<Double> stopLon
    ) throws Exception {
        if (stopLat.size() != stopLon.size()) {
            throw new IllegalArgumentException("stopLat and stopLon lengths differ");
        }

        AStarRouter router = new AStarRouter(graph);
        List<OsmNodeData> waypoints = new ArrayList<>();

        OsmNodeData startNode = router.findNearestConnectedNode(startLat, startLon);
        System.out.printf("DEBUG: Start → Node %d (%.6f, %.6f)%n",
                startNode.getId(), startNode.getLat(), startNode.getLon());
        waypoints.add(startNode);

        for (int i = 0; i < stopLat.size(); i++) {
            OsmNodeData wp = router.findNearestConnectedNode(stopLat.get(i), stopLon.get(i));
            System.out.printf("DEBUG: Stop %d → Node %d (%.6f, %.6f)%n",
                    i + 1, wp.getId(), wp.getLat(), wp.getLon());
            waypoints.add(wp);
        }

        OsmNodeData endNode = router.findNearestConnectedNode(endLat, endLon);
        System.out.printf("DEBUG: End → Node %d (%.6f, %.6f)%n",
                endNode.getId(), endNode.getLat(), endNode.getLon());
        waypoints.add(endNode);

        List<Map<String, Double>> allPath = new ArrayList<>();
        double totalDist = 0;

        for (int i = 0; i < waypoints.size() - 1; i++) {
            long fromId = waypoints.get(i).getId();
            long toId = waypoints.get(i + 1).getId();

            System.out.printf("DEBUG: Routing segment %d → %d%n", fromId, toId);

            AStarRouter.Result r = router.shortestPath(fromId, toId);

            if (r.distance == Double.POSITIVE_INFINITY) {
                System.out.printf("DEBUG: Segment %d → %d unreachable%n", fromId, toId);
            } else {
                System.out.printf("DEBUG: Segment %d → %d distance = %.2f m%n", fromId, toId, r.distance);
            }

            totalDist += r.distance;

            if (!allPath.isEmpty() && !r.path.isEmpty()) {
                r.path.remove(0); // avoid duplicate overlap
            }
            allPath.addAll(r.path);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("distanceMeters", totalDist);
        resp.put("path", allPath);
        resp.put("stopsCount", stopLat.size());
        return resp;
    }
}