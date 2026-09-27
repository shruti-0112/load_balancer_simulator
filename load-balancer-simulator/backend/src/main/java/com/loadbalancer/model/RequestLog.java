package com.loadbalancer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    @Column(name = "algorithm_used", nullable = false)
    private String algorithmUsed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(name = "response_time_ms", nullable = false)
    private Integer responseTimeMs;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public RequestLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Server getServer() { return server; }
    public void setServer(Server server) { this.server = server; }

    public String getAlgorithmUsed() { return algorithmUsed; }
    public void setAlgorithmUsed(String algorithmUsed) { this.algorithmUsed = algorithmUsed; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public Integer getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Integer responseTimeMs) { this.responseTimeMs = responseTimeMs; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
