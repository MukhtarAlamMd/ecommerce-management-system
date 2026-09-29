\# E-Commerce Management System



A full-stack e-commerce management system built using \*\*Java, Spring Boot Microservices, MySQL, React, Keycloak, Apache Kafka, Eureka, OpenFeign, and Docker\*\*.



The project is designed with a microservices architecture where product management, inventory, orders, payments, notifications, service discovery, API routing, and authentication are handled by separate services.



\## Architecture



```text

&#x20;                   ┌─────────────────────┐

&#x20;                   │     React Frontend  │

&#x20;                   │       Vite          │

&#x20;                   └──────────┬──────────┘

&#x20;                              │

&#x20;                              ▼

&#x20;                   ┌─────────────────────┐

&#x20;                   │    API Gateway      │

&#x20;                   │      :8080          │

&#x20;                   └──────────┬──────────┘

&#x20;                              │

&#x20;         ┌────────────────────┼────────────────────┐

&#x20;         │                    │                    │

&#x20;         ▼                    ▼                    ▼

&#x20;  Product Service       Order Service       Inventory Service

&#x20;     :8082                  :8084                 :8086

&#x20;         │                    │

&#x20;         │                    ├──────────────┐

&#x20;         │                    ▼              ▼

&#x20;         │             Payment Service   Kafka

&#x20;         │                :8085           :9092

&#x20;         │                                   │

&#x20;         │                                   ▼

&#x20;         │                         Notification Service

&#x20;         │                                :8087

&#x20;         │

&#x20;         ▼

&#x20;     MySQL Databases



&#x20;                   ┌─────────────────────┐

&#x20;                   │   Eureka Server     │

&#x20;                   │      :8761          │

&#x20;                   └─────────────────────┘



&#x20;                   ┌─────────────────────┐

&#x20;                   │      Keycloak       │

&#x20;                   │      :8088          │

&#x20;                   └─────────────────────┘

```



\## Services



| Service              | Port | Responsibility                          |

| -------------------- | ---: | --------------------------------------- |

| Service Registry     | 8761 | Service discovery using Eureka          |

| API Gateway          | 8080 | Central API routing and security        |

| Product Service      | 8082 | Products, categories and product images |

| Order Service        | 8084 | Orders, order items and order workflow  |

| Payment Service      | 8085 | Payment processing and payment status   |

| Inventory Service    | 8086 | Stock management                        |

| Notification Service | 8087 | Customer notifications                  |

| Keycloak             | 8088 | Authentication and authorization        |

| Kafka                | 9092 | Asynchronous event communication        |



\## Main Technologies



\### Backend



\* Java 17

\* Spring Boot

\* Spring Security

\* Spring Data JPA

\* Hibernate

\* Spring Cloud

\* Spring Cloud Gateway

\* Spring Cloud Netflix Eureka

\* OpenFeign

\* Resilience4j

\* Maven

\* MySQL



\### Security



\* Keycloak

\* OAuth 2.0 / OpenID Connect

\* JWT

\* Role-based access control

\* Roles:



&#x20; \* `ADMIN`

&#x20; \* `SELLER`

&#x20; \* `CUSTOMER`



\### Messaging



\* Apache Kafka

\* Kafka producers and consumers

\* Notification events

\* Outbox event pattern



\### Infrastructure



\* Docker

\* Docker Compose

\* Eureka Service Discovery



\### Testing



\* JUnit

\* Spring Boot Test

\* Controller tests

\* Service-layer tests



\## Key Features



\### Product Management



\* Create products

\* Update products

\* Delete products

\* View products

\* Product categories

\* Product image upload

\* Product search and management



\### Inventory Management



\* Track product stock

\* Create inventory records

\* Update stock

\* Validate available stock

\* Prevent insufficient-stock orders



\### Order Management



\* Create orders

\* Order items

\* Order status management

\* Order cancellation

\* Customer order tracking

\* Integration with product, inventory and payment services

\* Order event publishing



\### Payment Management



\* Payment processing

\* Payment status tracking

\* Payment method handling

\* Order/payment integration

\* Payment failure handling



\### Notification System



\* Kafka-based notification events

\* Asynchronous notification processing

\* Notification persistence

\* Notification status management

