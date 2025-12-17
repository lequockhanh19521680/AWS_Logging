import React from 'react';
import { useQuery } from '@tanstack/react-query';
import api from '../lib/axios';
import { useAuthStore } from '../store/authStore';

export const Dashboard = () => {
  const { user } = useAuthStore();

  const { data: wallet, isLoading: isWalletLoading, error: walletError } = useQuery({
    queryKey: ['wallet', user?.userId],
    queryFn: async () => {
      // Try to get wallet, if 404, try to create it? 
      // Or assume backend creates it.
      // For now, let's try to get it.
      try {
        const res = await api.get(`/api/wallets/${user?.userId}`);
        return res.data;
      } catch (err: any) {
        if (err.response?.status === 500 || err.response?.status === 404) {
             // Try to create wallet if not found (though backend should handle 404 specifically)
             const createRes = await api.post(`/api/wallets?userId=${user?.userId}`);
             return createRes.data;
        }
        throw err;
      }
    },
    enabled: !!user?.userId,
  });

  const { data: transactions, isLoading: isTxLoading } = useQuery({
    queryKey: ['transactions', user?.userId],
    queryFn: async () => {
      const res = await api.get(`/api/transactions/${user?.userId}`);
      return res.data;
    },
    enabled: !!user?.userId,
  });

  if (isWalletLoading || isTxLoading) {
    return <div>Loading...</div>;
  }

  if (walletError) {
      return <div>Error loading wallet. Please contact support.</div>
  }

  return (
    <div className="space-y-6">
      {/* Balance Card */}
      <div className="bg-white p-6 rounded-lg shadow-md">
        <h3 className="text-lg font-medium text-gray-500">Total Balance</h3>
        <p className="text-4xl font-bold text-slate-900 mt-2">
          {wallet?.currency} {wallet?.balance}
        </p>
        <div className="mt-2 text-sm text-gray-500">
            Status: <span className={wallet?.status === 'ACTIVE' ? 'text-green-500' : 'text-red-500'}>{wallet?.status}</span>
        </div>
      </div>

      {/* Transactions */}
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <div className="px-6 py-4 border-b">
          <h3 className="text-lg font-medium text-gray-900">Recent Transactions</h3>
        </div>
        <div className="divide-y divide-gray-200">
          {transactions?.length === 0 ? (
            <div className="p-6 text-center text-gray-500">No transactions yet.</div>
          ) : (
            transactions?.map((tx: any) => (
              <div key={tx.id} className="p-6 flex justify-between items-center hover:bg-gray-50">
                <div>
                  <p className="font-medium text-gray-900">{tx.type} - {tx.description || 'No description'}</p>
                  <p className="text-sm text-gray-500">{new Date(tx.createdAt).toLocaleString()}</p>
                  <p className="text-xs text-gray-400">Ref: {tx.referenceId || '-'}</p>
                </div>
                <div className="text-right">
                  <p className={`font-bold ${
                    tx.type === 'TOPUP' || (tx.type === 'TRANSFER' && tx.referenceId !== String(user?.userId)) 
                    ? 'text-green-600' : 'text-red-600' // Logic is a bit complex for transfer without more context, simplifying
                  }`}>
                    {tx.type === 'TOPUP' || (tx.type === 'TRANSFER' && tx.referenceId !== String(user?.userId) /* incoming? no refId logic is vague */) ? '+' : '-'} 
                    {tx.currency} {tx.amount}
                  </p>
                  <p className="text-xs uppercase text-gray-500">{tx.status}</p>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};
