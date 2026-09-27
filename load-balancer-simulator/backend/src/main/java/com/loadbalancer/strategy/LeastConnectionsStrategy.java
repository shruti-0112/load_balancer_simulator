package com.loadbalancer.strategy;

import com.loadbalancer.model.Server;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Sends each request to whichever healthy server currently has the
 * fewest active (in-flight) connections. Better than round robin when
 * requests take variable time to process, since it reacts to real load
 * instead of just alternating blindly.
 */
@Component
public class LeastConnectionsStrategy implements LoadBalancingStrategy {

    @Override
    public Server selectServer(List<Server> healthyServers) {
        if (healthyServers.isEmpty()) {
            throw new IllegalStateException("No healthy servers available to route request");
        }
        return healthyServers.stream()
                .min(Comparator.comparingInt(Server::getActiveConnections))
                .orElseThrow();
    }

    @Override
    public String getName() {
        return "LEAST_CONNECTIONS";
    }
}
