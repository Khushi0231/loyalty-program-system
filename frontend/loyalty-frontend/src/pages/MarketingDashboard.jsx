import React, { useState, useEffect } from 'react'
import { Card } from '../components/Card'
import { Button } from '../components/Button'
import { Input } from '../components/Input'
import { Select } from '../components/Select'
import { Modal } from '../components/Modal'
import { Megaphone, Users, Target, Plus, BarChart3, Loader2 } from 'lucide-react'
import { promotionAPI } from '../services/api'

const MarketingDashboard = () => {
  const [showCreateModal, setShowCreateModal] = useState(false)
  const [promotions, setPromotions] = useState([])
  const [loading, setLoading] = useState(true)
  const [creating, setCreating] = useState(false)

  const [promoForm, setPromoForm] = useState({
    name: '',
    description: '',
    type: 'DISCOUNT',
    status: 'ACTIVE',
    startDate: '',
    endDate: '',
    bonusPointsMultiplier: 1.0,
    minimumPurchaseAmount: 0,
    discountPercentage: 0,
    discountAmount: 0
  })

  useEffect(() => {
    fetchPromotions()
  }, [])

  const fetchPromotions = async () => {
    try {
      setLoading(true)
      const response = await promotionAPI.getAll()
      console.log('Marketing: Fetching promotions response:', response.data)
      const data = response.data.data?.content || response.data.data || []
      console.log('Marketing: Parsed data:', data)
      setPromotions(data)
    } catch (error) {
      console.error('Failed to fetch promotions:', error)
      console.error('Error details:', error.response || error.message)
    } finally {
      setLoading(false)
    }
  }

  const handleCreate = async (e) => {
    e.preventDefault()
    try {
      setCreating(true)
      await promotionAPI.create(promoForm)
      setShowCreateModal(false)
      setPromoForm({
        name: '',
        description: '',
        type: 'DISCOUNT',
        status: 'ACTIVE',
        startDate: '',
        endDate: '',
        bonusPointsMultiplier: 1.0,
        minimumPurchaseAmount: 0,
        discountPercentage: 0,
        discountAmount: 0
      })
      fetchPromotions()
    } catch (error) {
      console.error('Failed to create promotion:', error)
      alert('Failed to create promotion. Please check the logs.')
    } finally {
      setCreating(false)
    }
  }

  const typeOptions = [
    { value: 'DISCOUNT', label: 'Discount' },
    { value: 'DOUBLE_POINTS', label: 'Double Points' },
    { value: 'LOYALTY_BOOST', label: 'Loyalty Boost' },
    { value: 'BONUS_POINTS', label: 'Bonus Points' },
    { value: 'FLASH_SALE', label: 'Flash Sale' },
  ]

  const statusOptions = [
    { value: 'DRAFT', label: 'Draft' },
    { value: 'SCHEDULED', label: 'Scheduled' },
    { value: 'ACTIVE', label: 'Active' },
    { value: 'PAUSED', label: 'Paused' },
    { value: 'EXPIRED', label: 'Expired' },
  ]

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE': return 'bg-green-100 text-green-700'
      case 'SCHEDULED': return 'bg-blue-100 text-blue-700'
      case 'PAUSED': return 'bg-yellow-100 text-yellow-700'
      case 'EXPIRED': return 'bg-gray-100 text-gray-700'
      default: return 'bg-gray-100 text-gray-700'
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-800">Marketing Portal</h1>
        <Button onClick={() => setShowCreateModal(true)}>
          <Plus className="h-4 w-4 mr-2" />
          Create Promotion
        </Button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card className="bg-gradient-to-br from-purple-500 to-purple-700 text-white">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-purple-100">Active Promotions</p>
              <p className="text-3xl font-bold">{loading ? '...' : promotions.filter(p => p.status === 'ACTIVE').length}</p>
            </div>
            <Megaphone className="h-10 w-10 text-purple-200" />
          </div>
        </Card>
        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-500">Total Promotions</p>
              <p className="text-2xl font-bold text-gray-800">{loading ? '...' : promotions.length}</p>
            </div>
            <BarChart3 className="h-10 w-10 text-gray-300" />
          </div>
        </Card>
        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-500">Scheduled</p>
              <p className="text-2xl font-bold text-gray-800">{loading ? '...' : promotions.filter(p => p.status === 'SCHEDULED' || p.status === 'DRAFT').length}</p>
            </div>
            <Target className="h-10 w-10 text-blue-400" />
          </div>
        </Card>
        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-500">Customer Segments</p>
              <p className="text-2xl font-bold text-gray-800">5</p>
            </div>
            <Users className="h-10 w-10 text-green-400" />
          </div>
        </Card>
      </div>

      {/* Promotions List */}
      <Card title="Promotions">
        {loading ? (
          <div className="flex justify-center items-center py-12">
            <Loader2 className="h-8 w-8 text-primary-500 animate-spin" />
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Duration</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {promotions.map((promo) => (
                  <tr key={promo.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">{promo.name}</div>
                      <div className="text-xs text-gray-500 truncate max-w-xs">{promo.description}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="px-2 py-1 bg-gray-100 text-gray-700 rounded text-xs">{promo.type}</span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 rounded text-xs ${getStatusColor(promo.status)}`}>{promo.status}</span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {promo.startDate} - {promo.endDate}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm">
                      <Button variant="secondary" size="sm">Edit</Button>
                    </td>
                  </tr>
                ))}
                {promotions.length === 0 && (
                  <tr>
                    <td colSpan="5" className="px-6 py-4 text-center text-gray-500 text-sm">No promotions found.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </Card>

      {/* Create Promotion Modal */}
      <Modal isOpen={showCreateModal} onClose={() => setShowCreateModal(false)} title="Create New Promotion" size="lg">
        <form onSubmit={handleCreate} className="space-y-4">
          <Input
            label="Promotion Name"
            required
            value={promoForm.name}
            onChange={(e) => setPromoForm({ ...promoForm, name: e.target.value })}
          />
          <Input
            label="Description"
            value={promoForm.description}
            onChange={(e) => setPromoForm({ ...promoForm, description: e.target.value })}
          />

          <div className="grid grid-cols-2 gap-4">
            <Select
              label="Promotion Type"
              options={typeOptions}
              value={promoForm.type}
              onChange={(e) => setPromoForm({ ...promoForm, type: e.target.value })}
            />
            <Select
              label="Status"
              options={statusOptions}
              value={promoForm.status}
              onChange={(e) => setPromoForm({ ...promoForm, status: e.target.value })}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Start Date"
              type="date"
              required
              value={promoForm.startDate}
              onChange={(e) => setPromoForm({ ...promoForm, startDate: e.target.value })}
            />
            <Input
              label="End Date"
              type="date"
              required
              value={promoForm.endDate}
              onChange={(e) => setPromoForm({ ...promoForm, endDate: e.target.value })}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Bonus Points Multiplier"
              type="number"
              step="0.1"
              value={promoForm.bonusPointsMultiplier}
              onChange={(e) => setPromoForm({ ...promoForm, bonusPointsMultiplier: parseFloat(e.target.value) })}
            />
            <Input
              label="Minimum Purchase"
              type="number"
              value={promoForm.minimumPurchaseAmount}
              onChange={(e) => setPromoForm({ ...promoForm, minimumPurchaseAmount: parseFloat(e.target.value) })}
            />
          </div>

          <div className="flex gap-2 pt-4">
            <Button variant="secondary" type="button" onClick={() => setShowCreateModal(false)} className="flex-1">Cancel</Button>
            <Button type="submit" className="flex-1" disabled={creating}>
              {creating ? <Loader2 className="h-4 w-4 animate-spin mr-2" /> : <Plus className="h-4 w-4 mr-2" />}
              Create Promotion
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  )
}

export default MarketingDashboard


