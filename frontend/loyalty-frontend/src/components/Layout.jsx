import React, { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { LayoutDashboard, Users, ShoppingCart, Megaphone, BarChart3, LogOut, Menu, X } from 'lucide-react'

const Layout = ({ children }) => {
  const { role, logout, user, setRole } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const [sidebarOpen, setSidebarOpen] = useState(false)

  const roles = ['CUSTOMER', 'SALES', 'MARKETING', 'MANAGER']

  const menuItems = [
    { path: '/customer', label: 'My Dashboard', icon: LayoutDashboard, roles: ['CUSTOMER'] },
    { path: '/sales', label: 'Sales Portal', icon: ShoppingCart, roles: ['SALES'] },
    { path: '/marketing', label: 'Marketing Portal', icon: Megaphone, roles: ['MARKETING'] },
    { path: '/manager', label: 'Manager Dashboard', icon: BarChart3, roles: ['MANAGER'] },
  ]

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  const handleRoleChange = (newRole) => {
    setRole(newRole)
    navigate('/')
  }

  // Temporary fix: Show all items to help user navigate
  const filteredMenu = menuItems // menuItems.filter(item => item.roles.includes(role))

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Mobile sidebar backdrop */}
      {sidebarOpen && (
        <div className="fixed inset-0 z-40 bg-black bg-opacity-50 lg:hidden" onClick={() => setSidebarOpen(false)} />
      )}

      {/* Sidebar */}
      <aside className={`fixed top-0 left-0 z-50 h-full w-64 bg-white shadow-lg transform transition-transform lg:translate-x-0 ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}`}>
        <div className="flex items-center justify-between h-16 px-6 border-b">
          <h1 className="text-xl font-bold text-primary-600">RewardPlus</h1>
          <button className="lg:hidden" onClick={() => setSidebarOpen(false)}>
            <X className="h-6 w-6" />
          </button>
        </div>

        <nav className="p-4">
          <ul className="space-y-2">
            {filteredMenu.map((item) => (
              <li key={item.path}>
                <Link
                  to={item.path}
                  className={`flex items-center px-4 py-3 rounded-lg transition-colors ${location.pathname === item.path ? 'bg-primary-100 text-primary-600' : 'text-gray-600 hover:bg-gray-100'}`}
                >
                  <item.icon className="h-5 w-5 mr-3" />
                  {item.label}
                </Link>
              </li>
            ))}
          </ul>
        </nav>

        <div className="absolute bottom-0 left-0 right-0 p-4 border-t">
          <div className="flex items-center mb-4">
            <div className="h-10 w-10 rounded-full bg-primary-100 flex items-center justify-center">
              <span className="text-primary-600 font-semibold">{user?.firstName?.[0] || role[0]}</span>
            </div>
            <div className="ml-3">
              <p className="text-sm font-medium text-gray-800">{role} User</p>
              <p className="text-xs text-gray-500">{user?.email || 'demo@example.com'}</p>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="flex items-center w-full px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg"
          >
            <LogOut className="h-5 w-5 mr-3" />
            Sign Out
          </button>
        </div>
      </aside>

      {/* Main content */}
      <div className="lg:ml-64">
        {/* Top header */}
        <header className="bg-white shadow-sm h-16 flex items-center justify-between px-4 lg:px-8">
          <button className="lg:hidden" onClick={() => setSidebarOpen(true)}>
            <Menu className="h-6 w-6" />
          </button>
          <div className="flex items-center space-x-6">
            <div className="flex items-center">
              <span className="text-xs font-semibold text-gray-400 uppercase tracking-wider mr-2">Role:</span>
              <select
                value={role}
                onChange={(e) => handleRoleChange(e.target.value)}
                className="text-sm border-gray-300 rounded-md shadow-sm focus:border-primary-500 focus:ring-primary-500 bg-gray-50 px-2 py-1"
              >
                {roles.map(r => (
                  <option key={r} value={r}>{r}</option>
                ))}
              </select>
            </div>
            <span className="text-sm text-gray-500 hidden sm:inline">
              Welcome, {role} User
            </span>
          </div>
        </header>

        {/* Page content */}
        <main className="p-4 lg:p-8">
          {children}
        </main>
      </div>
    </div>
  )
}

export default Layout

