import React from 'react'
import { Card } from '../components/Card'
import { Users, DollarSign, Gift, TrendingUp, Activity } from 'lucide-react'

const ManagerDashboard = () => {
  const stats = [
    { label: 'Total Customers', value: '2,847', change: '+12%', icon: Users, color: 'bg-blue-500' },
    { label: 'Monthly Revenue', value: '$124,500', change: '+8%', icon: DollarSign, color: 'bg-green-500' },
    { label: 'Points Redeemed', value: '45,230', change: '-5%', icon: Gift, color: 'bg-purple-500' },
    { label: 'Active Members', value: '2,156', change: '+15%', icon: TrendingUp, color: 'bg-yellow-500' },
  ]

  const tierDistribution = [
    { tier: 'DIAMOND', count: 45, percentage: 1.6, color: 'bg-gradient-to-r from-cyan-400 to-blue-500' },
    { tier: 'PLATINUM', count: 120, percentage: 4.2, color: 'bg-gradient-to-r from-purple-400 to-purple-600' },
    { tier: 'GOLD', count: 380, percentage: 13.3, color: 'bg-gradient-to-r from-yellow-400 to-yellow-600' },
    { tier: 'SILVER', count: 720, percentage: 25.3, color: 'bg-gradient-to-r from-gray-300 to-gray-400' },
    { tier: 'BRONZE', count: 1582, percentage: 55.6, color: 'bg-gradient-to-r from-orange-300 to-orange-500' },
  ]

  const recentActivity = [
    { type: 'Enrollment', customer: 'Sarah Davis', time: '2 mins ago', detail: 'New member enrolled' },
    { type: 'Redemption', customer: 'John Smith', time: '5 mins ago', detail: 'Redeemed $25 Gift Card' },
    { type: 'Transaction', customer: 'Jane Doe', time: '10 mins ago', detail: 'Purchase: $250.00' },
    { type: 'Tier Upgrade', customer: 'Robert Johnson', time: '1 hour ago', detail: 'Upgraded to GOLD' },
  ]

  const topPerformers = [
    { name: 'Jane Doe', transactions: 45, revenue: 12500, tier: 'PLATINUM' },
    { name: 'Michael Brown', transactions: 38, revenue: 10200, tier: 'DIAMOND' },
    { name: 'David Wilson', transactions: 32, revenue: 8900, tier: 'GOLD' },
    { name: 'Emily Williams', transactions: 28, revenue: 7500, tier: 'SILVER' },
  ]

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">Manager Dashboard</h1>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, idx) => (
          <Card key={idx}>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-500">{stat.label}</p>
                <p className="text-2xl font-bold text-gray-800">{stat.value}</p>
                <p className="text-sm text-green-600">{stat.change} vs last month</p>
              </div>
              <div className={`p-3 rounded-full ${stat.color} text-white`}>
                <stat.icon className="h-6 w-6" />
              </div>
            </div>
          </Card>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Tier Distribution */}
        <Card title="Tier Distribution" className="lg:col-span-2">
          <div className="space-y-4">
            {tierDistribution.map((tier, idx) => (
              <div key={idx}>
                <div className="flex justify-between text-sm mb-1">
                  <span className="font-medium">{tier.tier}</span>
                  <span className="text-gray-500">{tier.count} members ({tier.percentage}%)</span>
                </div>
                <div className="h-3 bg-gray-200 rounded-full overflow-hidden">
                  <div className={`h-full ${tier.color} transition-all`} style={{ width: `${tier.percentage}%` }} />
                </div>
              </div>
            ))}
          </div>
        </Card>

        {/* Recent Activity */}
        <Card title="Recent Activity">
          <div className="space-y-4">
            {recentActivity.map((activity, idx) => (
              <div key={idx} className="flex items-start space-x-3">
                <div className="p-2 bg-gray-100 rounded-full">
                  <Activity className="h-4 w-4 text-gray-500" />
                </div>
                <div className="flex-1">
                  <p className="text-sm font-medium text-gray-800">{activity.type}</p>
                  <p className="text-sm text-gray-500">{activity.customer}</p>
                  <p className="text-xs text-gray-400">{activity.time}</p>
                </div>
              </div>
            ))}
          </div>
        </Card>
      </div>

      {/* Top Performers */}
      <Card title="Top Performing Customers">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Customer</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Tier</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Transactions</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Revenue</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {topPerformers.map((customer, idx) => (
                <tr key={idx} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">{customer.name}</div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 py-1 rounded-full text-xs ${
                      customer.tier === 'DIAMOND' ? 'bg-cyan-100 text-cyan-700' :
                      customer.tier === 'PLATINUM' ? 'bg-purple-100 text-purple-700' :
                      customer.tier === 'GOLD' ? 'bg-yellow-100 text-yellow-700' :
                      'bg-gray-100 text-gray-700'
                    }`}>{customer.tier}</span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{customer.transactions}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${customer.revenue.toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  )
}

export default ManagerDashboard

