package com.loadbalancer.service;

import com.loadbalancer.model.HealthCheck;
import com.loadbalancer.model.Server;
import com.loadbalancer.model.ServerStatus;
import com.loadbalancer.repository.HealthCheckRepository;
import com.loadbalancer.repository.ServerRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Periodic health-check daemon. Every 5 seconds each server has a small
 * random chance of flipping status - DOWN servers have a higher chance of
 * recovering than HEALTHY servers have of failing, so the system is mostly
 * stable but you can also just manually flip a server DOWN via the API to
 * demo failover on command.
 */
@Service
public class HealthCheckService {

    private final ServerRepository serverRepository;
    private final HealthCheckRepository healthCheckRepository;

    private static final double SPONTANEOUS_FAILURE_CHANCE = 0.03;
    private static final double RECOVERY_CHANCE = 0.4;

    public HealthCheckService(ServerRepository serverRepository, HealthCheckRepository healthCheckRepository) {
        this.serverRepository = serverRepository;
        this.healthCheckRepository = healthCheckRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void runHealthChecks() {
        List<Server> servers = serverRepository.findAll();
        for (Server server : servers) {
            ServerStatus newStatus = decideNewStatus(server);
            if (newStatus != server.getStatus()) {
                server.setStatus(newStatus);
                serverRepository.save(server);
            }
            HealthCheck check = new HealthCheck();
            check.setServer(server);
            check.setStatus(newStatus);
            healthCheckRepository.save(check);
        }
    }

    private ServerStatus decideNewStatus(Server server) {
        double roll = ThreadLocalRandom.current().nextDouble();
        if (server.getStatus() == ServerStatus.HEALTHY) {
            return roll < SPONTANEOUS_FAILURE_CHANCE ? ServerStatus.DOWN : ServerStatus.HEALTHY;
        } else {
            return roll < RECOVERY_CHANCE ? ServerStatus.HEALTHY : ServerStatus.DOWN;
        }
    }
}
