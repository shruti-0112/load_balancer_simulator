package com.loadbalancer.service;

import com.loadbalancer.model.*;
import com.loadbalancer.repository.RequestLogRepository;
import com.loadbalancer.repository.ServerRepository;
import com.loadbalancer.strategy.LoadBalancingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Central orchestrator. Holds the currently-active LoadBalancingStrategy and
 * exposes routeRequest(), which is the single entry point every simulated
 * (or real) client request goes through.
 *
 * Strategy pattern in action: swapping the active strategy at runtime
 * (see setActiveStrategy) changes routing behaviour for every subsequent
 * request with zero changes to any calling code.
 */
@Service
public class LoadBalancerService {

    private final ServerRepository serverRepository;
    private final RequestLogRepository requestLogRepository;
    private final Map<String, LoadBalancingStrategy> strategies;

    private final AtomicReference<String> activeStrategyName = new AtomicReference<>("ROUND_ROBIN");

    // Chaos-engineering knob: 0.0 = never fail, 1.0 = always fail.
    // Exposed via /api/chaos so you can demo failure handling live.
    private final AtomicReference<Double> failureRate = new AtomicReference<>(0.05);

    @Autowired
    public LoadBalancerService(ServerRepository serverRepository,
                                RequestLogRepository requestLogRepository,
                                List<LoadBalancingStrategy> strategyBeans) {
        this.serverRepository = serverRepository;
        this.requestLogRepository = requestLogRepository;
        this.strategies = strategyBeans.stream()
                .collect(Collectors.toMap(LoadBalancingStrategy::getName, s -> s));
    }

    public void setActiveStrategy(String name) {
        if (!strategies.containsKey(name)) {
            throw new IllegalArgumentException("Unknown strategy: " + name +
                    ". Available: " + strategies.keySet());
        }
        activeStrategyName.set(name);
    }

    public String getActiveStrategyName() {
        return activeStrategyName.get();
    }

    public double getFailureRate() {
        return failureRate.get();
    }

    public void setFailureRate(double rate) {
        if (rate < 0.0 || rate > 1.0) {
            throw new IllegalArgumentException("failureRate must be between 0.0 and 1.0");
        }
        failureRate.set(rate);
    }

    /**
     * Routes one simulated request end-to-end:
     * 1. picks a server using the active strategy
     * 2. marks a connection as "in flight"
     * 3. simulates processing time + possible failure
     * 4. updates the server's live stats (EMA response time)
     * 5. persists a RequestLog row
     */
    public RequestLog routeRequest() {
        List<Server> healthyServers = serverRepository.findByStatus(ServerStatus.HEALTHY);
        LoadBalancingStrategy strategy = strategies.get(activeStrategyName.get());

        Server chosen = strategy.selectServer(healthyServers);

        // mark connection as in-flight
        chosen.setActiveConnections(chosen.getActiveConnections() + 1);
        serverRepository.save(chosen);

        int simulatedResponseTime = simulateResponseTime();
        boolean failed = ThreadLocalRandom.current().nextDouble() < failureRate.get();

        // update EMA (exponential moving average) response time - alpha = 0.3
        double alpha = 0.3;
        double newAvg = alpha * simulatedResponseTime + (1 - alpha) * chosen.getAvgResponseTimeMs();
        chosen.setAvgResponseTimeMs(newAvg);

        // connection is done processing
        chosen.setActiveConnections(Math.max(0, chosen.getActiveConnections() - 1));
        serverRepository.save(chosen);

        RequestLog log = new RequestLog();
        log.setServer(chosen);
        log.setAlgorithmUsed(strategy.getName());
        log.setStatus(failed ? RequestStatus.FAILED : RequestStatus.SUCCESS);
        log.setResponseTimeMs(simulatedResponseTime);

        return requestLogRepository.save(log);
    }

    private int simulateResponseTime() {
        // 50ms - 300ms simulated processing time
        return ThreadLocalRandom.current().nextInt(50, 301);
    }
}
