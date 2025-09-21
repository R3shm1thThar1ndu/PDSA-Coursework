package com.pdsa.touristappbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for routing settings.
 * Maps properties with prefix "routing" from application configuration.
 * Fields:
 * - sqlitePath: path to the SQLite database file
 * - gridCellSizeDeg: size of grid cells in degrees (default 0.005)
 * Provides getters for the fields.
 * Example properties:
 * routing.sqlitePath=path/to/database.sqlite
 */
@Data
@Component
@ConfigurationProperties(prefix = "routing")
public class RoutingConfigProperties {
    private String sqlitePath;
    private double gridCellSizeDeg = 0.005;

    public String getSqlitePath() { return sqlitePath; }
    public double getGridCellSizeDeg() { return gridCellSizeDeg; }
}
