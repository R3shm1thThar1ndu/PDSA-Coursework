package com.pdsa.touristappbackend.routing;

import com.pdsa.touristappbackend.config.RoutingConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
                // ✅ Pass SQLite path, LazyGraph will handle connection
                lazyGraph = new LazyGraph(cfg.getSqlitePath());
                System.out.println("GraphProvider: using LazyGraph with DB = " + cfg.getSqlitePath());
            } catch (Exception e) {
                throw new RuntimeException("Failed to init LazyGraph: " + e.getMessage(), e);
            }
            return lazyGraph;
        }
    }
}
