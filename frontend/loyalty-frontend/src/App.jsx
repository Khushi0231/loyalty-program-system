import React from 'react'
import { Routes, Route, Link, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Layout from './components/Layout'
import CustomerDashboard from './pages/CustomerDashboard'
import SalesDashboard from './pages/SalesDashboard'
import MarketingDashboard from './pages/MarketingDashboard'
import ManagerDashboard from './pages/ManagerDashboard'

const ProtectedRoute = ({ children, allowedRoles }) => {
  const { role } = useAuth()
  if (!allowedRoles.includes(role)) {
    return <Navigate to="/" replace />
  }
  return children
}

const AppRoutes = () => {
  const { role } = useAuth()

  return (
    <Routes>
      <Route path="/" element={<Navigate to={`/${role.toLowerCase()}`} replace />} />
      
      <Route path="/customer" element={
        <ProtectedRoute allowedRoles={['CUSTOMER']}>
          <Layout>
            <CustomerDashboard />
          </Layout>
        </ProtectedRoute>
      } />
      
      <Route path="/sales" element={
        <ProtectedRoute allowedRoles={['SALES']}>
          <Layout>
            <SalesDashboard />
          </Layout>
        </ProtectedRoute>
      } />
      
      <Route path="/marketing" element={
        <ProtectedRoute allowedRoles={['MARKETING']}>
          <Layout>
            <MarketingDashboard />
          </Layout>
        </ProtectedRoute>
      } />
      
      <Route path="/manager" element={
        <ProtectedRoute allowedRoles={['MANAGER']}>
          <Layout>
            <ManagerDashboard />
          </Layout>
        </ProtectedRoute>
      } />
      
      <Route path="*" element={
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
          <div className="text-center">
            <h1 className="text-4xl font-bold text-gray-800 mb-4">404</h1>
            <p className="text-gray-600 mb-4">Page not found</p>
            <Link to="/" className="text-primary-600 hover:underline">Go back home</Link>
          </div>
        </div>
      } />
    </Routes>
  )
}

const App = () => {
  return (
    <AuthProvider>
      <AppRoutes />
    </AuthProvider>
  )
}

export default App

