package com.pdsa.touristappbackend.config;

import com.pdsa.touristappbackend.routing.LazyGraph;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to create and provide a LazyGraph bean initialized with the database path from properties.
 * Defines a bean method that constructs the LazyGraph instance.
 * Prints the database path to the console upon initialization.
 */
@Configuration
public class RoutingConfig {

    private final RoutingConfigProperties props;

    public RoutingConfig(RoutingConfigProperties props) {
        this.props = props;
    }

    @Bean
    public LazyGraph lazyGraph() throws Exception {
        return new LazyGraph(props.getSqlitePath());
}
}