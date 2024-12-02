
# Crypto Wallet Metrics Service

This project is a Spring Boot application that calculates financial metrics for a cryptocurrency wallet. It retrieves the latest prices from an external API and provides a REST API to return the total financial value of the wallet, as well as the best and worst performing assets.

## Features

- Retrieve and store cryptocurrency prices from an external API.
- Calculate total wallet value and asset performance metrics.
- Provide a REST API to access wallet metrics.
- Support for historical data analysis using timestamps.

## Technologies Used

- Java 17
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Gradle
- SLF4J and Logback for logging

## Getting Started

### Prerequisites

- Java 17
- Gradle
- PostgreSQL

### Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/yourusername/crypto-wallet-metrics.git
   cd crypto-wallet-metrics
   

2. **Configure the database:**

   Update the `application.properties` file with your database connection details:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/yourdatabase
   spring.datasource.username=yourusername
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **Build the project:**     

   ```bash
   ./gradlew build
   ```

4. **Run the application:** 

   ```bash
   ./gradlew bootRun
   ```

## Usage

### REST API Endpoints

- **Calculate Wallet Metrics:**

  - **URL:** `/api/wallet/metrics`
  - **Method:** `POST`
  - **Request Body:**

    ```json
    [
      {
        "symbol": "BTC",
        "quantity": 0.12345,
        "price": 37870.5058,
        "timestamp": 1733039882309
      },
      {
        "symbol": "ETH",
        "quantity": 4.89532,
        "price": 2004.9774
      }
    ]
    ```

  - **Response:**

    ```json
    {
      "total": 123456.78,
      "best_asset": "BTC",
      "best_performance": 12.34,
      "worst_asset": "ETH",
      "worst_performance": -5.67
    }
    ```
