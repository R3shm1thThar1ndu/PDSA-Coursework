package com.pdsa.touristappbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "routing")
public class RoutingConfigProperties {
    private String sqlitePath;
    private double gridCellSizeDeg = 0.005;

    // property name matches application.properties:
    public String getSqlitePath() { return sqlitePath; }
    public double getGridCellSizeDeg() { return gridCellSizeDeg; }
}
