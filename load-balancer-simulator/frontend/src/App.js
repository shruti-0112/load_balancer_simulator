import React, { useEffect, useState, useCallback } from 'react';
import './App.css';
import ServerPanel from './components/ServerPanel';
import RequestFeed from './components/RequestFeed';
import AlgorithmSwitcher from './components/AlgorithmSwitcher';
import StatsChart from './components/StatsChart';
import {
  getServers, setServerStatus,
  getRecentRequests, getStats,
  getActiveAlgorithm, setActiveAlgorithm,
  getFailureRate, setFailureRate,
} from './api';

function App() {
  const [servers, setServers] = useState([]);
  const [requests, setRequests] = useState([]);
  const [stats, setStats] = useState([]);
  const [activeAlgorithm, setActiveAlgorithmState] = useState('ROUND_ROBIN');
  const [failureRate, setFailureRateState] = useState(0.05);
  const [error, setError] = useState(null);

  const refreshAll = useCallback(async () => {
    try {
      const [serversData, requestsData, statsData, algoData, chaosData] = await Promise.all([
        getServers(),
        getRecentRequests(30),
        getStats(),
        getActiveAlgorithm(),
        getFailureRate(),
      ]);
      setServers(serversData);
      setRequests(requestsData);
      setStats(statsData);
      setActiveAlgorithmState(algoData.activeAlgorithm);
      setFailureRateState(chaosData.failureRate);
      setError(null);
    } catch (e) {
      setError('Could not reach backend at http://localhost:8080 - is Spring Boot running?');
    }
  }, []);

  useEffect(() => {
    refreshAll();
    const interval = setInterval(refreshAll, 2000);
    return () => clearInterval(interval);
  }, [refreshAll]);

  const handleToggleStatus = async (server) => {
    const newStatus = server.status === 'HEALTHY' ? 'DOWN' : 'HEALTHY';
    await setServerStatus(server.id, newStatus);
    refreshAll();
  };

  const handleAlgorithmChange = async (name) => {
    await setActiveAlgorithm(name);
    setActiveAlgorithmState(name);
  };

  const handleFailureRateChange = async (rate) => {
    await setFailureRate(rate);
    setFailureRateState(rate);
  };

  return (
    <div className="app">
      <div className="app-header">
        <div>
          <h1>Load Balancer & Request Router Simulator</h1>
          <div className="subtitle">
            Live routing across {servers.length} simulated servers - Strategy pattern, adaptive weighting, chaos testing
          </div>
        </div>
      </div>

      {error && (
        <div className="card" style={{ borderColor: '#e5484d', color: '#e5484d' }}>
          {error}
        </div>
      )}

      <div className="grid">
        <div>
          <ServerPanel servers={servers} onToggleStatus={handleToggleStatus} />
          <StatsChart stats={stats} />
          <RequestFeed requests={requests} />
        </div>
        <div>
          <AlgorithmSwitcher
            activeAlgorithm={activeAlgorithm}
            onAlgorithmChange={handleAlgorithmChange}
            failureRate={failureRate}
            onFailureRateChange={handleFailureRateChange}
          />
        </div>
      </div>
    </div>
  );
}

export default App;
