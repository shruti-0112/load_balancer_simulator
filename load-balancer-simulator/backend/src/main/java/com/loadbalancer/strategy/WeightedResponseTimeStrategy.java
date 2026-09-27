package com.loadbalancer.strategy;

import com.loadbalancer.model.Server;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * UNIQUE / ADAPTIVE STRATEGY - this is what differentiates this project from a
 * typical textbook load balancer implementation.
 *
 * Plain "weighted round robin" uses a static weight assigned once and never
 * revisited. This strategy instead computes a live desirability score for each
 * server as:
 *
 *      score = weight / (avgResponseTimeMs + smoothingFactor)
 *
 * A server that is nominally "high capacity" (high weight) but is currently
 * responding slowly (e.g. under load, or degraded) will automatically get
 * fewer requests routed to it - and recover traffic share once its average
 * response time drops again. Nothing needs to be manually reconfigured.
 *
 * This is closer to how real production load balancers (e.g. least-time
 * algorithms in NGINX/HAProxy) behave than a static weighted round robin is,
 * and it gives you a genuinely interesting story to tell in an interview:
 * "my load balancer adapts to real-time server performance, not just a
 * config file weight."
 */
@Component
public class WeightedResponseTimeStrategy implements LoadBalancingStrategy {

    // Prevents division by zero / an oversized score when response time is near 0
    private static final double SMOOTHING_FACTOR = 10.0;

    @Override
    public Server selectServer(List<Server> healthyServers) {
        if (healthyServers.isEmpty()) {
            throw new IllegalStateException("No healthy servers available to route request");
        }
        return healthyServers.stream()
                .max(Comparator.comparingDouble(this::score))
                .orElseThrow();
    }

    private double score(Server server) {
        double avgResponseTime = server.getAvgResponseTimeMs() == null ? 100.0 : server.getAvgResponseTimeMs();
        int weight = server.getWeight() == null ? 1 : server.getWeight();
        return weight / (avgResponseTime + SMOOTHING_FACTOR);
    }

    @Override
    public String getName() {
        return "WEIGHTED_RESPONSE_TIME";
    }
}
