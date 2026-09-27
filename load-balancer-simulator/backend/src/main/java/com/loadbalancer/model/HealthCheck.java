package com.loadbalancer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_checks")
public class HealthCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "server_id")
    private Server server;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServerStatus status;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt = LocalDateTime.now();

    public HealthCheck() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Server getServer() { return server; }
    public void setServer(Server server) { this.server = server; }

    public ServerStatus getStatus() { return status; }
    public void setStatus(ServerStatus status) { this.status = status; }

    public LocalDateTime getCheckedAt() { return checkedAt; }
    public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }
}
