# Currency Exchange Service

A Spring Boot application for managing currency exchange rates.

## Features

### Must Requirements (All Implemented)

- [x] Currency DB maintenance function (CRUD operations)
- [x] Call OANDA API for exchange rates
- [x] Call API and convert the data to form a new API providing:
    - Update time (format: 1990/01/30 00:00:00)
    - Currency-related information
- [x] All features include unit tests
- [x] Schedule synchronization of exchange rates

### Plus Requirements

- [x] Print out the request and response body log of all API calls
- [x] Swagger UI for API documentation
- [x] i18n design with English and Chinese support
- [x] Implementation of design patterns:
    - Singleton (Spring Beans)
    - Repository Pattern (Spring Data JPA)
    - Factory Pattern (Beans creation)
    - Builder Pattern (Using Lombok's @Builder)
    - Adapter Pattern (DTO mapping)
- [x] Docker support with Docker Compose
- [x] Error handling to decorate all API responses
- [x] AES encryption/decryption implementation

## Technology Stack

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database
- Spring WebFlux (for WebClient)
- Springdoc OpenAPI (Swagger)
- Maven
- Docker

## API Endpoints

### Currency Management

- `GET /api/currencies` - Get all currencies
- `GET /api/currencies/{id}` - Get currency by ID
- `GET /api/currencies/code/{code}` - Get currency by code
- `POST /api/currencies` - Create a new currency
- `PUT /api/currencies/{id}` - Update a currency
- `DELETE /api/currencies/{id}` - Delete a currency

### Exchange Rate Management

- `GET /api/exchange-rates?baseCode={base}&quoteCode={quote}&startDate={start}&endDate={end}` - Get exchange rates for a period
- `GET /api/exchange-rates/latest?baseCode={base}&quoteCode={quote}` - Get latest exchange rate
- `POST /api/exchange-rates` - Create a new exchange rate
- `POST /api/exchange-rates/sync` - Trigger manual sync of exchange rates

## Database Schema

```sql
-- This is the SQL schema used to create tables in H2 database
CREATE TABLE currency (
    id VARCHAR(36) PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) DEFAULT 'SYSTEM',
    updated_by VARCHAR(100) DEFAULT 'SYSTEM'
);

CREATE TABLE exchange_rate (
    id VARCHAR(36) PRIMARY KEY,
    base_currency_id VARCHAR(36) NOT NULL,
    quote_currency_id VARCHAR(36) NOT NULL,
    rate DECIMAL(19,6) NOT NULL,
    date TIMESTAMP NOT NULL,
    source VARCHAR(50) DEFAULT 'OANDA',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) DEFAULT 'SYSTEM',
    updated_by VARCHAR(100) DEFAULT 'SYSTEM',
    FOREIGN KEY (base_currency_id) REFERENCES currency(id),
    FOREIGN KEY (quote_currency_id) REFERENCES currency(id),
    CONSTRAINT unique_currency_pair_date UNIQUE (base_currency_id, quote_currency_id, date)
);
```

## Running the Application

### Using Maven

```bash
# Build
mvn clean package

# Run
java -jar target/currency-exchange-0.0.1-SNAPSHOT.jar
```

### Using Docker

```bash
# Build and run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f
```

## API Documentation

When the application is running, you can access the Swagger UI at:
```
http://localhost:8080/api/swagger-ui.html
```

## Environment Variables

- `SPRING_PROFILES_ACTIVE` - Active Spring profile (default: dev)
- `SERVER_PORT` - Server port (default: 8080)
- `SCHEDULER_EXCHANGE_RATE_ENABLED` - Enable/disable scheduler (default: true)
- `SCHEDULER_EXCHANGE_RATE_CRON` - Cron expression for scheduler (default: 0 0 * * * ?)
- `CRYPTO_AES_KEY` - AES encryption key
- `CRYPTO_AES_IV` - AES initialization vector#   c u r r e n c y e x c h a n g e 
 
 
