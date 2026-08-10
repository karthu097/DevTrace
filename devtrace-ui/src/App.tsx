import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { MainLayout } from './layouts/MainLayout';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

import { Dashboard } from './pages/Dashboard';
import { Traces } from './pages/Traces';
import { Dependencies } from './pages/Dependencies';
import { TraceDetail } from './pages/TraceDetail';

const Incidents = () => <div className="p-8 text-white">Incidents (Coming soon)</div>;
const Services = () => <div className="p-8 text-white">Services (Coming soon)</div>;

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<MainLayout />}>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="traces" element={<Traces />} />
            <Route path="traces/:traceId" element={<TraceDetail />} />
            <Route path="dependencies" element={<Dependencies />} />
            <Route path="incidents" element={<Incidents />} />
            <Route path="services" element={<Services />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  );
}

export default App;
