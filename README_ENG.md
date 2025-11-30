## Product Management Application
This application includes a frontend, backend, and automated testing stack (Automation + Performance), all containerized using Docker.

### Technologies Used
- **Frontend:** ReactJS  
- **Backend:** Spring Boot, Spring Security  
- **Testing:**  
  - k6/Grafana (Performance Testing)  
  - Cypress (Automation Testing)  
  - JUnit 5 (Backend Unit Test)  
- **Database:** PostgreSQL  
- **Containerization:** Docker & Docker Compose  

### Features
- Login using username/password.
- CRUD operations for products.
- API protected with Spring Security.

---

## How to Run the Application

### 1. System Requirements
You must install:
- Docker Desktop  
- Git  
- Node.js  
- Java 21  
- Maven  

---

### 2. Run Instructions

#### Clone repository:
```bash
git clone https://github.com/thanhdinh2005/software-testing.git
cd software-testing
```

#### Build frontend:
```bash
cd frontend
npm install
npm run build
cd ..
```

#### Start application with Docker Compose:
```bash
docker-compose up --build -d
```

#### Access the application:
- **Frontend:** http://localhost:3000  
- **Backend:** http://localhost:8080  
- **Adminer:** http://localhost:8081  

---

## Running Tests

### Frontend Unit Tests:
```bash
cd frontend
npm test
```

### Frontend Automation Tests (Cypress):
```bash
cd frontend
npm run test:e2e
```

### Backend Unit Tests:
```bash
cd backend
mvn clean test
```

### Performance Tests (k6):
```bash
cd backend
docker compose run k6 run login-test.js
docker compose run k6 run login-stress.js
docker compose run k6 run product-test.js
```
