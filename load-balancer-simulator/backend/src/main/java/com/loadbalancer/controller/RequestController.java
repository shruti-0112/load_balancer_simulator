package com.loadbalancer.controller;

import com.loadbalancer.dto.ServerStatsDto;
import com.loadbalancer.model.RequestLog;
import com.loadbalancer.model.Server;
import com.loadbalancer.repository.RequestLogRepository;
import com.loadbalancer.service.LoadBalancerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final LoadBalancerService loadBalancerService;
    private final RequestLogRepository requestLogRepository;

    public RequestController(LoadBalancerService loadBalancerService,
                              RequestLogRepository requestLogRepository) {
        this.loadBalancerService = loadBalancerService;
        this.requestLogRepository = requestLogRepository;
    }

    // Manually trigger one request - handy for testing without waiting on the scheduler
    @PostMapping
    public RequestLog triggerRequest() {
        return loadBalancerService.routeRequest();
    }

    @GetMapping
    public List<RequestLog> getRecentRequests(@RequestParam(defaultValue = "50") int limit) {
        return requestLogRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }

    @GetMapping("/stats")
    public List<ServerStatsDto> getStats() {
        List<RequestLog> allLogs = requestLogRepository.findAll();

        Map<String, List<RequestLog>> byServer = allLogs.stream()
                .filter(r -> r.getServer() != null)
                .collect(Collectors.groupingBy(r -> r.getServer().getName()));

        return byServer.entrySet().stream().map(entry -> {
            String serverName = entry.getKey();
            List<RequestLog> logs = entry.getValue();

            long total = logs.size();
            long failed = logs.stream().filter(r -> r.getStatus().name().equals("FAILED")).count();
            double avg = logs.stream().mapToInt(RequestLog::getResponseTimeMs).average().orElse(0.0);
            double p95 = percentile(logs, 95);

            return new ServerStatsDto(serverName, total, failed, avg, p95);
        }).collect(Collectors.toList());
    }

    // Simple nearest-rank percentile calculation over response times - used for p95 latency,
    // a much more meaningful production metric than a plain average.
    private double percentile(List<RequestLog> logs, int percentile) {
        List<Integer> sorted = logs.stream()
                .map(RequestLog::getResponseTimeMs)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
        if (sorted.isEmpty()) return 0.0;
        int index = (int) Math.ceil(percentile / 100.0 * sorted.size()) - 1;
        index = Math.max(0, Math.min(index, sorted.size() - 1));
        return sorted.get(index);
    }
}
