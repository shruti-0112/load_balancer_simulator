package com.loadbalancer.repository;

import com.loadbalancer.model.HealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface HealthCheckRepository extends JpaRepository<HealthCheck, Long> {
    List<HealthCheck> findAllByOrderByCheckedAtDesc(Pageable pageable);
}
