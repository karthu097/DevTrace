import React from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import { Activity, LayoutDashboard, Server, Search, AlertCircle, Network } from 'lucide-react';

export const MainLayout: React.FC = () => {
  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Traces', path: '/traces', icon: Activity },
    { name: 'Incidents', path: '/incidents', icon: AlertCircle },
    { name: 'Dependencies', path: '/dependencies', icon: Network },
    { name: 'Services', path: '/services', icon: Server },
  ];

  return (
    <div className="flex h-screen w-full bg-background overflow-hidden">
      {/* Sidebar */}
      <aside className="w-64 border-r border-slate-800 bg-slate-900/50 flex flex-col">
        <div className="h-16 flex items-center px-6 border-b border-slate-800">
          <Activity className="w-6 h-6 text-primary mr-2" />
          <h1 className="text-xl font-bold tracking-tight text-slate-100">DEVTRACE</h1>
        </div>
        
        <nav className="flex-1 overflow-y-auto py-6 px-4 space-y-1">
          <div className="text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2 px-2">
            Observability
          </div>
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.name}
                to={item.path}
                className={({ isActive }) =>
                  `flex items-center px-2 py-2 text-sm font-medium rounded-md transition-colors ${
                    isActive
                      ? 'bg-slate-800 text-primary'
                      : 'text-slate-400 hover:bg-slate-800/50 hover:text-slate-200'
                  }`
                }
              >
                <Icon className="mr-3 h-5 w-5 flex-shrink-0" />
                {item.name}
              </NavLink>
            );
          })}
        </nav>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Top Navbar */}
        <header className="h-16 flex items-center justify-between px-8 border-b border-slate-800 bg-background/95 backdrop-blur z-10">
          <div className="flex-1 flex items-center max-w-xl">
            <div className="relative w-full">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Search className="h-4 w-4 text-slate-500" />
              </div>
              <input
                type="text"
                className="block w-full pl-10 pr-3 py-2 border border-slate-700 rounded-md leading-5 bg-slate-800/50 text-slate-300 placeholder-slate-500 focus:outline-none focus:bg-slate-800 focus:border-primary focus:ring-1 focus:ring-primary sm:text-sm transition-colors"
                placeholder="Search traces, services, errors..."
              />
            </div>
          </div>
          <div className="ml-4 flex items-center space-x-4">
            <div className="text-sm text-slate-400">Environment: <span className="text-slate-200 font-medium">Production</span></div>
          </div>
        </header>

        {/* Page Content */}
        <div className="flex-1 overflow-y-auto">
          <Outlet />
        </div>
      </main>
    </div>
  );
};
