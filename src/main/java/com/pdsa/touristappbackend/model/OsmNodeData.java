package com.pdsa.touristappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class OsmNodeData implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private double lat;
    private double lon;
}
