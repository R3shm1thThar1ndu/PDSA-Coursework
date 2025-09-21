package com.pdsa.touristappbackend.controller;

import com.pdsa.touristappbackend.service.RoutingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * RouteController class handling routing requests
 * Endpoint:
 * - GET /api/route/by-coords: get route between two coordinates
 * Example request:
 * /api/route/by-coords?startLat=...&startLon=...&endLat=...&endLon=...
 * Response:
 * {
 *   "distanceMeters": total_distance,
 *   "path": [ { "lat": ..., "lon": ... }, ... ]
 * }
 */
@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RouteController {
    private final RoutingService routingService;

    @GetMapping("/by-coords")
    public ResponseEntity<?> byCoords(
            @RequestParam double startLat,
            @RequestParam double startLon,
            @RequestParam double endLat,
            @RequestParam double endLon) {

        try {
            RoutingService.RouteResult r = routingService.routeByCoords(startLat, startLon, endLat, endLon);
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of("error", e.getMessage())
            );
        }
    }
}
