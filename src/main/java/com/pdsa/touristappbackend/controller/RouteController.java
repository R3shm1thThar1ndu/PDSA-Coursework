package com.pdsa.touristappbackend.controller;

import com.pdsa.touristappbackend.model.Coordinate;
import com.pdsa.touristappbackend.model.RouteRequest;
import com.pdsa.touristappbackend.model.RouteResponse;
import com.pdsa.touristappbackend.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/route")
public class RouteController {

    @Autowired
    private GraphService graphService;

    @PostMapping("/calculate")
    public RouteResponse calculateRoute(@RequestBody RouteRequest request) {
        Coordinate start = new Coordinate(request.getStartLat(), request.getStartLon());
        Coordinate end = new Coordinate(request.getEndLat(), request.getEndLon());
        return graphService.calculateRoute(start, end);
    }
}
