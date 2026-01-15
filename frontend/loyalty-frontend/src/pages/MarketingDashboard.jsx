import React, { useState } from 'react'
import { Card } from '../components/Card'
import { Button } from '../components/Button'
import { Input } from '../components/Input'
import { Select } from '../components/Select'
import { Modal } from '../components/Modal'
import { Megaphone, Users, Target, Plus, BarChart3 } from 'lucide-react'

const MarketingDashboard = () => {
  const [showCreateModal, setShowCreateModal] = useState(false)
  const [promotions, setPromotions] = useState([
    { id: 1, name: 'New Year Bonus', type: 'DOUBLE_POINTS', status: 'ACTIVE', startDate: '2024-01-01', endDate: '2024-01-31', usage: 156 },
    { id: 2, name: 'Spring Sale', type: 'LOYALTY_BOOST', status: 'ACTIVE', startDate: '2024-03-01', endDate: '2024-03-31', usage: 89 },
    { id: 3, name: 'Senior Discount', type: 'DISCOUNT', status: 'SCHEDULED', startDate: '2024-04-01', endDate: '2024-04-30', usage: 0 },
    { id: 4, name: 'Student Special', type: 'DISCOUNT', status: 'ACTIVE', startDate: '2024-01-01', endDate: '2024-12-31', usage: 234 },
  ])

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
              <p className="text-3xl font-bold">{promotions.filter(p => p.status === 'ACTIVE').length}</p>
            </div>
            <Megaphone className="h-10 w-10 text-purple-200" />
          </div>
        </Card>
        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-500">Total Usage</p>
              <p className="text-2xl font-bold text-gray-800">{promotions.reduce((sum, p) => sum + p.usage, 0)}</p>
            </div>
            <BarChart3 className="h-10 w-10 text-gray-300" />
          </div>
        </Card>
        <Card>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-500">Scheduled</p>
              <p className="text-2xl font-bold text-gray-800">{promotions.filter(p => p.status === 'SCHEDULED').length}</p>
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
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Duration</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Usage</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {promotions.map((promo) => (
                <tr key={promo.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="text-sm font-medium text-gray-900">{promo.name}</div>
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
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {promo.usage} redemptions
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    <Button variant="secondary" size="sm">Edit</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>

      {/* Customer Segmentation */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card title="Customer Segments">
          <div className="space-y-3">
            {[
              { name: 'High Value Customers', count: 1250, criteria: 'Spend > $1000/month' },
              { name: 'New Customers', count: 340, criteria: 'Enrolled < 30 days' },
              { name: 'At Risk', count: 89, criteria: 'No activity > 60 days' },
              { name: 'Birthday This Month', count: 45, criteria: 'DOB this month' },
            ].map((segment, idx) => (
              <div key={idx} className="p-4 bg-gray-50 rounded-lg">
                <div className="flex justify-between items-start">
                  <div>
                    <h4 className="font-medium text-gray-800">{segment.name}</h4>
                    <p className="text-sm text-gray-500">{segment.criteria}</p>
                  </div>
                  <span className="text-lg font-bold text-primary-600">{segment.count}</span>
                </div>
              </div>
            ))}
          </div>
        </Card>

        <Card title="Campaign Performance">
          <div className="space-y-4">
            {promotions.filter(p => p.status === 'ACTIVE').map((promo) => (
              <div key={promo.id} className="p-4 border border-gray-200 rounded-lg">
                <div className="flex justify-between items-center mb-2">
                  <h4 className="font-medium text-gray-800">{promo.name}</h4>
                  <span className="text-sm text-gray-500">{promo.usage} uses</span>
                </div>
                <div className="h-2 bg-gray-200 rounded-full">
                  <div className="h-full bg-primary-500 rounded-full" style={{ width: `${Math.min(promo.usage / 5, 100)}%` }} />
                </div>
              </div>
            ))}
          </div>
        </Card>
      </div>

      {/* Create Promotion Modal */}
      <Modal isOpen={showCreateModal} onClose={() => setShowCreateModal(false)} title="Create New Promotion" size="lg">
        <div className="space-y-4">
          <Input label="Promotion Name" name="name" required />
          
          <div className="grid grid-cols-2 gap-4">
            <Select label="Promotion Type" name="type" options={typeOptions} placeholder="Select type" />
            <Select label="Status" name="status" options={statusOptions} placeholder="Select status" />
          </div>
          
          <div className="grid grid-cols-2 gap-4">
            <Input label="Start Date" name="startDate" type="date" />
            <Input label="End Date" name="endDate" type="date" />
          </div>
          
          <div className="grid grid-cols-2 gap-4">
            <Input label="Bonus Points Multiplier" name="multiplier" type="number" placeholder="e.g., 2.0" />
            <Input label="Usage Limit (0 = unlimited)" name="usageLimit" type="number" placeholder="0" />
          </div>
          
          <div className="flex gap-2 pt-4">
            <Button variant="secondary" onClick={() => setShowCreateModal(false)} className="flex-1">Cancel</Button>
            <Button onClick={() => setShowCreateModal(false)} className="flex-1">Create Promotion</Button>
          </div>
        </div>
      </Modal>
    </div>
  )
}

export default MarketingDashboard

