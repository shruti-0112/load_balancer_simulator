package com.loadbalancer.strategy;

import com.loadbalancer.model.Server;
import java.util.List;

/**
 * Strategy interface for the Strategy design pattern.
 * Each concrete strategy decides which healthy server should receive the next request.
 * The active strategy can be swapped at runtime via LoadBalancerService, without
 * touching any calling code - that's the whole point of using this pattern here.
 */
public interface LoadBalancingStrategy {

    Server selectServer(List<Server> healthyServers);

    String getName();
}
