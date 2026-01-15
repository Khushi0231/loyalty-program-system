import React, { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

export const useAuth = () => useContext(AuthContext)

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [role, setRole] = useState('CUSTOMER') // CUSTOMER, SALES, MARKETING, MANAGER

  const login = (userData, userRole) => {
    setUser(userData)
    setRole(userRole)
  }

  const logout = () => {
    setUser(null)
    setRole('CUSTOMER')
  }

  return (
    <AuthContext.Provider value={{ user, role, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

