# RewardPlus Loyalty Program System

Enterprise Customer Loyalty Program with microservices architecture.

## Architecture Overview

```
loyalty-program/
├── backend/loyalty-backend/          # Spring Boot API service
├── frontend/loyalty-frontend/        # React SPA application
├── database/mysql-init/              # MySQL schema and seed data
├── helm-charts/                      # Kubernetes Helm charts
│   ├── loyalty-db-helm/
│   ├── loyalty-backend-helm/
│   └── loyalty-frontend-helm/
└── .github/workflows/                # CI/CD pipeline
```

## Technology Stack

### Backend
- **Java 17** with Spring Boot 3.2
- **Spring Data JPA** for database access
- **MySQL 8.0** as the primary database
- **Lombok** for reducing boilerplate code
- **ModelMapper** for entity-DTO mapping
- **Swagger/OpenAPI** for API documentation

### Frontend
- **React 18** with Vite
- **Tailwind CSS** for styling
- **React Router** for navigation
- **Axios** for API calls

### Infrastructure
- **Docker** for containerization
- **Kubernetes** with **Helm** for deployment
- **GitHub Actions** for CI/CD

## Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 17+
- Maven 3.8+
- Node.js 20+
- kubectl (for Kubernetes deployment)
- Helm 3+

### Local Development

1. **Start the database:**
```bash
docker-compose up -d mysql
```

2. **Build and run the backend:**
```bash
cd backend/loyalty-backend
mvn clean install
mvn spring-boot:run
```

3. **Build and run the frontend:**
```bash
cd frontend/loyalty-frontend
npm install
npm run dev
```

### Docker Deployment

```bash
# Build all images
docker-compose build

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f
```

### Kubernetes Deployment

```bash
# Add Helm repositories
helm repo add bitnami https://charts.bitnami.com/bitnami

# Deploy database
helm upgrade --install loyalty-db helm-charts/loyalty-db-helm \
  --namespace loyalty --create-namespace

# Deploy backend
helm upgrade --install loyalty-backend helm-charts/loyalty-backend-helm \
  --namespace loyalty

# Deploy frontend
helm upgrade --install loyalty-frontend helm-charts/loyalty-frontend-helm \
  --namespace loyalty
```

### ArgoCD Integration

1. Create a new Application in ArgoCD:
```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: loyalty-program
  namespace: argocd
spec:
  project: default
  source:
    repoURL: https://github.com/your-org/loyalty-program-system
    targetRevision: main
    path: helm-charts/loyalty-backend-helm
  destination:
    server: https://kubernetes.default.svc
    namespace: loyalty
  syncPolicy:
    automated:
      prune: true
      selfHeal: true
```

2. Apply the Application:
```bash
kubectl apply -f argocd-application.yaml
```

## API Documentation

Once the backend is running, access Swagger UI at:
```
http://localhost:8080/api/swagger-ui.html
```

## Features

### Customer Portal
- View points balance
- View transaction history
- Redeem rewards
- Browse promotions

### Sales Portal
- Enroll new customers
- Lookup customer points
- Apply rewards at POS

### Marketing Portal
- Create promotions
- Segment customers
- View campaign results

### Manager Dashboard
- Program analytics
- Customer activity reports
- Redemption trends

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is proprietary software. All rights reserved.

## Support

For support, please contact: support@rewardplus.com

