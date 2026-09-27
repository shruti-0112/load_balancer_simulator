INSERT INTO servers (name, status, weight, active_connections, avg_response_time_ms, created_at)
SELECT * FROM (SELECT 'server-1', 'HEALTHY', 2, 0, 100.0, NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM servers WHERE name = 'server-1') LIMIT 1;

INSERT INTO servers (name, status, weight, active_connections, avg_response_time_ms, created_at)
SELECT * FROM (SELECT 'server-2', 'HEALTHY', 1, 0, 100.0, NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM servers WHERE name = 'server-2') LIMIT 1;

INSERT INTO servers (name, status, weight, active_connections, avg_response_time_ms, created_at)
SELECT * FROM (SELECT 'server-3', 'HEALTHY', 1, 0, 100.0, NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM servers WHERE name = 'server-3') LIMIT 1;

INSERT INTO servers (name, status, weight, active_connections, avg_response_time_ms, created_at)
SELECT * FROM (SELECT 'server-4', 'HEALTHY', 3, 0, 100.0, NOW()) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM servers WHERE name = 'server-4') LIMIT 1;
