package com.pdsa.touristappbackend.config;

import com.pdsa.touristappbackend.routing.LazyGraph;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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