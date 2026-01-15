import React, { useState } from 'react'
import { Card } from '../components/Card'
import { Button } from '../components/Button'
import { Input } from '../components/Input'
import { Modal } from '../components/Modal'
import { Table } from '../components/Table'
import { Search, UserPlus, Points, CheckCircle } from 'lucide-react'

const SalesDashboard = () => {
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedCustomer, setSelectedCustomer] = useState(null)
  const [showEnrollModal, setShowEnrollModal] = useState(false)
  const [customers, setCustomers] = useState([
    { id: 1, code: 'CUST000001', name: 'John Smith', email: 'john@email.com', points: 4000, tier: 'GOLD' },
    { id: 2, code: 'CUST000002', name: 'Jane Doe', email: 'jane@email.com', points: 7000, tier: 'PLATINUM' },
    { id: 3, code: 'CUST000003', name: 'Robert Johnson', email: 'robert@email.com', points: 3200, tier: 'SILVER' },
  ])

  const columns = [
    { header: 'Code', field: 'code' },
    { header: 'Name', field: 'name' },
    { header: 'Email', field: 'email' },
    { header: 'Points', field: 'points', render: (val) => val.toLocaleString() },
    { header: 'Tier', field: 'tier', render: (val) => <span className={`px-2 py-1 rounded-full text-xs ${val === 'PLATINUM' ? 'bg-purple-100 text-purple-700' : val === 'GOLD' ? 'bg-yellow-100 text-yellow-700' : 'bg-gray-100 text-gray-700'}`}>{val}</span> },
    { header: 'Actions', field: 'id', render: (val, row) => (
      <Button variant="secondary" size="sm" onClick={() => { setSelectedCustomer(row); }}>
        View
      </Button>
    )}
  ]

  const filteredCustomers = customers.filter(c => 
    c.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    c.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
    c.code.toLowerCase().includes(searchQuery.toLowerCase())
  )

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-800">Sales Portal</h1>
        <Button onClick={() => setShowEnrollModal(true)}>
          <UserPlus className="h-4 w-4 mr-2" />
          Enroll Customer
        </Button>
      </div>

      {/* Search */}
      <Card>
        <div className="flex gap-4">
          <div className="flex-1">
            <Input
              placeholder="Search by name, email, or customer code..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="mb-0"
            />
          </div>
          <Button>
            <Search className="h-4 w-4 mr-2" />
            Search
          </Button>
        </div>
      </Card>

      {/* Customer List */}
      <Card title="Customer Lookup">
        <Table columns={columns} data={filteredCustomers} onRowClick={(row) => setSelectedCustomer(row)} />
      </Card>

      {/* Customer Detail Modal */}
      <Modal isOpen={!!selectedCustomer} onClose={() => setSelectedCustomer(null)} title="Customer Details" size="lg">
        {selectedCustomer && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-gray-500">Customer Code</p>
                <p className="font-semibold">{selectedCustomer.code}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Tier</p>
                <p className="font-semibold">{selectedCustomer.tier}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Name</p>
                <p className="font-semibold">{selectedCustomer.name}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Email</p>
                <p className="font-semibold">{selectedCustomer.email}</p>
              </div>
              <div className="col-span-2 p-4 bg-primary-50 rounded-lg">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-primary-600">Available Points</p>
                    <p className="text-3xl font-bold text-primary-700">{selectedCustomer.points.toLocaleString()}</p>
                  </div>
                  <Points className="h-12 w-12 text-primary-300" />
                </div>
              </div>
            </div>
            
            <div className="flex gap-2 pt-4 border-t">
              <Button variant="primary" className="flex-1">
                <CheckCircle className="h-4 w-4 mr-2" />
                Apply Points at POS
              </Button>
              <Button variant="secondary" className="flex-1">
                Record Transaction
              </Button>
            </div>
          </div>
        )}
      </Modal>

      {/* Enroll Customer Modal */}
      <Modal isOpen={showEnrollModal} onClose={() => setShowEnrollModal(false)} title="Enroll New Customer" size="md">
        <div className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input label="First Name" name="firstName" required />
            <Input label="Last Name" name="lastName" required />
          </div>
          <Input label="Email" name="email" type="email" required />
          <Input label="Phone" name="phone" type="tel" />
          <Input label="Date of Birth" name="dob" type="date" required />
          
          <div className="flex gap-2 pt-4">
            <Button variant="secondary" onClick={() => setShowEnrollModal(false)} className="flex-1">
              Cancel
            </Button>
            <Button onClick={() => setShowEnrollModal(false)} className="flex-1">
              Enroll Customer
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  )
}

export default SalesDashboard

