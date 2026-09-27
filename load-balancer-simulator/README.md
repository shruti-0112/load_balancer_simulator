# Load Balancer & Request Router Simulator

A full-stack simulation of a load balancer that routes traffic across multiple
mock backend servers using pluggable routing strategies, with a live React
dashboard, MySQL-backed request history, and a chaos-engineering style failure
injector.

Built as a hands-on way to understand how request routing, health checking,
and load-balancing algorithms actually work under the hood — not just how to
call `nginx.conf` and move on.

---

## Architecture

```
                     ┌──────────────────────────┐
  Simulated Clients  │   TrafficSimulatorService │  (Java @Scheduled + @Async)
  (bursts every 1.5s)│  fires 1-5 concurrent     │
                      │  requests per tick        │
                      └───────────┬───────────────┘
                                  │
                                  ▼
                     ┌──────────────────────────┐
                     │   LoadBalancerService      │
                     │  (Strategy pattern core)   │
                     └───────────┬───────────────┘
                                  │
              ┌───────────────────┼───────────────────┐
              ▼                   ▼                   ▼
      RoundRobinStrategy  LeastConnectionsStrategy  WeightedResponseTimeStrategy
                                  │
                                  ▼
                     ┌──────────────────────────┐
                     │   MySQL (servers,          │
                     │   requests, health_checks) │
                     └──────────────────────────┘
                                  ▲
                                  │
                     ┌──────────────────────────┐
                     │   React Dashboard          │
                     │  (polls REST API every 2s) │
                     └──────────────────────────┘
```

**Backend:** Java 17, Spring Boot 3, Spring Data JPA, MySQL
**Frontend:** React 18, Recharts, Axios

---

## What makes this different from a typical "load balancer simulator" project

Round-robin load balancer simulators are a very common student/portfolio
project. This one adds three things that most versions don't have, on purpose,
so it's a stronger talking point in an interview:

1. **Adaptive weighted routing (`WeightedResponseTimeStrategy`)** — not a
   static weighted round robin. Each server's live exponential-moving-average
   response time is factored into the routing score in real time, so a server
   that's currently responding slowly automatically gets less traffic, and
   earns it back once it recovers. This behaves more like a real production
   load balancer's "least time" algorithm than a config-file weight does.

2. **Chaos engineering knob** — a live failure-rate slider (`/api/chaos`)
   lets you dial up simulated request failures on demand, so you can actually
   *demonstrate* failover and degraded-mode behavior in an interview instead
   of only showing the happy path.

3. **p95 latency tracking**, not just averages — `/api/requests/stats`
   computes a 95th-percentile response time per server, which is a much more
   meaningful real-world reliability metric than a plain average and shows
   you understand why averages hide tail latency problems.

If you have extra time, consider adding one more of these to push it further:
- WebSocket push updates instead of polling (removes the 2s dashboard lag)
- JWT-based admin/viewer roles
- Dockerfile + docker-compose for one-command setup
- A "circuit breaker" that temporarily stops routing to a server after N
  consecutive failures, independent of the health-check daemon

---

## Project Structure

```
load-balancer-simulator/
├── backend/     Spring Boot application (Java 17, Maven)
└── frontend/    React dashboard
```

---

## Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8+ running locally

### 1. Database
Create nothing manually — the app auto-creates the `loadbalancer_db` schema
and tables on first run (via `createDatabaseIfNotExist=true` and
`spring.jpa.hibernate.ddl-auto=update`). You only need MySQL itself running.

Update the credentials in `backend/src/main/resources/application.properties`
to match your local MySQL setup:

```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

### 2. Run the backend

```bash
cd backend
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. Four seed servers (`server-1`
through `server-4`) are inserted automatically on first run, and the
traffic simulator + health checker start immediately.

### 3. Run the frontend

```bash
cd frontend
npm install
npm start
```

Opens on `http://localhost:3000` and polls the backend every 2 seconds.

---

## API Reference

| Method | Endpoint                    | Purpose                                      |
|--------|------------------------------|-----------------------------------------------|
| GET    | `/api/servers`               | List all servers + live status                |
| POST   | `/api/servers`                | Add a new mock server                         |
| PATCH  | `/api/servers/{id}/status`    | Manually mark a server HEALTHY/DOWN            |
| POST   | `/api/requests`               | Manually trigger one simulated request        |
| GET    | `/api/requests?limit=50`      | Recent request log                            |
| GET    | `/api/requests/stats`         | Per-server totals, failures, avg + p95 latency |
| GET    | `/api/algorithm`              | Get currently active routing strategy         |
| PUT    | `/api/algorithm`              | Switch strategy at runtime                    |
| GET    | `/api/chaos/failure-rate`     | Get current simulated failure rate            |
| PUT    | `/api/chaos/failure-rate`     | Set simulated failure rate (0.0 – 1.0)         |

---

## Things to demo in an interview

1. Switch the algorithm dropdown live and point out how the bar chart shifts.
2. Manually click a server card to mark it DOWN — show traffic instantly
   rerouting to the remaining healthy servers.
3. Drag the chaos slider up and show the failed-request badges appearing in
   the live feed.
4. Open `LoadBalancerService.java` and explain the Strategy pattern — this is
   the single best "tell me about a design pattern you used" answer you can
   give from this project.

---

## Disclaimer

This is a simulation for learning/demo purposes — servers are database rows
with simulated response times, not real network services. It's meant to
demonstrate understanding of load-balancing algorithms, concurrency, and
system design, not to be a production load balancer.
