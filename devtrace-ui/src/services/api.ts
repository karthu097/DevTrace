import axios from 'axios';
import type { ReconstructedTrace, RootCauseAnalysis, IncidentReport, Page } from '../types';

const api = axios.create({
  baseURL: '/api',
});

// Since we have limited endpoints right now, we map the actual endpoints:
// GET /api/traces (assuming pageable) -> returning Page<Trace> or flat list
// GET /api/traces/{traceId} -> ReconstructedTrace
// GET /api/traces/{traceId}/root-cause -> RootCauseAnalysis
// GET /api/traces/{traceId}/investigation -> IncidentReport

export const apiService = {
  getTraces: async (page = 0, size = 20) => {
    // For now, let's fetch a list of traces. The backend currently exposes Page<Trace>.
    // Since our backend Trace entity is flat, we'll cast to any or a minimal Trace interface
    const response = await api.get(`/traces?page=${page}&size=${size}&sort=startTime,desc`);
    return response.data as Page<any>;
  },

  getTrace: async (traceId: string) => {
    const response = await api.get(`/traces/${traceId}`);
    return response.data as ReconstructedTrace;
  },

  getRootCause: async (traceId: string) => {
    const response = await api.get(`/traces/${traceId}/root-cause`);
    return response.data as RootCauseAnalysis;
  },

  getInvestigation: async (traceId: string) => {
    const response = await api.get(`/traces/${traceId}/investigation`);
    return response.data as IncidentReport;
  },
};
