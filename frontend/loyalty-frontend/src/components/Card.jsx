import React from 'react'

export const Card = ({ children, title, className = '' }) => {
  return (
    <div className={`bg-white rounded-lg shadow-md p-6 ${className}`}>
      {title && <h3 className="text-lg font-semibold text-gray-800 mb-4">{title}</h3>}
      {children}
    </div>
  )
}

export const CardHeader = ({ children }) => (
  <div className="border-b border-gray-200 pb-4 mb-4">{children}</div>
)

export const CardBody = ({ children }) => <div>{children}</div>

