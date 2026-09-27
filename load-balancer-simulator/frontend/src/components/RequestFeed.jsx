import React from 'react';

export default function RequestFeed({ requests }) {
  return (
    <div className="card">
      <h2>Live Request Feed</h2>
      <div className="scroll-box">
        <table className="request-table">
          <thead>
            <tr>
              <th>Time</th>
              <th>Server</th>
              <th>Algorithm</th>
              <th>Response Time</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {requests.map((r) => (
              <tr key={r.id}>
                <td>{new Date(r.createdAt).toLocaleTimeString()}</td>
                <td>{r.server ? r.server.name : '-'}</td>
                <td>{r.algorithmUsed}</td>
                <td>{r.responseTimeMs}ms</td>
                <td>
                  <span className={`badge ${r.status === 'SUCCESS' ? 'success' : 'failed'}`}>
                    {r.status}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
