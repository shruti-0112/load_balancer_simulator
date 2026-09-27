import React from 'react';
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid, Legend,
} from 'recharts';

export default function StatsChart({ stats }) {
  return (
    <div className="card">
      <h2>Requests Per Server</h2>
      <ResponsiveContainer width="100%" height={220}>
        <BarChart data={stats}>
          <CartesianGrid strokeDasharray="3 3" stroke="#2a2e37" />
          <XAxis dataKey="serverName" stroke="#9aa0a8" fontSize={12} />
          <YAxis stroke="#9aa0a8" fontSize={12} />
          <Tooltip
            contentStyle={{ background: '#1c2029', border: '1px solid #2a2e37' }}
          />
          <Legend />
          <Bar dataKey="totalRequests" name="Total Requests" fill="#3ecf8e" />
          <Bar dataKey="failedRequests" name="Failed" fill="#e5484d" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
