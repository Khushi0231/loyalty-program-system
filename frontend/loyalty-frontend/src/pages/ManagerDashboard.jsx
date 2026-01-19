import React, { useState, useEffect } from 'react'
import { Card } from '../components/Card'
import { Users, DollarSign, Gift, TrendingUp, Activity, Loader2 } from 'lucide-react'
import { analyticsAPI, transactionAPI, rewardAPI } from '../services/api'

const ManagerDashboard = () => {
  const [summary, setSummary] = useState(null)
  const [recentActivity, setRecentActivity] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchSummary()
  }, [])

  const fetchSummary = async () => {
    try {
      setLoading(true)
      const [summaryRes, transactionsRes, redemptionsRes] = await Promise.all([
        analyticsAPI.getSummary(),
        transactionAPI.getRecent(5),
        rewardAPI.getRecentRedemptions(5)
      ])

      console.log('Manager: Fetched summary:', summaryRes.data)
      setSummary(summaryRes.data.data)

      const transactions = (transactionsRes.data.data || []).map(t => ({
        type: 'Transaction',
        customer: t.customerName,
        time: new Date(t.transactionDate).toLocaleString(),
        detail: `Spent $${t.netAmount} (+${t.pointsEarned} pts)`
      }))

      const redemptions = (redemptionsRes.data.data || []).map(r => ({
        type: 'Redemption',
        customer: r.customerName,
        time: new Date(r.redemptionDate || r.createdAt).toLocaleString(),
        detail: `Redeemed ${r.rewardName || 'Reward'}`
      }))

      const combined = [...transactions, ...redemptions]
        .sort((a, b) => new Date(b.time) - new Date(a.time))
        .slice(0, 5)

      setRecentActivity(combined)
    } catch (error) {
      console.error('Failed to fetch analytics summary:', error)
      console.error('Error details:', error.response || error.message)
    } finally {
      setLoading(false)
    }
  }

  const stats = [
    { label: 'Total Customers', value: summary?.totalCustomers?.toLocaleString() || '0', change: '+12%', icon: Users, color: 'bg-blue-500' },
    { label: 'Monthly Revenue', value: `$${(summary?.monthlyRevenue || 0).toLocaleString()}`, change: '+8%', icon: DollarSign, color: 'bg-green-500' },
    { label: 'Points Redeemed', value: summary?.totalPointsRedeemed?.toLocaleString() || '0', change: '-5%', icon: Gift, color: 'bg-purple-500' },
    { label: 'Active Members', value: summary?.activeCustomers?.toLocaleString() || '0', change: '+15%', icon: TrendingUp, color: 'bg-yellow-500' },
  ]

  const tierDistribution = summary?.tierDistribution ? Object.entries(summary.tierDistribution).map(([tier, count]) => ({
    tier,
    count,
    percentage: summary.totalCustomers > 0 ? (count / summary.totalCustomers * 100).toFixed(1) : 0,
    color: tier === 'DIAMOND' ? 'bg-gradient-to-r from-cyan-400 to-blue-500' :
      tier === 'PLATINUM' ? 'bg-gradient-to-r from-purple-400 to-purple-600' :
        tier === 'GOLD' ? 'bg-gradient-to-r from-yellow-400 to-yellow-600' :
          tier === 'SILVER' ? 'bg-gradient-to-r from-gray-300 to-gray-400' :
            'bg-gradient-to-r from-orange-300 to-orange-500'
  })).sort((a, b) => b.count - a.count) : []



  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <Loader2 className="h-12 w-12 text-primary-500 animate-spin" />
      </div>
    )
  }

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
            {tierDistribution.length > 0 ? tierDistribution.map((tier, idx) => (
              <div key={idx}>
                <div className="flex justify-between text-sm mb-1">
                  <span className="font-medium">{tier.tier}</span>
                  <span className="text-gray-500">{tier.count} members ({tier.percentage}%)</span>
                </div>
                <div className="h-3 bg-gray-200 rounded-full overflow-hidden">
                  <div className={`h-full ${tier.color} transition-all`} style={{ width: `${tier.percentage}%` }} />
                </div>
              </div>
            )) : <p className="text-gray-500 text-center py-4">No data available.</p>}
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

      {/* Campaign Performance Placeholder */}
      <Card title="Campaign Performance Summary">
        <div className="p-4 bg-gray-50 rounded-lg border border-dashed border-gray-300">
          <p className="text-gray-500 text-center">Campaign analytics integration in progress.</p>
        </div>
      </Card>
    </div>
  )
}

export default ManagerDashboard


