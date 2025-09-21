package com.pdsa.touristappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

/**
 * Edge class representing a graph edge with destination node id and weight in meters
 * Used in routing algorithms
 * Implements Serializable for easy transmission/storage
 * Fields:
 * - to: destination node id
 * - weightMeters: weight of the edge in meters
 */
@Data
@AllArgsConstructor
public class Edge implements Serializable {
    private static final long serialVersionUID = 1L;
    private long to;
    private double weightMeters;
}
