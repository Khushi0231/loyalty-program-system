# RewardPlus Loyalty Frontend

React + Vite + Tailwind CSS frontend for the RewardPlus Loyalty Program.

## Features

- **Customer Portal**: View points, history, redeem rewards, view promotions
- **Sales Portal**: Enroll customers, lookup points, apply rewards at POS
- **Marketing Portal**: Create promotions, segment customers, view campaign results

## Tech Stack

- React 18 with Vite
- Tailwind CSS for styling
- React Router for navigation
- Axios for API calls
- Lucide React for icons

## Getting Started

### Prerequisites
- Node.js 20+
- npm or yarn

### Installation

```bash
npm install
```

### Development

```bash
npm run dev
```

Access at http://localhost:3000

### Build

```bash
npm run build
```

Output will be in the `dist` directory.

### Docker

```bash
# Build image
docker build -t rewardplus/loyalty-frontend:latest .

# Run container
docker run -p 80:80 rewardplus/loyalty-frontend:latest
```

## Project Structure

```
src/
├── components/      # Reusable UI components
│   ├── Button.jsx
│   ├── Card.jsx
│   ├── Input.jsx
│   ├── Modal.jsx
│   ├── Select.jsx
│   ├── Table.jsx
│   └── Layout.jsx
├── context/         # React context providers
│   └── AuthContext.jsx
├── pages/           # Page components
│   ├── CustomerDashboard.jsx
│   ├── SalesDashboard.jsx
│   ├── MarketingDashboard.jsx
│   └── ManagerDashboard.jsx
├── services/        # API services
│   └── api.js
├── App.jsx          # Main app component
├── main.jsx         # Entry point
└── index.css        # Global styles
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| VITE_API_URL | Backend API URL | `/api` |

## Portals

### Customer Portal
- View points balance and tier
- Transaction history
- Available rewards
- Active promotions

### Sales Portal
- Customer lookup
- Enrollment
- Point redemption at POS

### Marketing Portal
- Promotion management
- Customer segmentation
- Campaign analytics

### Manager Dashboard
- Program metrics
- Tier distribution
- Top performers

