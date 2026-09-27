package com.loadbalancer.strategy;

import com.loadbalancer.model.Server;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cycles through healthy servers in order.
 * AtomicInteger is used (not a plain int) because multiple simulated
 * client threads can call selectServer() concurrently.
 */
@Component
public class RoundRobinStrategy implements LoadBalancingStrategy {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Server selectServer(List<Server> healthyServers) {
        if (healthyServers.isEmpty()) {
            throw new IllegalStateException("No healthy servers available to route request");
        }
        int index = Math.floorMod(counter.getAndIncrement(), healthyServers.size());
        return healthyServers.get(index);
    }

    @Override
    public String getName() {
        return "ROUND_ROBIN";
    }
}
