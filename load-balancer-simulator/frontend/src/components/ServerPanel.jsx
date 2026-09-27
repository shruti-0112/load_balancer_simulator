import React from 'react';

export default function ServerPanel({ servers, onToggleStatus }) {
  return (
    <div className="card">
      <h2>Servers</h2>
      <div className="server-grid">
        {servers.map((s) => (
          <div
            key={s.id}
            className={`server-card ${s.status === 'HEALTHY' ? 'healthy' : 'down'}`}
            onClick={() => onToggleStatus(s)}
            title="Click to toggle status (manual failover demo)"
            style={{ cursor: 'pointer' }}
          >
            <div className="name">{s.name}</div>
            <div className="status">{s.status}</div>
            <div className="stat-row">
              <span>Conns</span>
              <span>{s.activeConnections}</span>
            </div>
            <div className="stat-row">
              <span>Avg RT</span>
              <span>{Math.round(s.avgResponseTimeMs)}ms</span>
            </div>
            <div className="stat-row">
              <span>Weight</span>
              <span>{s.weight}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
