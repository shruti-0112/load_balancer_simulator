package com.loadbalancer.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulates real client traffic hitting the load balancer.
 *
 * Fires a burst of 1-5 concurrent "requests" every 1.5 seconds using @Async,
 * so that LeastConnectionsStrategy and WeightedResponseTimeStrategy actually
 * have concurrent in-flight connections to make decisions on - a single
 * sequential loop wouldn't exercise that logic at all.
 */
@Service
public class TrafficSimulatorService {

    private final LoadBalancerService loadBalancerService;

    public TrafficSimulatorService(LoadBalancerService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
    }

    @Scheduled(fixedRate = 1500)
    public void simulateTrafficBurst() {
        int burstSize = ThreadLocalRandom.current().nextInt(1, 6);
        for (int i = 0; i < burstSize; i++) {
            handleOneRequestAsync();
        }
    }

    @Async
    public void handleOneRequestAsync() {
        try {
            // stagger start times slightly so connections genuinely overlap
            Thread.sleep(ThreadLocalRandom.current().nextInt(0, 100));
            loadBalancerService.routeRequest();
        } catch (Exception e) {
            // In a simulator, a routing failure (e.g. no healthy servers) is expected
            // occasionally during a chaos demo - log and move on rather than crash the scheduler.
            System.err.println("Simulated request failed: " + e.getMessage());
        }
    }
}
