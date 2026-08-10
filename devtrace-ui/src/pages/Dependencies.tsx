import { ReactFlow, Controls, Background } from 'reactflow';
import type { Node, Edge } from 'reactflow';
import 'reactflow/dist/style.css';
import { Server, AlertTriangle } from 'lucide-react';

// Custom Node for microservices
const ServiceNode = ({ data }: any) => {
  return (
    <div className={`px-4 py-3 shadow-md rounded-lg border-2 ${data.status === 'ERROR' ? 'border-red-500 bg-red-950/40' : 'border-slate-600 bg-slate-800'}`}>
      <div className="flex items-center">
        {data.status === 'ERROR' ? <AlertTriangle className="text-red-400 w-5 h-5 mr-2" /> : <Server className="text-primary w-5 h-5 mr-2" />}
        <div className="font-bold text-slate-100">{data.label}</div>
      </div>
      <div className="mt-2 text-xs text-slate-300">
        <div className="flex justify-between"><span>Req/s:</span> <span className="font-mono">{data.requests}</span></div>
        <div className="flex justify-between"><span>Latency:</span> <span className="font-mono">{data.latency}ms</span></div>
        <div className="flex justify-between"><span>Errors:</span> <span className={`font-mono ${data.errorRate > 0 ? 'text-red-400' : ''}`}>{data.errorRate}%</span></div>
      </div>
    </div>
  );
};

const nodeTypes = {
  serviceNode: ServiceNode,
};

const initialNodes: Node[] = [
  {
    id: 'api-gateway',
    type: 'serviceNode',
    position: { x: 400, y: 50 },
    data: { label: 'API Gateway', status: 'OK', requests: 1240, latency: 45, errorRate: 0.1 },
  },
  {
    id: 'order-service',
    type: 'serviceNode',
    position: { x: 400, y: 200 },
    data: { label: 'Order Service', status: 'OK', requests: 450, latency: 120, errorRate: 0.5 },
  },
  {
    id: 'inventory-service',
    type: 'serviceNode',
    position: { x: 200, y: 350 },
    data: { label: 'Inventory Service', status: 'OK', requests: 200, latency: 30, errorRate: 0 },
  },
  {
    id: 'payment-service',
    type: 'serviceNode',
    position: { x: 600, y: 350 },
    data: { label: 'Payment Service', status: 'ERROR', requests: 250, latency: 3100, errorRate: 15.4 },
  },
  {
    id: 'payment-provider',
    type: 'serviceNode',
    position: { x: 600, y: 500 },
    data: { label: 'Payment Provider', status: 'ERROR', requests: 250, latency: 5050, errorRate: 18.2 },
  },
];

const initialEdges: Edge[] = [
  { id: 'e-gw-order', source: 'api-gateway', target: 'order-service', animated: true, style: { stroke: '#94a3b8' } },
  { id: 'e-order-inv', source: 'order-service', target: 'inventory-service', animated: true, style: { stroke: '#94a3b8' } },
  { id: 'e-order-pay', source: 'order-service', target: 'payment-service', animated: true, style: { stroke: '#ef4444', strokeWidth: 2 } },
  { id: 'e-pay-prov', source: 'payment-service', target: 'payment-provider', animated: true, style: { stroke: '#ef4444', strokeWidth: 2 } },
];

export const Dependencies = () => {
  return (
    <div className="h-full w-full flex flex-col p-8 max-w-7xl mx-auto">
      <div className="mb-6 flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-slate-100">Service Dependencies</h1>
          <p className="text-slate-400 mt-1">Real-time topology and traffic flow.</p>
        </div>
        <div className="flex space-x-4">
          <div className="flex items-center text-sm text-slate-300">
            <span className="w-3 h-3 rounded-full bg-slate-500 mr-2"></span> Healthy
          </div>
          <div className="flex items-center text-sm text-slate-300">
            <span className="w-3 h-3 rounded-full bg-red-500 mr-2"></span> Failing
          </div>
        </div>
      </div>
      
      <div className="flex-1 bg-slate-900 border border-slate-700 rounded-lg overflow-hidden relative">
        <ReactFlow
          nodes={initialNodes}
          edges={initialEdges}
          nodeTypes={nodeTypes}
          fitView
          className="bg-slate-900"
        >
          <Background color="#334155" gap={16} />
          <Controls className="bg-slate-800 border-slate-700 fill-slate-300" />
        </ReactFlow>
      </div>
    </div>
  );
};
