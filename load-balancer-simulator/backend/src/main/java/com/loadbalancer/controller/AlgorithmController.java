package com.loadbalancer.controller;

import com.loadbalancer.dto.AlgorithmUpdateRequest;
import com.loadbalancer.service.LoadBalancerService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/algorithm")
public class AlgorithmController {

    private final LoadBalancerService loadBalancerService;

    public AlgorithmController(LoadBalancerService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
    }

    @GetMapping
    public Map<String, String> getActiveAlgorithm() {
        return Map.of("activeAlgorithm", loadBalancerService.getActiveStrategyName());
    }

    @PutMapping
    public Map<String, String> setActiveAlgorithm(@RequestBody AlgorithmUpdateRequest request) {
        loadBalancerService.setActiveStrategy(request.getName().toUpperCase());
        return Map.of("activeAlgorithm", loadBalancerService.getActiveStrategyName());
    }
}
