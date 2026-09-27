package com.loadbalancer.dto;

public class ServerStatusUpdateRequest {
    private String status; // "HEALTHY" or "DOWN"
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
