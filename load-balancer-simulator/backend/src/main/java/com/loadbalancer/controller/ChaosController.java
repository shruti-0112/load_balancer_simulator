package com.loadbalancer.controller;

import com.loadbalancer.dto.FailureRateUpdateRequest;
import com.loadbalancer.service.LoadBalancerService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * "Chaos engineering" style control - lets you dial up the simulated
 * failure rate live from the dashboard to demo how routing/health-checks
 * behave under degraded conditions, instead of only ever showing the happy path.
 */
@RestController
@RequestMapping("/api/chaos")
public class ChaosController {

    private final LoadBalancerService loadBalancerService;

    public ChaosController(LoadBalancerService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
    }

    @GetMapping("/failure-rate")
    public Map<String, Double> getFailureRate() {
        return Map.of("failureRate", loadBalancerService.getFailureRate());
    }

    @PutMapping("/failure-rate")
    public Map<String, Double> setFailureRate(@RequestBody FailureRateUpdateRequest request) {
        loadBalancerService.setFailureRate(request.getRate());
        return Map.of("failureRate", loadBalancerService.getFailureRate());
    }
}
