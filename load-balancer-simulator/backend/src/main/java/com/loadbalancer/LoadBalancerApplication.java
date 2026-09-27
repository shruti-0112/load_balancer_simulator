package com.loadbalancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling   // powers TrafficSimulatorService and HealthCheckService
@EnableAsync        // lets TrafficSimulatorService fire concurrent simulated requests
public class LoadBalancerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoadBalancerApplication.class, args);
    }
}
