import React from 'react';

const ALGORITHMS = [
  { value: 'ROUND_ROBIN', label: 'Round Robin' },
  { value: 'LEAST_CONNECTIONS', label: 'Least Connections' },
  { value: 'WEIGHTED_RESPONSE_TIME', label: 'Weighted Response Time (adaptive)' },
];

export default function AlgorithmSwitcher({
  activeAlgorithm,
  onAlgorithmChange,
  failureRate,
  onFailureRateChange,
}) {
  return (
    <div className="card">
      <h2>Controls</h2>
      <div className="controls-row">
        <div>
          <div className="control-label"><span>Active Algorithm</span></div>
          <select
            value={activeAlgorithm || ''}
            onChange={(e) => onAlgorithmChange(e.target.value)}
          >
            {ALGORITHMS.map((a) => (
              <option key={a.value} value={a.value}>{a.label}</option>
            ))}
          </select>
        </div>

        <div>
          <div className="control-label">
            <span>Chaos: Failure Rate</span>
            <span>{Math.round((failureRate || 0) * 100)}%</span>
          </div>
          <input
            type="range"
            min="0"
            max="1"
            step="0.05"
            value={failureRate || 0}
            onChange={(e) => onFailureRateChange(parseFloat(e.target.value))}
          />
        </div>
      </div>
    </div>
  );
}
