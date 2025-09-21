package com.pdsa.touristappbackend.routing;

import com.pdsa.touristappbackend.config.RoutingConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Provides a singleton instance of LazyGraph initialized with the database path from configuration.
 * Uses double-checked locking for thread-safe lazy initialization.
 * Prints the database path to the console upon initialization.
 */
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
                System.out.println("GraphProvider: using LazyGraph with DB = " + cfg.getSqlitePath());
            } catch (Exception e) {
                throw new RuntimeException("Failed to init LazyGraph: " + e.getMessage(), e);
            }
            return lazyGraph;
        }
    }
}
