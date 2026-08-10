export interface Span {
  spanId: string;
  parentSpanId?: string;
  serviceName: string;
  spanName: string;
  startTime: string; // ISO String
  endTime: string;   // ISO String
  durationMs: number;
  status: string;
  statusMessage?: string;
  attributes: Record<string, any>;
  events: Array<{
    name: string;
    timestamp: string;
    attributes: Record<string, any>;
  }>;
}

export interface ReconstructedTrace {
  traceId: string;
  rootServiceName: string;
  startTime: string;
  endTime: string;
  durationMs: number;
  status: string;
  spans: Span[];
}

export interface RootCauseCandidate {
  service: string;
  dependency?: string;
  type: string;
  description: string;
  score: number;
}

export interface Evidence {
  type: string;
  sourceSpan: string;
  service: string;
  message: string;
  timestamp: string;
}

export interface FailureChainEvent {
  service: string;
  event: string;
}

export interface RootCauseAnalysis {
  traceId: string;
  overallStatus: string;
  rootCause: RootCauseCandidate;
  confidence: number;
  evidence: Evidence[];
  affectedServices: string[];
  failureChain: FailureChainEvent[];
  criticalPath: string[];
}

export interface IncidentReport {
  traceId: string;
  severity: string;
  incidentSummary: string;
  rootCause: RootCauseCandidate;
  whatHappened: string[];
  evidence: string[];
  impact: string[];
  recommendedInvestigation: string[];
  aiAssessment: string;
}

export interface Page<T> {
  content: T[];
  pageable: any;
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
