package com.loadbalancer.repository;

import com.loadbalancer.model.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
    List<RequestLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
