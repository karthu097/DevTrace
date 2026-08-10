import React from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { apiService } from '../services/api';
import { ShieldAlert, AlertCircle, Clock, Zap, Cpu, Bot, Network, CheckCircle, Server } from 'lucide-react';
import clsx from 'clsx';
import { format } from 'date-fns';

export const TraceDetail = () => {
  const { traceId } = useParams<{ traceId: string }>();

  // Fetch Trace, RootCause, and AI Report in parallel
  const { data: trace, isLoading: loadingTrace } = useQuery({
    queryKey: ['trace', traceId],
    queryFn: () => apiService.getTrace(traceId!),
    enabled: !!traceId,
  });

  const { data: rca, isLoading: loadingRca } = useQuery({
    queryKey: ['rca', traceId],
    queryFn: () => apiService.getRootCause(traceId!),
    enabled: !!traceId,
  });

  const { data: aiReport, isLoading: loadingAi } = useQuery({
    queryKey: ['ai', traceId],
    queryFn: () => apiService.getInvestigation(traceId!),
    enabled: !!traceId,
  });

  if (loadingTrace || loadingRca || loadingAi) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="flex flex-col items-center space-y-4">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
          <p className="text-slate-400">Loading trace investigation...</p>
        </div>
      </div>
    );
  }

  if (!trace) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="bg-slate-800 border border-slate-700 rounded-lg p-8 max-w-md text-center">
          <AlertCircle className="w-12 h-12 text-red-500 mx-auto mb-4" />
          <h2 className="text-xl font-bold text-white mb-2">Trace Not Found</h2>
          <p className="text-slate-400">Unable to retrieve trace data from the DevTrace backend.</p>
        </div>
      </div>
    );
  }

  const isError = trace.status === 'ERROR';

  return (
    <div className="flex flex-col h-full bg-background overflow-hidden">
      {/* Header */}
      <header className="px-8 py-6 border-b border-slate-800 bg-slate-900/80 shrink-0">
        <div className="flex justify-between items-start">
          <div>
            <div className="flex items-center space-x-3 mb-2">
              <h1 className="text-2xl font-bold text-slate-100 font-mono tracking-tight">TRACE {trace.traceId}</h1>
              <span className={clsx("px-3 py-1 rounded-full text-xs font-bold border", 
                isError ? "bg-red-900/30 text-red-400 border-red-800/50" : "bg-emerald-900/30 text-emerald-400 border-emerald-800/50")}>
                {trace.status}
              </span>
            </div>
            <div className="flex space-x-6 text-sm text-slate-400">
              <span className="flex items-center"><Server className="w-4 h-4 mr-1.5" /> Root: {trace.rootServiceName}</span>
              <span className="flex items-center"><Clock className="w-4 h-4 mr-1.5" /> {trace.durationMs}ms</span>
              <span className="flex items-center"><Zap className="w-4 h-4 mr-1.5" /> {trace.spans?.length} spans</span>
            </div>
          </div>
        </div>
      </header>

      <div className="flex-1 overflow-y-auto p-8 space-y-8">
        {/* ROOT CAUSE PANEL */}
        {rca?.rootCause && isError && (
          <section className="bg-slate-900/80 border border-red-900/50 rounded-xl overflow-hidden shadow-lg shadow-red-900/10">
            <div className="px-6 py-4 border-b border-red-900/30 bg-red-950/20 flex items-center justify-between">
              <div className="flex items-center text-red-400 font-semibold uppercase tracking-wider text-sm">
                <ShieldAlert className="w-5 h-5 mr-2" /> Deterministic Root Cause
              </div>
              <div className="text-xs font-medium px-2.5 py-1 bg-red-900/40 text-red-300 rounded border border-red-800/50">
                Confidence: {Math.round(rca.confidence * 100)}%
              </div>
            </div>
            <div className="p-6">
              <h2 className="text-2xl font-bold text-slate-100 mb-2">{rca.rootCause.description}</h2>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-6">
                <div>
                  <h4 className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Failure Type</h4>
                  <span className="inline-flex items-center px-2.5 py-1 rounded text-xs font-bold bg-orange-900/30 text-orange-400 border border-orange-800/50">
                    {rca.rootCause.type}
                  </span>
                </div>
                <div>
                  <h4 className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Critical Path</h4>
                  <div className="flex flex-wrap items-center text-sm font-mono text-slate-300">
                    {rca.criticalPath.map((svc, i) => (
                      <React.Fragment key={svc}>
                        <span className={svc === rca.rootCause.service ? "text-red-400 font-bold" : ""}>{svc}</span>
                        {i < rca.criticalPath.length - 1 && <span className="mx-2 text-slate-600">→</span>}
                      </React.Fragment>
                    ))}
                  </div>
                </div>
                <div>
                  <h4 className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Affected Services</h4>
                  <div className="flex flex-wrap gap-2">
                    {rca.affectedServices.map(svc => (
                      <span key={svc} className="px-2 py-1 text-xs font-medium bg-slate-800 text-slate-300 rounded border border-slate-700">
                        {svc}
                      </span>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          </section>
        )}

        {/* AI INVESTIGATION PANEL */}
        {aiReport && isError && (
          <section className="bg-slate-900/80 border border-indigo-900/50 rounded-xl overflow-hidden shadow-lg shadow-indigo-900/10">
            <div className="px-6 py-4 border-b border-indigo-900/30 bg-indigo-950/20 flex items-center justify-between">
              <div className="flex items-center text-indigo-400 font-semibold uppercase tracking-wider text-sm">
                <Bot className="w-5 h-5 mr-2" /> AI Incident Investigation
              </div>
              <div className="flex items-center text-xs font-medium px-2.5 py-1 bg-emerald-900/40 text-emerald-400 rounded border border-emerald-800/50">
                <CheckCircle className="w-3.5 h-3.5 mr-1.5" />
                {aiReport.aiAssessment || "Agrees with deterministic analysis"}
              </div>
            </div>
            
            <div className="p-6 grid grid-cols-1 lg:grid-cols-2 gap-8">
              <div className="space-y-6">
                <div>
                  <h3 className="text-sm font-semibold text-slate-400 uppercase tracking-wider mb-3">Summary</h3>
                  <p className="text-slate-200 leading-relaxed text-lg">{aiReport.incidentSummary}</p>
                </div>
                
                <div>
                  <h3 className="text-sm font-semibold text-slate-400 uppercase tracking-wider mb-3">What Happened</h3>
                  <ol className="relative border-l border-slate-700 ml-2 space-y-4">
                    {aiReport.whatHappened.map((step, i) => (
                      <li key={i} className="ml-5">
                        <div className="absolute w-2.5 h-2.5 bg-slate-500 rounded-full mt-1.5 -left-[5px] border border-slate-900"></div>
                        <p className="text-slate-300 text-sm leading-relaxed">{step}</p>
                      </li>
                    ))}
                  </ol>
                </div>
              </div>

              <div className="space-y-6">
                <div>
                  <h3 className="text-sm font-semibold text-slate-400 uppercase tracking-wider mb-3">Recommended Next Steps</h3>
                  <div className="bg-slate-800/50 border border-slate-700 rounded-lg p-4 space-y-3">
                    {aiReport.recommendedInvestigation.map((rec, i) => (
                      <div key={i} className="flex items-start">
                        <ArrowRight className="w-4 h-4 text-primary mt-0.5 mr-2 shrink-0" />
                        <span className="text-sm text-slate-200">{rec}</span>
                      </div>
                    ))}
                  </div>
                </div>
                
                <div>
                  <h3 className="text-sm font-semibold text-slate-400 uppercase tracking-wider mb-3">Impact</h3>
                  <ul className="list-disc list-inside text-sm text-slate-300 space-y-1">
                    {aiReport.impact.map((imp, i) => <li key={i}>{imp}</li>)}
                  </ul>
                </div>
              </div>
            </div>
          </section>
        )}

        {/* EVIDENCE CARDS */}
        {rca?.evidence && rca.evidence.length > 0 && (
          <section className="space-y-4">
            <h3 className="text-lg font-bold text-slate-200 flex items-center">
              <Cpu className="w-5 h-5 mr-2 text-slate-400" /> Evidence
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {rca.evidence.map((ev, i) => (
                <div key={i} className="bg-slate-800/50 border border-slate-700 rounded-lg p-5">
                  <div className="flex justify-between items-start mb-3">
                    <span className="text-xs font-semibold px-2 py-1 rounded bg-slate-700 text-slate-300">{ev.type}</span>
                    <span className="text-xs text-slate-500 font-mono">{format(new Date(ev.timestamp), 'HH:mm:ss.SSS')}</span>
                  </div>
                  <h4 className="text-sm font-medium text-slate-200 mb-1">{ev.service}</h4>
                  <p className="text-sm text-slate-400 font-mono bg-slate-900 p-2 rounded border border-slate-800 mt-2 break-words">
                    {ev.message}
                  </p>
                </div>
              ))}
            </div>
          </section>
        )}

        {/* TRACE WATERFALL */}
        <section className="bg-slate-900 border border-slate-700 rounded-xl overflow-hidden flex flex-col h-[500px]">
          <div className="px-6 py-4 border-b border-slate-800 bg-slate-900 flex justify-between items-center shrink-0">
            <h3 className="text-lg font-bold text-slate-200 flex items-center">
              <Network className="w-5 h-5 mr-2 text-slate-400" /> Trace Waterfall
            </h3>
            <span className="text-xs text-slate-500">Total duration: {trace.durationMs}ms</span>
          </div>
          <div className="flex-1 overflow-auto p-4">
            {trace.spans && <Waterfall spans={trace.spans} totalDuration={trace.durationMs} />}
          </div>
        </section>

      </div>
    </div>
  );
};

// --- Waterfall Subcomponents --- //

const Waterfall = ({ spans, totalDuration }: { spans: any[], totalDuration: number }) => {
  // Sort spans by start time
  const sortedSpans = [...spans].sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime());
  const traceStartTime = new Date(sortedSpans[0]?.startTime).getTime();

  return (
    <div className="space-y-1 relative min-w-[800px]">
      {sortedSpans.map(span => {
        const startOffset = new Date(span.startTime).getTime() - traceStartTime;
        const leftPercent = (startOffset / totalDuration) * 100;
        const widthPercent = Math.max((span.durationMs / totalDuration) * 100, 0.5); // min 0.5% width
        const isError = span.status === 'ERROR';

        return (
          <div key={span.spanId} className="flex items-center hover:bg-slate-800/50 py-1 rounded cursor-pointer group">
            <div className="w-48 shrink-0 flex items-center pr-4">
              <div className={clsx("w-2 h-2 rounded-full mr-2", isError ? "bg-red-500" : "bg-primary")}></div>
              <div className="truncate text-sm font-medium text-slate-300 group-hover:text-white" title={span.serviceName}>
                {span.serviceName}
              </div>
            </div>
            
            <div className="flex-1 relative h-6 border-l border-slate-700 pl-4">
              <div 
                className={clsx(
                  "absolute h-4 rounded mt-1 shadow-sm transition-all",
                  isError ? "bg-red-500/80 hover:bg-red-400" : "bg-primary/80 hover:bg-primary"
                )}
                style={{ left: `calc(${leftPercent}% + 1rem)`, width: `${widthPercent}%` }}
              ></div>
              <span 
                className="absolute text-xs text-slate-400 mt-1"
                style={{ left: `calc(${leftPercent + widthPercent}% + 1.5rem)` }}
              >
                {span.durationMs}ms
              </span>
            </div>
          </div>
        );
      })}
    </div>
  );
};

// Helper for lucide arrow in UI
const ArrowRight = ({ className }: any) => (
  <svg className={className} xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
);
