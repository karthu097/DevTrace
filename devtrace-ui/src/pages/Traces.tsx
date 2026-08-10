import { useQuery } from '@tanstack/react-query';
import { apiService } from '../services/api';
import { Link } from 'react-router-dom';
import { Search, Filter, ArrowRight } from 'lucide-react';
import { format } from 'date-fns';

export const Traces = () => {
  const { data, isLoading, error } = useQuery({
    queryKey: ['traces'],
    queryFn: () => apiService.getTraces(0, 50),
  });

  return (
    <div className="p-8 max-w-7xl mx-auto flex flex-col h-full">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Traces</h1>
          <p className="text-slate-400 mt-1">Investigate distributed traces across your microservices.</p>
        </div>
        <div className="flex space-x-3">
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Search className="h-4 w-4 text-slate-500" />
            </div>
            <input
              type="text"
              className="block w-64 pl-10 pr-3 py-2 border border-slate-700 rounded-md bg-slate-800/50 text-slate-300 placeholder-slate-500 focus:outline-none focus:border-primary text-sm"
              placeholder="Search trace ID..."
            />
          </div>
          <button className="flex items-center px-3 py-2 border border-slate-700 rounded-md bg-slate-800/50 text-slate-300 hover:bg-slate-700 text-sm">
            <Filter className="w-4 h-4 mr-2" />
            Filters
          </button>
        </div>
      </div>

      <div className="flex-1 bg-slate-800/30 border border-slate-700 rounded-lg overflow-hidden flex flex-col">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-slate-700">
            <thead className="bg-slate-900/50">
              <tr>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-slate-400 uppercase tracking-wider">Trace ID</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-slate-400 uppercase tracking-wider">Timestamp</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-slate-400 uppercase tracking-wider">Root Service</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-slate-400 uppercase tracking-wider">Duration</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-slate-400 uppercase tracking-wider">Status</th>
                <th scope="col" className="px-6 py-3 text-right text-xs font-medium text-slate-400 uppercase tracking-wider">Action</th>
              </tr>
            </thead>
            <tbody className="bg-slate-800/20 divide-y divide-slate-700/50">
              {isLoading && (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center text-slate-400">Loading traces...</td>
                </tr>
              )}
              {error && (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center text-red-400">Failed to load traces. Backend may be down.</td>
                </tr>
              )}
              {data?.content?.map((trace: any) => (
                <tr key={trace.traceId} className="hover:bg-slate-800/50 transition-colors">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-mono text-primary truncate max-w-[150px]">
                    <Link to={`/traces/${trace.traceId}`}>{trace.traceId}</Link>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-300">
                    {trace.startTime ? format(new Date(trace.startTime), 'HH:mm:ss.SSS') : '-'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-300">
                    {trace.rootServiceName || 'unknown'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-300">
                    {trace.durationMs ? `${trace.durationMs}ms` : '-'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    {trace.status === 'ERROR' ? (
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-900/30 text-red-400 border border-red-800/50">
                        ERROR
                      </span>
                    ) : (
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-900/30 text-emerald-400 border border-emerald-800/50">
                        OK
                      </span>
                    )}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <Link to={`/traces/${trace.traceId}`} className="text-slate-400 hover:text-primary flex justify-end items-center">
                      Investigate <ArrowRight className="w-4 h-4 ml-1" />
                    </Link>
                  </td>
                </tr>
              ))}
              {data?.content?.length === 0 && (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center text-slate-400">No traces found. Generate some traffic!</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
        
        {/* Pagination mock */}
        <div className="mt-auto px-6 py-3 border-t border-slate-700 bg-slate-900/50 flex items-center justify-between">
          <p className="text-sm text-slate-400">Showing 1 to {data?.content?.length || 0} of {data?.totalElements || 0} traces</p>
          <div className="flex space-x-2">
            <button className="px-3 py-1 border border-slate-700 rounded bg-slate-800 text-slate-400 text-sm opacity-50 cursor-not-allowed">Previous</button>
            <button className="px-3 py-1 border border-slate-700 rounded bg-slate-800 text-slate-300 text-sm hover:bg-slate-700">Next</button>
          </div>
        </div>
      </div>
    </div>
  );
};
