package com.pdsa.touristappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class Edge implements Serializable {
    private static final long serialVersionUID = 1L;
    private long to;
    private double weightMeters;
}
