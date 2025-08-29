package com.pdsa.touristappbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // converts a place name to its latitude and longitude using the Nominatim API
    public double[] getCoordinates(String placename) throws Exception {
        String url ="https://nominatim.openstreetmap.org/search?format=json&q=" + placename + "&format=json&limit=1";

        String response = restTemplate.getForObject(url, String.class);
        JsonNode root = objectMapper.readTree(response);

        if (root.isArray() && root.size() > 0){
            double lat  = root.get(0).get("lat").asDouble();
            double lon  = root.get(0).get("lon").asDouble();
            return new double[]{lat, lon};
        }else {
            throw new Exception("Place not found" + placename);
        }
    }
}
