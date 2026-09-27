package com.loadbalancer.dto;

public class ServerStatsDto {
    private String serverName;
    private long totalRequests;
    private long failedRequests;
    private double avgResponseTimeMs;
    private double p95ResponseTimeMs;

    public ServerStatsDto(String serverName, long totalRequests, long failedRequests,
                           double avgResponseTimeMs, double p95ResponseTimeMs) {
        this.serverName = serverName;
        this.totalRequests = totalRequests;
        this.failedRequests = failedRequests;
        this.avgResponseTimeMs = avgResponseTimeMs;
        this.p95ResponseTimeMs = p95ResponseTimeMs;
    }

    public String getServerName() { return serverName; }
    public long getTotalRequests() { return totalRequests; }
    public long getFailedRequests() { return failedRequests; }
    public double getAvgResponseTimeMs() { return avgResponseTimeMs; }
    public double getP95ResponseTimeMs() { return p95ResponseTimeMs; }
}
