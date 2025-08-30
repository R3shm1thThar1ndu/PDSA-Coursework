package com.pdsa.touristappbackend.routing;

import com.pdsa.touristappbackend.config.RoutingConfigProperties;
import com.pdsa.touristappbackend.model.Graph;
import com.pdsa.touristappbackend.routing.index.GridIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
@RequiredArgsConstructor
public class GraphProvider {
    private final RoutingConfigProperties cfg;
    private volatile LazyGraph lazyGraph;

    public LazyGraph getLazyGraph() {
        if (lazyGraph != null) return lazyGraph;
        synchronized (this) {
            if (lazyGraph != null) return lazyGraph;
            try {
                lazyGraph = new LazyGraph(cfg.getSqlitePath());
                System.out.println("LazyGraph initialized with SQLite: " + cfg.getSqlitePath());
            } catch (Exception e) {
                throw new RuntimeException("Failed to init LazyGraph", e);
            }
        }
        return lazyGraph;
    }
}
