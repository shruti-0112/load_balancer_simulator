package com.loadbalancer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "servers")
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServerStatus status = ServerStatus.HEALTHY;

    // Used by weighted strategies - simulates servers with different capacity
    @Column(nullable = false)
    private Integer weight = 1;

    // Live counter incremented/decremented as requests are in-flight.
    // This is what LeastConnectionsStrategy reads.
    @Column(name = "active_connections", nullable = false)
    private Integer activeConnections = 0;

    // Exponential moving average of response time, updated after every request.
    // Powers the adaptive WeightedResponseTimeStrategy.
    @Column(name = "avg_response_time_ms", nullable = false)
    private Double avgResponseTimeMs = 100.0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Server() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ServerStatus getStatus() { return status; }
    public void setStatus(ServerStatus status) { this.status = status; }

    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }

    public Integer getActiveConnections() { return activeConnections; }
    public void setActiveConnections(Integer activeConnections) { this.activeConnections = activeConnections; }

    public Double getAvgResponseTimeMs() { return avgResponseTimeMs; }
    public void setAvgResponseTimeMs(Double avgResponseTimeMs) { this.avgResponseTimeMs = avgResponseTimeMs; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