\* Customer notification retrieval



\### Authentication and Authorization



The application uses \*\*Keycloak\*\* for authentication and role-based authorization.



The main application roles are:



```text

ADMIN

SELLER

CUSTOMER

```



Services validate JWT access tokens issued by Keycloak.



\## Communication Between Services



The application uses two major communication patterns.



\### Synchronous Communication



OpenFeign is used for service-to-service REST communication.



Example:



```text

Order Service

&#x20;    │

&#x20;    ├──► Product Service

&#x20;    │

&#x20;    ├──► Inventory Service

&#x20;    │

&#x20;    └──► Payment Service

```



\### Asynchronous Communication



Apache Kafka is used for notification events.



```text

Order Service

&#x20;    │

&#x20;    ▼

&#x20;Outbox Event

&#x20;    │

&#x20;    ▼

&#x20;  Kafka

&#x20;    │

&#x20;    ▼

Notification Service

&#x20;    │

&#x20;    ▼

Notification Database

```



This allows notification processing to happen asynchronously without tightly coupling the order and notification services.



\## Database Structure



The application uses separate databases for different microservices.



Examples:



```text

product\_db

inventory\_db

order\_db

payment\_db

notification\_db

```



Each microservice owns its own data.



\## Docker



The project includes Docker configuration for running the backend services and infrastructure.



Main infrastructure includes:



```text

Eureka

Kafka

Product Service

Inventory Service

Order Service

Payment Service

Notification Service

API Gateway

```



\## Configuration



Database credentials are intentionally not stored directly in the repository.



Services use environment variables such as:



```text

DB\_PASSWORD

```



You should provide your own environment-specific configuration when running the application.



\## Running the Project



\### Prerequisites



Install:



\* Java 17

\* Maven

\* MySQL

\* Docker Desktop

\* Node.js and npm for the React frontend

\* Keycloak configuration

\* Git



\### Clone the Repository



```bash

git clone https://github.com/MukhtarAlamMd/ecommerce-management-system.git

cd ecommerce-management-system

```



\### Start Backend Infrastructure



Use Docker Compose:



```bash

docker compose up -d

```



Check running containers:



```bash

docker ps

```



\### Start Individual Services



Each Spring Boot service can also be started independently using its Maven wrapper.



Example:



```bash

cd product-service/product-service

./mvnw spring-boot:run

```



On Windows:



```powershell

.\\mvnw.cmd spring-boot:run

```



\## Default Service Ports



```text

Eureka              8761

API Gateway         8080

Product Service     8082

Order Service       8084

Payment Service     8085

Inventory Service   8086

Notification       8087

Keycloak            8088

Kafka               9092

```



\## Security Notes



This repository intentionally excludes sensitive and local-only files such as:



\* Database passwords

\* Keycloak realm exports containing secrets

\* Build output

\* Maven local repository

\* Uploaded runtime product images

\* Local environment files



Before running the application, configure your own credentials and environment variables.



\## Project Structure



```text

ecommerce-management-system/

│

├── api-gateway/

├── inventory-service/

├── notification-service/

├── order-service/

├── payment-service/

├── product-service/

├── service-registry/

│

├── docker-compose.yml

├── .gitignore

└── README.md

```



\## Architecture Highlights



This project demonstrates several backend engineering concepts:



\* Microservices architecture

\* Service discovery

\* API Gateway pattern

\* REST APIs

\* Database-per-service architecture

\* JWT authentication

\* Role-based authorization

\* Inter-service communication

\* OpenFeign

\* Fault tolerance with Resilience4j

\* Event-driven architecture

\* Apache Kafka

\* Transactional Outbox pattern

\* Docker containerization

\* Automated API routing

\* Unit and controller testing



\## Future Improvements



Possible future improvements include:



\* Production deployment

\* CI/CD pipeline

\* Cloud deployment

\* Centralized configuration

\* Distributed tracing

\* Monitoring and observability

\* Redis caching

\* Automated database migrations

\* Payment gateway integration

\* Advanced search

\* Email/SMS notification integration



\## Author



\*\*Mukhtar Alam\*\*



GitHub:



https://github.com/MukhtarAlamMd



\---



\## License



This project is currently provided for portfolio and demonstration purposes.



