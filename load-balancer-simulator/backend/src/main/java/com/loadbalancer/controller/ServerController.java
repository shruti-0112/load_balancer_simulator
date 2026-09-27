package com.loadbalancer.controller;

import com.loadbalancer.dto.ServerStatusUpdateRequest;
import com.loadbalancer.model.Server;
import com.loadbalancer.model.ServerStatus;
import com.loadbalancer.repository.ServerRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
public class ServerController {

    private final ServerRepository serverRepository;

    public ServerController(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @GetMapping
    public List<Server> getAllServers() {
        return serverRepository.findAll();
    }

    @PostMapping
    public Server addServer(@RequestBody Server server) {
        server.setId(null); // ensure it's treated as a new row
        if (server.getStatus() == null) server.setStatus(ServerStatus.HEALTHY);
        if (server.getWeight() == null) server.setWeight(1);
        if (server.getAvgResponseTimeMs() == null) server.setAvgResponseTimeMs(100.0);
        return serverRepository.save(server);
    }

    @PatchMapping("/{id}/status")
    public Server updateStatus(@PathVariable Long id, @RequestBody ServerStatusUpdateRequest request) {
        Server server = serverRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Server not found: " + id));
        server.setStatus(ServerStatus.valueOf(request.getStatus().toUpperCase()));
        return serverRepository.save(server);
    }

    @DeleteMapping("/{id}")
    public void deleteServer(@PathVariable Long id) {
        serverRepository.deleteById(id);
    }
}
