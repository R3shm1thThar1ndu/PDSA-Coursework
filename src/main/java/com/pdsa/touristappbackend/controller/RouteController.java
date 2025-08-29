package com.pdsa.touristappbackend.controller;

import com.pdsa.touristappbackend.model.Coordinate;
import com.pdsa.touristappbackend.model.RouteResponse;
import com.pdsa.touristappbackend.service.GeocodingService;
import com.pdsa.touristappbackend.service.GraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/route")
public class RouteController {

    private final GraphService graphService;
    private final GeocodingService geocodingService;

    public RouteController(GraphService graphService, GeocodingService geocodingService) {
        this.graphService = graphService;
        this.geocodingService = geocodingService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<?>CalculateRoute(@RequestBody Map<String, String> requestBody) {
        try{
            String startName = requestBody.get("start");
            String endName = requestBody.get("end");

            // Get coordinates from geocoding service
            double[] startCoords = geocodingService.getCoordinates(startName);
            double[] endCoords = geocodingService.getCoordinates(endName);

            RouteResponse response = graphService.calculateRoute(
                    new Coordinate(startCoords[0], startCoords[1]),
                    new Coordinate(endCoords[0], endCoords[1])
            );
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
