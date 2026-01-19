import React, { useState, useEffect } from 'react'
import { Card } from '../components/Card'
import { Button } from '../components/Button'
import { Gift, History, Star, TrendingUp, Loader2 } from 'lucide-react'
import { customerAPI, transactionAPI, rewardAPI, promotionAPI } from '../services/api'

const CustomerDashboard = () => {
  // Demo customer ID for this walkthrough
  const demoCustomerId = 1

  const [points, setPoints] = useState(0)
  const [tier, setTier] = useState('BRONZE')
  const [transactions, setTransactions] = useState([])
  const [rewards, setRewards] = useState([])
  const [promotions, setPromotions] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchDashboardData()
  }, [])

  const fetchDashboardData = async () => {
    try {
      setLoading(true)
      const [pointsRes, transRes, rewardsRes, promoRes] = await Promise.all([
        customerAPI.getPoints(demoCustomerId),
        transactionAPI.getAll(demoCustomerId),
        rewardAPI.getAvailable(),
        promotionAPI.getForCustomer(demoCustomerId)
      ])

      setPoints(pointsRes.data.data.currentBalance || 0)
      setTier(pointsRes.data.data.tier || 'BRONZE')
      setTransactions(transRes.data.data?.content || transRes.data.data || [])
      setRewards(rewardsRes.data.data?.content || rewardsRes.data.data || [])
      setPromotions(promoRes.data.data?.content || promoRes.data.data || [])
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error)
    } finally {
      setLoading(false)
    }
  }

  const tierProgress = {
    BRONZE: { current: 0, next: 1000 },
    SILVER: { current: 1000, next: 5000 },
    GOLD: { current: 5000, next: 10000 },
    PLATINUM: { current: 10000, next: 25000 },
    DIAMOND: { current: 25000, next: Infinity }
  }

  const progress = tierProgress[tier] ?
    ((points - tierProgress[tier].current) / (tierProgress[tier].next - tierProgress[tier].current)) * 100 : 0

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <Loader2 className="h-12 w-12 text-primary-500 animate-spin" />
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">My Dashboard</h1>

      {/* Points Overview */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card className="bg-gradient-to-br from-primary-500 to-primary-700 text-white">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-primary-100">Current Balance</p>
              <p className="text-4xl font-bold">{points.toLocaleString()}</p>
              <p className="text-primary-100 mt-2">Points</p>
            </div>
            <Star className="h-16 w-16 text-primary-200" />
          </div>
        </Card>

        <Card>
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-gray-800">Current Tier</h3>
            <span className={`px-3 py-1 rounded-full text-sm font-medium ${tier === 'GOLD' ? 'bg-yellow-100 text-yellow-800' : tier === 'PLATINUM' ? 'bg-purple-100 text-purple-800' : 'bg-gray-100 text-gray-800'}`}>{tier}</span>
          </div>
          <div className="mb-2">
            <div className="flex justify-between text-sm text-gray-600 mb-1">
              <span>{tierProgress[tier]?.current.toLocaleString()}</span>
              <span>{tierProgress[tier]?.next === Infinity ? 'MAX' : tierProgress[tier]?.next.toLocaleString()}</span>
            </div>
            <div className="h-3 bg-gray-200 rounded-full overflow-hidden">
              <div className="h-full bg-gradient-to-r from-yellow-400 to-yellow-600 transition-all" style={{ width: `${Math.min(progress, 100)}%` }} />
            </div>
          </div>
          <p className="text-sm text-gray-500">{tierProgress[tier]?.next === Infinity ? 'Maximum Tier Reached' : `${Math.max(0, Math.round(tierProgress[tier]?.next - points)).toLocaleString()} points to next tier`}</p>
        </Card>

        <Card>
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-gray-800">Quick Actions</h3>
            <Gift className="h-5 w-5 text-gray-400" />
          </div>
          <div className="space-y-2">
            <Button variant="secondary" className="w-full justify-start">
              <History className="h-4 w-4 mr-2" />
              View History
            </Button>
            <Button variant="secondary" className="w-full justify-start">
              <TrendingUp className="h-4 w-4 mr-2" />
              Browse Rewards
            </Button>
          </div>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Transactions */}
        <Card title="Recent Transactions">
          <div className="space-y-3">
            {transactions.length > 0 ? transactions.map((tx) => (
              <div key={tx.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                <div>
                  <p className="font-medium text-gray-800">{tx.description || tx.type || 'Transaction'}</p>
                  <p className="text-sm text-gray-500">{tx.transactionDate || tx.date}</p>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-green-600">+{tx.pointsEarned || tx.points} pts</p>
                  {(tx.amount || tx.transactionAmount) > 0 && <p className="text-sm text-gray-500">${(tx.amount || tx.transactionAmount).toFixed(2)}</p>}
                </div>
              </div>
            )) : <p className="text-gray-500 py-4 text-center">No recent transactions.</p>}
          </div>
        </Card>

        {/* Available Rewards */}
        <Card title="Available Rewards">
          <div className="space-y-3">
            {rewards.length > 0 ? rewards.slice(0, 3).map((reward) => (
              <div key={reward.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                <div className="flex items-center">
                  <Gift className="h-8 w-8 text-primary-500 mr-3" />
                  <div>
                    <p className="font-medium text-gray-800">{reward.name}</p>
                    <p className="text-sm text-gray-500">{reward.rewardType || reward.category}</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-primary-600">{(reward.pointsRequired || reward.points).toLocaleString()} pts</p>
                  <Button variant="primary" size="sm" className="mt-1">
                    Redeem
                  </Button>
                </div>
              </div>
            )) : <p className="text-gray-500 py-4 text-center">No rewards available.</p>}
          </div>
        </Card>

        {/* Promotions */}
        <Card title="Active Promotions" className="lg:col-span-2">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {promotions.length > 0 ? promotions.map((promo) => (
              <div key={promo.id} className="p-4 bg-gradient-to-r from-purple-50 to-pink-50 rounded-lg border border-purple-100">
                <div className="flex items-start justify-between">
                  <div>
                    <h4 className="font-semibold text-gray-800">{promo.name}</h4>
                    <p className="text-sm text-gray-600 mt-1">{promo.description}</p>
                    <p className="text-xs text-purple-600 mt-2">Ends: {promo.endDate}</p>
                  </div>
                  <span className="px-2 py-1 bg-purple-100 text-purple-700 rounded text-xs font-medium">{promo.status || 'Active'}</span>
                </div>
              </div>
            )) : <p className="text-gray-500 py-4 text-center">No active promotions.</p>}
          </div>
        </Card>
      </div>
    </div>
  )
}

export default CustomerDashboard


