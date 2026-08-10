import { Activity, AlertTriangle, Clock, Server } from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';

const data = [
  { time: '12:00', requests: 4000, errors: 24 },
  { time: '12:05', requests: 3000, errors: 13 },
  { time: '12:10', requests: 2000, errors: 98 },
  { time: '12:15', requests: 2780, errors: 39 },
  { time: '12:20', requests: 1890, errors: 48 },
  { time: '12:25', requests: 2390, errors: 38 },
  { time: '12:30', requests: 3490, errors: 43 },
];

export const Dashboard = () => {
  return (
    <div className="p-8 max-w-7xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-slate-100">System Overview</h1>
        <p className="text-slate-400 mt-1">Real-time health and metrics across all services.</p>
      </div>

      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <MetricCard title="Total Requests" value="128,420" icon={Activity} trend="+12%" />
        <MetricCard title="Error Rate" value="2.4%" icon={AlertTriangle} trend="-0.4%" isError />
        <MetricCard title="Avg Latency" value="182ms" icon={Clock} trend="-12ms" />
        <MetricCard title="P95 Latency" value="740ms" icon={Server} trend="+45ms" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-8">
        <div className="bg-slate-800/50 border border-slate-700 rounded-lg p-6">
          <h3 className="text-base font-medium text-slate-200 mb-4">Request Volume</h3>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={data} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
                <defs>
                  <linearGradient id="colorReq" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.3}/>
                    <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <XAxis dataKey="time" stroke="#475569" fontSize={12} tickLine={false} axisLine={false} />
                <YAxis stroke="#475569" fontSize={12} tickLine={false} axisLine={false} tickFormatter={(val) => `${val / 1000}k`} />
                <Tooltip 
                  contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155', borderRadius: '0.375rem', color: '#e2e8f0' }}
                  itemStyle={{ color: '#e2e8f0' }}
                />
                <Area type="monotone" dataKey="requests" stroke="#3b82f6" fillOpacity={1} fill="url(#colorReq)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="bg-slate-800/50 border border-slate-700 rounded-lg p-6">
          <h3 className="text-base font-medium text-slate-200 mb-4">Active Incidents</h3>
          <div className="space-y-4">
            <IncidentRow 
              id="INC-1024" 
              title="Payment Provider Timeout" 
              service="payment-service" 
              time="12 mins ago" 
              severity="HIGH" 
            />
            <IncidentRow 
              id="INC-1023" 
              title="Database Connection Pool Exhausted" 
              service="inventory-service" 
              time="2 hours ago" 
              severity="CRITICAL" 
            />
            <IncidentRow 
              id="INC-1022" 
              title="High Latency on User Search" 
              service="api-gateway" 
              time="5 hours ago" 
              severity="MEDIUM" 
            />
          </div>
        </div>
      </div>
    </div>
  );
};

const MetricCard = ({ title, value, icon: Icon, trend, isError = false }: any) => (
  <div className="bg-slate-800/50 border border-slate-700 rounded-lg p-5 flex items-start justify-between">
    <div>
      <p className="text-sm font-medium text-slate-400 truncate">{title}</p>
      <p className="mt-1 text-3xl font-semibold text-slate-100">{value}</p>
      <p className={`mt-1 text-sm ${isError ? 'text-red-400' : 'text-emerald-400'}`}>
        {trend} from last hour
      </p>
    </div>
    <div className={`p-3 rounded-md ${isError ? 'bg-red-900/20 text-red-400' : 'bg-primary/10 text-primary'}`}>
      <Icon className="w-6 h-6" />
    </div>
  </div>
);

const IncidentRow = ({ id, title, service, time, severity }: any) => {
  const colors: Record<string, string> = {
    HIGH: 'bg-orange-900/30 text-orange-400 border-orange-800/50',
    CRITICAL: 'bg-red-900/30 text-red-400 border-red-800/50',
    MEDIUM: 'bg-yellow-900/30 text-yellow-400 border-yellow-800/50',
  };

  return (
    <div className="flex items-center justify-between p-3 rounded-md bg-slate-800/30 border border-slate-700/50 hover:bg-slate-800/80 transition-colors cursor-pointer">
      <div className="flex flex-col">
        <span className="text-sm font-medium text-slate-200">{title}</span>
        <div className="flex items-center mt-1 space-x-2 text-xs text-slate-400">
          <span>{id}</span>
          <span>•</span>
          <span>{service}</span>
          <span>•</span>
          <span>{time}</span>
        </div>
      </div>
      <span className={`px-2.5 py-1 rounded-full text-xs font-medium border ${colors[severity]}`}>
        {severity}
      </span>
    </div>
  );
};
