import React, { useState, useEffect } from 'react'
import { Card } from '../components/Card'
import { Button } from '../components/Button'
import { Input } from '../components/Input'
import { Modal } from '../components/Modal'
import { Table } from '../components/Table'
import { Search, UserPlus, Coins, CheckCircle, Loader2 } from 'lucide-react'
import { customerAPI, transactionAPI } from '../services/api'

const SalesDashboard = () => {
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedCustomer, setSelectedCustomer] = useState(null)
  const [showEnrollModal, setShowEnrollModal] = useState(false)
  const [customers, setCustomers] = useState([])
  const [loading, setLoading] = useState(true)
  const [enrolling, setEnrolling] = useState(false)

  const [enrollForm, setEnrollForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    dateOfBirth: ''
  })

  const [showTransactionModal, setShowTransactionModal] = useState(false)
  const [recording, setRecording] = useState(false)
  const [transactionForm, setTransactionForm] = useState({
    amount: '',
    description: '',
    transactionCode: ''
  })

  useEffect(() => {
    fetchCustomers()
  }, [])

  const fetchCustomers = async () => {
    try {
      setLoading(true)
      console.log('Sales: Fetching customers response:', response.data)
      // API returns a list or a page object. Assuming it has a data field or is the list itself.
      // Based on typical Spring Data REST it might be in content.
      const data = response.data.data?.content || response.data.data || []
      console.log('Sales: Parsed data:', data)
      setCustomers(data)
    } catch (error) {
      console.error('Failed to fetch customers:', error)
      console.error('Error details:', error.response || error.message)
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = async () => {
    if (!searchQuery.trim()) {
      fetchCustomers()
      return
    }
    try {
      setLoading(true)
      const response = await customerAPI.search(searchQuery)
      setCustomers(response.data.data?.content || response.data.data || [])
    } catch (error) {
      console.error('Search failed:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleEnroll = async (e) => {
    e.preventDefault()
    try {
      setEnrolling(true)
      await customerAPI.enroll(enrollForm)
      setShowEnrollModal(false)
      setEnrollForm({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        dateOfBirth: ''
      })
      fetchCustomers()
    } catch (error) {
      console.error('Enrollment failed:', error)
      alert('Failed to enroll customer. Please check the logs.')
    } finally {
      setEnrolling(false)
    }
  }

  const handleRecordTransaction = async (e) => {
    e.preventDefault()
    try {
      setRecording(true)
      await transactionAPI.create(selectedCustomer.id, {
        amount: parseFloat(transactionForm.amount),
        description: transactionForm.description,
        transactionCode: transactionForm.transactionCode || `TRX-${Date.now()}`
      })
      setShowTransactionModal(false)
      setTransactionForm({ amount: '', description: '', transactionCode: '' })
      fetchCustomers()
      setSelectedCustomer(null)
      alert('Transaction recorded successfully!')
    } catch (error) {
      console.error('Failed to record transaction:', error)
      alert('Failed to record transaction. Please try again.')
    } finally {
      setRecording(false)
    }
  }

  const columns = [
    { header: 'Code', field: 'customerCode' },
    { header: 'Name', field: 'id', render: (val, row) => `${row.firstName} ${row.lastName}` },
    { header: 'Email', field: 'email' },
    { header: 'Tier', field: 'tier', render: (val) => <span className={`px-2 py-1 rounded-full text-xs ${val === 'PLATINUM' ? 'bg-purple-100 text-purple-700' : val === 'GOLD' ? 'bg-yellow-100 text-yellow-700' : 'bg-gray-100 text-gray-700'}`}>{val}</span> },
    {
      header: 'Actions', field: 'id', render: (val, row) => (
        <Button variant="secondary" size="sm" onClick={() => { setSelectedCustomer(row); }}>
          View
        </Button>
      )
    }
  ]

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
              onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
              className="mb-0"
            />
          </div>
          <Button onClick={handleSearch} disabled={loading}>
            {loading ? <Loader2 className="h-4 w-4 mr-2 animate-spin" /> : <Search className="h-4 w-4 mr-2" />}
            Search
          </Button>
        </div>
      </Card>

      {/* Customer List */}
      <Card title="Customer Lookup">
        {loading ? (
          <div className="flex justify-center items-center py-12">
            <Loader2 className="h-8 w-8 text-primary-500 animate-spin" />
          </div>
        ) : (
          <Table
            columns={columns}
            data={customers}
            onRowClick={(row) => setSelectedCustomer(row)}
            emptyMessage="No customers found."
          />
        )}
      </Card>

      {/* Customer Detail Modal */}
      <Modal isOpen={!!selectedCustomer} onClose={() => setSelectedCustomer(null)} title="Customer Details" size="lg">
        {selectedCustomer && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-gray-500">Customer Code</p>
                <p className="font-semibold">{selectedCustomer.customerCode}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Tier</p>
                <p className="font-semibold">{selectedCustomer.tier}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Name</p>
                <p className="font-semibold">{selectedCustomer.firstName} {selectedCustomer.lastName}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Email</p>
                <p className="font-semibold">{selectedCustomer.email}</p>
              </div>
              <div className="col-span-2 p-4 bg-primary-50 rounded-lg">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-primary-600">Points Balance</p>
                    <p className="text-2xl font-bold text-primary-700">{selectedCustomer.currentPointsBalance || 0} pts</p>
                  </div>
                  <Coins className="h-12 w-12 text-primary-300" />
                </div>
              </div>
            </div>

            <div className="flex gap-2 pt-4 border-t">
              <Button variant="primary" className="flex-1">
                <CheckCircle className="h-4 w-4 mr-2" />
                Apply Points at POS
              </Button>
              <Button variant="secondary" className="flex-1" onClick={() => setShowTransactionModal(true)}>
                Record Transaction
              </Button>
            </div>
          </div>
        )}
      </Modal>

      {/* Enroll Customer Modal */}
      <Modal isOpen={showEnrollModal} onClose={() => setShowEnrollModal(false)} title="Enroll New Customer" size="md">
        <form onSubmit={handleEnroll} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="First Name"
              required
              value={enrollForm.firstName}
              onChange={(e) => setEnrollForm({ ...enrollForm, firstName: e.target.value })}
            />
            <Input
              label="Last Name"
              required
              value={enrollForm.lastName}
              onChange={(e) => setEnrollForm({ ...enrollForm, lastName: e.target.value })}
            />
          </div>
          <Input
            label="Email"
            type="email"
            required
            value={enrollForm.email}
            onChange={(e) => setEnrollForm({ ...enrollForm, email: e.target.value })}
          />
          <Input
            label="Phone"
            type="tel"
            value={enrollForm.phone}
            onChange={(e) => setEnrollForm({ ...enrollForm, phone: e.target.value })}
          />
          <Input
            label="Date of Birth"
            type="date"
            required
            value={enrollForm.dateOfBirth}
            onChange={(e) => setEnrollForm({ ...enrollForm, dateOfBirth: e.target.value })}
          />

          <div className="flex gap-2 pt-4">
            <Button variant="secondary" type="button" onClick={() => setShowEnrollModal(false)} className="flex-1">
              Cancel
            </Button>
            <Button type="submit" className="flex-1" disabled={enrolling}>
              {enrolling ? <Loader2 className="h-4 w-4 animate-spin mr-2" /> : <UserPlus className="h-4 w-4 mr-2" />}
              Enroll Customer
            </Button>
          </div>
        </form>
      </Modal>
      {/* Record Transaction Modal */}
      <Modal isOpen={showTransactionModal} onClose={() => setShowTransactionModal(false)} title="Record New Transaction" size="md">
        <form onSubmit={handleRecordTransaction} className="space-y-4">
          <p className="text-sm text-gray-500 mb-2">
            Recording transaction for <strong>{selectedCustomer?.firstName} {selectedCustomer?.lastName}</strong>
          </p>
          <Input
            label="Transaction Amount ($)"
            type="number"
            step="0.01"
            required
            value={transactionForm.amount}
            onChange={(e) => setTransactionForm({ ...transactionForm, amount: e.target.value })}
            placeholder="0.00"
          />
          <Input
            label="Description"
            required
            value={transactionForm.description}
            onChange={(e) => setTransactionForm({ ...transactionForm, description: e.target.value })}
            placeholder="e.g. Weekly Groceries"
          />
          <Input
            label="Transaction Code (Optional)"
            value={transactionForm.transactionCode}
            onChange={(e) => setTransactionForm({ ...transactionForm, transactionCode: e.target.value })}
            placeholder="Auto-generated if empty"
          />

          <div className="flex gap-2 pt-4">
            <Button variant="secondary" type="button" onClick={() => setShowTransactionModal(false)} className="flex-1">
              Cancel
            </Button>
            <Button type="submit" className="flex-1" disabled={recording}>
              {recording ? <Loader2 className="h-4 w-4 animate-spin mr-2" /> : <Coins className="h-4 w-4 mr-2" />}
              Complete Transaction
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  )
}

export default SalesDashboard


