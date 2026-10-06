# E-Commerce Management System — Frontend

A modern **React + Vite frontend** for a full-stack E-Commerce Management System built with **Java, Spring Boot Microservices, MySQL, Kafka, Keycloak, Docker, and React**.

The application provides separate experiences for **Customers, Sellers, and Administrators**, with role-based access control, product management, inventory management, order processing, payments, notifications, and AI-powered customer message classification.

---

## 🚀 Features

### 👤 Customer

* Keycloak-based authentication
* Role-based authorization
* Browse products and categories
* Product details
* Shopping cart
* Checkout
* Order history
* Order details
* Payment integration
* Customer profile
* Change password
* Order notifications
* Notification bell
* AI-powered customer support/message classification

### 🏪 Seller

* Seller dashboard
* Product and inventory management
* View inventory
* Manage orders
* Track order status

### 🛡️ Administrator

* Admin dashboard
* Product management
* Category management
* Inventory management
* Order management
* Payment management
* User management
* Create users
* Role-based access control
* View system information

---

## 🧰 Technology Stack

| Technology              | Purpose                          |
| ----------------------- | -------------------------------- |
| React.js                | Frontend UI                      |
| Vite                    | Frontend build tool              |
| JavaScript              | Frontend programming             |
| React Router            | Client-side routing              |
| Keycloak                | Authentication and authorization |
| OAuth2 / OpenID Connect | Authentication protocol          |
| JWT                     | API authorization                |
| CSS                     | Styling                          |
| REST APIs               | Backend communication            |
| Docker                  | Containerization                 |

---

## 🔐 Authentication & Authorization

Authentication and authorization are handled using **Keycloak**.

The frontend uses:

* Keycloak
* OAuth2 / OpenID Connect
* JWT access tokens
* PKCE
* Role-based access control
* Protected routes

Supported application roles:

```text
ADMIN
SELLER
CUSTOMER
```

The frontend reads the authenticated user's roles from the Keycloak token and displays the appropriate application features.

---

## 🏗️ Frontend Architecture

```text
ecommerce-frontend/
│
├── public/
│
├── src/
│   │
│   ├── assets/
│   │
│   ├── components/
│   │
│   ├── context/
│   │
│   ├── pages/
│   │
│   ├── services/
│   │
│   ├── hooks/
│   │
│   ├── utils/
│   │
│   ├── App.jsx
│   └── main.jsx
│
├── .gitignore
├── eslint.config.js
├── index.html
├── package.json
├── package-lock.json
├── vite.config.js
└── README.md
```

---

## 🧩 Backend Architecture

The React frontend communicates with the backend through the API Gateway.

```text
                         ┌──────────────────────┐
                         │   React Frontend     │
                         │      :5173           │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │     API Gateway      │
                         │        :8080         │
                         └──────────┬───────────┘
                                    │
             ┌──────────────────────┼──────────────────────┐
             │                      │                      │
             ▼                      ▼                      ▼
      Product Service         Order Service          Payment Service
          :8082                  :8084                   :8085
             │                      │                      │
             └──────────────────────┼──────────────────────┘
                                    │
                  ┌─────────────────┼─────────────────┐
                  │                 │                 │
                  ▼                 ▼                 ▼
          Inventory Service  Notification Service   AI Service
               :8086                 :8087             :8089

                  │
                  ▼
             MySQL Databases

                  │
                  ▼
               Kafka

                  │
                  ▼
        Event-driven Notifications


Keycloak :8088
Service Registry :8761
```

---

## 🏢 Backend Microservices

| Service              | Port | Responsibility                   |
| -------------------- | ---: | -------------------------------- |
| API Gateway          | 8080 | Central API entry point          |
| Product Service      | 8082 | Products and categories          |
| Order Service        | 8084 | Order management                 |
| Payment Service      | 8085 | Payment processing               |
| Inventory Service    | 8086 | Stock and inventory              |
| Notification Service | 8087 | Notifications                    |
| Keycloak             | 8088 | Authentication and authorization |
| AI Service           | 8089 | Customer message classification  |
| Service Registry     | 8761 | Eureka service discovery         |
| React Frontend       | 5173 | Web application                  |

---

## 🤖 AI Customer Support

The project includes an AI service for classifying customer messages.

For example:

