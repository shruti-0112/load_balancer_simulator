import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

export const getServers = () => api.get('/servers').then(r => r.data);
export const addServer = (server) => api.post('/servers', server).then(r => r.data);
export const setServerStatus = (id, status) =>
  api.patch(`/servers/${id}/status`, { status }).then(r => r.data);

export const getRecentRequests = (limit = 50) =>
  api.get(`/requests?limit=${limit}`).then(r => r.data);
export const getStats = () => api.get('/requests/stats').then(r => r.data);
export const triggerRequest = () => api.post('/requests').then(r => r.data);

export const getActiveAlgorithm = () => api.get('/algorithm').then(r => r.data);
export const setActiveAlgorithm = (name) =>
  api.put('/algorithm', { name }).then(r => r.data);

export const getFailureRate = () => api.get('/chaos/failure-rate').then(r => r.data);
export const setFailureRate = (rate) =>
  api.put('/chaos/failure-rate', { rate }).then(r => r.data);

export default api;
