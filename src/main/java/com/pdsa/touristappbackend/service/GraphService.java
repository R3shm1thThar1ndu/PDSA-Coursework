package com.pdsa.touristappbackend.service;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.Profile;
import com.pdsa.touristappbackend.model.Coordinate;
import com.pdsa.touristappbackend.model.RouteResponse;
import org.springframework.stereotype.Service;

@Service
public class GraphService {

    private final GraphHopper hopper;

    public GraphService() {
        hopper = new GraphHopper();
        hopper.setOSMFile("src/main/resources/data/sri-lanka-latest.osm.pbf");
        hopper.setGraphHopperLocation("graph-cache");
        hopper.setProfiles(new Profile("car").setVehicle("car").setWeighting("fastest"));
        hopper.importOrLoad();
    }

    public RouteResponse calculateRoute(Coordinate start, Coordinate end) {
        GHRequest request = new GHRequest(start.getLat(), start.getLon(), end.getLat(), end.getLon())
                .setProfile("car");

        GHResponse response = hopper.route(request);

        if (response.hasErrors()) {
            throw new RuntimeException(response.getErrors().toString());
        }

        RouteResponse route = new RouteResponse();
        route.setDistance(response.getBest().getDistance());
        route.setTime(response.getBest().getTime() / 1000);

        // Convert points to Coordinate list
        response.getBest().getPoints().forEach(p -> {
            route.getPath().add(new Coordinate(p.getLat(), p.getLon()));
        });

        return route;
    }
}
