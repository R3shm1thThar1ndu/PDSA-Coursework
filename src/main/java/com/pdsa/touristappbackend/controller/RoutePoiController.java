package com.pdsa.touristappbackend.controller;

import com.pdsa.touristappbackend.model.OsmNodeData;
import com.pdsa.touristappbackend.routing.AStarRouter;
import com.pdsa.touristappbackend.routing.LazyGraph;
import com.pdsa.touristappbackend.service.UserInterestService;
import com.pdsa.touristappbackend.repository.PoiSqliteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * RoutePoiController class handling routing with POI endpoints
 * Endpoint:
 * - GET /api/route/poi: get route with POIs based on user interests
 * Example request:
 * /api/route/poi?username=...&password=...&startLat=...&startLon=...&endLat=...&endLon=...
 * Response:
 * {
 *   "distanceMeters": total_distance,
 *   "path": [ { "lat": ..., "lon": ... }, ... ],
 *   "userInterests": { "interest1": weight1, ... },
 *   "pois": [ { "id": ..., "name": ..., "lat": ..., "lon": ..., "category": ..., "rating": ... }, ... ]
 * }
 */
@RestController
@RequestMapping("/api/route")
@CrossOrigin(origins = "*")
public class RoutePoiController {

    private final AStarRouter router;
    private final UserInterestService userInterestService;
    private final PoiSqliteRepository poiRepo;

    public RoutePoiController(LazyGraph graph,
                              UserInterestService userInterestService,
                              PoiSqliteRepository poiRepo) {
        this.router = new AStarRouter(graph);
        this.userInterestService = userInterestService;
        this.poiRepo = poiRepo;
    }

    @GetMapping("/poi")
    public Map<String, Object> routeWithPoi(@RequestParam String username,
                                            @RequestParam String password,
                                            @RequestParam double startLat,
                                            @RequestParam double startLon,
                                            @RequestParam double endLat,
                                            @RequestParam double endLon) throws Exception {

        Map<String, Integer> interests = userInterestService.getWeightedUserInterests(username);
        if (interests.isEmpty()) {
            throw new RuntimeException("No interests found for user " + username);
        }

        OsmNodeData start = router.findNearestNode(startLat, startLon);
        OsmNodeData end = router.findNearestNode(endLat, endLon);
        var result = router.shortestPath(start.getId(), end.getId());

        List<Map<String, Double>> pathCoords = new ArrayList<>();
        for (Map<String, Double> coord : result.path) {
            Map<String, Double> point = new HashMap<>();
            point.put("lat", coord.get("lat"));
            point.put("lon", coord.get("lon"));
            pathCoords.add(point);
        }

        List<String> categories = new ArrayList<>(interests.keySet());
        List<Map<String, Object>> pois = poiRepo.findPoisByCategoryNearPath(result.path, categories);

        pois.sort((a, b) -> {
            int r1 = interests.getOrDefault(a.get("category"), 1);
            int r2 = interests.getOrDefault(b.get("category"), 1);
            return Integer.compare(r2, r1); // higher rating first
        });

        Map<String, Object> resp = new HashMap<>();
        resp.put("distanceMeters", result.distance);
        resp.put("path", pathCoords);
        resp.put("userInterests", interests);
        resp.put("pois", pois);

        return resp;
    }
}