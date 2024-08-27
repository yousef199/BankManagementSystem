# Banking Microservices Project

This project implements a banking system using microservices architecture with Spring Boot. It consists of two main services: Customer Management and Account Management, along with supporting infrastructure components.

## Architecture Overview

- Eureka Server: Service discovery
- Spring API Gateway: Central entry point for all requests
- Customer Service: Manages customer information
- Account Service: Manages customer accounts
- Feign Client: For inter-service communication

## Services

### Customer Service

Responsible for managing customer information.

- Customer attributes:
    - Name
    - Legal ID (7 digits)
    - Type (retail, corporate, investment)
    - Address
    - Other relevant information

### Account Service

Manages customer accounts.

- Account attributes:
    - Account number (10 digits, starts with customer ID)
    - Balance
    - Account status
    - Account type (salary, savings, investment)

## Database

- Each service (Customer and Account) has its own PostgreSQL database
- Flyway is used for database migration and version control

## API Specifications

- RESTful APIs following the Open API Specification standard
- Proper request/response validation

## Validation Rules

- Customer ID: 7 digits
- Account number: 10 digits (first 7 digits are the customer ID)
- Maximum 10 accounts per customer
- Account types:
    - One salary account allowed per customer
    - Multiple savings or investment accounts allowed

## Project Structure
BankManagementSystem/
├── eureka-server/
├── api-gateway/
├── customer/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   │       └── db/migration/
│   └── pom.xml
├── account/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   │       └── db/migration/
│   └── pom.xml
└── pom.xml

## Setup and Running

1. Start the Eureka Server
2. Start the API Gateway
3. Start the Customer Service
4. Start the Account Service

Ensure that PostgreSQL is running and the necessary databases (customer , account) are created before starting the services.

## Technologies Used

- Spring Boot
- Spring Cloud Netflix (Eureka)
- Spring Cloud Gateway
- Feign Client
- PostgreSQL
- Flyway
- OpenAPI (for API documentation)

## Development

- Follow proper code structure and best practices for Spring Boot applications
- Use Flyway for database migrations
- Implement comprehensive validation for API requests/responses and database operations
- Ensure proper error handling and logging

## Testing

- Implement unit tests for each service
- Create integration tests to verify inter-service communication
- Test API endpoints using tools like Postman or curl

## Deployment

Instructions for deploying the microservices will depend on your target environment (e.g., Docker, Kubernetes, cloud platforms).