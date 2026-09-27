package com.loadbalancer.repository;

import com.loadbalancer.model.Server;
import com.loadbalancer.model.ServerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServerRepository extends JpaRepository<Server, Long> {
    List<Server> findByStatus(ServerStatus status);
}
