package com.pdsa.touristappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

/**
 * OsmNodeData class representing an OpenStreetMap node with id, latitude, and longitude
 * Implements Serializable for easy transmission/storage
 * Fields:
 * - id: unique identifier of the OSM node
 * - lat: latitude of the node
 * - lon: longitude of the node
 * Used for representing OSM nodes in the application
 */
@Data
@AllArgsConstructor
public class OsmNodeData implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private double lat;
    private double lon;
}
