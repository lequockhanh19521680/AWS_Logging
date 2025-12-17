import React from 'react';
import { Outlet, Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { LogOut, Wallet, ArrowRightLeft, ArrowUpCircle, ArrowDownCircle } from 'lucide-react';

export const Layout = () => {
  const { logout, user } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!user) {
    return <Outlet />;
  }

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <div className="w-64 bg-slate-900 text-white p-4">
        <h1 className="text-2xl font-bold mb-8 text-blue-400">FinTech App</h1>
        <nav className="space-y-2">
          <Link to="/" className="flex items-center space-x-2 p-2 hover:bg-slate-800 rounded">
            <Wallet size={20} />
            <span>Dashboard</span>
          </Link>
          <Link to="/topup" className="flex items-center space-x-2 p-2 hover:bg-slate-800 rounded">
            <ArrowUpCircle size={20} />
            <span>Top Up</span>
          </Link>
          <Link to="/withdraw" className="flex items-center space-x-2 p-2 hover:bg-slate-800 rounded">
            <ArrowDownCircle size={20} />
            <span>Withdraw</span>
          </Link>
          <Link to="/transfer" className="flex items-center space-x-2 p-2 hover:bg-slate-800 rounded">
            <ArrowRightLeft size={20} />
            <span>Transfer</span>
          </Link>
        </nav>
        <div className="absolute bottom-4">
            <button onClick={handleLogout} className="flex items-center space-x-2 p-2 hover:bg-slate-800 rounded text-red-400">
                <LogOut size={20} />
                <span>Logout</span>
            </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 overflow-auto">
        <header className="bg-white shadow p-4 flex justify-between items-center">
            <h2 className="text-xl font-semibold text-gray-800">Welcome, {user.sub}</h2>
        </header>
        <main className="p-8">
            <Outlet />
        </main>
      </div>
    </div>
  );
};