```text
Customer Message:

"Payment was deducted but my order is still pending"
```

The AI service can classify the message as:

```text
Category: PAYMENT_ISSUE
Priority: HIGH
Confidence: 0.94
```

This functionality can help an e-commerce support system identify the type and priority of customer issues.

---

## 🔔 Event-Driven Notifications

The application uses **Apache Kafka** for event-driven notification processing.

Example order events include:

```text
ORDER_CONFIRMED
PROCESSING
SHIPPED
DELIVERED
CANCELLED
```

The general flow is:

```text
Order Service
     │
     ▼
   Kafka
     │
     ▼
Notification Service
     │
     ▼
React Notification Bell
```

This allows notification processing to be separated from the main order-processing flow.

---

## 🔄 API Communication

The frontend communicates with backend services through REST APIs.

Typical flow:

```text
React
  │
  ▼
API Gateway
  │
  ├── Product Service
  ├── Order Service
  ├── Payment Service
  ├── Inventory Service
  ├── Notification Service
  └── AI Service
```

The API Gateway provides a single entry point for frontend API communication.

---

## 📦 Main Frontend Modules

The frontend contains modules for:

* Authentication
* Products
* Categories
* Shopping Cart
* Orders
* Payments
* Inventory
* Notifications
* User Management
* Admin Dashboard
* Seller Dashboard
* Customer Dashboard
* AI Customer Support

---

## 🛠️ Local Development

### Prerequisites

Install the following:

* Node.js
* npm
* Java 17
* MySQL
* Docker
* Docker Compose
* Keycloak
* Apache Kafka

---

## ▶️ Run the Frontend

Navigate to the frontend directory:

```bash
cd ecommerce-frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

The frontend will normally be available at:

```text
http://localhost:5173
```

---

## 🏭 Production Build

Create a production build:

```bash
npm run build
```

Preview the production build:

```bash
npm run preview
```

---

## 🐳 Run the Complete Application with Docker

From the project root:

```bash
docker compose up --build
```

This starts the required backend services and infrastructure defined in the Docker Compose configuration.

---

## 🔑 Keycloak Configuration

The frontend is configured to communicate with Keycloak.

Typical local configuration:

```text
Keycloak:
http://localhost:8088

Realm:
ecommerce

Frontend Client:
ecommerce-frontend
```

Authentication uses:

```text
Keycloak
   │
   ▼
OAuth2 / OpenID Connect
   │
   ▼
JWT Access Token
   │
   ▼
API Gateway
   │
   ▼
Spring Boot Microservices
```

---

## 🧪 Development & Testing

The project can be tested using tools such as:

* Postman
* Browser Developer Tools
* Spring Boot Actuator
* Docker logs
* Git
* GitHub

The frontend and backend can be developed and tested independently.

---

## 📸 Project Highlights

The application demonstrates a practical full-stack e-commerce architecture including:

* React frontend
* Java 17
* Spring Boot
* Spring Cloud
* Microservices
* REST APIs
* MySQL
* JPA / Hibernate
* Keycloak
* OAuth2 / JWT
* Role-Based Access Control
* Eureka Service Discovery
* API Gateway
* OpenFeign
* Resilience4j
* Apache Kafka
* Docker
* Docker Compose
* AI customer-message classification

---

## 📚 What This Project Demonstrates

This project demonstrates practical experience with:

* Full-stack application development
* REST API integration
* Microservice architecture
* Authentication and authorization
* Database-driven applications
* Event-driven architecture
* Inter-service communication
* API Gateway patterns
* Service discovery
* Fault tolerance
* Containerization
* Frontend-backend integration
* Debugging distributed applications

---

## 🔗 Project Repository

Main GitHub repository:

**E-Commerce Management System**

```text
https://github.com/MukhtarAlamMd/ecommerce-management-system
```

---

## 👨‍💻 Developer

**Md Mukhtar Alam**

Java Spring Boot Developer | Microservices | REST APIs | React

### Core Skills

```text
Java
Spring Boot
Spring Security
Spring Data JPA
Hibernate
REST APIs
Microservices
MySQL
React
Keycloak
OAuth2
JWT
Kafka
Docker
Git
GitHub
```

---

## 📄 License

This project is intended primarily as a learning, portfolio, and demonstration project.
